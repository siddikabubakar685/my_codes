package com.example.addmission_update_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class Activity_Splash_Screen extends AppCompatActivity implements Database_Countdown_and_Notice.OnDataFetchCompleteListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize the Database_Countdown_and_Notice class
        Database_Countdown_and_Notice databaseHelper = new Database_Countdown_and_Notice(this);

        // Start data fetching and pass "this" as the listener
        databaseHelper.fetchDataAndStoreLocally(this);

        // Store all unique IDs in the database
        storeAllUniqueIds();
    }

    private void storeAllUniqueIds() {
        Database_Favourite_Unique_Id dbHelper = new Database_Favourite_Unique_Id(this);
        Set<String> allUniqueIds = new HashSet<>();

        // Get all unique IDs from Activity_Expandable_Section_Text
        Map<String, Map<String, String>> expandableSections = Activity_Expandable_Section_Text.getExpandableSections();
        for (Map<String, String> sectionItems : expandableSections.values()) {
            allUniqueIds.addAll(sectionItems.keySet());
        }

        // Check if the all_unique_id_list table already has data
        if (dbHelper.isAllUniqueIdsTableEmpty()) {
            // Insert unique IDs into the database
            dbHelper.insertUniqueIds(allUniqueIds);

            // Show toast with the total number of unique IDs stored
           Toast.makeText(this, "Stored " + allUniqueIds.size() + " unique IDs for the first time!", Toast.LENGTH_LONG).show();
        } else {
            // Show toast indicating that data already exists

          Toast.makeText(this, "All unique IDs already exist in the database.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDataFetchComplete() {
        // Data fetching is complete
        //Toast.makeText(this, "All 4 tables data stored successfully!", Toast.LENGTH_SHORT).show();

        // Navigate to MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onDataFetchError(String errorMessage) {
        // Handle the error
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }
}