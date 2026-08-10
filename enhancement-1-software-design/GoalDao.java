package com.example.weighttracker.data;

import com.example.weighttracker.data.model.Goal;


public interface GoalDao {

    long saveGoal(int userId, double goalWeight, String phoneNumber, boolean smsEnabled);

    /** Returns the user's goal, or {@code null} if they have not set one. */
    Goal getGoalForUser(int userId);
}
