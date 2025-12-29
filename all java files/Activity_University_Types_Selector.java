package com.example.addmission_update_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Map;

public class Activity_University_Types_Selector extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_university_types_selector);

        findViewById(R.id.back).setOnClickListener(v -> finish());
        // Get which grid item was clicked and make it final
        final String gridClickedValue;
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("grid_clicked")) {
            gridClickedValue = intent.getStringExtra("grid_clicked");
            String message = "Opened from: " + getHumanReadableSource(gridClickedValue);
          //  Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } else {
            gridClickedValue = null;
        }

        // Rest of your university list setup
        LinearLayout universityListContainer = findViewById(R.id.universityListContainer);
        Map<String, Map<String, String>> universityTypes = Activity_Expandable_Section_Text.getExpandableSections();

        for (String universityType : universityTypes.keySet()) {
            View itemView = LayoutInflater.from(this).inflate(R.layout.item_text_selector_university_types_name_subjects, universityListContainer, false);
            TextView textTv = itemView.findViewById(R.id.text_tv);
            textTv.setText(universityType);

            // Create a final copy of universityType for use in lambda
            final String finalUniversityType = universityType;
            itemView.setOnClickListener(v -> {
                Intent uniIntent = new Intent(this, Activity_University_Name_Selector.class);
                uniIntent.putExtra("universityType", finalUniversityType);

                // Pass the same grid_clicked value to next activity
                if (gridClickedValue != null) {
                    uniIntent.putExtra("grid_clicked", gridClickedValue);
                }

                startActivity(uniIntent);
            });

            universityListContainer.addView(itemView);
        }
    }

    private String getHumanReadableSource(String source) {
        switch (source) {
            case "university_details":
                return "University List";
            case "seat_number":
                return "Seat Numbers";
            case "website_list":
                return "Website Links";
            default:
                return "Unknown Source";
        }
    }
}