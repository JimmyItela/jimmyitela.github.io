package com.example.weighttracker.data.room;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

/** Compile-time verified Room queries against the weights table. */
@Dao
public interface RoomWeightDao {

    @Insert
    long insert(WeightEntity entry);

    @Query("UPDATE weights SET weight = :weight, entry_date = :entryDate WHERE id = :entryId")
    int update(int entryId, double weight, String entryDate);

    @Query("DELETE FROM weights WHERE id = :entryId")
    int delete(int entryId);

    @Query("SELECT * FROM weights WHERE user_id = :userId ORDER BY entry_date DESC, id DESC")
    List<WeightEntity> findByUser(int userId);

    @Query("SELECT * FROM weights WHERE id = :entryId")
    WeightEntity findById(int entryId);

    /**
     * Weekly average weight, aggregated directly in SQL rather than in the trend engine, so a
     * caller that only needs a coarser view of the history doesn't have to load every row.
     */
    @Query("SELECT strftime('%Y-%W', entry_date) AS week, AVG(weight) AS averageWeight "
            + "FROM weights WHERE user_id = :userId GROUP BY week ORDER BY week")
    List<WeeklyAverage> getWeeklyAverages(int userId);
}
