package com.example.weighttracker.trend;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.example.weighttracker.data.model.WeightEntry;

/**
 * Computes weight statistics and projects a goal date using a least-squares regression.
 * Dates must use ISO-8601 (yyyy-MM-dd); invalid dates are ignored. Analysis runs in
 * O(n log n) due to sorting, followed by an O(n) pass for statistics and regression.
 * The results are recomputed on each request because the application's small dataset
 * doesn't justify the added complexity of maintaining an incremental cache.
 */

public final class WeightTrendAnalyzer {

    private static final int MOVING_AVERAGE_WINDOW = 7;
    private static final double FLAT_SLOPE_EPSILON = 1e-9;

    private WeightTrendAnalyzer() {
    }

/**
 * Parses and sorts weight entries by date, skipping invalid dates.
 * Provided separately for callers that only need the chronological series.
 */

    public static List<DatedWeight> sortedSeries(List<WeightEntry> rawEntries) {
        List<DatedWeight> parsed = new ArrayList<>();
        for (WeightEntry entry : rawEntries) {
            LocalDate date = parseDate(entry.getEntryDate());
            if (date != null) {
                parsed.add(new DatedWeight(date, entry.getWeight()));
            }
        }
        parsed.sort(Comparator.comparing(DatedWeight::getDate));
        return parsed;
    }

    public static TrendSummary analyze(List<WeightEntry> rawEntries, Double goalWeight, LocalDate today) {
        List<DatedWeight> entries = sortedSeries(rawEntries);
        if (entries.isEmpty()) {
            return TrendSummary.empty();
        }

        double currentWeight = entries.get(entries.size() - 1).getWeight();
        double totalChange = currentWeight - entries.get(0).getWeight();

        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        for (DatedWeight entry : entries) {
            min = Math.min(min, entry.getWeight());
            max = Math.max(max, entry.getWeight());
        }

        double movingAverage7 = movingAverage(entries, MOVING_AVERAGE_WINDOW);

        LocalDate firstDate = entries.get(0).getDate();
        Regression regression = fitLinearRegression(entries, firstDate);

        LocalDate projectedDate = null;
        if (goalWeight != null && Math.abs(regression.slope) > FLAT_SLOPE_EPSILON) {
            long xToday = ChronoUnit.DAYS.between(firstDate, today);
            double currentTrendWeight = regression.slope * xToday + regression.intercept;
            double daysToGoal = (goalWeight - currentTrendWeight) / regression.slope;
            if (daysToGoal > 0) {
                projectedDate = today.plusDays(Math.round(daysToGoal));
            }
        }

        return TrendSummary.of(currentWeight, totalChange, movingAverage7, min, max,
                regression.slope, regression.intercept, firstDate, projectedDate);
    }

    private static double movingAverage(List<DatedWeight> entries, int window) {
        int count = Math.min(window, entries.size());
        double sum = 0;
        for (int i = entries.size() - count; i < entries.size(); i++) {
            sum += entries.get(i).getWeight();
        }
        return sum / count;
    }

    /** Computes a least-squares linear regression for weight over time. */

    private static Regression fitLinearRegression(List<DatedWeight> entries, LocalDate firstDate) {
        int n = entries.size();
        double sumX = 0;
        double sumY = 0;
        double sumXY = 0;
        double sumXX = 0;
        for (DatedWeight entry : entries) {
            double x = ChronoUnit.DAYS.between(firstDate, entry.getDate());
            double y = entry.getWeight();
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumXX += x * x;
        }

        double denominator = n * sumXX - sumX * sumX;
        // Use a flat slope when there are too few distinct dates to calculate one.
        
        if (n < 2 || Math.abs(denominator) < FLAT_SLOPE_EPSILON) {
            return new Regression(0, sumY / n);
        }

        double slope = (n * sumXY - sumX * sumY) / denominator;
        double intercept = (sumY - slope * sumX) / n;
        return new Regression(slope, intercept);
    }

    private static LocalDate parseDate(String rawDate) {
        try {
            return LocalDate.parse(rawDate);
        } catch (DateTimeParseException | NullPointerException e) {
            return null;
        }
    }

    private static final class Regression {
        final double slope;
        final double intercept;

        Regression(double slope, double intercept) {
            this.slope = slope;
            this.intercept = intercept;
        }
    }
}
