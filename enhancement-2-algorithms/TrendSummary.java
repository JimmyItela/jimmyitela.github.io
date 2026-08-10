package com.example.weighttracker.trend;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** Immutable snapshot of the statistics {@link WeightTrendAnalyzer} computes from a user's weight history. */
public class TrendSummary {

    private final boolean hasData;
    private final double currentWeight;
    private final double totalChange;
    private final double movingAverage7;
    private final double minWeight;
    private final double maxWeight;
    private final double slopePerDay;
    private final double interceptWeight;
    private final LocalDate firstEntryDate;
    private final LocalDate projectedGoalDate;

    private TrendSummary(boolean hasData, double currentWeight, double totalChange, double movingAverage7,
                          double minWeight, double maxWeight, double slopePerDay, double interceptWeight,
                          LocalDate firstEntryDate, LocalDate projectedGoalDate) {
        this.hasData = hasData;
        this.currentWeight = currentWeight;
        this.totalChange = totalChange;
        this.movingAverage7 = movingAverage7;
        this.minWeight = minWeight;
        this.maxWeight = maxWeight;
        this.slopePerDay = slopePerDay;
        this.interceptWeight = interceptWeight;
        this.firstEntryDate = firstEntryDate;
        this.projectedGoalDate = projectedGoalDate;
    }

    static TrendSummary empty() {
        return new TrendSummary(false, 0, 0, 0, 0, 0, 0, 0, null, null);
    }

    static TrendSummary of(double currentWeight, double totalChange, double movingAverage7, double minWeight,
                           double maxWeight, double slopePerDay, double interceptWeight, LocalDate firstEntryDate,
                           LocalDate projectedGoalDate) {
        return new TrendSummary(true, currentWeight, totalChange, movingAverage7, minWeight, maxWeight,
                slopePerDay, interceptWeight, firstEntryDate, projectedGoalDate);
    }

    public boolean hasData() {
        return hasData;
    }

    public double getCurrentWeight() {
        return currentWeight;
    }

    public double getTotalChange() {
        return totalChange;
    }

    public double getMovingAverage7() {
        return movingAverage7;
    }

    public double getMinWeight() {
        return minWeight;
    }

    public double getMaxWeight() {
        return maxWeight;
    }

    public double getSlopePerDay() {
        return slopePerDay;
    }

    /** Non-null only when the trend is moving toward the goal at a non-zero rate. */
    public LocalDate getProjectedGoalDate() {
        return projectedGoalDate;
    }

    /** True once at least one valid entry has been analyzed, so a fitted line can be drawn. */
    public boolean hasTrendLine() {
        return hasData;
    }

    /** Evaluates the fitted regression line at an arbitrary date, for rendering the trend line. */
    public double trendWeightOn(LocalDate date) {
        long day = ChronoUnit.DAYS.between(firstEntryDate, date);
        return slopePerDay * day + interceptWeight;
    }
}
