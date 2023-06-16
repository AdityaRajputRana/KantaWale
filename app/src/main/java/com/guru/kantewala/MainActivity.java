package com.guru.kantewala;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationBarView;
import com.guru.kantewala.Helpers.SubscriptionInterface;
import com.guru.kantewala.MainFragments.HomeFragment;
import com.guru.kantewala.MainFragments.ProfileFragment;
import com.guru.kantewala.MainFragments.SubscriptionFragment;
import com.guru.kantewala.databinding.ActivityMainBinding;
import com.guru.kantewala.rest.response.UnlockedStatesRP;

public class MainActivity extends AppCompatActivity implements SubscriptionInterface.AskToSubListener {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setUpFragmentsAndNavigation();
        checkAttachments();
        loadLeastPriorityData();
    }

    private void loadLeastPriorityData() {
        UnlockedStatesRP.loadUnlockStates();
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