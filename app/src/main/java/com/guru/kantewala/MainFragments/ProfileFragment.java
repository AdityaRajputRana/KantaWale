package com.guru.kantewala.MainFragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.guru.kantewala.EditCompanyActivity;
import com.guru.kantewala.InformationActivity;
import com.guru.kantewala.LoginActivity;
import com.guru.kantewala.PlanDetailsActivity;
import com.guru.kantewala.R;
import com.guru.kantewala.RegisterActivity;
import com.guru.kantewala.Tools.Constants;
import com.guru.kantewala.Tools.Methods;
import com.guru.kantewala.Tools.ProfileUtils;
import com.guru.kantewala.Tools.Transformations.RoundedCornerTransformation;
import com.guru.kantewala.databinding.FragmentProfileBinding;
import com.guru.kantewala.rest.api.APIMethods;
import com.guru.kantewala.rest.api.interfaces.APIResponseListener;
import com.guru.kantewala.rest.response.UserRP;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {


    FragmentProfileBinding binding;
    UserRP userRP;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        loadData();
        setListeners();
        return binding.getRoot();
    }

    private void setListeners() {
        binding.recommendedList.setOnClickListener(view->{
            getActivity().startActivity(new Intent(getActivity(), InformationActivity.class));
        });

        binding.myPlansBtn.setOnClickListener(view->{
            getActivity().startActivity(new Intent(getActivity(), PlanDetailsActivity.class));
        });
        binding.logoutBtn.setOnClickListener(view->{
            confirmLogout();
        });
        binding.editUserProfileBtn.setOnClickListener(view->{
            launchEditProfileActivity();
        });

        binding.editCompanyProfileBtn.setOnClickListener(view->{
            Intent intent = new Intent(getActivity(), EditCompanyActivity.class);
            startActivity(intent);
        });

        binding.helpBtn.setOnClickListener(view->{
            Intent i = new Intent(getActivity(), InformationActivity.class);
            i.putExtra("isBody", true);
            i.putExtra("isHead", true);
            i.putExtra("head", "Help");
            i.putExtra("body", Constants.helpMessage);
            getActivity().startActivity(i);
        });


    }

    private void launchEditProfileActivity() {
        Intent intent = new Intent(getActivity(), RegisterActivity.class);
        startActivity(intent);
    }

    private void confirmLogout() {
        new AlertDialog.Builder(getActivity())
                .setTitle("Logout")
                .setMessage("Are you sure you want to sign out of your account on Kantewala?")
                .setCancelable(true)
                .setPositiveButton("Logout", (dialog, which) -> logOut())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void logOut() {
        ProfileUtils.signOut(getActivity());
        Intent i = new Intent(getActivity(), LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getActivity().startActivity(i);
        getActivity().finish();
    }

    private void loadData() {
        setUIFromAuth();
        if (userRP != null){
            updateUI();
            return;
        }

        APIMethods.getUserProfile(getActivity(), new APIResponseListener<UserRP>() {
            @Override
            public void success(UserRP response) {
                userRP = response;
                updateUI();
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                binding.locationTxt.setText("Some Error Occurred");
                Methods.showFailedAlert(getActivity(), code, message,
                        redirectLink, retry, cancellable);
            }
        });
    }

    private void setUIFromAuth() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user.getDisplayName() != null && !user.getDisplayName().isEmpty()){
            binding.nameTxt.setText(user.getDisplayName());
        }

        if (user.getPhotoUrl() != null && !user.getPhotoUrl().toString().isEmpty()){
            Picasso.get()
                    .load(user.getPhotoUrl())
                    .transform(new RoundedCornerTransformation(500, 0))
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .into(binding.profileImageView);
        } else {
            Picasso.get().load(R.drawable.ic_profile_placeholder)
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .into(binding.profileImageView);
        }

    }

    private void updateUI() {
        if (userRP.getName() != null && !userRP.getName().isEmpty()){
            binding.nameTxt.setText(userRP.getName());
        }

        if (userRP.getPhotoUrl() != null && !userRP.getPhotoUrl().isEmpty()){
            Picasso.get()
                    .load(userRP.getPhotoUrl())
                    .transform(new RoundedCornerTransformation(500, 0))
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .into(binding.profileImageView);
        }

        try {
            String location = userRP.getCity().trim() + ", " + userRP.getState().trim();
            binding.locationTxt.setText(location);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}