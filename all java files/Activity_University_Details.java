package com.example.addmission_update_project;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Activity_University_Details extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UniversityDetailsAdapter adapter;
    private Database_Favourite_Unique_Id dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_university_details);

        dbHelper = new Database_Favourite_Unique_Id(this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.back).setOnClickListener(v -> finish());

        String uniqueId = getIntent().getStringExtra("uniqueId");
        fetchUniversityData(uniqueId);
    }

    private void fetchUniversityData(String uniqueId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                "university_name",
                "history",
                "units_name_and_details",
                "faculties_details",
                "subjects",
                "seat_numbers",
                "admission_info",
                "other_info",
                "website_url",
                "contact_info",
                "social_link",
                "university_representative_info",
                "join_our_community"
        };

        Cursor cursor = db.query(
                "stored_university_info_data",
                projection,
                "unique_id = ?",
                new String[]{uniqueId},
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            String universityName = cursor.getString(cursor.getColumnIndexOrThrow("university_name"));
            TextView titleBarText = findViewById(R.id.title_bar_text);
            titleBarText.setText(Html.fromHtml(universityName).toString());

            List<UniversityDetailsAdapter.DetailItem> detailItems = new ArrayList<>();
            addIfNotEmpty(detailItems, "ইতিহাস", cursor.getString(cursor.getColumnIndexOrThrow("history")));
            addIfNotEmpty(detailItems, "ইউনিট সমূহ", cursor.getString(cursor.getColumnIndexOrThrow("units_name_and_details")));
            addIfNotEmpty(detailItems, "ফ্যাকাল্টি সমূহ", cursor.getString(cursor.getColumnIndexOrThrow("faculties_details")));
            addIfNotEmpty(detailItems, "সাবজেক্ট সমূহ", cursor.getString(cursor.getColumnIndexOrThrow("subjects")));
            addIfNotEmpty(detailItems, "সিট সংখ্যা", cursor.getString(cursor.getColumnIndexOrThrow("seat_numbers")));
            addIfNotEmpty(detailItems, "ভর্তি তথ্য", cursor.getString(cursor.getColumnIndexOrThrow("admission_info")));
            addIfNotEmpty(detailItems, "অন্যান্য তথ্য", cursor.getString(cursor.getColumnIndexOrThrow("other_info")));
            addIfNotEmpty(detailItems, "ওয়েবসাইট লিংক", cursor.getString(cursor.getColumnIndexOrThrow("website_url")));
            addIfNotEmpty(detailItems, "যোগাযোগ", cursor.getString(cursor.getColumnIndexOrThrow("contact_info")));
            addIfNotEmpty(detailItems, "সামাজিক যোগাযোগ মাধ্যম", cursor.getString(cursor.getColumnIndexOrThrow("social_link")));
            addIfNotEmpty(detailItems, "বিশ্ববিদ্যালয় প্রতিনিধি", cursor.getString(cursor.getColumnIndexOrThrow("university_representative_info")));
            addIfNotEmpty(detailItems, "ভর্তি সম্পর্কিত তথ্য পেতে ও যেকোনো কনফিউশন দূর করতে যুক্ত হও আমাদের কমিউনিটিতে",
                    cursor.getString(cursor.getColumnIndexOrThrow("join_our_community")));

            adapter = new UniversityDetailsAdapter(this, universityName);
            recyclerView.setAdapter(adapter);
            adapter.setDetailItems(detailItems);
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
    }

    private void addIfNotEmpty(List<UniversityDetailsAdapter.DetailItem> items, String title, String content) {
        if (content != null && !content.trim().isEmpty()) {
            items.add(new UniversityDetailsAdapter.DetailItem(title, content));
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }

    // Nested Adapter Class
    public static class UniversityDetailsAdapter extends RecyclerView.Adapter<UniversityDetailsAdapter.ViewHolder> {
        private Context context;
        private List<DetailItem> detailItems = new ArrayList<>();
        private List<DetailItem> filteredItems = new ArrayList<>();
        private String universityName;

        public UniversityDetailsAdapter(Context context, String universityName) {
            this.context = context;
            this.universityName = universityName;
        }

        public void setDetailItems(List<DetailItem> items) {
            this.detailItems = items;
            filterItems();
            if (!filteredItems.isEmpty()) {
                filteredItems.get(0).setExpanded(true);
            }
            notifyDataSetChanged();
        }

        private void filterItems() {
            filteredItems.clear();
            for (DetailItem item : detailItems) {
                if (item.getContent() != null && !item.getContent().trim().isEmpty()) {
                    if (item.getTitle().equals("ইতিহাস")) {
                        filteredItems.add(item);
                        addUniversityTypeIfExists();
                    } else if (!item.getTitle().equals("বিশ্ববিদ্যালয়ের ধরন")) {
                        filteredItems.add(item);
                    }
                }
            }
        }

        private void addUniversityTypeIfExists() {
            for (DetailItem item : detailItems) {
                if (item.getTitle().equals("বিশ্ববিদ্যালয়ের ধরন") &&
                        item.getContent() != null && !item.getContent().trim().isEmpty()) {
                    filteredItems.add(item);
                    break;
                }
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_university_details_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            DetailItem item = filteredItems.get(position);
            holder.title.setText(item.getTitle());

            Spanned spannedContent = Html.fromHtml(item.getContent(), Html.FROM_HTML_MODE_LEGACY);
            holder.description.setText(spannedContent);

            holder.divider.setVisibility(position == filteredItems.size() - 1 ? View.GONE : View.VISIBLE);
            holder.arrow.setRotation(item.isExpanded() ? 180 : 0);
            holder.expandableContent.setVisibility(item.isExpanded() ? View.VISIBLE : View.GONE);

            holder.itemView.setOnClickListener(v -> {
                boolean newExpandedState = !item.isExpanded();
                item.setExpanded(newExpandedState);

                if (newExpandedState) {
                    Animation fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in);
                    holder.expandableContent.startAnimation(fadeIn);
                    holder.expandableContent.setVisibility(View.VISIBLE);
                    rotateArrow(holder.arrow, 180);
                } else {
                    Animation fadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out);
                    holder.expandableContent.startAnimation(fadeOut);
                    holder.expandableContent.setVisibility(View.GONE);
                    rotateArrow(holder.arrow, 0);
                }
            });
        }

        @Override
        public int getItemCount() {
            return filteredItems.size();
        }

        private void rotateArrow(ImageView arrow, float toDegrees) {
            arrow.animate().rotation(toDegrees).setDuration(300).start();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView title, description;
            ImageView arrow;
            LinearLayout expandableContent;
            View divider;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.title);
                description = itemView.findViewById(R.id.description);
                arrow = itemView.findViewById(R.id.expandable_arrow);
                expandableContent = itemView.findViewById(R.id.expandableContent);
                divider = itemView.findViewById(R.id.divider);
            }
        }

        public static class DetailItem {
            private String title;
            private String content;
            private boolean expanded;

            public DetailItem(String title, String content) {
                this.title = title;
                this.content = content;
                this.expanded = false;
            }

            public String getTitle() {
                return title;
            }

            public String getContent() {
                return content;
            }

            public boolean isExpanded() {
                return expanded;
            }

            public void setExpanded(boolean expanded) {
                this.expanded = expanded;
            }
        }
    }
}