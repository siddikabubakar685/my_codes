package com.example.addmission_update_project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Activity_Applying_Capability_Check extends AppCompatActivity {

    private Spinner sscYearSpinner, hscYearSpinner, sscGroupSpinner, hscGroupSpinner;
    private EditText sscGpaInput, hscGpaInput;
    private CheckBox secondTimeCheckBox;
    private Button submitButton;
    private Database_Applying_Capability dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applying_eligibility_check);

        dbHelper = new Database_Applying_Capability(this);

        // Initialize views
        sscYearSpinner = findViewById(R.id.sscYearSpinner);
        hscYearSpinner = findViewById(R.id.hscYearSpinner);
        sscGroupSpinner = findViewById(R.id.sscGroupSpinner);
        hscGroupSpinner = findViewById(R.id.hscGroupSpinner);
        sscGpaInput = findViewById(R.id.sscGpaInput);
        hscGpaInput = findViewById(R.id.hscGpaInput);
        secondTimeCheckBox = findViewById(R.id.secondTimeCheckBox);
        submitButton = findViewById(R.id.submitButton);

        findViewById(R.id.back).setOnClickListener(v -> finish());
        // Setup year spinners
        setupYearSpinners();

        // Setup group spinners
        setupGroupSpinners();

        submitButton.setOnClickListener(v -> validateAndSubmit());
    }

    private void setupYearSpinners() {
        List<String> years = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int year = 2020; year <= currentYear; year++) {
            years.add(String.valueOf(year));
        }

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sscYearSpinner.setAdapter(yearAdapter);
        hscYearSpinner.setAdapter(yearAdapter);

        // Set default to current year
        sscYearSpinner.setSelection(years.size() - 1);
        hscYearSpinner.setSelection(years.size() - 1);
    }

    private void setupGroupSpinners() {
        String[] groups = {"Science", "Arts", "Commerce"};

        ArrayAdapter<String> groupAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, groups);
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sscGroupSpinner.setAdapter(groupAdapter);
        hscGroupSpinner.setAdapter(groupAdapter);
    }

    private void validateAndSubmit() {
        try {
            // Get input values
            float sscGpa = Float.parseFloat(sscGpaInput.getText().toString());
            float hscGpa = Float.parseFloat(hscGpaInput.getText().toString());
            boolean secondTimeOnly = secondTimeCheckBox.isChecked();

            float totalGpa = sscGpa + hscGpa;

            // Validate GPA range
            if (sscGpa < 0 || sscGpa > 5 || hscGpa < 0 || hscGpa > 5) {
                Toast.makeText(this, "GPA must be between 0 and 5", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get all universities from database
            List<Database_Applying_Capability.ApplyingCapabilityModel> allUniversities =
                    dbHelper.getAllData();

            // Filter based on criteria
            ArrayList<String> eligibleUniversityNames = new ArrayList<>();

            for (Database_Applying_Capability.ApplyingCapabilityModel university : allUniversities) {
                float universityTotalGpa = (float)(university.getSscGpa() + university.getHscGpa());

                if (totalGpa >= universityTotalGpa &&
                        (!secondTimeOnly || university.getSecondTimeOpportunity().equals("Yes"))) {

                    eligibleUniversityNames.add(university.getUniversityName());
                }
            }

            // Show results
            if (eligibleUniversityNames.isEmpty()) {
                Toast.makeText(this, "No universities match your criteria", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(this, Activity_Eligible_University_List.class);
                intent.putStringArrayListExtra("universityNames", eligibleUniversityNames);
                startActivity(intent);
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid GPA values", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "An error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}