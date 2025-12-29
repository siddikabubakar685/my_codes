package com.example.addmission_update_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database_admission_info extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "local_stored_data.db";
    private static final int DATABASE_VERSION = 3;
    private Context context;

    // Table names
    private static final String TABLE_ADMIT_CARD = "admit_card_data";
    private static final String TABLE_SEAT_PLAN = "seat_plan_data";
    private static final String TABLE_RESULT = "result_data";
    private static final String TABLE_WAITING_LIST = "waiting_list_data";
    private static final String TABLE_QUESTION_ANSWER = "question_answer_data";
    private static final String TABLE_CUT_MARK = "cut_mark_data";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_UNIQUE_ID = "unique_id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_ANNOUNCEMENT_DATE = "announcement_date";

    public Database_admission_info(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        // Force database creation
        SQLiteDatabase db = getWritableDatabase();
        createAllTables(db);
        db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createAllTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropAllTables(db);
        onCreate(db);
    }

    private void createAllTables(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_ADMIT_CARD + "("
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + KEY_UNIQUE_ID + " TEXT UNIQUE,"
                    + KEY_TITLE + " TEXT,"
                    + KEY_DESCRIPTION + " TEXT,"
                    + KEY_ANNOUNCEMENT_DATE + " TEXT)");

            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_SEAT_PLAN + "("
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + KEY_UNIQUE_ID + " TEXT UNIQUE,"
                    + KEY_TITLE + " TEXT,"
                    + KEY_DESCRIPTION + " TEXT,"
                    + KEY_ANNOUNCEMENT_DATE + " TEXT)");

            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_RESULT + "("
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + KEY_UNIQUE_ID + " TEXT UNIQUE,"
                    + KEY_TITLE + " TEXT,"
                    + KEY_DESCRIPTION + " TEXT,"
                    + KEY_ANNOUNCEMENT_DATE + " TEXT)");

            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_WAITING_LIST + "("
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + KEY_UNIQUE_ID + " TEXT UNIQUE,"
                    + KEY_TITLE + " TEXT,"
                    + KEY_DESCRIPTION + " TEXT,"
                    + KEY_ANNOUNCEMENT_DATE + " TEXT)");

            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_QUESTION_ANSWER + "("
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + KEY_UNIQUE_ID + " TEXT UNIQUE,"
                    + KEY_TITLE + " TEXT,"
                    + KEY_DESCRIPTION + " TEXT,"
                    + KEY_ANNOUNCEMENT_DATE + " TEXT)");

            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CUT_MARK + "("
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + KEY_UNIQUE_ID + " TEXT UNIQUE,"
                    + KEY_TITLE + " TEXT,"
                    + KEY_DESCRIPTION + " TEXT,"
                    + KEY_ANNOUNCEMENT_DATE + " TEXT)");

            Log.d("Database", "All tables created successfully");
        } catch (Exception e) {
            Log.e("Database", "Error creating tables", e);
            Toast.makeText(context, "Database error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void dropAllTables(SQLiteDatabase db) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADMIT_CARD);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEAT_PLAN);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESULT);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_WAITING_LIST);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTION_ANSWER);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUT_MARK);
        } catch (Exception e) {
            Log.e("Database", "Error dropping tables", e);
        }
    }

    public void fetchAndStoreData() {
        String admission_info_Url = context.getString(R.string.domain_name) + "/app_projects/get_admission_info_data.php";

        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                admission_info_Url,
                null,
                response -> {
                    try {
                        processResponse(response);
                        Toast.makeText(context, "admission info updated", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e("DataProcessing", "Error processing response", e);
                        Toast.makeText(context, "Error processing data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    String errorMsg = "Network error: " + (error.getMessage() != null ? error.getMessage() : "Unknown error");
                    if (error.networkResponse != null) {
                        errorMsg += " (Status: " + error.networkResponse.statusCode + ")";
                    }
                    Log.e("Network", errorMsg);
                    Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();
                }
        );

        queue.add(jsonObjectRequest);
    }

    private void processResponse(JSONObject response) throws JSONException {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.beginTransaction();

            processDataArray(db, response.optJSONArray("admit_card_data"), TABLE_ADMIT_CARD);
            processDataArray(db, response.optJSONArray("seat_plan_data"), TABLE_SEAT_PLAN);
            processDataArray(db, response.optJSONArray("result_data"), TABLE_RESULT);
            processDataArray(db, response.optJSONArray("waiting_list_data"), TABLE_WAITING_LIST);
            processDataArray(db, response.optJSONArray("question_answer_data"), TABLE_QUESTION_ANSWER);
            processDataArray(db, response.optJSONArray("cut_mark_data"), TABLE_CUT_MARK);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    private void processDataArray(SQLiteDatabase db, JSONArray jsonArray, String tableName) throws JSONException {
        if (jsonArray == null) {
            Log.w("DataProcessing", "Null array for table: " + tableName);
            return;
        }

        db.delete(tableName, null, null);

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject item = jsonArray.getJSONObject(i);
                ContentValues values = new ContentValues();

                values.put(KEY_UNIQUE_ID, item.optString("unique_id", ""));
                values.put(KEY_TITLE, item.optString("title", ""));
                values.put(KEY_DESCRIPTION, item.optString("description", ""));
                values.put(KEY_ANNOUNCEMENT_DATE, item.optString("announcement_date", ""));

                db.insertWithOnConflict(tableName, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            } catch (JSONException e) {
                Log.e("DataProcessing", "Error parsing item " + i + " in " + tableName, e);
            }
        }
    }

    // Data retrieval methods
    public List<Map<String, String>> getAllAdmitCardData() {
        return getAllDataFromTable(TABLE_ADMIT_CARD);
    }

    public List<Map<String, String>> getAllSeatPlanData() {
        return getAllDataFromTable(TABLE_SEAT_PLAN);
    }

    public List<Map<String, String>> getAllResultData() {
        return getAllDataFromTable(TABLE_RESULT);
    }

    public List<Map<String, String>> getAllWaitingListData() {
        return getAllDataFromTable(TABLE_WAITING_LIST);
    }

    public List<Map<String, String>> getAllQuestionAnswerData() {
        return getAllDataFromTable(TABLE_QUESTION_ANSWER);
    }

    public List<Map<String, String>> getAllCutMarkData() {
        return getAllDataFromTable(TABLE_CUT_MARK);
    }

    private List<Map<String, String>> getAllDataFromTable(String tableName) {
        List<Map<String, String>> dataList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(tableName,
                    new String[]{KEY_UNIQUE_ID, KEY_TITLE, KEY_DESCRIPTION, KEY_ANNOUNCEMENT_DATE},
                    null, null, null, null, KEY_ANNOUNCEMENT_DATE + " DESC");

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Map<String, String> data = new HashMap<>();
                    data.put("unique_id", cursor.getString(0));
                    data.put("title", cursor.getString(1));
                    data.put("description", cursor.getString(2));
                    data.put("announcement_date", cursor.getString(3));

                    dataList.add(data);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("Database", "Error reading from " + tableName, e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return dataList;
    }

    public Map<String, String> getItemByUniqueId(String tableName, String uniqueId) {
        SQLiteDatabase db = getReadableDatabase();
        Map<String, String> item = new HashMap<>();
        Cursor cursor = null;

        try {
            cursor = db.query(tableName,
                    new String[]{KEY_UNIQUE_ID, KEY_TITLE, KEY_DESCRIPTION, KEY_ANNOUNCEMENT_DATE},
                    KEY_UNIQUE_ID + "=?",
                    new String[]{uniqueId},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                item.put("unique_id", cursor.getString(0));
                item.put("title", cursor.getString(1));
                item.put("description", cursor.getString(2));
                item.put("announcement_date", cursor.getString(3));
            }
        } catch (Exception e) {
            Log.e("Database", "Error getting item from " + tableName, e);
        } finally {
            if (cursor != null) cursor.close();
            db.close();
        }
        return item;
    }
}