package com.example.weighttracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "weight_tracker.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_USERS = "users";
    public static final String TABLE_WEIGHTS = "weights";
    public static final String TABLE_GOALS = "goals";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL)");

        db.execSQL("CREATE TABLE " + TABLE_WEIGHTS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "weight REAL NOT NULL, " +
                "entry_date TEXT NOT NULL, " +
                "FOREIGN KEY(user_id) REFERENCES users(id))");

        db.execSQL("CREATE TABLE " + TABLE_GOALS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER UNIQUE NOT NULL, " +
                "goal_weight REAL NOT NULL, " +
                "phone_number TEXT, " +
                "sms_enabled INTEGER DEFAULT 0, " +
                "FOREIGN KEY(user_id) REFERENCES users(id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GOALS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WEIGHTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public boolean createUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public boolean validateUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT id FROM " + TABLE_USERS + " WHERE username = ? AND password = ?",
                new String[]{username, password}
        );
        boolean isValid = cursor.moveToFirst();
        cursor.close();
        return isValid;
    }

    public boolean userExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT id FROM " + TABLE_USERS + " WHERE username = ?",
                new String[]{username}
        );
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public int getUserId(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT id FROM " + TABLE_USERS + " WHERE username = ?",
                new String[]{username}
        );

        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(0);
        }
        cursor.close();
        return userId;
    }

    public long insertWeight(int userId, double weight, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("weight", weight);
        values.put("entry_date", date);
        return db.insert(TABLE_WEIGHTS, null, values);
    }

    public int updateWeight(int entryId, double weight, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("weight", weight);
        values.put("entry_date", date);
        return db.update(TABLE_WEIGHTS, values, "id = ?", new String[]{String.valueOf(entryId)});
    }

    public int deleteWeight(int entryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_WEIGHTS, "id = ?", new String[]{String.valueOf(entryId)});
    }

    public Cursor getWeightsForUser(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT id, entry_date, weight FROM " + TABLE_WEIGHTS + " WHERE user_id = ? ORDER BY entry_date DESC, id DESC",
                new String[]{String.valueOf(userId)}
        );
    }

    public Cursor getWeightById(int entryId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT id, entry_date, weight FROM " + TABLE_WEIGHTS + " WHERE id = ?",
                new String[]{String.valueOf(entryId)}
        );
    }

    public long saveGoal(int userId, double goalWeight, String phoneNumber, boolean smsEnabled) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", userId);
        values.put("goal_weight", goalWeight);
        values.put("phone_number", phoneNumber);
        values.put("sms_enabled", smsEnabled ? 1 : 0);

        Cursor cursor = db.rawQuery(
                "SELECT id FROM " + TABLE_GOALS + " WHERE user_id = ?",
                new String[]{String.valueOf(userId)}
        );
        boolean exists = cursor.moveToFirst();
        cursor.close();

        if (exists) {
            return db.update(TABLE_GOALS, values, "user_id = ?", new String[]{String.valueOf(userId)});
        }
        return db.insert(TABLE_GOALS, null, values);
    }

    public Cursor getGoalForUser(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT goal_weight, phone_number, sms_enabled FROM " + TABLE_GOALS + " WHERE user_id = ?",
                new String[]{String.valueOf(userId)}
        );
    }
}
