package com.example.addmission_update_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Database_University_info {
    private Context context;
    private DatabaseHelper dbHelper;

    public Database_University_info(Context context) {
        this.context = context;
        this.dbHelper = new DatabaseHelper(context);

        // Initialize database by getting a writable instance
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.close(); // Close immediately just to ensure creation
    }

    public void fetchAndStoreUniversityData() {
        String university_infoUrl = context.getString(R.string.domain_name) + "/app_projects/get_university_info_data.php";

        RequestQueue queue = Volley.newRequestQueue(context);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                university_infoUrl,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        SQLiteDatabase db = null;
                        try {
                            db = dbHelper.getWritableDatabase();

                            // Verify table exists or create it
                            if (!isTableExists(db, "stored_university_info_data")) {
                                dbHelper.onCreate(db);
                                Log.d("Database", "Created university info table");
                            }

                            // Begin transaction for batch operations
                            db.beginTransaction();
                            try {
                                // Clear existing data
                                db.delete("stored_university_info_data", null, null);

                                // Insert new data
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject university = response.getJSONObject(i);

                                    ContentValues values = new ContentValues();
                                    values.put("unique_id", university.optString("unique_id", ""));
                                    values.put("university_name", university.optString("university_name", ""));
                                    values.put("university_types", university.optString("university_types", ""));
                                    values.put("history", university.optString("history", ""));
                                    values.put("units_name_and_details", university.optString("units_name_and_details", ""));
                                    values.put("faculties_details", university.optString("faculties_details", ""));
                                    values.put("subjects", university.optString("subjects", ""));
                                    values.put("seat_numbers", university.optString("seat_numbers", ""));
                                    values.put("admission_info", university.optString("admission_info", ""));
                                    values.put("other_info", university.optString("other_info", ""));
                                    values.put("website_url", university.optString("website_url", ""));
                                    values.put("contact_info", university.optString("contact_info", ""));
                                    values.put("social_link", university.optString("social_link", ""));
                                    values.put("university_representative_info", university.optString("university_representative_info", ""));
                                    values.put("join_our_community", university.optString("join_our_community", ""));

                                    db.insert("stored_university_info_data", null, values);
                                }

                                db.setTransactionSuccessful();
                                Toast.makeText(context, "University data updated", Toast.LENGTH_SHORT).show();
                                Log.d("Database", "Successfully stored university data");
                            } finally {
                                db.endTransaction();
                            }
                        } catch (JSONException e) {
                            Log.e("JSON Error", "Failed to parse university data", e);
                            Toast.makeText(context, "Failed to process university data", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.e("Database Error", "Failed to store university data", e);
                            Toast.makeText(context, "Failed to save university data", Toast.LENGTH_SHORT).show();
                        } finally {
                            if (db != null) {
                                db.close();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Network Error", "Failed to fetch university data: " + error.getMessage());
                        Toast.makeText(context, "Failed to fetch university data", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        queue.add(jsonArrayRequest);
    }

    private boolean isTableExists(SQLiteDatabase db, String tableName) {
        if (db == null || !db.isOpen()) {
            return false;
        }

        try {
            db.execSQL("SELECT 1 FROM " + tableName + " LIMIT 1");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static class DatabaseHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "local_stored_data.db";
        private static final int DATABASE_VERSION = 3;

        private static final String CREATE_UNIVERSITY_TABLE =
                "CREATE TABLE IF NOT EXISTS stored_university_info_data (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "unique_id TEXT, " +
                        "university_name TEXT, " +
                        "university_types TEXT, " +
                        "history TEXT, " +
                        "units_name_and_details TEXT, " +
                        "faculties_details TEXT, " +
                        "subjects TEXT, " +
                        "seat_numbers TEXT, " +
                        "admission_info TEXT, " +
                        "other_info TEXT, " +
                        "website_url TEXT, " +
                        "contact_info TEXT, " +
                        "social_link TEXT, " +
                        "university_representative_info TEXT, " +
                        "join_our_community TEXT)";

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(CREATE_UNIVERSITY_TABLE);
                Log.d("Database", "Created university info table");
            } catch (Exception e) {
                Log.e("Database", "Failed to create table", e);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                db.execSQL("DROP TABLE IF EXISTS stored_university_info_data");
                onCreate(db);
                Log.d("Database", "Upgraded database from version " + oldVersion + " to " + newVersion);
            } catch (Exception e) {
                Log.e("Database", "Failed to upgrade database", e);
            }
        }
    }
}