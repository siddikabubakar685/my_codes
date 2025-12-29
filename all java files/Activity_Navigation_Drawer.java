package com.example.addmission_update_project;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Activity_Navigation_Drawer extends LinearLayout {

    private LinearLayout expandableSectionsContainer;
    private Database_Favourite_Unique_Id dbHelper;
    private AppCompatButton getProButton;
    private SharedPreferences sharedPreferences;
    private boolean isPremiumUser = false;
    private TextView sampleText;
    private boolean isTextExpanded = false;

    public Activity_Navigation_Drawer(Context context) {
        super(context);
        init();
    }

    public Activity_Navigation_Drawer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Activity_Navigation_Drawer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.activity_navigation_drawer, this, true);

        // Initialize views
        expandableSectionsContainer = findViewById(R.id.expandable_sections_container);
        getProButton = findViewById(R.id.get_pro_button);
        sampleText = findViewById(R.id.sample_text);
        dbHelper = new Database_Favourite_Unique_Id(getContext());

        // Initialize SharedPreferences
        sharedPreferences = getContext().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);

        // Check if the user is already a premium user
        isPremiumUser = sharedPreferences.getBoolean("isPremiumUser", false);

        // Update the button text based on premium status
        if (isPremiumUser) {
            getProButton.setText("Premium User");
        }

        // Set click listener for the "Get Pro" button
        getProButton.setOnClickListener(v -> enablePremiumFeatures());

        // Set click listeners for facilities
        findViewById(R.id.settings_layout).setOnClickListener(v -> showToast("সেটিংস ক্লিক করা হয়েছে"));
        findViewById(R.id.favorites_layout).setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, Activity_favourite_List.class);
            context.startActivity(intent);
        });

        findViewById(R.id.privacy_policy_layout).setOnClickListener(v -> {
            Context context = v.getContext();
            String url = context.getString(R.string.privacy_policy);
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.startsWith("http") ? url : "http://" + url));
            context.startActivity(intent);
        });

        findViewById(R.id.rate_app_layout).setOnClickListener(v -> {
            Context context = v.getContext();
            String packageName = "com.example.addmission_update_project";
            try {
                // Try to open in Play Store app
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
            } catch (android.content.ActivityNotFoundException e) {
                // If Play Store app not available, open in browser
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
            }
        });

        findViewById(R.id.share_app_layout).setOnClickListener(v -> {
            Context context = v.getContext();
            String packageName = "com.example.addmission_update_project";
            String shareText = "Referral code- 88493 । সকল পাবলিক বিশ্ববিদ্যালয়ের ভর্তি সংক্রান্ত তথ্য একসাথে পাবে এই এপে। \n\ndownload now \n\uD83D\uDD16Admission\u200E Update App\uD83D\uDD16-" + "\n\nhttps://play.google.com/store/apps/details?id=" + packageName;

            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);

            Intent shareIntent = Intent.createChooser(sendIntent, "আপনার বন্ধুদের সাথে শেয়ার করুন");
            context.startActivity(shareIntent);
        });

        findViewById(R.id.join_community_layout).setOnClickListener(v -> showBottomSheetDialog());


        // Set click listener for sample text to expand/collapse
        sampleText.setOnClickListener(v -> toggleTextExpansion());

        // Set up expandable sections
        setupExpandableSections();
    }

    private void enablePremiumFeatures() {
        // Change the button text to "Premium User"
        getProButton.setText("Premium User");

        // Save the premium user status in SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isPremiumUser", true);
        editor.apply();

        // Enable premium features
        isPremiumUser = true;
        showToast("আপনি এখন একজন প্রিমিয়াম ইউজার!");
    }

    private void setupExpandableSections() {
        // Get section titles from Activity_Expandable_Section_Text
        String[] sectionTitles = Activity_Expandable_Section_Text.getSectionTitles();

        // Get user's favorite unique IDs
        Set<String> favoriteUniqueIds = dbHelper.getFavoriteUniqueIds();

        for (String title : sectionTitles) {
            // Inflate the expandable section layout
            View sectionView = LayoutInflater.from(getContext()).inflate(R.layout.item_expandable_section, expandableSectionsContainer, false);

            // Initialize views
            TextView header = sectionView.findViewById(R.id.expandable_header);
            ImageView arrow = sectionView.findViewById(R.id.expandable_arrow);
            LinearLayout content = sectionView.findViewById(R.id.expandable_content);

            // Set section title
            header.setText(title);

            // Add items to the expandable section
            Map<String, String> items = Activity_Expandable_Section_Text.getSectionItems(title);
            for (Map.Entry<String, String> entry : items.entrySet()) {
                String uniqueId = entry.getKey(); // Unique ID (e.g., "buet50")
                String itemText = entry.getValue(); // Item text (e.g., "বাংলাদেশ প্রকৌশল বিশ্ববিদ্যালয় (BUET)")

                CheckBox checkBox = new CheckBox(getContext());
                checkBox.setText(itemText);
                checkBox.setTextSize(12);
                checkBox.setTextColor(getResources().getColor(android.R.color.black));
                checkBox.setTag(uniqueId); // Set unique ID as a tag

                // Set checkbox state based on user's favorites
                if (favoriteUniqueIds.contains(uniqueId)) {
                    checkBox.setChecked(true);
                }

                // Handle checkbox state changes (only for premium users)
                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isPremiumUser) {
                        if (isChecked) {
                            dbHelper.addFavoriteUniqueId(uniqueId); // Add to favorites
                        } else {
                            dbHelper.removeFavoriteUniqueId(uniqueId); // Remove from favorites
                        }
                    } else {
                        // Show a message that premium features are required
                        showToast("প্রিমিয়াম ইউজার হতে হবে ফ্যাভারিট সেভ করার জন্য");
                        checkBox.setChecked(false); // Uncheck the checkbox
                    }
                });

                content.addView(checkBox);
            }

            // Set click listener for the section header to expand/collapse
            sectionView.setOnClickListener(v -> toggleExpandableContent(content, arrow));

            // Add the section to the container
            expandableSectionsContainer.addView(sectionView);
        }
    }

    private void toggleExpandableContent(LinearLayout expandableContent, ImageView expandableArrow) {
        if (expandableContent.getVisibility() == View.VISIBLE) {
            // Collapse the section
            Animation fadeOut = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
            expandableContent.startAnimation(fadeOut);
            expandableContent.setVisibility(View.GONE);
            rotateArrow(expandableArrow, 180, 0); // Rotate arrow back to original position
        } else {
            // Expand the section
            Animation fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
            expandableContent.startAnimation(fadeIn);
            expandableContent.setVisibility(View.VISIBLE);
            rotateArrow(expandableArrow, 0, 180); // Rotate arrow 180 degrees
        }
    }

    private void rotateArrow(ImageView arrow, float fromDegrees, float toDegrees) {
        RotateAnimation rotateAnimation = new RotateAnimation(fromDegrees, toDegrees, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(300); // Animation duration in milliseconds
        rotateAnimation.setFillAfter(true); // Maintain the final state after animation
        arrow.startAnimation(rotateAnimation);
    }

    private void toggleTextExpansion() {
        if (isTextExpanded) {
            sampleText.setMaxLines(2);
            sampleText.setEllipsize(android.text.TextUtils.TruncateAt.END);
        } else {
            sampleText.setMaxLines(Integer.MAX_VALUE);
            sampleText.setEllipsize(null);
        }
        isTextExpanded = !isTextExpanded;
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
    //++++++++++++++++++++++++++++++++++++++++++++++

    private void showBottomSheetDialog() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_layout, null);
        final com.google.android.material.bottomsheet.BottomSheetDialog dialog = new com.google.android.material.bottomsheet.BottomSheetDialog(getContext());
        dialog.setContentView(view);

        LinearLayout linkContainer = view.findViewById(R.id.link_container);

        // Prepare list of items
        List<LinkItem> items = new ArrayList<>();
        items.add(new LinkItem("Join Facebook Group", "https://www.facebook.com/groups/yourgroup", R.drawable.icon_facebook));
        items.add(new LinkItem("Join WhatsApp Group", "https://chat.whatsapp.com/yourlink", R.drawable.icon_whatsapp));
        items.add(new LinkItem("Visit Our Website", "https://www.yourwebsite.com", R.drawable.icon_website));


        for (int i = 0; i < items.size(); i++) {
            LinkItem item = items.get(i);
            View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_bottom_sheet, linkContainer, false);

            ImageView icon = itemView.findViewById(R.id.icon);
            TextView title = itemView.findViewById(R.id.title);
            View divider = itemView.findViewById(R.id.divider);

            icon.setImageResource(item.iconResId);
            title.setText(item.title);
            title.setTextColor(Color.DKGRAY);

            if (i == items.size() - 1) {
                // Last item → Hide divider
                divider.setVisibility(View.GONE);
            }

            itemView.setOnClickListener(v -> {
                openUrl(item.url);
                dialog.dismiss();
            });

            linkContainer.addView(itemView);
        }
        dialog.show();
    }

    private void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
    }

    // LinkItem class
    private static class LinkItem {
        String title;
        String url;
        int iconResId;

        LinkItem(String title, String url, int iconResId) {
            this.title = title;
            this.url = url;
            this.iconResId = iconResId;
        }
    }


}