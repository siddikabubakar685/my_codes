package com.example.addmission_update_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Database_Countdown_and_Notice extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "local_stored_data.db"; // Same database name
    private static final int DATABASE_VERSION = 3; // Same version as Database_Favourite_Unique_Id

    // Table names
    private static final String TABLE_APPLY_DATA = "stored_countdown_apply_data";
    private static final String TABLE_EXAM_DATA = "stored_countdown_exam_data";
    private static final String TABLE_RESULT_DATA = "stored_countdown_result_data";
    private static final String TABLE_NOTIFICATION_DATA = "stored_notification_data";

    // Column names
    private static final String COLUMN_UNIQUE_ID = "unique_id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_TARGET_DATE = "target_date_to_count";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_SMALL_IMAGE_URL = "small_image_url";
    private static final String COLUMN_BIG_IMAGE_URL = "big_image_url";

    private Context context;

    public Database_Countdown_and_Notice(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tables are already created by Database_Favourite_Unique_Id, so no need to create them here
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades
        if (oldVersion < 3) {
            // If upgrading from version 2 to 3, drop and recreate tables
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPLY_DATA);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXAM_DATA);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESULT_DATA);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATION_DATA);
            onCreate(db);
        }
    }

    public void fetchDataAndStoreLocally(OnDataFetchCompleteListener listener) {
        // Retrieve URLs from strings.xml
        String applyUrl = context.getString(R.string.domain_name) + "/app_projects/get_countdown_apply_data.php";
        String examUrl = context.getString(R.string.domain_name) + "/app_projects/get_countdown_exam_data.php";
        String resultUrl = context.getString(R.string.domain_name) + "/app_projects/get_countdown_result_data.php";
        String notificationUrl = context.getString(R.string.domain_name) + "/app_projects/get_notification_data.php";

        // Fetch data from the first URL (Apply Data)
        fetchDataFromUrl(applyUrl, TABLE_APPLY_DATA, new OnDataFetchCompleteListener() {
            @Override
            public void onDataFetchComplete() {
                // Fetch data from the second URL (Exam Data)
                fetchDataFromUrl(examUrl, TABLE_EXAM_DATA, new OnDataFetchCompleteListener() {
                    @Override
                    public void onDataFetchComplete() {
                        // Fetch data from the third URL (Result Data)
                        fetchDataFromUrl(resultUrl, TABLE_RESULT_DATA, new OnDataFetchCompleteListener() {
                            @Override
                            public void onDataFetchComplete() {
                                // Fetch data from the fourth URL (Notification Data)
                                fetchNotificationDataFromUrl(notificationUrl, TABLE_NOTIFICATION_DATA, new OnDataFetchCompleteListener() {
                                    @Override
                                    public void onDataFetchComplete() {
                                        // Notify the listener that all data is fetched and stored
                                        listener.onDataFetchComplete();
                                    }

                                    @Override
                                    public void onDataFetchError(String errorMessage) {
                                        // Notify the listener of the error
                                        listener.onDataFetchError("Error storing notification data: " + errorMessage);
                                    }
                                });
                            }

                            @Override
                            public void onDataFetchError(String errorMessage) {
                                // Notify the listener of the error
                                listener.onDataFetchError("Error storing result data: " + errorMessage);
                            }
                        });
                    }

                    @Override
                    public void onDataFetchError(String errorMessage) {
                        // Notify the listener of the error
                        listener.onDataFetchError("Error storing exam data: " + errorMessage);
                    }
                });
            }

            @Override
            public void onDataFetchError(String errorMessage) {
                // Notify the listener of the error
                listener.onDataFetchError("Error storing apply data: " + errorMessage);
            }
        });
    }

    private void fetchDataFromUrl(String url, String tableName, OnDataFetchCompleteListener listener) {
        // Initialize Volley RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        // Create a JsonArrayRequest to fetch data from the URL
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            SQLiteDatabase db = getWritableDatabase();

                            // Clear the existing data in the table
                            db.execSQL("DELETE FROM " + tableName);

                            // Iterate through the JSON array and insert data into the table
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String uniqueId = jsonObject.getString("unique_id");
                                String title = jsonObject.getString("title");
                                String targetDate = jsonObject.getString("target_date_to_count");

                                // Insert data into the table
                                ContentValues values = new ContentValues();
                                values.put(COLUMN_UNIQUE_ID, uniqueId);
                                values.put(COLUMN_TITLE, title);
                                values.put(COLUMN_TARGET_DATE, targetDate);
                                db.insert(tableName, null, values);
                            }

                            // Close the database
                            db.close();

                            // Notify listener that data fetch and store is complete
                            listener.onDataFetchComplete();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            listener.onDataFetchError("Error parsing JSON data");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onDataFetchError("Error fetching data: " + error.getMessage());
                    }
                }
        );

        // Add the request to the RequestQueue
        requestQueue.add(jsonArrayRequest);
    }

    private void fetchNotificationDataFromUrl(String url, String tableName, OnDataFetchCompleteListener listener) {
        // Initialize Volley RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        // Create a JsonArrayRequest to fetch data from the URL
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            SQLiteDatabase db = getWritableDatabase();

                            // Clear the existing data in the table
                            db.execSQL("DELETE FROM " + tableName);

                            // Iterate through the JSON array and insert data into the table
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String uniqueId = jsonObject.getString("unique_id");
                                String date = jsonObject.getString("date");
                                String title = jsonObject.getString("title");
                                String description = jsonObject.getString("description");
                                String smallImageUrl = jsonObject.getString("small_image_url");
                                String bigImageUrl = jsonObject.getString("big_image_url");

                                // Insert data into the table
                                ContentValues values = new ContentValues();
                                values.put(COLUMN_UNIQUE_ID, uniqueId);
                                values.put(COLUMN_DATE, date);
                                values.put(COLUMN_TITLE, title);
                                values.put(COLUMN_DESCRIPTION, description);
                                values.put(COLUMN_SMALL_IMAGE_URL, smallImageUrl);
                                values.put(COLUMN_BIG_IMAGE_URL, bigImageUrl);
                                db.insert(tableName, null, values);
                            }

                            // Close the database
                            db.close();

                            // Notify listener that data fetch and store is complete
                            listener.onDataFetchComplete();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            listener.onDataFetchError("Error parsing JSON data");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onDataFetchError("Error fetching data: " + error.getMessage());
                    }
                }
        );

        // Add the request to the RequestQueue
        requestQueue.add(jsonArrayRequest);
    }

    // Callback interface to notify when data fetching is complete
    public interface OnDataFetchCompleteListener {
        void onDataFetchComplete(); // Called when data fetching is complete
        void onDataFetchError(String errorMessage); // Called if there's an error
    }
}