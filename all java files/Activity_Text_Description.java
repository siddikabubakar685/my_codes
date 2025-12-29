package com.example.addmission_update_project;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class Activity_Text_Description extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_description);

        // Get intent data
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");

        findViewById(R.id.back).setOnClickListener(v -> finish());

        // Set title in title bar
        TextView titleBar = findViewById(R.id.titleBar);
        titleBar.setText(title != null ? title : "");

        // Set description text with HTML support and clickable links
        TextView descView = findViewById(R.id.descriptionTextView);
        if (description != null) {
            // Enable link clicking
            descView.setMovementMethod(LinkMovementMethod.getInstance());

            // Display HTML content if present, otherwise plain text
            if (description.contains("<") && description.contains(">")) {
                descView.setText(Html.fromHtml(description));
            } else {
                descView.setText(description);
            }
        } else {
            descView.setText("No description available");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}