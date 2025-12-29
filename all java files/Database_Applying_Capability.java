package com.example.addmission_update_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
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
import java.util.List;

public class Database_Applying_Capability extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "local_stored_data.db";
    private static final int DATABASE_VERSION = 3;
    private static final String TABLE_NAME = "applying_capability_data";

    // Column names
    private static final String KEY_UNIQUE_ID = "unique_id";
    private static final String KEY_UNIVERSITY_NAME = "university_name";
    private static final String KEY_SSC_GPA = "ssc_gpa";
    private static final String KEY_HSC_GPA = "hsc_gpa";
    private static final String KEY_SSC_YEAR = "ssc_exam_year";
    private static final String KEY_HSC_YEAR = "hsc_exam_year";
    private static final String KEY_SECOND_CHANCE = "second_time_opportunity";

    private Context context;
    private RequestQueue requestQueue;

    public Database_Applying_Capability(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                    + KEY_UNIQUE_ID + " TEXT PRIMARY KEY,"
                    + KEY_UNIVERSITY_NAME + " TEXT,"
                    + KEY_SSC_GPA + " REAL,"
                    + KEY_HSC_GPA + " REAL,"
                    + KEY_SSC_YEAR + " INTEGER,"
                    + KEY_HSC_YEAR + " INTEGER,"
                    + KEY_SECOND_CHANCE + " TEXT" + ")";
            db.execSQL(CREATE_TABLE);
            Log.d("Database", "Table created successfully");
        } catch (SQLiteException e) {
            Log.e("Database", "Error creating table", e);
            showToast("Database creation error");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
            Log.d("Database", "Database upgraded");
        } catch (SQLiteException e) {
            Log.e("Database", "Error upgrading database", e);
            showToast("Database upgrade error");
        }
    }

    public void fetchAndStoreData() {
        String url = context.getString(R.string.domain_name) + "/app_projects/get_applying_capability_data.php";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        SQLiteDatabase db = getWritableDatabase();
                        try {
                            // Ensure table exists
                            onCreate(db);

                            if (response.getString("status").equals("success")) {
                                JSONArray dataArray = response.getJSONArray("data");
                                List<ApplyingCapabilityModel> dataList = new ArrayList<>();

                                db.beginTransaction();
                                try {
                                    // Clear existing data
                                    db.delete(TABLE_NAME, null, null);

                                    // Insert new data
                                    for (int i = 0; i < dataArray.length(); i++) {
                                        JSONObject item = dataArray.getJSONObject(i);
                                        ApplyingCapabilityModel model = new ApplyingCapabilityModel(
                                                item.getString(KEY_UNIQUE_ID),
                                                item.getString(KEY_UNIVERSITY_NAME),
                                                item.getDouble(KEY_SSC_GPA),
                                                item.getDouble(KEY_HSC_GPA),
                                                item.getInt(KEY_SSC_YEAR),
                                                item.getInt(KEY_HSC_YEAR),
                                                item.getString(KEY_SECOND_CHANCE)
                                        );
                                        addData(db, model);
                                        dataList.add(model);
                                    }
                                    db.setTransactionSuccessful();
                                    showToast("Applying capability data updated");
                                } finally {
                                    db.endTransaction();
                                }
                            } else {
                                showToast("Server error: " + response.optString("message", "Unknown error"));
                            }
                        } catch (JSONException e) {
                            Log.e("JSON", "Error parsing response", e);
                            showToast("Data format error");
                        } catch (SQLiteException e) {
                            Log.e("Database", "Database operation failed", e);
                            showToast("Database operation failed");
                        } finally {
                            db.close();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMessage = "Network error";
                        if (error.networkResponse != null) {
                            errorMessage += " (HTTP " + error.networkResponse.statusCode + ")";
                        }
                        Log.e("Network", errorMessage, error);
                        showToast(errorMessage);
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    private void addData(SQLiteDatabase db, ApplyingCapabilityModel model) {
        ContentValues values = new ContentValues();
        values.put(KEY_UNIQUE_ID, model.getUniqueId());
        values.put(KEY_UNIVERSITY_NAME, model.getUniversityName());
        values.put(KEY_SSC_GPA, model.getSscGpa());
        values.put(KEY_HSC_GPA, model.getHscGpa());
        values.put(KEY_SSC_YEAR, model.getSscExamYear());
        values.put(KEY_HSC_YEAR, model.getHscExamYear());
        values.put(KEY_SECOND_CHANCE, model.getSecondTimeOpportunity());

        try {
            db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        } catch (SQLiteException e) {
            Log.e("Database", "Error inserting data", e);
            throw e;
        }
    }

    public List<ApplyingCapabilityModel> getAllData() {
        List<ApplyingCapabilityModel> dataList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;

        try {
            // Verify all columns exist
            String[] columns = {
                    KEY_UNIQUE_ID,
                    KEY_UNIVERSITY_NAME,
                    KEY_SSC_GPA,
                    KEY_HSC_GPA,
                    KEY_SSC_YEAR,
                    KEY_HSC_YEAR,
                    KEY_SECOND_CHANCE
            };

            cursor = db.query(TABLE_NAME, columns,
                    null, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    ApplyingCapabilityModel model = new ApplyingCapabilityModel(
                            cursor.getString(cursor.getColumnIndexOrThrow(KEY_UNIQUE_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(KEY_UNIVERSITY_NAME)),
                            cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_SSC_GPA)),
                            cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_HSC_GPA)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(KEY_SSC_YEAR)),
                            cursor.getInt(cursor.getColumnIndexOrThrow(KEY_HSC_YEAR)),
                            cursor.getString(cursor.getColumnIndexOrThrow(KEY_SECOND_CHANCE))
                    );
                    dataList.add(model);
                } while (cursor.moveToNext());
            }
        } catch (SQLiteException e) {
            Log.e("Database", "Error reading data", e);
            showToast("Error loading data");
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return dataList;
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    // Data model class
    public static class ApplyingCapabilityModel {
        private String uniqueId;
        private String universityName;
        private double sscGpa;
        private double hscGpa;
        private int sscExamYear;
        private int hscExamYear;
        private String secondTimeOpportunity;

        public ApplyingCapabilityModel(String uniqueId, String universityName, double sscGpa,
                                       double hscGpa, int sscExamYear, int hscExamYear,
                                       String secondTimeOpportunity) {
            this.uniqueId = uniqueId;
            this.universityName = universityName;
            this.sscGpa = sscGpa;
            this.hscGpa = hscGpa;
            this.sscExamYear = sscExamYear;
            this.hscExamYear = hscExamYear;
            this.secondTimeOpportunity = secondTimeOpportunity;
        }

        // Getters
        public String getUniqueId() { return uniqueId; }
        public String getUniversityName() { return universityName; }
        public double getSscGpa() { return sscGpa; }
        public double getHscGpa() { return hscGpa; }
        public int getSscExamYear() { return sscExamYear; }
        public int getHscExamYear() { return hscExamYear; }
        public String getSecondTimeOpportunity() { return secondTimeOpportunity; }
    }
}