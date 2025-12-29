package com.example.addmission_update_project;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Fragment_Notice extends Fragment {

    private RecyclerView noticeRecyclerView;
    private NoticeAdapter adapter;
    private List<Notice> noticeList;
    private Database_Favourite_Unique_Id dbHelper;
    private SharedPreferences clickedNoticesPrefs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notice, container, false);

        clickedNoticesPrefs = requireContext().getSharedPreferences("ClickedNotices", Context.MODE_PRIVATE);

        noticeRecyclerView = view.findViewById(R.id.noticeRecyclerView);
        noticeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dbHelper = new Database_Favourite_Unique_Id(getContext());
        noticeList = new ArrayList<>();
        adapter = new NoticeAdapter(noticeList);
        noticeRecyclerView.setAdapter(adapter);

        new FetchNoticeDataTask().execute();

        return view;
    }

    private String getNoticeKey(String title) {
        return "notice_" + title.hashCode();
    }

    private class FetchNoticeDataTask extends AsyncTask<Void, Void, List<Notice>> {
        @Override
        protected List<Notice> doInBackground(Void... voids) {
            List<Notice> items = new ArrayList<>();
            Set<String> favoriteIds = dbHelper.getFavoriteUniqueIds();

            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor;

            if (favoriteIds.isEmpty()) {
                cursor = db.query("stored_notification_data", null, null, null, null, null, "date DESC");
            } else {
                String selection = "unique_id IN (" + makePlaceholders(favoriteIds.size()) + ")";
                String[] selectionArgs = favoriteIds.toArray(new String[0]);
                cursor = db.query("stored_notification_data", null, selection, selectionArgs, null, null, "date DESC");
            }

            if (cursor != null) {
                int titleIndex = cursor.getColumnIndex("title");
                int descriptionIndex = cursor.getColumnIndex("description");
                int dateIndex = cursor.getColumnIndex("date");
                int smallImageUrlIndex = cursor.getColumnIndex("small_image_url");
                int bigImageUrlIndex = cursor.getColumnIndex("big_image_url");
                int uniqueIdIndex = cursor.getColumnIndex("unique_id");

                if (cursor.moveToFirst()) {
                    do {
                        if (titleIndex >= 0 && descriptionIndex >= 0 && dateIndex >= 0 &&
                                smallImageUrlIndex >= 0 && bigImageUrlIndex >= 0 && uniqueIdIndex >= 0) {
                            String title = cursor.getString(titleIndex);
                            String description = cursor.getString(descriptionIndex);
                            String date = cursor.getString(dateIndex);
                            String smallImageUrl = cursor.getString(smallImageUrlIndex);
                            String bigImageUrl = cursor.getString(bigImageUrlIndex);
                            String uniqueId = cursor.getString(uniqueIdIndex);

                            String formattedDate = formatDateToBangla(date);
                            items.add(new Notice(title, description, date, formattedDate, smallImageUrl, bigImageUrl, uniqueId));
                        }
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
            db.close();
            return items;
        }

        @Override
        protected void onPostExecute(List<Notice> items) {
            noticeList.clear();
            noticeList.addAll(items);
            adapter.notifyDataSetChanged();
        }
    }

    private String formatDateToBangla(String date) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("d MMMM yyyy", new Locale("bn", "BD"));

        try {
            Date parsedDate = inputFormat.parse(date);
            if (parsedDate != null) {
                return outputFormat.format(parsedDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    private boolean isWithinLast4Days(String originalDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            Date noticeDate = sdf.parse(originalDate);
            Date currentDate = new Date();

            if (noticeDate == null) return false;

            // Calculate absolute difference (works for both past and future dates)
            long diffMillis = Math.abs(currentDate.getTime() - noticeDate.getTime());
            long daysDiff = TimeUnit.MILLISECONDS.toDays(diffMillis);

            return daysDiff <= 4;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String makePlaceholders(int len) {
        if (len <= 0) return "";
        StringBuilder sb = new StringBuilder(len * 2 - 1);
        sb.append("?");
        for (int i = 1; i < len; i++) {
            sb.append(",?");
        }
        return sb.toString();
    }

    class Notice {
        String title, description, originalDate, formattedDate, smallImageUrl, bigImageUrl, uniqueId;

        public Notice(String title, String description, String originalDate, String formattedDate,
                      String smallImageUrl, String bigImageUrl, String uniqueId) {
            this.title = title;
            this.description = description;
            this.originalDate = originalDate;
            this.formattedDate = formattedDate;
            this.smallImageUrl = smallImageUrl;
            this.bigImageUrl = bigImageUrl;
            this.uniqueId = uniqueId;
        }
    }

    class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder> {
        private List<Notice> noticeList;

        public NoticeAdapter(List<Notice> noticeList) {
            this.noticeList = noticeList;
        }

        @NonNull
        @Override
        public NoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notice_list, parent, false);
            return new NoticeViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull NoticeViewHolder holder, int position) {
            Notice notice = noticeList.get(position);

            holder.noticeTitle.setText(notice.title);
            holder.noticeDescription.setText(Html.fromHtml(notice.description).toString());
            holder.noticeDate.setText(notice.formattedDate);

            holder.progressBar.setVisibility(View.VISIBLE);
            if (notice.smallImageUrl != null && !notice.smallImageUrl.isEmpty()) {
                Picasso.get().load(notice.smallImageUrl).into(holder.noticePicture, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        holder.progressBar.setVisibility(View.GONE);
                    }
                });
            } else {
                holder.progressBar.setVisibility(View.GONE);
            }

            String noticeKey = getNoticeKey(notice.title);
            boolean isClicked = clickedNoticesPrefs.getBoolean(noticeKey, false);

            if (isClicked) {
                holder.clickedIndicator.setBackgroundColor(Color.WHITE);
                holder.latestTag.setVisibility(View.GONE);
            } else {
                holder.clickedIndicator.setBackgroundColor(getResources().getColor(R.color.light_background));
                holder.latestTag.setVisibility(isWithinLast4Days(notice.originalDate) ? View.VISIBLE : View.GONE);
            }

            holder.itemView.setOnClickListener(v -> {
                SharedPreferences.Editor editor = clickedNoticesPrefs.edit();
                editor.putBoolean(noticeKey, true);
                editor.apply();

                holder.clickedIndicator.setBackgroundColor(Color.WHITE);
                holder.latestTag.setVisibility(View.GONE);

                Intent intent = new Intent(v.getContext(), Activity_Notice_Details.class);
                intent.putExtra("title", notice.title);
                intent.putExtra("date", notice.formattedDate);
                intent.putExtra("description", notice.description);
                intent.putExtra("imageUrl", notice.bigImageUrl);
                v.getContext().startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return noticeList.size();
        }

        class NoticeViewHolder extends RecyclerView.ViewHolder {
            TextView noticeTitle, noticeDescription, noticeDate, latestTag;
            ImageView noticePicture;
            ProgressBar progressBar;
            RelativeLayout clickedIndicator;

            public NoticeViewHolder(@NonNull View itemView) {
                super(itemView);
                noticeTitle = itemView.findViewById(R.id.notice_title);
                noticeDescription = itemView.findViewById(R.id.notice_description);
                noticeDate = itemView.findViewById(R.id.date);
                latestTag = itemView.findViewById(R.id.latest_tag);
                noticePicture = itemView.findViewById(R.id.notice_picture);
                progressBar = itemView.findViewById(R.id.progressBar);
                clickedIndicator = itemView.findViewById(R.id.clicked_indicator);
            }
        }
    }
}