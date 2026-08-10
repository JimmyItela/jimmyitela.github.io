package com.example.weighttracker.data.room;

/** Weekly average weight returned by {@link RoomWeightDao#getWeeklyAverages}. */
public class WeeklyAverage {

    public final String week;
    public final double averageWeight;

    public WeeklyAverage(String week, double averageWeight) {
        this.week = week;
        this.averageWeight = averageWeight;
    }
}
