package com.example.addmission_update_project;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class Activity_Notice_Details extends AppCompatActivity {

    private TextView titleTextView, dateTextView, descriptionTextView;
    private ImageView noticeImageView;
    private ProgressBar loadingProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_details);

        // Initialize views
        titleTextView = findViewById(R.id.notice_detail_title);
        dateTextView = findViewById(R.id.notice_detail_date);
        noticeImageView = findViewById(R.id.notice_detail_image);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);

        findViewById(R.id.back).setOnClickListener(v -> finish());

        // Get data from Intent
        Intent intent = getIntent();
        if (intent != null) {
            String title = intent.getStringExtra("title");
            String date = intent.getStringExtra("date");
            String imageUrl = intent.getStringExtra("imageUrl");
            String descriptionHtml = intent.getStringExtra("description");

            // Set data to views
            titleTextView.setText(title);
            dateTextView.setText(date);

            // Load HTML content into TextView
            if (descriptionHtml != null) {
                Spanned spannedText = Html.fromHtml(descriptionHtml, Html.FROM_HTML_MODE_COMPACT);
                descriptionTextView.setText(spannedText);
            }

            // Load image using Picasso
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Picasso.get()
                        .load(imageUrl)
                        .into(noticeImageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                // Hide ProgressBar and show ImageView
                                loadingProgressBar.setVisibility(android.view.View.GONE);
                                noticeImageView.setVisibility(android.view.View.VISIBLE);
                            }

                            @Override
                            public void onError(Exception e) {
                                // Hide ProgressBar and show a placeholder image
                                loadingProgressBar.setVisibility(android.view.View.GONE);
                                noticeImageView.setVisibility(android.view.View.VISIBLE);

                            }
                        });
            } else {
                // Hide ProgressBar and show a placeholder image
                loadingProgressBar.setVisibility(android.view.View.GONE);
                noticeImageView.setVisibility(android.view.View.VISIBLE);

            }
        }
    }
}