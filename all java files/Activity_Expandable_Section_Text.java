package com.example.addmission_update_project;

import java.util.Map;

public class Activity_Expandable_Section_Text {

    // Method to get all expandable sections with their unique IDs
    public static Map<String, Map<String, String>> getExpandableSections() {
        return Activity_University_listing.getAllUniversityTypes();
    }

    // Method to get all section titles
    public static String[] getSectionTitles() {
        return Activity_University_listing.getAllUniversityTypes().keySet().toArray(new String[0]);
    }

    // Method to get items (with unique IDs) for a specific section
    public static Map<String, String> getSectionItems(String sectionTitle) {
        return Activity_University_listing.getAllUniversityTypes().get(sectionTitle);
    }

    // Method to get the unique ID for a specific item
    public static String getUniqueId(String sectionTitle, String itemText) {
        Map<String, String> items = Activity_University_listing.getAllUniversityTypes().get(sectionTitle);
        if (items != null) {
            for (Map.Entry<String, String> entry : items.entrySet()) {
                if (entry.getValue().equals(itemText)) {
                    return entry.getKey(); // Return the unique ID
                }
            }
        }
        return null; // If no match is found
    }
}