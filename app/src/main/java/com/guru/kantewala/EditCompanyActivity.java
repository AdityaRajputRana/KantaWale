package com.guru.kantewala;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.guru.kantewala.Adapters.CompanyImagesRVAdapter;
import com.guru.kantewala.Models.Company;
import com.guru.kantewala.Tools.Constants;
import com.guru.kantewala.Tools.Methods;
import com.guru.kantewala.Tools.Transformations.RoundedCornerTransformation;
import com.guru.kantewala.databinding.ActivityEditCompanyBinding;
import com.guru.kantewala.databinding.DialogLoadingBinding;
import com.guru.kantewala.rest.api.APIMethods;
import com.guru.kantewala.rest.api.interfaces.APIResponseListener;
import com.guru.kantewala.rest.requests.EditCompanyDetailsReq;
import com.guru.kantewala.rest.response.MessageRP;
import com.guru.kantewala.rest.response.MyCompanyRP;
import com.guru.kantewala.rest.response.UserRP;
import com.squareup.picasso.Picasso;

public class EditCompanyActivity extends AppCompatActivity {

    ActivityEditCompanyBinding binding;
    MyCompanyRP myCompanyRP;
    int selectedStateIndex = 0;

    AlertDialog dialog;
    DialogLoadingBinding dialogBinding;
    private void startProgress(String message) {
        if (dialogBinding == null){
            dialogBinding = DialogLoadingBinding.inflate(getLayoutInflater(), null, false);
        }

        dialogBinding.imageView.setVisibility(View.GONE);
        dialogBinding.progressBar.setVisibility(View.VISIBLE);
        dialogBinding.titleTxt.setText("Please wait");
        dialogBinding.bodyTxt.setText(message);

        if (dialog == null){
            dialog = new android.app.AlertDialog.Builder(this)
                    .setView(dialogBinding.getRoot())
                    .setOnDismissListener(d->{
                        dialogBinding = null;
                        dialog = null;
                    })
                    .show();

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.setCancelable(false);
    }
    private void showError(String message){
        if (dialogBinding == null){
            dialogBinding = DialogLoadingBinding.inflate(getLayoutInflater(), null, false);
        }

        dialogBinding.imageView.setVisibility(View.VISIBLE);
        dialogBinding.imageView.setImageDrawable(getDrawable(R.drawable.ic_error_bg));
        dialogBinding.progressBar.setVisibility(View.GONE);
        dialogBinding.titleTxt.setText("Some Error Occurred");
        dialogBinding.bodyTxt.setText(message);

        if (dialog == null) {
            dialog = new AlertDialog.Builder(this)
                    .setView(dialogBinding.getRoot())
                    .setOnDismissListener(d->{
                        dialogBinding = null;
                        dialog = null;
                    })
                    .show();

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.setCancelable(true);
    }
    private void showSuccess(String message, RegisterActivity.OnDismissListener listener){
        if (dialogBinding == null){
            dialogBinding = DialogLoadingBinding.inflate(getLayoutInflater(), null, false);
        }

        dialogBinding.imageView.setVisibility(View.VISIBLE);
        dialogBinding.imageView.setImageDrawable(getDrawable(R.drawable.ic_done_bg));
        dialogBinding.progressBar.setVisibility(View.GONE);
        dialogBinding.titleTxt.setText("Completed");
        dialogBinding.bodyTxt.setText(message);

        if (dialog == null) {
            dialog = new AlertDialog.Builder(this)
                    .setView(dialogBinding.getRoot())
                    .setOnDismissListener(d->{
                        dialogBinding = null;
                        dialog = null;
                    })
                    .show();

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.setCancelable(true);
        dialog.setOnCancelListener(dialog1 -> {
            dialog.setOnCancelListener(null);
            if (listener != null)
                listener.onCancel();
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditCompanyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadUI();
        setListeners();
        fetchData();
    }

    private void setListeners() {
        binding.continueBtn.setOnClickListener(view->{
            if (areCompanyDetailsValid()){
                saveCompanyDetails();
            }
        });

        binding.editDetailsBtn.setOnClickListener(view->{
            showDetailsLayout();
        });
    }

    int screenState = 0;

    private void showDetailsLayout() {
        binding.companyImageLayout.setVisibility(View.GONE);
        binding.companyDetailsLayout.setVisibility(View.VISIBLE);
        screenState = 1;
    }

    private void showImageLayout(){
        binding.companyImageLayout.setVisibility(View.VISIBLE);
        binding.companyDetailsLayout.setVisibility(View.GONE);
        screenState = 0;
    }

    @Override
    public void onBackPressed() {
        switch (screenState) {
            case 1:
                showImageLayout();
                break;
            default:
            super.onBackPressed();
        }
    }

    private boolean areCompanyDetailsValid(){
        boolean isValid = true;


        if (binding.companyEt.getText().toString().isEmpty()){
            binding.companyEt.setError("Required");
            isValid = false;
        } else if (binding.companyEt.getText().toString().contains("'")){
            isValid = false;
            binding.companyEt.setError("Special Characters like ' are not allowed");
        } else {
            binding.companyEt.setError(null);
        }

        if (binding.gstET.getText().toString().isEmpty()){
            binding.gstET.setError("Required");
            isValid = false;
        } else {
            binding.gstET.setError(null);
        }

        if (binding.cityEt.getText().toString().isEmpty()){
            binding.cityEt.setError("Required");
            isValid = false;
        } else {
            binding.cityEt.setError(null);
        }

        if (binding.addressEt.getText().toString().isEmpty()){
            binding.addressEt.setError("Required");
            isValid = false;
        } else {
            binding.addressEt.setError(null);
        }

        if (selectedStateIndex == 0){
            binding.stateSpinner.setBackgroundColor(Color.YELLOW);
            isValid = false;
        } else {
            binding.stateSpinner.setBackground(getResources().getDrawable(R.drawable.bg_et_main));
        }


        return isValid;
    }

    private void saveCompanyDetails() {
        EditCompanyDetailsReq req = new EditCompanyDetailsReq(binding.companyEt.getText().toString(),
                binding.gstET.getText().toString(),
                binding.addressEt.getText().toString(),
                binding.cityEt.getText().toString(),
                selectedStateIndex);

        startProgress("Saving company details");
        APIMethods.editCompanyDetails(req, new APIResponseListener<MessageRP>() {
            @Override
            public void success(MessageRP response) {
                showSuccess(response.getMessage(), new RegisterActivity.OnDismissListener() {
                    @Override
                    public void onCancel() {
                        loadUI();
                        fetchData();
                    }
                });
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                showError(message);
            }
        });
    }

    private void loadUI() {
        binding.companyDetailsLayout.setVisibility(View.GONE);
        binding.companyImageLayout.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                R.layout.item_spinner, Constants.getIndianStates());
        spinnerAdapter.setDropDownViewResource(R.layout.item_dropdown);
        binding.stateSpinner.setAdapter(spinnerAdapter);
        binding.stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedStateIndex = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void fetchData() {
        binding.progressBar.setVisibility(View.VISIBLE);

        APIMethods.getCompany(new APIResponseListener<MyCompanyRP>() {
            @Override
            public void success(MyCompanyRP response) {
                binding.progressBar.setVisibility(View.GONE);
                myCompanyRP = response;
                if (!response.isCompanyLinked()){
                    showCreateCompanyLayout();
                } else {
                    showEditCompanyLayout();
                }
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                binding.progressBar.setVisibility(View.GONE);
                Methods.showFailedAlert(EditCompanyActivity.this, code, message, redirectLink, retry, cancellable);
            }
        });
    }

    private void showEditCompanyLayout() {
        binding.progressBar.setVisibility(View.GONE);
        binding.companyImageLayout.setVisibility(View.VISIBLE);


        Company company = myCompanyRP.getCompany();

        binding.companyEt.setText(company.getName());
        binding.cityEt.setText(company.getCity());
        binding.addressEt.setText(company.getAddress());
        selectedStateIndex = company.getStateCode();
        binding.stateSpinner.setSelection(company.getStateCode());
        binding.gstET.setText(myCompanyRP.getUser().getCompanyGST());



        if (company.getName() == null || company.getName().isEmpty()){
            binding.companyNameTxt.setText("");
        } else {
            binding.companyNameTxt.setText(company.getName());
            binding.companyEt.setText(company.getName());
        }

        if (company.getLocation() == null || company.getLocation().isEmpty()){
            binding.locationTxt.setText("");
        } else {
            binding.locationTxt.setText(company.getLocation());
        }

        if (company.getAddress() == null || company.getAddress().isEmpty()){
            binding.addressTxt.setText("");
        } else {
            binding.addressTxt.setText(company.getAddress());
        }

        if (company.getLogoUrl() == null || company.getLogoUrl().isEmpty()){
            Picasso.get()
                    .load(R.drawable.ic_baseline_mode_edit_24)
                    .placeholder(R.drawable.ic_baseline_mode_edit_24)
                    .into(binding.companyLogoImg);
        } else {
            Picasso.get()
                    .load(company.getLogoUrl())
                    .transform(new RoundedCornerTransformation(10))
                    .into(binding.companyLogoImg);
        }

        binding.imageRV.setVisibility(View.VISIBLE);
        CompanyImagesRVAdapter adapter = new CompanyImagesRVAdapter(company.getCompanyImages(), this);
        binding.imageRV.setAdapter(adapter);
        binding.imageRV.setLayoutManager(new LinearLayoutManager(this));
    }

    private void showCreateCompanyLayout() {
        UserRP userRP = myCompanyRP.getUser();
        binding.progressBar.setVisibility(View.GONE);
        binding.companyDetailsLayout.setVisibility(View.VISIBLE);
        binding.continueBtn.setText("Create Company");
        binding.detailsHeader.setText("Add a new company");

        binding.companyEt.setText(userRP.getCompany());
        binding.gstET.setText(userRP.getCompanyGST());
        binding.cityEt.setText(userRP.getCity());
        selectedStateIndex = userRP.getStateCode();
        binding.stateSpinner.setSelection(selectedStateIndex);
    }
}