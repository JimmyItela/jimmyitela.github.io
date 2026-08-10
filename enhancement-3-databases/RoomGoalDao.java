package com.example.weighttracker.data.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

/** Room DAO providing compile-time verified goal queries. */
@Dao
public interface RoomGoalDao {

    @Query("SELECT * FROM goals WHERE user_id = :userId")
    GoalEntity findByUserId(int userId);

    @Insert
    long insert(GoalEntity goal);

    @Query("UPDATE goals SET goal_weight = :goalWeight, phone_number = :phoneNumber, sms_enabled = :smsEnabled WHERE user_id = :userId")
    int updateByUserId(int userId, double goalWeight, String phoneNumber, boolean smsEnabled);
}
