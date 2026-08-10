package com.example.weighttracker.data.room;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/** Room's on-disk representation of a user row. Matches the table the original SQLiteOpenHelper created. */
@Entity(tableName = "users", indices = {@Index(value = "username", unique = true)})
public class UserEntity {

    @PrimaryKey(autoGenerate = true)
    public final int id;

    @NonNull
    public final String username;

    @NonNull
    public final String salt;

    @ColumnInfo(name = "password_hash")
    @NonNull
    public final String passwordHash;

    public UserEntity(int id, @NonNull String username, @NonNull String salt, @NonNull String passwordHash) {
        this.id = id;
        this.username = username;
        this.salt = salt;
        this.passwordHash = passwordHash;
    }
}
