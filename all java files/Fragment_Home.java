package com.example.addmission_update_project;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Fragment_Home extends Fragment {

    private GridView gridView1, gridView2;
    private ViewPager sliderViewPager;
    private LinearLayout layoutDots;
    private ArrayList<String> sliderImages = new ArrayList<>();
    private Timer sliderTimer;
    private int currentPage = 0;
    private static final long SLIDER_DELAY = 2000;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        gridView1 = view.findViewById(R.id.gridView1);
        gridView2 = view.findViewById(R.id.gridView2);
        sliderViewPager = view.findViewById(R.id.sliderViewPager);
        layoutDots = view.findViewById(R.id.layoutDots);

        // Initialize grid views
        initializeGridViews(view);

        // Load slider images
        loadSliderImages();

        return view;
    }

    private void initializeGridViews(View view) {
        // GridView 1 Data
        ArrayList<GridItem> gridItems1 = new ArrayList<>();
        gridItems1.add(new GridItem(R.drawable.icon_university, "বিশ্ববিদ্যালয় সমূহ"));
        gridItems1.add(new GridItem(R.drawable.icon_subject_review, "সাবজেক্ট সিলেকশন"));
        gridItems1.add(new GridItem(R.drawable.icon_seat_number, "আসন সংখ্যা"));
        gridItems1.add(new GridItem(R.drawable.icon_website_list, "ওয়েবসাইট"));
        gridItems1.add(new GridItem(R.drawable.icon_application_capability, "আবেদনের যোগ্যতা যাচাই"));

        // GridView 2 Data
        ArrayList<GridItem> gridItems2 = new ArrayList<>();
        gridItems2.add(new GridItem(R.drawable.icon_admit_card, "এডমিট কার্ড"));
        gridItems2.add(new GridItem(R.drawable.icon_seat_plan, "সিট প্ল্যান"));
        gridItems2.add(new GridItem(R.drawable.icon_result, "রেজাল্ট"));
        gridItems2.add(new GridItem(R.drawable.icon_waiting_list, "ওয়েটিং লিস্ট"));
        gridItems2.add(new GridItem(R.drawable.icon_question_answer, "প্রশ্ন সমাধান"));
        gridItems2.add(new GridItem(R.drawable.icon_cut_mark, "কাট মার্ক"));

        // Set adapters
        gridView1.setAdapter(new GridAdapter(getActivity(), gridItems1));
        gridView2.setAdapter(new GridAdapter(getActivity(), gridItems2));

        // Handle GridView1 clicks
        gridView1.setOnItemClickListener((parent, view1, position, id) -> {
            Intent intent;
            switch (position) {
                case 0: // University List
                    intent = new Intent(getActivity(), Activity_University_Types_Selector.class);
                    intent.putExtra("grid_clicked", "university_details");
                    startActivity(intent);
                    break;
                case 1: // Subject Selection (Second item)
                    intent = new Intent(getActivity(), Activity_Faculty_selection.class);
                    startActivity(intent);
                    break;
                case 2: // Seat Number
                    intent = new Intent(getActivity(), Activity_University_Types_Selector.class);
                    intent.putExtra("grid_clicked", "seat_number");
                    startActivity(intent);
                    break;
                case 3: // Website List
                    intent = new Intent(getActivity(), Activity_University_Types_Selector.class);
                    intent.putExtra("grid_clicked", "website_list");
                    startActivity(intent);
                    break;
                case 4: // Applying Capability Check
                    intent = new Intent(getActivity(), Activity_Applying_Capability_Check.class);
                    startActivity(intent);
                    break;
                default:
                    // Do nothing for other positions
                    break;
            }
        });

        // Handle GridView2 clicks (unchanged)
        gridView2.setOnItemClickListener((parent, view1, position, id) -> {
            String[] tableNames = {
                    "admit_card_data",
                    "seat_plan_data",
                    "result_data",
                    "waiting_list_data",
                    "question_answer_data",
                    "cut_mark_data"
            };

            String banglaTitle = gridItems2.get(position).getTitle();
            String tableType = tableNames[position];

            Intent intent = new Intent(getActivity(), Activity_Admission_info_text_title_date.class);
            intent.putExtra("bangla_title", banglaTitle);
            intent.putExtra("table_type", tableType);
            startActivity(intent);
        });
    }

    private void loadSliderImages() {
        String domain = getResources().getString(R.string.domain_name);
        String url = domain + "/app_projects/get_image_slider_url_data.php";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            if (response.length() > 0) {
                                JSONObject sliderData = response.getJSONObject(0);
                                sliderImages.clear();

                                // Check for all possible URL fields (url_1 to url_5)
                                for (int i = 1; i <= 5; i++) {
                                    String urlKey = "url_" + i;
                                    if (sliderData.has(urlKey) && !sliderData.isNull(urlKey)) {
                                        String imageUrl = sliderData.getString(urlKey);
                                        if (!imageUrl.isEmpty()) {
                                            sliderImages.add(imageUrl);
                                        }
                                    }
                                }

                                if (!sliderImages.isEmpty()) {
                                    setupSlider();
                                } else {
                                    // Hide slider if no images found
                                    sliderViewPager.setVisibility(View.GONE);
                                    layoutDots.setVisibility(View.GONE);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Hide slider on error
                        sliderViewPager.setVisibility(View.GONE);
                        layoutDots.setVisibility(View.GONE);
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(jsonArrayRequest);
    }

    private void setupSlider() {
        SliderAdapter sliderAdapter = new SliderAdapter(getActivity(), sliderImages);
        sliderViewPager.setAdapter(sliderAdapter);

        // Add dots indicator
        addDotsIndicator(sliderImages.size());

        // Auto slide
        startAutoSlider(sliderImages.size());

        // ViewPager change listener
        sliderViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                updateDots(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void addDotsIndicator(int count) {
        ImageView[] dots = new ImageView[count];
        layoutDots.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(getActivity());
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.dot_non_active));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(4, 0, 4, 0);
            layoutDots.addView(dots[i], params);
        }

        if (dots.length > 0) {
            dots[0].setImageDrawable(getResources().getDrawable(R.drawable.dot_active));
        }
    }

    private void updateDots(int currentPosition) {
        for (int i = 0; i < layoutDots.getChildCount(); i++) {
            ImageView dot = (ImageView) layoutDots.getChildAt(i);
            if (i == currentPosition) {
                dot.setImageDrawable(getResources().getDrawable(R.drawable.dot_active));
            } else {
                dot.setImageDrawable(getResources().getDrawable(R.drawable.dot_non_active));
            }
        }
    }

    private void startAutoSlider(final int count) {
        if (count <= 1) return; // No auto-slide if only one image

        sliderTimer = new Timer();
        sliderTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (currentPage == count) {
                                currentPage = 0;
                            }
                            sliderViewPager.setCurrentItem(currentPage++, true);
                        }
                    });
                }
            }
        }, SLIDER_DELAY, SLIDER_DELAY);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (sliderTimer != null) {
            sliderTimer.cancel();
        }
    }

    // Slider Adapter
    private static class SliderAdapter extends androidx.viewpager.widget.PagerAdapter {
        private Context context;
        private ArrayList<String> images;
        private LayoutInflater layoutInflater;

        public SliderAdapter(Context context, ArrayList<String> images) {
            this.context = context;
            this.images = images;
            this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = layoutInflater.inflate(R.layout.item_slider, container, false);
            ImageView imageView = itemView.findViewById(R.id.sliderImage);

            // Load image with Picasso
            Picasso.get()
                    .load(images.get(position))
                    .fit()
                    .centerCrop()
                    .into(imageView);

            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }




    // Nested GridItem class
    public static class GridItem {
        private int iconResId;
        private String title;

        public GridItem(int iconResId, String title) {
            this.iconResId = iconResId;
            this.title = title;
        }

        public int getIconResId() {
            return iconResId;
        }

        public String getTitle() {
            return title;
        }
    }

    // Nested GridAdapter class
    public static class GridAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<GridItem> gridItems;

        public GridAdapter(Context context, ArrayList<GridItem> gridItems) {
            this.context = context;
            this.gridItems = gridItems;
        }

        @Override
        public int getCount() {
            return gridItems.size();
        }

        @Override
        public Object getItem(int position) {
            return gridItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_home_grid, parent, false);
            }

            // Get the current item
            GridItem item = gridItems.get(position);

            // Bind data to views
            ImageView icon = convertView.findViewById(R.id.gridItemIcon);
            TextView title = convertView.findViewById(R.id.gridItemTitle);

            icon.setImageResource(item.getIconResId());
            title.setText(item.getTitle());

            return convertView;
        }









    }




















}