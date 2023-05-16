package com.guru.kantewala.Tools;

import android.content.Context;
import android.content.SharedPreferences;

public class ProfileUtils {
    private enum ProfileKeys{signUpPending};
    private static String key(ProfileKeys key){
        return String.valueOf(key);
    }
    private static SharedPreferences profilePrefs;
    private static SharedPreferences getProfilePrefs(Context context){
        if (profilePrefs == null)
            profilePrefs = context.getSharedPreferences("MY_PROFILE_PREFS", Context.MODE_PRIVATE);
        return profilePrefs;
    }



    public static boolean isProfileEditRequired(Context context){
        return getProfilePrefs(context).getBoolean(key(ProfileKeys.signUpPending), false);
    }

}
