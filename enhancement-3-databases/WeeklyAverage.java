package com.example.weighttracker.data.room;

/** Result row for {@link RoomWeightDao#getWeeklyAverages}: an ISO year-week label and its average weight. */
public class WeeklyAverage {

    public final String week;
    public final double averageWeight;

    public WeeklyAverage(String week, double averageWeight) {
        this.week = week;
        this.averageWeight = averageWeight;
    }
}
