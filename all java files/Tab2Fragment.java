package com.example.addmission_update_project;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class Tab2Fragment extends Fragment {

    private RecyclerView recyclerView;
    private Countdown_Item_and_Adapter.CountdownAdapter adapter;
    private List<Countdown_Item_and_Adapter.CountdownItem> countdownItems;
    private Database_Favourite_Unique_Id dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab2_fragment, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerViewCountdown);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Initialize the list of countdown items
        countdownItems = new ArrayList<>();

        // Initialize the adapter
        adapter = new Countdown_Item_and_Adapter.CountdownAdapter(countdownItems);
        recyclerView.setAdapter(adapter);

        // Initialize Database_Favourite_Unique_Id
        dbHelper = new Database_Favourite_Unique_Id(getActivity());

        // Fetch data from the database
        new FetchCountdownDataTask().execute();

        return view;
    }

    // AsyncTask to fetch countdown exam data from the database
    private class FetchCountdownDataTask extends AsyncTask<Void, Void, List<Countdown_Item_and_Adapter.CountdownItem>> {
        @Override
        protected List<Countdown_Item_and_Adapter.CountdownItem> doInBackground(Void... voids) {
            List<Countdown_Item_and_Adapter.CountdownItem> items = new ArrayList<>();

            // Fetch favorite unique IDs
            Set<String> favoriteIds = dbHelper.getFavoriteUniqueIds();

            // Fetch data from the stored_countdown_exam_data table
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor;

            if (favoriteIds.isEmpty()) {
                // If no favorites, fetch all data
                cursor = db.query(
                        "stored_countdown_exam_data", // Table name
                        null, // Columns (null means all columns)
                        null, // Selection (WHERE clause)
                        null, // Selection arguments
                        null, // Group by
                        null, // Having
                        null  // Order by
                );
            } else {
                // If favorites exist, fetch only data matching favorite IDs
                String selection = "unique_id IN (" + makePlaceholders(favoriteIds.size()) + ")";
                String[] selectionArgs = favoriteIds.toArray(new String[0]);

                cursor = db.query(
                        "stored_countdown_exam_data", // Table name
                        null, // Columns (null means all columns)
                        selection, // Selection (WHERE clause)
                        selectionArgs, // Selection arguments
                        null, // Group by
                        null, // Having
                        null  // Order by
                );
            }

            if (cursor != null) {
                int titleIndex = cursor.getColumnIndex("title");
                int targetDateIndex = cursor.getColumnIndex("target_date_to_count");

                if (cursor.moveToFirst()) {
                    do {
                        // Check if column indices are valid
                        if (titleIndex >= 0 && targetDateIndex >= 0) {
                            String title = cursor.getString(titleIndex);
                            String targetDate = cursor.getString(targetDateIndex);
                            items.add(new Countdown_Item_and_Adapter.CountdownItem(title, targetDate, "exam"));
                        }
                    } while (cursor.moveToNext());
                }

                cursor.close();
            }

            db.close();

            // Sort items by time left (ascending order)
            Collections.sort(items, new Comparator<Countdown_Item_and_Adapter.CountdownItem>() {
                @Override
                public int compare(Countdown_Item_and_Adapter.CountdownItem item1, Countdown_Item_and_Adapter.CountdownItem item2) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    try {
                        Date date1 = format.parse(item1.getTargetDate());
                        Date date2 = format.parse(item2.getTargetDate());
                        if (date1 != null && date2 != null) {
                            return date1.compareTo(date2); // Sort by target date (ascending)
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return 0;
                }
            });

            return items;
        }

        @Override
        protected void onPostExecute(List<Countdown_Item_and_Adapter.CountdownItem> items) {
            // Update the adapter with the fetched data
            countdownItems.clear();
            countdownItems.addAll(items);
            adapter.notifyDataSetChanged();
        }
    }

    // Helper method to create placeholders for SQL query
    private String makePlaceholders(int len) {
        if (len <= 0) {
            return ""; // Return an empty string if there are no placeholders
        }
        StringBuilder sb = new StringBuilder(len * 2 - 1);
        sb.append("?");
        for (int i = 1; i < len; i++) {
            sb.append(",?");
        }
        return sb.toString();
    }
}