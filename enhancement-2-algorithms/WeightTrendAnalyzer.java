package com.example.weighttracker.trend;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.example.weighttracker.data.model.WeightEntry;

/**
 * Computes summary statistics and a projected goal date from a user's weight history: current
 * weight, total change, a 7-day moving average, min/max, and a hand-implemented least-squares
 * linear regression used to project when the user will reach their goal weight.
 *
 * <p>This class has no Android framework dependency, so it runs under plain JUnit on the host
 * JVM with no emulator or Robolectric needed. Dates are expected in ISO-8601 ({@code yyyy-MM-dd});
 * any entry whose date fails to parse is dropped rather than failing the whole analysis, since a
 * single malformed row (from before date validation was enforced) shouldn't take down the
 * dashboard.
 *
 * <p><b>Complexity:</b> sorting the entries ascending by date is O(n log n); the summary
 * statistics and the regression sums (Sx, Sy, Sxy, Sxx) are then accumulated in a single O(n)
 * pass.
 *
 * <p><b>Caching trade-off:</b> the regression sums are each a linear accumulation, so they could
 * be maintained incrementally in O(1) per insert. Min and max cannot be updated in O(1) under
 * deletion in the general case - removing the current minimum forces a rescan - so a fully
 * incremental cache would still need an O(n) fallback for edits and deletes. Given that, and
 * given this app's realistic data volume (one person's daily weigh-ins, at most a few thousand
 * rows over years of use), a full recompute on every dashboard refresh costs microseconds. The
 * bookkeeping and invalidation logic a true incremental cache would need isn't worth it at this
 * scale, so this class recomputes from scratch on every call instead of maintaining persistent
 * state.
 */
public final class WeightTrendAnalyzer {

    private static final int MOVING_AVERAGE_WINDOW = 7;
    private static final double FLAT_SLOPE_EPSILON = 1e-9;

    private WeightTrendAnalyzer() {
    }

    /**
     * Parses and sorts the raw entries ascending by date, dropping any with an unparseable date.
     * Exposed separately from {@link #analyze} so callers that only need the chronological series
     * (for example, a chart) don't have to duplicate the date parsing.
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

    /** Single-pass least-squares linear regression: weight as a function of days since the first entry. */
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
        // Fewer than two points, or every point on the same day: the line's slope is undefined.
        // Treat it as flat rather than dividing by (near) zero.
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
