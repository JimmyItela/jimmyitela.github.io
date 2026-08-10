package com.example.weighttracker.data.room;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

//** Room entity representing a goal record from the original database schema. */
@Entity(
        tableName = "goals",
        indices = {@Index(value = "user_id", unique = true)},
        foreignKeys = {@ForeignKey(entity = UserEntity.class, parentColumns = "id", childColumns = "user_id")}
)
public class GoalEntity {

    @PrimaryKey(autoGenerate = true)
    public final int id;

    @ColumnInfo(name = "user_id")
    public final int userId;

    @ColumnInfo(name = "goal_weight")
    public final double goalWeight;

    @ColumnInfo(name = "phone_number")
    @Nullable
    public final String phoneNumber;

    @ColumnInfo(name = "sms_enabled")
    public final boolean smsEnabled;

    public GoalEntity(int id, int userId, double goalWeight, @Nullable String phoneNumber, boolean smsEnabled) {
        this.id = id;
        this.userId = userId;
        this.goalWeight = goalWeight;
        this.phoneNumber = phoneNumber;
        this.smsEnabled = smsEnabled;
    }
}
