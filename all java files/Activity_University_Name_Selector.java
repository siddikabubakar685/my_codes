package com.example.addmission_update_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Map;

public class Activity_University_Name_Selector extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_university_name_selector);

        findViewById(R.id.back).setOnClickListener(v -> finish());
        // Get the grid_clicked value
        String gridClicked = getIntent().getStringExtra("grid_clicked");

        LinearLayout universityListContainer = findViewById(R.id.universityListContainer);
        Map<String, String> universityNames = Activity_Expandable_Section_Text.getSectionItems(
                getIntent().getStringExtra("universityType")
        );

        for (Map.Entry<String, String> entry : universityNames.entrySet()) {
            final String uniqueId = entry.getKey(); // This is what we'll pass

            View itemView = LayoutInflater.from(this).inflate(R.layout.item_text_selector_university_types_name_subjects, universityListContainer, false);
            TextView textTv = itemView.findViewById(R.id.text_tv);
            textTv.setText(entry.getValue());

            itemView.setOnClickListener(v -> {
                Intent intent;

                switch (gridClicked) {
                    case "university_details":
                        intent = new Intent(this, Activity_University_Details.class);
                        break;
                    case "seat_number":
                        intent = new Intent(this, Activity_Seat_Number.class);
                        break;
                    case "website_list":
                        intent = new Intent(this, Activity_Web_Url_Loader.class);
                        break;
                    default:
                        return; // No action for unknown sources
                }

                // Only pass the uniqueId
                intent.putExtra("uniqueId", uniqueId);
                startActivity(intent);
            });

            universityListContainer.addView(itemView);
        }
    }
}