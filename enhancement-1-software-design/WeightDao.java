package com.example.weighttracker.data;

import java.util.List;

import com.example.weighttracker.data.model.WeightEntry;

public interface WeightDao {

    long insertWeight(int userId, double weight, String entryDate);

    int updateWeight(int entryId, double weight, String entryDate);

    int deleteWeight(int entryId);

    List<WeightEntry> getWeightsForUser(int userId);

    /** Returns the matching entry, or {@code null} if no entry has that id. */
    WeightEntry getWeightById(int entryId);
}
