package com.example.addmission_update_project;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Activity_Seat_Number extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_number);

        // Receive the uniqueId
        String uniqueId = getIntent().getStringExtra("uniqueId");
        if (uniqueId == null || uniqueId.isEmpty()) {
            Toast.makeText(this, "No university selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        TextView titleTextView = findViewById(R.id.title);
        TextView seatDetailsTextView = findViewById(R.id.seat_details);

        findViewById(R.id.back).setOnClickListener(v -> finish());
        // Initialize database helper
        Database_Favourite_Unique_Id dbHelper = new Database_Favourite_Unique_Id(this);

        // Fetch data from database
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                "stored_university_info_data",
                new String[]{"university_name", "seat_numbers"},
                "unique_id = ?",
                new String[]{uniqueId},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            // Set university name in title
            String universityName = cursor.getString(cursor.getColumnIndexOrThrow("university_name"));
            titleTextView.setText(universityName);

            // Get seat numbers data with HTML formatting
            String seatNumbersHtml = cursor.getString(cursor.getColumnIndexOrThrow("seat_numbers"));

            // Process HTML content
            Spanned spannedSeatNumbers;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                spannedSeatNumbers = Html.fromHtml(seatNumbersHtml, Html.FROM_HTML_MODE_LEGACY);
            } else {
                spannedSeatNumbers = Html.fromHtml(seatNumbersHtml);
            }

            seatDetailsTextView.setText(spannedSeatNumbers);
            cursor.close();
        } else {
            Toast.makeText(this, "Seat information not available", Toast.LENGTH_SHORT).show();
            finish();
        }

        db.close();
        dbHelper.close();
    }
}