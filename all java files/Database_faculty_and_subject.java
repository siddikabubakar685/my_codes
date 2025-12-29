package com.example.addmission_update_project;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.ContentValues;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class Database_faculty_and_subject extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "local_stored_data.db";
    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_FACULTY = "faculty";
    private static final String TABLE_SUBJECT = "subject";

    private final Context context;

    public Database_faculty_and_subject(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

        // This ensures tables are created before any operation
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_FACULTY = "CREATE TABLE IF NOT EXISTS " + TABLE_FACULTY + " (" +
                "faculty_id TEXT PRIMARY KEY, " +
                "faculty_name TEXT);";

        String CREATE_TABLE_SUBJECT = "CREATE TABLE IF NOT EXISTS " + TABLE_SUBJECT + " (" +
                "subject_id TEXT PRIMARY KEY, " +
                "faculty_id TEXT, " +
                "subject_name TEXT, " +
                "subject_details TEXT, " +
                "subject_image_url TEXT, " +    // <-- Added this line
                "FOREIGN KEY(faculty_id) REFERENCES " + TABLE_FACULTY + "(faculty_id));";

        db.execSQL(CREATE_TABLE_FACULTY);
        db.execSQL(CREATE_TABLE_SUBJECT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBJECT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FACULTY);
        onCreate(db);
    }

    public void fetchAndStoreData() {
        String faculty_and_subject_data_url = context.getString(R.string.domain_name) + "/app_projects/get_faculty_and_subject_data.php";

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, faculty_and_subject_data_url,
                response -> {
                    try {
                        JSONObject rootObject = new JSONObject(response);
                        String status = rootObject.getString("status");

                        if (!status.equals("success")) {
                            Toast.makeText(context, "Data fetch failed: Status not success", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONArray facultyArray = rootObject.getJSONArray("data");

                        SQLiteDatabase db = this.getWritableDatabase();
                        db.beginTransaction();

                        // Make sure tables exist
                        onCreate(db);

                        db.execSQL("DELETE FROM " + TABLE_SUBJECT);
                        db.execSQL("DELETE FROM " + TABLE_FACULTY);

                        for (int i = 0; i < facultyArray.length(); i++) {
                            JSONObject facultyObj = facultyArray.getJSONObject(i);
                            String faculty_id = facultyObj.getString("faculty_id");
                            String faculty_name = facultyObj.getString("faculty_name");

                            ContentValues facultyValues = new ContentValues();
                            facultyValues.put("faculty_id", faculty_id);
                            facultyValues.put("faculty_name", faculty_name);
                            db.insert(TABLE_FACULTY, null, facultyValues);

                            JSONArray subjectArray = facultyObj.getJSONArray("subjects");

                            for (int j = 0; j < subjectArray.length(); j++) {
                                JSONObject subjectObj = subjectArray.getJSONObject(j);
                                String subject_id = subjectObj.getString("subject_id");
                                String subject_name = subjectObj.getString("subject_name");
                                String subject_details = subjectObj.getString("subject_details");
                                String subject_image_url = subjectObj.getString("subject_image_url"); // <-- Added this line

                                ContentValues subjectValues = new ContentValues();
                                subjectValues.put("subject_id", subject_id);
                                subjectValues.put("faculty_id", faculty_id);
                                subjectValues.put("subject_name", subject_name);
                                subjectValues.put("subject_details", subject_details);
                                subjectValues.put("subject_image_url", subject_image_url); // <-- Added this line

                                db.insert(TABLE_SUBJECT, null, subjectValues);
                            }
                        }

                        db.setTransactionSuccessful();
                        Toast.makeText(context, "Faculty & Subject Data Updated", Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Parsing Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    } finally {
                        SQLiteDatabase db = getWritableDatabase();
                        db.endTransaction();
                        db.close();
                    }
                },
                error -> {
                    error.printStackTrace();
                    String message = error.getMessage();
                    if (message == null && error.networkResponse != null) {
                        message = "HTTP Code: " + error.networkResponse.statusCode;
                    }
                    Toast.makeText(context, "Network Error: " + message, Toast.LENGTH_LONG).show();
                });

        queue.add(stringRequest);
    }
}
