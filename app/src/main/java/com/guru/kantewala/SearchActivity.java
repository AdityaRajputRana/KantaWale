package com.guru.kantewala;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.guru.kantewala.Adapters.SearchRVAdapter;
import com.guru.kantewala.Helpers.SubscriptionInterface;
import com.guru.kantewala.Models.Category;
import com.guru.kantewala.Tools.Constants;
import com.guru.kantewala.Tools.Methods;
import com.guru.kantewala.Tools.Utils;
import com.guru.kantewala.databinding.ActivitySearchBinding;
import com.guru.kantewala.rest.api.APIMethods;
import com.guru.kantewala.rest.api.interfaces.APIResponseListener;
import com.guru.kantewala.rest.requests.SearchReq;
import com.guru.kantewala.rest.response.CategoryRP;
import com.guru.kantewala.rest.response.SearchRP;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements SubscriptionInterface.AskToSubListener {


    private ActivitySearchBinding binding;
    private int processSemaphore = 0;

    private int selectedCategoryId = -1;
    private CategoryRP categoryRP;

    private List<String> stateList;
    private int selectedState = 0;

    boolean isSearchChange = true;
    private SearchRP searchRP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        setListeners();
        loadLocalData();
        fetchData();
        updateUI();
    }

    private void setListeners() {
        binding.backBtn.setOnClickListener(view->onBackPressed());
        binding.searchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    fetchCompanies();
                    return true;
            }
        });
        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                isSearchChange= true;
                if (binding.searchEt.getText().toString().contains("\n")){
                    String text = binding.searchEt.getText().toString().trim()
                            .replace("\n", "");
                    binding.searchEt.setText(text);
                    Utils.hideSoftKeyboard(SearchActivity.this);
                    fetchCompanies();
                }
            }
        });
    }


    private void fetchData() {
       fetchCategories();
       fetchCompanies();
    }

    private void fetchCompanies() {
        if (!isSearchChange){
            return;
        }

        processSemaphore++;
        Utils.hideSoftKeyboard(this);
        isSearchChange = false;
        updateUI();

        APIMethods.search(1, binding.searchEt.getText().toString(), selectedCategoryId, selectedState, new APIResponseListener<SearchRP>() {
            @Override
            public void success(SearchRP response) {
                searchRP = response;
                processSemaphore--;
                updateUI();
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                processSemaphore--;
                updateUI();
                Methods.showFailedAlert(SearchActivity.this, code, message, redirectLink, retry, cancellable);
            }
        });
    }

    private void fetchCategories() {
        if (categoryRP != null){
            return;
        }
        processSemaphore++;
        updateUI();
        APIMethods.getCategories(new APIResponseListener<CategoryRP>() {
            @Override
            public void success(CategoryRP response) {
                processSemaphore--;
                categoryRP = response;
                initialiseCategorySpinner();
                updateUI();
            }

            @Override
            public void fail(String code, String message, String redirectLink, boolean retry, boolean cancellable) {
                processSemaphore--;
                updateUI();
                Methods.showFailedAlert(SearchActivity.this, code, message, redirectLink, retry, cancellable);
            }
        });
    }

    private void loadLocalData() {
        boolean isCategoryIdAttached = getIntent().getBooleanExtra("isCatIDAttached", false);
        boolean isCategoryAttached = getIntent().getBooleanExtra("isCategoryAttached", false);
        boolean isCategoryRPAttached = getIntent().getBooleanExtra("isCategoryRPAttached", false);


        if (isCategoryRPAttached) {
            categoryRP = new Gson().fromJson(getIntent().getStringExtra("categoryRP"), CategoryRP.class);
            initialiseCategorySpinner();
        }
        if (isCategoryIdAttached) {
            selectedCategoryId = getIntent().getIntExtra("categoryId", -1);
        }

        stateList = Constants.getIndianStates();
        initialiseStateSpinner();
        initialiseCategorySpinner();
    }


    private void updateUI(){
        if (processSemaphore <= 0){
            binding.infoTxt.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.GONE);
            binding.recyclerView.setVisibility(View.VISIBLE);
            if (searchRP.getRecommendedCompanies().size() <= 0){
                binding.infoTxt.setVisibility(View.VISIBLE);
                binding.infoTxt.setText("No results found for your search :(");
            }
            initialiseRecyclerView();
        } else {
            binding.infoTxt.setVisibility(View.VISIBLE);
            binding.infoTxt.setText("Please wait");
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.recyclerView.setVisibility(View.GONE);
        }
    }

    private void initialiseRecyclerView() {
        SearchRVAdapter adapter = new SearchRVAdapter(searchRP, this);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initialiseStateSpinner() {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                R.layout.item_spinner,stateList);
        spinnerAdapter.setDropDownViewResource(R.layout.item_dropdown);
        binding.stateSpinner.setAdapter(spinnerAdapter);
        binding.stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("eta-search", "stateSelected");
                if (selectedState != position)
                        isSearchChange = true;
                selectedState = position;
                fetchCompanies();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initialiseCategorySpinner() {
        if (categoryRP == null){
            return;
        }
        ArrayList<String> cats = categoryRP.getCategoriesForDropDown();
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                R.layout.item_spinner, cats);
        spinnerAdapter.setDropDownViewResource(R.layout.item_dropdown);
        binding.categorySpinner.setAdapter(spinnerAdapter);
        int currentPos = categoryRP.getPositionFromID(selectedCategoryId);
        binding.categorySpinner.setSelection(currentPos);

        binding.categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("eta-search", "categorySelected");
                int newID;
                if (position == 0) {
                    newID = -1;
                }else {
                    newID = categoryRP.getIdFromDropDownItem(cats.get(position), position);
                }
                if (newID != selectedCategoryId) {
                    isSearchChange = true;
                }
                selectedCategoryId = newID;
                fetchCompanies();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void redirectToSubscribeFragment() {
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("showSubFrag", true);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }
}

//Todo: Paginate Requests - Edtech