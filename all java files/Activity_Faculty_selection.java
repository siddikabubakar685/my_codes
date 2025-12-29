package com.example.addmission_update_project;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Activity_Faculty_selection extends AppCompatActivity {

    private GridView facultyGridView;
    private ArrayList<String> facultyIds = new ArrayList<>();
    private ArrayList<String> facultyNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_selection);


        findViewById(R.id.back).setOnClickListener(v -> finish());

        facultyGridView = findViewById(R.id.faculty_grid);
        loadFacultyData();

        facultyGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Activity_Faculty_selection.this, Activity_Faculty_Subjects.class);
                intent.putExtra("FACULTY_ID", facultyIds.get(position));
                intent.putExtra("FACULTY_NAME", facultyNames.get(position));
                startActivity(intent);
            }
        });
    }

    private void loadFacultyData() {
        Database_faculty_and_subject dbHelper = new Database_faculty_and_subject(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                "faculty",
                new String[]{"faculty_id", "faculty_name"},
                null, null, null, null, null
        );

        if (cursor.moveToFirst()) {
            do {
                facultyIds.add(cursor.getString(0));
                facultyNames.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        FacultyGridAdapter adapter = new FacultyGridAdapter(this, facultyNames);
        facultyGridView.setAdapter(adapter);
    }

    // Inner class adapter
    private class FacultyGridAdapter extends android.widget.BaseAdapter {
        private ArrayList<String> facultyNames;
        private android.content.Context context;

        public FacultyGridAdapter(Context context, ArrayList<String> facultyNames) {
            this.context = context;
            this.facultyNames = facultyNames;
        }

        @Override
        public int getCount() {
            return facultyNames.size();
        }

        @Override
        public Object getItem(int position) {
            return facultyNames.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
        // Inside FacultyGridAdapter class
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context)
                        .inflate(R.layout.item_text_selector_faculty_list, parent, false);
            }

            TextView textView = convertView.findViewById(R.id.text_tv);
            textView.setText(facultyNames.get(position));

            return convertView;
        }
    }
}