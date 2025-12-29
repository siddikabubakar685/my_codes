package com.example.addmission_update_project;

import java.util.HashMap;
import java.util.Map;

public class Activity_University_listing {

    // Method to get all university types and their corresponding universities with unique IDs
    public static Map<String, Map<String, String>> getAllUniversityTypes() {
        Map<String, Map<String, String>> universityTypes = new HashMap<>();

        // Section 1: Engineering Universities
        Map<String, String> engineeringUniversities = new HashMap<>();
        engineeringUniversities.put("buet50", "বাংলাদেশ প্রকৌশল বিশ্ববিদ্যালয় (BUET)");
        engineeringUniversities.put("ruet003", "রাজশাহী প্রকৌশল ও প্রযুক্তি বিশ্ববিদ্যালয় (RUET)");
        engineeringUniversities.put("cuet007", "চট্টগ্রাম প্রকৌশল ও প্রযুক্তি বিশ্ববিদ্যালয় (CUET)");
        engineeringUniversities.put("kuet009", "খুলনা প্রকৌশল ও প্রযুক্তি বিশ্ববিদ্যালয় (KUET)");
        universityTypes.put("প্রকৌশল বিশ্ববিদ্যালয়", engineeringUniversities);

        // Section 2: Science and Technology Universities
        Map<String, String> scienceAndTechUniversities = new HashMap<>();
        scienceAndTechUniversities.put("sust100", "শাহজালাল বিজ্ঞান ও প্রযুক্তি বিশ্ববিদ্যালয় (SUST)");
        scienceAndTechUniversities.put("ku200", "খুলনা বিশ্ববিদ্যালয় (KU)");
        scienceAndTechUniversities.put("ju300", "জাহাঙ্গীরনগর বিশ্ববিদ্যালয় (JU)");
        scienceAndTechUniversities.put("ru400", "রাজশাহী বিশ্ববিদ্যালয় (RU)");
        universityTypes.put("বিজ্ঞান ও প্রযুক্তি বিশ্ববিদ্যালয়", scienceAndTechUniversities);

        // Section 3: Medical and Dental Units
        Map<String, String> medicalAndDentalUnits = new HashMap<>();
        medicalAndDentalUnits.put("bsmmu500", "বঙ্গবন্ধু শেখ মুজিব মেডিকেল বিশ্ববিদ্যালয় (BSMMU)");
        medicalAndDentalUnits.put("dmc600", "ঢাকা মেডিকেল কলেজ (DMC)");
        medicalAndDentalUnits.put("ssmc700", "স্যার সলিমুল্লাহ মেডিকেল কলেজ (SSMC)");
        medicalAndDentalUnits.put("cmc800", "চট্টগ্রাম মেডিকেল কলেজ (CMC)");
        universityTypes.put("মেডিকেল ও ডেন্টাল ইউনিট", medicalAndDentalUnits);


        // Section 4: Agricultural Universities
        Map<String, String> agriculturalUniversities = new HashMap<>();
        agriculturalUniversities.put("bau900", "বাংলাদেশ কৃষি বিশ্ববিদ্যালয় (BAU)");
        agriculturalUniversities.put("sau910", "শেরেবাংলা কৃষি বিশ্ববিদ্যালয় (SAU)");
        agriculturalUniversities.put("hau920", "হাজী মোহাম্মদ দানেশ বিজ্ঞান ও প্রযুক্তি বিশ্ববিদ্যালয় (HSTU)");
        agriculturalUniversities.put("pstu930", "পটুয়াখালী বিজ্ঞান ও প্রযুক্তি বিশ্ববিদ্যালয় (PSTU)");
        universityTypes.put("কৃষি বিশ্ববিদ্যালয়", agriculturalUniversities);


        // Add more sections here as needed

        return universityTypes;
    }
}