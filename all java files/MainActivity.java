package com.example.addmission_update_project;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    Database_University_info fetchUniversityData;
    private DrawerLayout drawerLayout;
    private ImageView menuIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Database_University_info universityFetcher = new Database_University_info(this);
        universityFetcher.fetchAndStoreUniversityData();

        //Initialize admission info
        Database_admission_info admissionDbHelper = new Database_admission_info(this);
        admissionDbHelper.fetchAndStoreData();

        //Initialize applying capability
        Database_Applying_Capability applyingDbHelper = new Database_Applying_Capability(this);
        applyingDbHelper.fetchAndStoreData();


        Database_faculty_and_subject dbHelper = new Database_faculty_and_subject(this);
        dbHelper.fetchAndStoreData();


        // Initialize views
        drawerLayout = findViewById(R.id.drawerLayout);
        menuIcon = findViewById(R.id.menuIcon);

        // Load the default fragment (Fragment_Home)
        loadFragment(new Fragment_Home());

        // Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                loadFragment(new Fragment_Home());
                return true;
            } else if (itemId == R.id.nav_countdown) {
                loadFragment(new Fragment_Countdown());
                return true;
            } else if (itemId == R.id.nav_notice) {
                loadFragment(new Fragment_Notice());
                return true;
            }
            return false;
        });

        // Set click listener for menu icon
        menuIcon.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(findViewById(R.id.navigationDrawer))) {
                drawerLayout.closeDrawer(findViewById(R.id.navigationDrawer));
            } else {
                drawerLayout.openDrawer(findViewById(R.id.navigationDrawer));
            }
        });
    }

    // Load Fragment into app_window
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.app_window, fragment);
        transaction.commit();
    }
}