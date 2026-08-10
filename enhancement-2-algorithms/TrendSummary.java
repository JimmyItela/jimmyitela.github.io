package com.example.weighttracker.trend;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** Immutable weight-trend analysis results. */
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

    /** Present only when the trend is moving toward the goal. */
    public LocalDate getProjectedGoalDate() {
        return projectedGoalDate;
    }

    /** True when enough data exists to draw a trend line. */
    public boolean hasTrendLine() {
        return hasData;
    }

    /** Evaluates the regression line for a given date. */
    public double trendWeightOn(LocalDate date) {
        long day = ChronoUnit.DAYS.between(firstEntryDate, date);
        return slopePerDay * day + interceptWeight;
    }
}
