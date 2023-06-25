package com.guru.kantewala;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.guru.kantewala.Adapters.CompanyImagesRVAdapter;
import com.guru.kantewala.Models.Company;
import com.guru.kantewala.Tools.Methods;
import com.guru.kantewala.Tools.Transformations.RoundedCornerTransformation;
import com.guru.kantewala.Tools.Utils;
import com.guru.kantewala.databinding.ActivityCompanyBinding;
import com.guru.kantewala.rest.api.APIMethods;
import com.guru.kantewala.rest.api.interfaces.APIResponseListener;
import com.squareup.picasso.Picasso;

public class CompanyActivity extends AppCompatActivity {

    ActivityCompanyBinding binding;
    int cid;
    Company company;
    boolean isRecommended = false;

    public enum ToggleState{DETAILS, IMAGES};

    ToggleState state = ToggleState.DETAILS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCompanyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setListeners();
        loadAttachedData();
    }

    private void loadAttachedData() {
        isRecommended = getIntent().getBooleanExtra("isRecommendation", false);
        cid = getIntent().getIntExtra("companyId", Integer.MIN_VALUE);
        if (cid == Integer.MIN_VALUE) {
            if (!getIntent().getBooleanExtra("hasCompanyAttached", false)){
                Methods.showError(this, "Bad request (EC-L-1)");
                return;
            }
            cid = new Gson().fromJson(getIntent().getStringExtra("company"), Company.class).getId();
        }

        if (getIntent().getBooleanExtra("hasCompanyAttached", false)) {
            company = new Gson().fromJson(getIntent().getStringExtra("company"), Company.class);
            loadCompanyUI();
        }

        fetchCompanyDetails();
    }

    private void fetchCompanyDetails() {
        binding.progressBar.setVisibility(View.VISIBLE);
        APIMethods.getCompany(cid, isRecommended, new APIResponseListener<Company>() {
            @Override
            public void success(Company response) {
                company = response;
                loadCompanyUI();
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                binding.progressBar.setVisibility(View.GONE);
                Methods.showFailedAlert(CompanyActivity.this, code, message, redirectLink, retry, cancellable);
            }
        });
    }

    private void loadCompanyUI() {
        if (company.isLocked()){
            Methods.showError(this, "You don't have permission to access this page (EC-P-100)");
            return;
        }
        binding.progressBar.setVisibility(View.GONE);

        if (company.getName() == null || company.getName().isEmpty()){
            binding.companyNameTxt.setText("");
        } else {
            binding.companyNameTxt.setText(company.getName());
        }

        if (company.getLocation() == null || company.getLocation().isEmpty()){
            binding.locationTxt.setText("");
        } else {
            binding.locationTxt.setText(company.getLocation());
        }

        if (company.getLogoUrl() == null || company.getLogoUrl().isEmpty()){
            Picasso.get()
                    .load(R.drawable.ic_laucher_foreground)
                    .into(binding.companyLogoImg);
        } else {
            Picasso.get()
                    .load(company.getLogoUrl())
                    .transform(new RoundedCornerTransformation(10))
                    .into(binding.companyLogoImg);
        }


        if (company.isShowDetails()){
            if (state == ToggleState.DETAILS)
                binding.detailsLayout.setVisibility(View.VISIBLE);
            binding.detailsToggle.setVisibility(View.VISIBLE);
        } else {
            binding.detailsToggle.setVisibility(View.GONE);
            binding.detailsLayout.setVisibility(View.GONE);
        }


        if (company.getFullName() == null || company.getFullName().isEmpty()){
            binding.nameTxt.setText("-");
        } else {
            binding.nameTxt.setText(company.getFullName());
        }


        if (company.getAddress() == null || company.getAddress().isEmpty()){
            binding.addressTxt.setText("-");
        } else {
            binding.addressTxt.setText(company.getAddress());
        }

        if (company.getPhoneNumber() == null || company.getPhoneNumber().isEmpty()){
            binding.phoneNumberTxt.setText("-");
        } else {
            binding.phoneNumberTxt.setText(company.getPhoneNumber());
        }
        binding.phoneNumberTxt.setOnClickListener(view->{
            if (company.getPhoneNumber() != null && !company.getPhoneNumber().isEmpty()) {
                Utils.openDialer(company.getPhoneNumber(), CompanyActivity.this);
            }
        });
        binding.phoneCopyBtn.setOnClickListener(view->copy(company.getPhoneNumber()));



        if (company.getEmailId() == null || company.getEmailId().isEmpty()){
            binding.emailTxt.setText("-");
        } else {
            binding.emailTxt.setText(company.getEmailId());
        }
        binding.emailTxt.setOnClickListener(view->copy(company.getEmailId()));

        if (company.getGst() == null || company.getGst().isEmpty()){
            binding.gstTxt.setText("-");
        } else {
            binding.gstTxt.setText(company.getGst());
        }
        binding.gstTxt.setOnClickListener(view->copy(company.getGst()));


        //Images Section

        if (company.getCompanyImages() == null || company.getCompanyImages().getBlocks() == null
         || company.getCompanyImages().getBlocks().size() == 0){
            binding.imageRV.setVisibility(View.GONE);
            binding.imagesToggle.setVisibility(View.GONE);
        } else {
            if (state == ToggleState.IMAGES)
                binding.imageRV.setVisibility(View.VISIBLE);
            binding.imagesToggle.setVisibility(View.VISIBLE);
            binding.imageRV.setAdapter(new CompanyImagesRVAdapter(company.getCompanyImages(), this));
            binding.imageRV.setLayoutManager(new LinearLayoutManager(this));
        }

    }

    private void copy(String content) {
        Utils.copyToClipboard(this, content, getString(R.string.app_name));
    }

    private void setListeners() {
        setUpToggle();
        binding.backBtn.setOnClickListener(view->onBackPressed());
    }

    private void setUpToggle() {
        binding.detailsToggle.setOnClickListener(view->{
            binding.detailsToggle.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.color_cta)));
            binding.detailsToggle.setTextColor(getResources().getColor(R.color.color_fg));

            binding.imagesToggle.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.color_fg)));
            binding.imagesToggle.setTextColor(getResources().getColor(R.color.color_cta));

            binding.detailsLayout.setVisibility(View.VISIBLE);
            binding.imageRV.setVisibility(View.GONE);
        });

        binding.imagesToggle.setOnClickListener(view->{
            binding.imagesToggle.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.color_cta)));
            binding.imagesToggle.setTextColor(getResources().getColor(R.color.color_fg));

            binding.detailsToggle.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.color_fg)));
            binding.detailsToggle.setTextColor(getResources().getColor(R.color.color_cta));

            binding.imageRV.setVisibility(View.VISIBLE);
            binding.detailsLayout.setVisibility(View.GONE);
        });
    }

}