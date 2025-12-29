package com.example.addmission_update_project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Activity_favourite_List extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FavouriteAdapter adapter;
    private Database_Favourite_Unique_Id dbHelper;
    private Map<String, String> allUniversitiesMap;
    private LinearLayout emptyStateLayout;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_list);

        // Initialize views
        recyclerView = findViewById(R.id.favourite_recycler_view);
        emptyStateLayout = findViewById(R.id.empty_state_layout);
        backButton = findViewById(R.id.back);

        // Set up back button
        backButton.setOnClickListener(v -> onBackPressed());

        // Initialize database helper
        dbHelper = new Database_Favourite_Unique_Id(this);

        // Load all universities data
        allUniversitiesMap = getAllUniversitiesMap();

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load favorite universities
        loadFavouriteUniversities();
    }

    private void loadFavouriteUniversities() {
        // Get favorite unique IDs from database
        Set<String> favoriteIds = dbHelper.getFavoriteUniqueIds();

        // Convert to list of university names with numbering
        List<String> favoriteUniversities = new ArrayList<>();
        int counter = 1;
        for (String id : favoriteIds) {
            if (allUniversitiesMap.containsKey(id)) {
                favoriteUniversities.add(counter + ". " + allUniversitiesMap.get(id));
                counter++;
            }
        }

        // Show empty state if no favorites
        if (favoriteUniversities.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);

            // Set up adapter
            adapter = new FavouriteAdapter(favoriteUniversities);
            recyclerView.setAdapter(adapter);
        }
    }

    // Helper method to create a map of all universities (id -> name)
    private Map<String, String> getAllUniversitiesMap() {
        Map<String, String> universitiesMap = new HashMap<>();
        Map<String, Map<String, String>> allTypes = Activity_University_listing.getAllUniversityTypes();

        for (Map<String, String> category : allTypes.values()) {
            universitiesMap.putAll(category);
        }

        return universitiesMap;
    }

    // Adapter class
    private static class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.FavouriteViewHolder> {

        private final List<String> universityNames;

        public FavouriteAdapter(List<String> universityNames) {
            this.universityNames = universityNames;
        }

        @NonNull
        @Override
        public FavouriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_text_selector_eligible_university_favourite_list, parent, false);
            return new FavouriteViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FavouriteViewHolder holder, int position) {
            holder.bind(universityNames.get(position));
        }

        @Override
        public int getItemCount() {
            return universityNames.size();
        }

        // ViewHolder class
        static class FavouriteViewHolder extends RecyclerView.ViewHolder {
            private final TextView textView;

            public FavouriteViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.universityName);
            }

            public void bind(String universityName) {
                textView.setText(universityName);
            }
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}