package com.example.weighttracker.data.room;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
* Room entity representing a weight record. Includes a composite index on
* {@code (user_id, entry_date)} to optimize weight history queries.
*/
@Entity(
        tableName = "weights",
        indices = {@Index(value = {"user_id", "entry_date"})},
        foreignKeys = {@ForeignKey(entity = UserEntity.class, parentColumns = "id", childColumns = "user_id")}
)
public class WeightEntity {

    @PrimaryKey(autoGenerate = true)
    public final int id;

    @ColumnInfo(name = "user_id")
    public final int userId;

    public final double weight;

    @ColumnInfo(name = "entry_date")
    @NonNull
    public final String entryDate;

    public WeightEntity(int id, int userId, double weight, @NonNull String entryDate) {
        this.id = id;
        this.userId = userId;
        this.weight = weight;
        this.entryDate = entryDate;
    }
}
