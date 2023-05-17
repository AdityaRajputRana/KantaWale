package com.guru.kantewala.Tools;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;

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
        return FirebaseAuth.getInstance().getCurrentUser() == null || FirebaseAuth.getInstance().getCurrentUser().getDisplayName() == null || FirebaseAuth.getInstance().getCurrentUser().getDisplayName().isEmpty();
//        return getProfilePrefs(context).getBoolean(key(ProfileKeys.signUpPending), false);
    }

    public static void saveProfileEditRequired(Context context, boolean isRequired){
        getProfilePrefs(context).edit()
                .putBoolean(key(ProfileKeys.signUpPending), isRequired)
                .commit();
    }

}
