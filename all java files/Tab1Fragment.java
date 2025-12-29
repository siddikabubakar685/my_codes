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
import java.util.TimeZone;

public class Tab1Fragment extends Fragment {

    private RecyclerView recyclerView;
    private Countdown_Item_and_Adapter.CountdownAdapter adapter;
    private List<Countdown_Item_and_Adapter.CountdownItem> countdownItems;
    private Database_Favourite_Unique_Id dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab1_fragment, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewCountdown);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        countdownItems = new ArrayList<>();
        adapter = new Countdown_Item_and_Adapter.CountdownAdapter(countdownItems);
        recyclerView.setAdapter(adapter);

        dbHelper = new Database_Favourite_Unique_Id(getActivity());
        new FetchCountdownDataTask().execute();

        return view;
    }

    private class FetchCountdownDataTask extends AsyncTask<Void, Void, List<Countdown_Item_and_Adapter.CountdownItem>> {
        @Override
        protected List<Countdown_Item_and_Adapter.CountdownItem> doInBackground(Void... voids) {
            List<Countdown_Item_and_Adapter.CountdownItem> items = new ArrayList<>();
            Set<String> favoriteIds = dbHelper.getFavoriteUniqueIds();
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor;

            if (favoriteIds.isEmpty()) {
                cursor = db.query("stored_countdown_apply_data", null, null, null, null, null, null);
            } else {
                String selection = "unique_id IN (" + makePlaceholders(favoriteIds.size()) + ")";
                cursor = db.query("stored_countdown_apply_data", null, selection, favoriteIds.toArray(new String[0]), null, null, null);
            }

            if (cursor != null) {
                int titleIndex = cursor.getColumnIndex("title");
                int targetDateIndex = cursor.getColumnIndex("target_date_to_count");

                if (cursor.moveToFirst()) {
                    do {
                        if (titleIndex >= 0 && targetDateIndex >= 0) {
                            String title = cursor.getString(titleIndex);
                            String targetDate = cursor.getString(targetDateIndex);
                            items.add(new Countdown_Item_and_Adapter.CountdownItem(title, targetDate, "apply"));
                        }
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            db.close();

            // Sort items with past dates at the end
            Collections.sort(items, new Comparator<Countdown_Item_and_Adapter.CountdownItem>() {
                @Override
                public int compare(Countdown_Item_and_Adapter.CountdownItem item1, Countdown_Item_and_Adapter.CountdownItem item2) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    format.setTimeZone(TimeZone.getTimeZone("GMT+6"));
                    try {
                        Date date1 = format.parse(item1.getTargetDate());
                        Date date2 = format.parse(item2.getTargetDate());
                        Date now = new Date();

                        if (date1 != null && date2 != null) {
                            boolean isPast1 = date1.before(now);
                            boolean isPast2 = date2.before(now);

                            // If both are past or both are future, sort normally
                            if (isPast1 == isPast2) {
                                return date1.compareTo(date2);
                            }
                            // Put past dates after future dates
                            return isPast1 ? 1 : -1;
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
            countdownItems.clear();
            countdownItems.addAll(items);
            adapter.notifyDataSetChanged();
        }
    }

    private String makePlaceholders(int len) {
        StringBuilder sb = new StringBuilder(len * 2 - 1);
        sb.append("?");
        for (int i = 1; i < len; i++) {
            sb.append(",?");
        }
        return sb.toString();
    }
}