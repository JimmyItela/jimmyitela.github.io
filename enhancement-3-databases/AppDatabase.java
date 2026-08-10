package com.example.weighttracker.data.room;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Replaces the hand-written {@code SQLiteOpenHelper} (formerly {@code DatabaseHelper}) whose
 * {@code onUpgrade} dropped and recreated every table on any schema change - the most serious
 * defect found in the Milestone One code review. {@link #MIGRATION_2_3} is the non-destructive
 * fix: every row is copied forward into the new schema before anything old is dropped.
 *
 * <p>The database file name and version 2 schema exactly match what the old
 * {@code SQLiteOpenHelper} produced (including the already-hashed passwords from the Category
 * One enhancement), so a device upgrading from that version opens the same file Room now owns,
 * rather than starting from an empty database.
 */
@Database(entities = {UserEntity.class, WeightEntity.class, GoalEntity.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "weight_tracker.db";
    private static volatile AppDatabase instance;

    public abstract RoomUserDao userDao();

    public abstract RoomWeightDao weightDao();

    public abstract RoomGoalDao goalDao();

    public static AppDatabase getInstance(Context context) {
        AppDatabase result = instance;
        if (result == null) {
            synchronized (AppDatabase.class) {
                result = instance;
                if (result == null) {
                    result = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME)
                            .addMigrations(MIGRATION_2_3)
                            .build();
                    instance = result;
                }
            }
        }
        return result;
    }

    /**
     * The original hand-written {@code CREATE TABLE} statements declared each {@code id} column
     * as {@code INTEGER PRIMARY KEY AUTOINCREMENT} without an explicit {@code NOT NULL}, and
     * relied on inline {@code UNIQUE} column constraints, which SQLite backs with an unnamed
     * {@code sqlite_autoindex_*} rather than a named index. Room's schema validator requires an
     * explicit {@code NOT NULL} on primary keys and explicitly named indices for every
     * {@code @Index}, and SQLite's {@code ALTER TABLE} cannot change either of those on an
     * existing column. So this migration rebuilds each table with the exact target schema -
     * create the new table, copy every row across, drop the old table, rename the new one - and
     * adds the composite index the dashboard's dominant query (weights by user, ordered by date)
     * needs. Every row survives; nothing is ever dropped without first being copied forward.
     */
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL("CREATE TABLE users_new ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                    + "username TEXT NOT NULL, "
                    + "salt TEXT NOT NULL, "
                    + "password_hash TEXT NOT NULL)");
            db.execSQL("INSERT INTO users_new (id, username, salt, password_hash) "
                    + "SELECT id, username, salt, password_hash FROM users");
            db.execSQL("DROP TABLE users");
            db.execSQL("ALTER TABLE users_new RENAME TO users");
            db.execSQL("CREATE UNIQUE INDEX index_users_username ON users(username)");

            db.execSQL("CREATE TABLE weights_new ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                    + "user_id INTEGER NOT NULL, "
                    + "weight REAL NOT NULL, "
                    + "entry_date TEXT NOT NULL, "
                    + "FOREIGN KEY(user_id) REFERENCES users(id))");
            db.execSQL("INSERT INTO weights_new (id, user_id, weight, entry_date) "
                    + "SELECT id, user_id, weight, entry_date FROM weights");
            db.execSQL("DROP TABLE weights");
            db.execSQL("ALTER TABLE weights_new RENAME TO weights");
            db.execSQL("CREATE INDEX index_weights_user_id_entry_date ON weights(user_id, entry_date)");

            db.execSQL("CREATE TABLE goals_new ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                    + "user_id INTEGER NOT NULL, "
                    + "goal_weight REAL NOT NULL, "
                    + "phone_number TEXT, "
                    + "sms_enabled INTEGER NOT NULL DEFAULT 0, "
                    + "FOREIGN KEY(user_id) REFERENCES users(id))");
            db.execSQL("INSERT INTO goals_new (id, user_id, goal_weight, phone_number, sms_enabled) "
                    + "SELECT id, user_id, goal_weight, phone_number, sms_enabled FROM goals");
            db.execSQL("DROP TABLE goals");
            db.execSQL("ALTER TABLE goals_new RENAME TO goals");
            db.execSQL("CREATE UNIQUE INDEX index_goals_user_id ON goals(user_id)");
        }
    };
}
