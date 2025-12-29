package com.example.addmission_update_project;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Activity_Faculty_Subjects extends AppCompatActivity {

    private RecyclerView subjectsRecyclerView;
    private ArrayList<String> subjectIds = new ArrayList<>();
    private ArrayList<String> subjectNames = new ArrayList<>();
    private ArrayList<String> subjectDetails = new ArrayList<>();
    private ArrayList<String> subjectImageUrls = new ArrayList<>();  // <-- Added this line

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_subjects);

        // Get faculty data from intent
        Intent intent = getIntent();
        String facultyId = intent.getStringExtra("FACULTY_ID");
        String facultyName = intent.getStringExtra("FACULTY_NAME");

        // Title bar setup
        TextView titleText = findViewById(R.id.title_bar_text);
        titleText.setText(facultyName);
        findViewById(R.id.back).setOnClickListener(v -> finish());

        subjectsRecyclerView = findViewById(R.id.subjects_recycler);
        subjectsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadSubjectsData(facultyId);
    }

    private void loadSubjectsData(String facultyId) {
        Database_faculty_and_subject dbHelper = new Database_faculty_and_subject(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                "subject",
                new String[]{"subject_id", "subject_name", "subject_details", "subject_image_url"},  // <-- Added subject_image_url
                "faculty_id=?",
                new String[]{facultyId},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            do {
                subjectIds.add(cursor.getString(0));
                subjectNames.add(cursor.getString(1));
                subjectDetails.add(cursor.getString(2));
                subjectImageUrls.add(cursor.getString(3)); // <-- Added this line
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        SubjectsAdapter adapter = new SubjectsAdapter(subjectNames);
        subjectsRecyclerView.setAdapter(adapter);
    }

    // Inner class adapter
    private class SubjectsAdapter extends RecyclerView.Adapter<SubjectsAdapter.SubjectViewHolder> {
        private ArrayList<String> subjects;

        public SubjectsAdapter(ArrayList<String> subjects) {
            this.subjects = subjects;
        }

        @Override
        public SubjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_text_selector_university_types_name_subjects, parent, false);
            return new SubjectViewHolder(view);
        }

        @Override
        public void onBindViewHolder(SubjectViewHolder holder, int position) {
            holder.textView.setText(subjects.get(position));
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(Activity_Faculty_Subjects.this,
                        Activity_Faculty_Subjects_details.class);
                intent.putExtra("SUBJECT_DETAILS", subjectDetails.get(position));
                intent.putExtra("SUBJECT_NAME", subjectNames.get(position));
                intent.putExtra("SUBJECT_IMAGE_URL", subjectImageUrls.get(position));  // <-- Added this line
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return subjects.size();
        }

        class SubjectViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            public SubjectViewHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.text_tv);
            }
        }
    }
}
