package com.guru.kantewala;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.google.android.material.chip.Chip;
import com.guru.kantewala.Adapters.CompanyImagesRVAdapter;
import com.guru.kantewala.Models.Category;
import com.guru.kantewala.Models.Company;
import com.guru.kantewala.Models.CompanyImages;
import com.guru.kantewala.Models.TextModels;
import com.guru.kantewala.Tools.Constants;
import com.guru.kantewala.Tools.Methods;
import com.guru.kantewala.Tools.Transformations.RoundedCornerTransformation;
import com.guru.kantewala.Tools.Utils;
import com.guru.kantewala.databinding.ActivityEditCompanyBinding;
import com.guru.kantewala.databinding.DialogInputBinding;
import com.guru.kantewala.databinding.DialogLoadingBinding;
import com.guru.kantewala.rest.api.APIMethods;
import com.guru.kantewala.rest.api.interfaces.APIResponseListener;
import com.guru.kantewala.rest.api.interfaces.FileTransferResponseListener;
import com.guru.kantewala.rest.requests.EditCompanyDetailsReq;
import com.guru.kantewala.rest.response.CategoryRP;
import com.guru.kantewala.rest.response.MessageRP;
import com.guru.kantewala.rest.response.MyCompanyRP;
import com.guru.kantewala.rest.response.UserRP;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

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

        binding.companyLogoImg.setOnClickListener(view->{
            pickImageForLogo();
        });

        binding.editDetailsBtn.setOnClickListener(view->{
            showDetailsLayout();
        });

        binding.addImageBlockBtn.setOnClickListener(view->{
            inputImageBlockName();
        });

        binding.backBtn.setOnClickListener(view->onBackPressed());
    }

    private void pickImageForLogo() {
        ActivityResultContracts.PickVisualMedia.VisualMediaType mediaType =
                (ActivityResultContracts.PickVisualMedia.VisualMediaType) ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE;
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(mediaType)
                .build());
    }

    private void inputImageBlockName() {
        DialogInputBinding inputBinding = DialogInputBinding.inflate(getLayoutInflater());
        AlertDialog inputDialog = new AlertDialog.Builder(this)
                .setView(inputBinding.getRoot())
                .setCancelable(true)
                .create();
        inputBinding.continueBtn.setOnClickListener(view->{
            if (inputBinding.editText.getText().toString().isEmpty()){
                inputBinding.editText.setError("Required");
            } else {
                Utils.hideSoftKeyboard(EditCompanyActivity.this);
                inputBinding.editText.setError(null);
                saveImageBlock(inputBinding.editText.getText().toString());
                inputDialog.dismiss();
            }
        });
        inputDialog.show();
        inputDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void saveImageBlock(String blockName) {
        startProgress("Creating new Image Block");
        APIMethods.createImageBlock(myCompanyRP.getCompany().getId(), blockName, new APIResponseListener<MessageRP>() {
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
    private void saveImageBlock(String blockName, CompanyImages.ImageBlock block) {
        startProgress("Creating new Image Block");
        APIMethods.editImageBlock(block, myCompanyRP.getCompany().getId(), blockName, new APIResponseListener<MessageRP>() {
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

    private void inputImageBlockNameForEdit(CompanyImages.ImageBlock block) {
        DialogInputBinding inputBinding = DialogInputBinding.inflate(getLayoutInflater());
        AlertDialog inputDialog = new AlertDialog.Builder(this)
                .setView(inputBinding.getRoot())
                .setCancelable(true)
                .create();
        inputBinding.editText.setText(block.getTitle());
        inputBinding.titleTxt.setText("Change Block name");
        inputBinding.continueBtn.setText("Save Changes");
        inputBinding.continueBtn.setOnClickListener(view->{
            if (inputBinding.editText.getText().toString().isEmpty()) {
                inputBinding.editText.setError("Required");
            } else if (inputBinding.editText.getText().toString().equals(block.getTitle())){
                inputBinding.editText.setError("Block name is same as previous");
            } else {
                Utils.hideSoftKeyboard(EditCompanyActivity.this);
                inputBinding.editText.setError(null);
                saveImageBlock(inputBinding.editText.getText().toString(), block);
                inputDialog.dismiss();
            }
        });
        inputDialog.show();
        inputDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
                isPendingChanges(response);
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                binding.progressBar.setVisibility(View.GONE);
                Methods.showFailedAlert(EditCompanyActivity.this, code, message, redirectLink, retry, cancellable);
            }
        });
    }

    private void isPendingChanges(MyCompanyRP response) {
        if (response.isPendingReq()){
            binding.pendingReqText.setVisibility(View.VISIBLE);

            MyCompanyRP.PendingReq pendingReq = response.getPendingReq();

            binding.companyEt.setText(pendingReq.getName());
            binding.gstET.setText(pendingReq.getGst());
            binding.cityEt.setText(pendingReq.getCity());
            binding.addressEt.setText(pendingReq.getLocation());
            selectedStateIndex = pendingReq.getStateCode();
            binding.stateSpinner.setSelection(selectedStateIndex);

            if (pendingReq.getStatus() == 0){
                binding.continueBtn.setEnabled(false);
                binding.continueBtn.setVisibility(View.GONE);
                binding.pendingReqText.setText("You already have a edit company request pending approval. It usually take less than 48 hours to approve.");
                binding.pendingReqText.setBackgroundColor(getResources().getColor(R.color.color_bg));
            } else if (pendingReq.getStatus() == 1){
                binding.continueBtn.setEnabled(true);
                binding.continueBtn.setVisibility(View.VISIBLE);
                binding.pendingReqText.setText("Edit Request Rejected: " + pendingReq.getRemarks());
                binding.pendingReqText.setBackgroundColor(getResources().getColor(R.color.red_bg));
            }
        } else {
            binding.continueBtn.setVisibility(View.VISIBLE);
            binding.pendingReqText.setVisibility(View.GONE);
            binding.continueBtn.setEnabled(true);
        }
    }
    
    ArrayList<Integer> selectedTagIds = new ArrayList<>();

    private void showEditCompanyLayout() {

        //tags
        binding.tagsGroup.removeAllViews();
        selectedTagIds = new ArrayList<>();
        for (TextModels tag: myCompanyRP.getCompany().getTagList()){
            selectedTagIds.add(tag.getId());
            Chip chip = new Chip(this);
            chip.setText(tag.getName());
            chip.setClickable(true);
            chip.setOnClickListener(view->confirmDeleteTag(tag));
            chip.setCheckable(false);
            chip.setChipStrokeColor(ColorStateList.valueOf(this.getResources().getColor(R.color.primaryText)));
            chip.setChipStrokeWidth(5);
            chip.setChipBackgroundColor(ColorStateList.valueOf(Color.TRANSPARENT));
            chip.setRippleColor(ColorStateList.valueOf(this
                    .getResources().getColor(R.color.color_bg)));
            chip.setTextColor(this.getResources().getColor(R.color.primaryText));
            binding.tagsGroup.addView(chip);
        }

        Chip chip = new Chip(this);
        chip.setText("Add/Remove Categories");
        chip.setClickable(true);
        chip.setOnClickListener(view->addCategory());
        chip.setCheckable(false);
        chip.setChipStrokeColor(ColorStateList.valueOf(this.getResources().getColor(R.color.color_cta)));
        chip.setChipStrokeWidth(5);
        chip.setChipBackgroundColor(ColorStateList.valueOf(Color.TRANSPARENT));
        chip.setRippleColor(ColorStateList.valueOf(this
                .getResources().getColor(R.color.color_bg)));
        chip.setTextColor(this.getResources().getColor(R.color.color_cta));
        binding.tagsGroup.addView(chip);

        // end tags

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
        CompanyImagesRVAdapter adapter = new CompanyImagesRVAdapter(company.getCompanyImages(), this, true, new CompanyImagesRVAdapter.EditListener() {
            @Override
            public void addImage(CompanyImages.ImageBlock block) {
                pickImage(block);
            }

            @Override
            public void deleteBlock(CompanyImages.ImageBlock block) {
                confirmDeleteBlock(block);
            }

            @Override
            public void editBlock(CompanyImages.ImageBlock block) {
                inputImageBlockNameForEdit(block);
            }

            @Override
            public void deleteImage(CompanyImages.ImageBlock.Image image) {
                deleteImageFromServer(image);
            }
        });
        binding.imageRV.setAdapter(adapter);
        binding.imageRV.setLayoutManager(new LinearLayoutManager(this));
    }

    private void deleteImageFromServer(CompanyImages.ImageBlock.Image image) {
        startProgress("Deleting Image");
        APIMethods.deleteImage(image, new APIResponseListener<MessageRP>() {
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

    CategoryRP categoryRP;

    private void addCategory() {
        binding.progressBar.setVisibility(View.GONE);
        if (categoryRP == null) {
            binding.progressBar.setVisibility(View.VISIBLE);
            APIMethods.getCategories(new APIResponseListener<CategoryRP>() {
                @Override
                public void success(CategoryRP response) {
                    categoryRP = response;
                    addCategory();
                }

                @Override
                public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                    showError(message);
                }
            });
            return;
        }

        binding.tagsGroup.removeAllViews();

        for (Category tag: categoryRP.getCategories()){
            Chip chip = new Chip(this);
            chip.setText(tag.getName());
            chip.setCheckable(true);
            chip.setId(tag.getId());
            chip.setChecked(selectedTagIds.contains(tag.getId()));
            chip.setChipStrokeColor(ColorStateList.valueOf(this.getResources().getColor(R.color.primaryText)));
            chip.setChipStrokeWidth(5);
            chip.setChipBackgroundColor(ColorStateList.valueOf(Color.TRANSPARENT));
            chip.setRippleColor(ColorStateList.valueOf(this
                    .getResources().getColor(R.color.color_bg)));
            chip.setTextColor(this.getResources().getColor(R.color.primaryText));
            binding.tagsGroup.addView(chip);
        }

        Chip chip = new Chip(this);
        chip.setText("Save Changes");
        chip.setClickable(true);
        chip.setOnClickListener(view->saveSelectedCategories());
        chip.setCheckable(false);
        chip.setChipStrokeColor(ColorStateList.valueOf(this.getResources().getColor(R.color.color_cta)));
        chip.setChipStrokeWidth(5);
        chip.setChipBackgroundColor(ColorStateList.valueOf(Color.TRANSPARENT));
        chip.setRippleColor(ColorStateList.valueOf(this
                .getResources().getColor(R.color.color_bg)));
        chip.setTextColor(this.getResources().getColor(R.color.color_cta));
        binding.tagsGroup.addView(chip);
        
        
    }

    private void saveSelectedCategories() {
        startProgress("Saving Changes");
        APIMethods.updateCategories(myCompanyRP.getCompany().getId(), binding.tagsGroup.getCheckedChipIds(), new APIResponseListener<MessageRP>() {
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

    private void confirmDeleteTag(TextModels tag) {
    }

    CompanyImages.ImageBlock imageBlock;

    @SuppressLint("Con")
    private void pickImage(CompanyImages.ImageBlock block) {
        imageBlock = block;
        ActivityResultContracts.PickVisualMedia.VisualMediaType mediaType =
                (ActivityResultContracts.PickVisualMedia.VisualMediaType) ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE;
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(mediaType)
                .build());
    }

    ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    if (imageBlock != null) {
                        startImageBlockUpload(uri, imageBlock);
                        imageBlock = null;
                    } else {
                        uploadCompanyLog(uri);
                    }
                }
            });

    private void uploadCompanyLog(Uri uri) {
        startProgress("Uploading Logo");
        APIMethods.uploadCompanyLogo(uri, this, myCompanyRP.getCompany().getId(),  new FileTransferResponseListener<MessageRP>() {
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
            public void onProgress(int percent) {
                EditCompanyActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("Eta Progress", String.valueOf(percent));
                        if (percent <= 0)
                            startProgress("Preparing Image Before Upload");
                        else if  (percent >= 100)
                            startProgress("Saving Image");
                        else
                            startProgress("Uploading Image: " + percent + "%");
                    }
                });
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                showError(message);
            }
        });
    }

    private void startImageBlockUpload(Uri uri, CompanyImages.ImageBlock imageBlock) {
        startProgress("Uploading picture");
        APIMethods.uploadImageForBlock(uri, this, imageBlock, new FileTransferResponseListener<MessageRP>() {
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
            public void onProgress(int percent) {
                EditCompanyActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("Eta Progress", String.valueOf(percent));
                        if (percent <= 0)
                            startProgress("Preparing Image Before Upload");
                        else if  (percent >= 100)
                            startProgress("Saving Image");
                        else
                            startProgress("Uploading Image: " + percent + "%");
                    }
                });
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                showError(message);
            }
        });
    }

    private void confirmDeleteBlock(CompanyImages.ImageBlock block) {
        new AlertDialog.Builder(this)
                .setTitle("Delete \""+ block.getTitle() + "\"")
                .setMessage("Are you sure you want to delete the selected block. This action is irreversible")
                .setCancelable(true)
                .setPositiveButton("Delete", (dialog1, which) -> deleteImageBlock(block))
                .setNegativeButton("Cancel", (dialog1, which) -> dialog1.dismiss())
                .show();
    }

    private void deleteImageBlock(CompanyImages.ImageBlock block) {
        startProgress("Deleting");
        APIMethods.deleteImageBlock(block, new APIResponseListener<MessageRP>() {
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