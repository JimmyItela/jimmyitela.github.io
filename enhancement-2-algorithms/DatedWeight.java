package com.example.weighttracker.trend;

import java.time.LocalDate;

/** A weight reading paired with a parsed, comparable date - the unit {@link WeightTrendAnalyzer} sorts and analyzes. */
public class DatedWeight {

    private final LocalDate date;
    private final double weight;

    public DatedWeight(LocalDate date, double weight) {
        this.date = date;
        this.weight = weight;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getWeight() {
        return weight;
    }
}
