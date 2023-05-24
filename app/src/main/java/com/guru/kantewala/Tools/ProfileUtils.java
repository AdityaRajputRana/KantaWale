package com.guru.kantewala.Tools;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.guru.kantewala.rest.response.UserRP;

public class ProfileUtils {
    public static void signOut(Context context){
        FirebaseAuth.getInstance().signOut();
        getProfilePrefs(context).edit().clear();
    }
    private enum ProfileKeys{signUpPending, UserProfile};
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

    public static void saveProfile(Context context, UserRP userProfile){
        String profile = new Gson().toJson(userProfile);
        getProfilePrefs(context).edit()
                .putString(key(ProfileKeys.UserProfile), profile)
                .commit();
    }


}
