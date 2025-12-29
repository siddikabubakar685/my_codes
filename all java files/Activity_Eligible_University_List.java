package com.example.addmission_update_project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class Activity_Eligible_University_List extends AppCompatActivity {

    private RecyclerView resultsRecyclerView;
    private UniversityAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eligible_universities_list);

        resultsRecyclerView = findViewById(R.id.resultsRecyclerView);
        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get university names from intent
        ArrayList<String> universityNames = getIntent().getStringArrayListExtra("universityNames");

        adapter = new UniversityAdapter(universityNames);
        resultsRecyclerView.setAdapter(adapter);
    }

    // Inner Adapter Class with numbered universities
    private class UniversityAdapter extends RecyclerView.Adapter<UniversityAdapter.UniversityViewHolder> {
        private final ArrayList<String> universityNames;

        public UniversityAdapter(ArrayList<String> universityNames) {
            this.universityNames = universityNames;
        }

        @NonNull
        @Override
        public UniversityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_text_selector_eligible_university_favourite_list, parent, false);
            return new UniversityViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull UniversityViewHolder holder, int position) {
            // Add numbering (position + 1 because index starts at 0)
            String numberedUniversity = (position + 1) + ". " + universityNames.get(position);
            holder.universityNameText.setText(numberedUniversity);
        }

        @Override
        public int getItemCount() {
            return universityNames.size();
        }

        // ViewHolder class
        class UniversityViewHolder extends RecyclerView.ViewHolder {
            TextView universityNameText;

            public UniversityViewHolder(@NonNull View itemView) {
                super(itemView);
                universityNameText = itemView.findViewById(R.id.universityName);
            }
        }
    }
}