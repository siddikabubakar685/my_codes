package com.example.addmission_update_project;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class Countdown_Item_and_Adapter {

    // Model class for countdown items
    public static class CountdownItem {
        private String title;
        private String targetDate;
        private String type; // "apply", "exam", or "result"
        private Date parsedDate;

        public CountdownItem(String title, String targetDate, String type) {
            this.title = title;
            this.targetDate = targetDate;
            this.type = type;

            // Parse the date immediately
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            format.setTimeZone(TimeZone.getTimeZone("GMT+6"));
            try {
                this.parsedDate = format.parse(targetDate);
            } catch (ParseException e) {
                e.printStackTrace();
                this.parsedDate = new Date(); // Fallback to current date
            }
        }

        public String getTitle() {
            return title;
        }

        public String getTargetDate() {
            return targetDate;
        }

        public String getType() {
            return type;
        }

        public Date getParsedDate() {
            return parsedDate;
        }

        // Method to check if the target date is within 3 days
        public boolean isWithinThreeDays() {
            if (parsedDate == null) return false;
            Date now = new Date();
            long diff = parsedDate.getTime() - now.getTime();
            long diffDays = TimeUnit.MILLISECONDS.toDays(diff);
            return diffDays >= 0 && diffDays <= 3;
        }

        // Rest of your existing methods...
        public String getFormattedDate() {
            SimpleDateFormat outputFormat = new SimpleDateFormat("d MMMM yyyy", new Locale("bn", "BD"));
            String formattedDate = outputFormat.format(parsedDate);
            switch (type) {
                case "apply":
                    return "আবেদনের শেষ তারিখ : " + formattedDate;
                case "exam":
                    return "পরীক্ষার তারিখ : " + formattedDate;
                case "result":
                    return "রেজাল্ট প্রকাশ : " + formattedDate;
            }
            return targetDate;
        }

        public SpannableString getTimeLeft() {
            Date now = new Date();
            long diff = parsedDate.getTime() - now.getTime();

            if (diff > 0) {
                long days = TimeUnit.MILLISECONDS.toDays(diff);
                if (days > 0) {
                    String timeLeftText;
                    switch (type) {
                        case "apply":
                            timeLeftText = "আবেদনের সময় বাকি " + toBanglaDigits(days) + " দিন";
                            break;
                        case "exam":
                            timeLeftText = "পরীক্ষার সময় বাকি " + toBanglaDigits(days) + " দিন";
                            break;
                        case "result":
                            timeLeftText = "রেজাল্ট প্রকাশিত হতে সময় বাকি " + toBanglaDigits(days) + " দিন";
                            break;
                        default:
                            timeLeftText = "";
                    }
                    return makeBoldDigits(timeLeftText, toBanglaDigits(days));
                } else {
                    long hours = TimeUnit.MILLISECONDS.toHours(diff);
                    String timeLeftText;
                    switch (type) {
                        case "apply":
                            timeLeftText = "আবেদনের সময় বাকি " + toBanglaDigits(hours) + " ঘণ্টা";
                            break;
                        case "exam":
                            timeLeftText = "পরীক্ষার সময় বাকি " + toBanglaDigits(hours) + " ঘণ্টা";
                            break;
                        case "result":
                            timeLeftText = "রেজাল্ট প্রকাশিত হতে সময় বাকি " + toBanglaDigits(hours) + " ঘণ্টা";
                            break;
                        default:
                            timeLeftText = "";
                    }
                    return makeBoldDigits(timeLeftText, toBanglaDigits(hours));
                }
            } else {
                String timeUpText;
                switch (type) {
                    case "apply":
                        timeUpText = "আবেদনের সময় শেষ";
                        break;
                    case "exam":
                        timeUpText = "পরীক্ষার সময় শেষ";
                        break;
                    case "result":
                        timeUpText = "রেজাল্ট প্রকাশিত হয়েছে";
                        break;
                    default:
                        timeUpText = "";
                }
                SpannableString spannable = new SpannableString(timeUpText);
                spannable.setSpan(new android.text.style.ForegroundColorSpan(Color.RED), 0, timeUpText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                return spannable;
            }
        }

        private String toBanglaDigits(long number) {
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("bn", "BD"));
            DecimalFormat df = new DecimalFormat("0", symbols);
            return df.format(number);
        }

        private SpannableString makeBoldDigits(String text, String digits) {
            SpannableString spannable = new SpannableString(text);
            int start = text.indexOf(digits);
            int end = start + digits.length();
            if (start >= 0) {
                spannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannable.setSpan(new AbsoluteSizeSpan(16, true), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            return spannable;
        }
    }

    // Adapter for RecyclerView
    public static class CountdownAdapter extends RecyclerView.Adapter<CountdownAdapter.CountdownViewHolder> {
        private List<CountdownItem> countdownItems;

        public CountdownAdapter(List<CountdownItem> countdownItems) {
            this.countdownItems = countdownItems;
        }

        @NonNull
        @Override
        public CountdownViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_countdown_list, parent, false);
            return new CountdownViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CountdownViewHolder holder, int position) {
            CountdownItem item = countdownItems.get(position);
            holder.countdownTitle.setText(item.getTitle());
            holder.targetDate.setText(item.getFormattedDate());

            // Set time left text with bold digits
            SpannableString timeLeft = item.getTimeLeft();
            holder.timeLeft.setText(timeLeft);

            // Change text color to red if time is up
            if (timeLeft.toString().contains("শেষ") || timeLeft.toString().contains("হয়েছে")) {
                holder.timeLeft.setTextColor(Color.RED);
            } else {
                holder.timeLeft.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.app_theme_color_second));
            }

            // Show Alert! tag if target date is within 3 days
            holder.alertTag.setVisibility(item.isWithinThreeDays() ? View.VISIBLE : View.GONE);
        }

        @Override
        public int getItemCount() {
            return countdownItems.size();
        }

        public static class CountdownViewHolder extends RecyclerView.ViewHolder {
            TextView countdownTitle;
            TextView targetDate;
            TextView timeLeft;
            TextView alertTag;

            public CountdownViewHolder(@NonNull View itemView) {
                super(itemView);
                countdownTitle = itemView.findViewById(R.id.countdown_title);
                targetDate = itemView.findViewById(R.id.date);
                timeLeft = itemView.findViewById(R.id.time_left);
                alertTag = itemView.findViewById(R.id.alert_tag);
            }
        }
    }
}