package com.guru.kantewala;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.guru.kantewala.Helpers.SubscriptionInterface;
import com.guru.kantewala.MainFragments.HomeFragment;
import com.guru.kantewala.MainFragments.ProfileFragment;
import com.guru.kantewala.MainFragments.SubscriptionFragment;
import com.guru.kantewala.databinding.ActivityMainBinding;
import com.guru.kantewala.rest.response.UnlockedStatesRP;

public class MainActivity extends AppCompatActivity implements SubscriptionInterface.AskToSubListener {

    ActivityMainBinding binding;

    private static final int MY_REQUEST_CODE = 100;

    private void checkAppUpdate() {
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(this);

// Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

// Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // This example applies an immediate update. To apply a flexible update
                    // instead, pass in AppUpdateType.FLEXIBLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                // Request the update.

                try {
                    appUpdateManager.startUpdateFlowForResult(
                            // Pass the intent that is returned by 'getAppUpdateInfo()'.
                            appUpdateInfo,
                            // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                            AppUpdateType.IMMEDIATE,
                            // The current activity making the update request.
                            this,
                            // Include a request code to later monitor this update request.
                            MY_REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setUpFragmentsAndNavigation();
        checkAttachments();
        loadLeastPriorityData();

        checkAppUpdate();
    }

    private void loadLeastPriorityData() {
        UnlockedStatesRP.loadUnlockStates(this);
    }

    private void checkAttachments() {
        if (getIntent().getBooleanExtra("showSubFrag", false)){
            redirectToSubscribeFragment();
        }
    }


    HomeFragment homeFragment;
    SubscriptionFragment subscriptionFragment;
    ProfileFragment profileFragment;

    private void setUpFragmentsAndNavigation() {
        homeFragment = new HomeFragment();
        subscriptionFragment = new SubscriptionFragment();
        profileFragment = new ProfileFragment();

        binding.bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frameLayout, homeFragment)
                                .commit();
                        return true;
                    case R.id.premium:
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frameLayout, subscriptionFragment)
                                .commit();
                        return true;
                    case R.id.profile:
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.frameLayout, profileFragment)
                                .commit();
                        return true;
                    default:
                        return false;
                }
            }
        });

        binding.bottomNavigation.setSelectedItemId(R.id.home);
    }

    @Override
    public void redirectToSubscribeFragment() {
        binding.bottomNavigation.setSelectedItemId(R.id.premium);
    }
}