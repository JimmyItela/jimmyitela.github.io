package com.example.weighttracker.data.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

/** Room DAO for user table operations. */
@Dao
public interface RoomUserDao {

    @Insert
    long insert(UserEntity user);

    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE username = :username)")
    boolean usernameExists(String username);

    @Query("SELECT * FROM users WHERE username = :username")
    UserEntity findByUsername(String username);
}
