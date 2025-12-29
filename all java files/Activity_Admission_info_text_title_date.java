package com.example.addmission_update_project;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Activity_Admission_info_text_title_date extends AppCompatActivity {

    private Database_admission_info dbHelper;
    private RecyclerView recyclerView;
    private AdmissionInfoAdapter adapter;
    private String tableType;
    private String banglaTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admission_info_text_title_date);

        // Get intent extras
        banglaTitle = getIntent().getStringExtra("bangla_title");
        tableType = getIntent().getStringExtra("table_type");

        // Set title
        TextView titleText = findViewById(R.id.title_bar_text);
        titleText.setText(banglaTitle != null ? banglaTitle : "Admission Info");

        findViewById(R.id.back).setOnClickListener(v -> finish());

        // Initialize database helper
        dbHelper = new Database_admission_info(this);

        // Setup RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load data
        loadData();
    }

    private void loadData() {
        List<Map<String, String>> data = getDataForTable(tableType);

        if (adapter == null) {
            adapter = new AdmissionInfoAdapter(data);
            recyclerView.setAdapter(adapter);
        } else {
            adapter.updateData(data);
        }
    }

    private List<Map<String, String>> getDataForTable(String tableType) {
        switch (tableType) {
            case "admit_card_data": return dbHelper.getAllAdmitCardData();
            case "seat_plan_data": return dbHelper.getAllSeatPlanData();
            case "result_data": return dbHelper.getAllResultData();
            case "waiting_list_data": return dbHelper.getAllWaitingListData();
            case "question_answer_data": return dbHelper.getAllQuestionAnswerData();
            case "cut_mark_data": return dbHelper.getAllCutMarkData();
            default: return new ArrayList<>();
        }
    }

    // Inner Adapter Class
    private class AdmissionInfoAdapter extends RecyclerView.Adapter<AdmissionInfoAdapter.ViewHolder> {
        private List<Map<String, String>> dataList;

        public AdmissionInfoAdapter(List<Map<String, String>> dataList) {
            this.dataList = dataList;
        }

        public void updateData(List<Map<String, String>> newData) {
            this.dataList = newData;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_admission_info, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Map<String, String> item = dataList.get(position);

            holder.titleText.setText(item.get("title"));
            holder.dateText.setText(formatDate(item.get("announcement_date")));

            holder.itemView.setOnClickListener(v -> {
                String description = item.get("description");

                if (isPureUrl(description)) {
                    // Only open WebView if description is exactly a URL
                    Intent webIntent = new Intent(Activity_Admission_info_text_title_date.this,
                            Activity_Web_Url_Loader.class);
                    webIntent.putExtra("direct_url", description.trim());
                    startActivity(webIntent);
                } else {
                    // Open in TextDescription for all other cases
                    Intent textIntent = new Intent(Activity_Admission_info_text_title_date.this,
                            Activity_Text_Description.class);
                    textIntent.putExtra("title", item.get("title"));
                    textIntent.putExtra("date", item.get("announcement_date"));
                    textIntent.putExtra("description", description);
                    startActivity(textIntent);
                }
            });
        }

        private String formatDate(String rawDate) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                Date date = inputFormat.parse(rawDate);
                return outputFormat.format(date);
            } catch (Exception e) {
                return rawDate;
            }
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView titleText, dateText;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                titleText = itemView.findViewById(R.id.titleText);
                dateText = itemView.findViewById(R.id.dateText);
            }
        }
    }

    // Strict URL validation - returns true only for pure URLs
    private boolean isPureUrl(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }

        // Remove whitespace
        String trimmed = text.trim();

        // Check for standard URL patterns
        String urlPattern = "^(https?|ftp)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        if (trimmed.matches(urlPattern)) {
            return true;
        }

        // Check for www URLs without protocol
        if (trimmed.startsWith("www.") && trimmed.contains(".") &&
                !trimmed.contains(" ") && trimmed.length() > 4) {
            return true;
        }

        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}