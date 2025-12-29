package com.example.addmission_update_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashSet;
import java.util.Set;

public class Database_Favourite_Unique_Id extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "local_stored_data.db";
    private static final int DATABASE_VERSION = 3;

    // Table names
    private static final String TABLE_ALL_UNIQUE_IDS = "all_unique_id_list";
    private static final String TABLE_USER_FAVORITES = "user_favorite_unique_id_list";
    private static final String TABLE_APPLY_DATA = "stored_countdown_apply_data";
    private static final String TABLE_EXAM_DATA = "stored_countdown_exam_data";
    private static final String TABLE_RESULT_DATA = "stored_countdown_result_data";
    private static final String TABLE_NOTIFICATION_DATA = "stored_notification_data";

    // Column names
    public static final String COLUMN_UNIQUE_ID = "unique_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_TARGET_DATE = "target_date_to_count";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_SMALL_IMAGE_URL = "small_image_url";
    public static final String COLUMN_BIG_IMAGE_URL = "big_image_url";

    // Create table queries
    private static final String CREATE_TABLE_ALL_UNIQUE_IDS =
            "CREATE TABLE " + TABLE_ALL_UNIQUE_IDS + "("
                    + COLUMN_UNIQUE_ID + " TEXT PRIMARY KEY"
                    + ")";

    private static final String CREATE_TABLE_USER_FAVORITES =
            "CREATE TABLE " + TABLE_USER_FAVORITES + "("
                    + COLUMN_UNIQUE_ID + " TEXT PRIMARY KEY"
                    + ")";

    private static final String CREATE_TABLE_APPLY_DATA =
            "CREATE TABLE " + TABLE_APPLY_DATA + "("
                    + COLUMN_UNIQUE_ID + " TEXT PRIMARY KEY, "
                    + COLUMN_TITLE + " TEXT, "
                    + COLUMN_TARGET_DATE + " TEXT"
                    + ")";

    private static final String CREATE_TABLE_EXAM_DATA =
            "CREATE TABLE " + TABLE_EXAM_DATA + "("
                    + COLUMN_UNIQUE_ID + " TEXT PRIMARY KEY, "
                    + COLUMN_TITLE + " TEXT, "
                    + COLUMN_TARGET_DATE + " TEXT"
                    + ")";

    private static final String CREATE_TABLE_RESULT_DATA =
            "CREATE TABLE " + TABLE_RESULT_DATA + "("
                    + COLUMN_UNIQUE_ID + " TEXT PRIMARY KEY, "
                    + COLUMN_TITLE + " TEXT, "
                    + COLUMN_TARGET_DATE + " TEXT"
                    + ")";

    private static final String CREATE_TABLE_NOTIFICATION_DATA =
            "CREATE TABLE " + TABLE_NOTIFICATION_DATA + "("
                    + COLUMN_UNIQUE_ID + " TEXT PRIMARY KEY, "
                    + COLUMN_DATE + " TEXT, "
                    + COLUMN_TITLE + " TEXT, "
                    + COLUMN_DESCRIPTION + " TEXT, "
                    + COLUMN_SMALL_IMAGE_URL + " TEXT, "
                    + COLUMN_BIG_IMAGE_URL + " TEXT"
                    + ")";

    public Database_Favourite_Unique_Id(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create all tables
        db.execSQL(CREATE_TABLE_ALL_UNIQUE_IDS);
        db.execSQL(CREATE_TABLE_USER_FAVORITES);
        db.execSQL(CREATE_TABLE_APPLY_DATA);
        db.execSQL(CREATE_TABLE_EXAM_DATA);
        db.execSQL(CREATE_TABLE_RESULT_DATA);
        db.execSQL(CREATE_TABLE_NOTIFICATION_DATA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if they exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALL_UNIQUE_IDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_FAVORITES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPLY_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXAM_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESULT_DATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATION_DATA);
        onCreate(db);
    }

    // Method to insert unique IDs into the all_unique_id_list table
    public void insertUniqueIds(Set<String> uniqueIds) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (String uniqueId : uniqueIds) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_UNIQUE_ID, uniqueId);
            db.insertWithOnConflict(TABLE_ALL_UNIQUE_IDS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        }
        db.close();
    }

    // Method to check if the all_unique_id_list table is empty
    public boolean isAllUniqueIdsTableEmpty() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ALL_UNIQUE_IDS, null, null, null, null, null, null);
        boolean isEmpty = cursor.getCount() == 0;
        cursor.close();
        db.close();
        return isEmpty;
    }

    // Method to get all favorite unique IDs from the user_favorite_unique_id_list table
    public Set<String> getFavoriteUniqueIds() {
        Set<String> favoriteIds = new HashSet<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER_FAVORITES, new String[]{COLUMN_UNIQUE_ID}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                favoriteIds.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return favoriteIds;
    }

    // Method to add a favorite unique ID to the user_favorite_unique_id_list table
    public void addFavoriteUniqueId(String uniqueId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_UNIQUE_ID, uniqueId);
        db.insertWithOnConflict(TABLE_USER_FAVORITES, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    // Method to remove a favorite unique ID from the user_favorite_unique_id_list table
    public void removeFavoriteUniqueId(String uniqueId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER_FAVORITES, COLUMN_UNIQUE_ID + " = ?", new String[]{uniqueId});
        db.close();
    }

    // Method to get all unique IDs from the all_unique_id_list table
    public Set<String> getAllUniqueIds() {
        Set<String> allUniqueIds = new HashSet<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ALL_UNIQUE_IDS, new String[]{COLUMN_UNIQUE_ID}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                allUniqueIds.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return allUniqueIds;
    }

    // Method to fetch filtered data from the apply_data table based on favorite unique IDs
    public Cursor getFilteredApplyData(Set<String> favoriteIds) {
        SQLiteDatabase db = this.getReadableDatabase();
        if (favoriteIds.isEmpty()) {
            // If there are no favorite IDs, return an empty cursor
            return db.query(TABLE_APPLY_DATA, null, "1=0", null, null, null, null);
        }
        String selection = COLUMN_UNIQUE_ID + " IN (" + makePlaceholders(favoriteIds.size()) + ")";
        String[] selectionArgs = favoriteIds.toArray(new String[0]);
        return db.query(TABLE_APPLY_DATA, null, selection, selectionArgs, null, null, null);
    }

    // Helper method to create placeholders for SQL query
    private String makePlaceholders(int len) {
        if (len <= 0) {
            return ""; // Return an empty string if there are no placeholders
        }
        StringBuilder sb = new StringBuilder(len * 2 - 1);
        sb.append("?");
        for (int i = 1; i < len; i++) {
            sb.append(",?");
        }
        return sb.toString();
    }

    // Method to insert data into the apply_data table
    public void insertApplyData(String uniqueId, String title, String targetDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_UNIQUE_ID, uniqueId);
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_TARGET_DATE, targetDate);
        db.insertWithOnConflict(TABLE_APPLY_DATA, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

}