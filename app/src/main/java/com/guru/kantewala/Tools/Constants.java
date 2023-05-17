package com.guru.kantewala.Tools;

import java.util.Arrays;
import java.util.List;

public class Constants {
    private static List<String> indianStates;
    private static List<String> generateIndianStates(){
        indianStates =  Arrays.asList(
                "Select State",
                "Andhra Pradesh",
                "Arunachal Pradesh",
                "Assam",
                "Bihar",
                "Chhattisgarh",
                "Goa",
                "Gujarat",
                "Haryana",
                "Himachal Pradesh",
                "Jharkhand",
                "Karnataka",
                "Kerala",
                "Madhya Pradesh",
                "Maharashtra",
                "Manipur",
                "Meghalaya",
                "Mizoram",
                "Nagaland",
                "Odisha",
                "Punjab",
                "Rajasthan",
                "Sikkim",
                "Tamil Nadu",
                "Telangana",
                "Tripura",
                "Uttarakhand",
                "Uttar Pradesh",
                "West Bengal",
                "Andaman and Nicobar Islands",
                "Chandigarh",
                "Dadra and Nagar Haveli",
                "Daman and Diu",
                "Delhi",
                "Lakshadweep",
                "Puducherry",
                "Jammu and Kashmir",
                "Ladakh"
        );
        return indianStates;
    }
    public static List<String> getIndianStates(){
        if (indianStates == null)
            indianStates = generateIndianStates();
        return indianStates;
    }
}