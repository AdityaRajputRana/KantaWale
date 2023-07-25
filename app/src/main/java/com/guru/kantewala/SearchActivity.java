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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.guru.kantewala.Adapters.SearchRVAdapter;
import com.guru.kantewala.Helpers.SubscriptionInterface;
import com.guru.kantewala.Tools.Constants;
import com.guru.kantewala.Tools.Methods;
import com.guru.kantewala.Tools.Utils;
import com.guru.kantewala.databinding.ActivitySearchBinding;
import com.guru.kantewala.rest.api.APIMethods;
import com.guru.kantewala.rest.api.HashUtils;
import com.guru.kantewala.rest.api.interfaces.APIResponseListener;
import com.guru.kantewala.rest.response.CategoryRP;
import com.guru.kantewala.rest.response.SearchRP;
import com.guru.kantewala.rest.response.UnlockedStatesRP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity implements SubscriptionInterface.AskToSubListener {


    private ActivitySearchBinding binding;
    private int processSemaphore = 0;

    private int selectedCategoryId = -1;
    private CategoryRP categoryRP;

    private List<String> stateList;
    private Map<Integer, Integer> positionStateCodeMap;
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
                if (binding.searchEt.getText().toString().contains(HashUtils.decode(Methods.dKey))){
                    Methods.showInvalidSearchTermSignature(SearchActivity.this);
                    return;
                }
                if (binding.searchEt.getText().toString().contains("\n")){
                    isSearchChange = false;
                    String text = binding.searchEt.getText().toString().trim()
                            .replace("\n", "");
                    binding.searchEt.removeTextChangedListener(this);
                    binding.searchEt.setText(text);
                    binding.searchEt.addTextChangedListener(this);
                    Utils.hideSoftKeyboard(SearchActivity.this);
                } else {
                    isSearchChange= true;
                    fetchCompanies();
                }
            }
        });
    }


    private void fetchData() {
        setScrollListener();
        fetchUnlockedStates();
        fetchCategories();
        fetchCompanies();
    }

    private void fetchCompaniesFromServer(){
        binding.swiperefresh.setRefreshing(true);
        APIMethods.search(1, binding.searchEt.getText().toString().trim(), selectedCategoryId, selectedState, new APIResponseListener<SearchRP>() {
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

    private void setScrollListener() {
        binding.swiperefresh.setOnRefreshListener(this::fetchCompaniesFromServer);
        binding.swiperefresh.setColorSchemeResources(R.color.color_cta,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
    }

    private void fetchUnlockedStates() {
        Log.i("Eta-Kw", "Fetching Unlocked States");
        UnlockedStatesRP.getUnlockedStates(new UnlockedStatesRP.OnStatesListener() {
            @Override
            public void onGotUnlockedStates(ArrayList<Integer> states) {
                Log.i("Eta-Kw", "Got Unlocked States");
                initialiseStateSpinner(states);
            }
        }, this);
    }


    private void fetchCompanies() {
        if (!isSearchChange){
            return;
        }

        processSemaphore++;
//        Utils.hideSoftKeyboard(this);
        isSearchChange = false;
        updateUI();

        fetchCompaniesFromServer();
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

        initialiseStateSpinner(null);
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
        binding.swiperefresh.setRefreshing(false);
    }

    private void initialiseStateSpinner(ArrayList<Integer> unlockedStates) {
        buildStateList(unlockedStates);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                R.layout.item_spinner,stateList);
        spinnerAdapter.setDropDownViewResource(R.layout.item_dropdown);
        binding.stateSpinner.setAdapter(spinnerAdapter);
        binding.stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("eta-search", "stateSelected");
                position = positionStateCodeMap.get(position);
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

    private void buildStateList(ArrayList<Integer> unlockedStates) {
        Log.i("Eta-Kw", "Building Unlocked States");
        int counter = 0;
        List<String> allStatesList = Constants.getIndianStates();
        stateList = new ArrayList<String>();
        positionStateCodeMap = new HashMap<>();


        //All State Option
        String g= "All States";
        stateList.add(g);
        positionStateCodeMap.put(counter, 0);
        counter++;

        //Unlocked States
        if (unlockedStates != null && unlockedStates.size() > 0){
            for (Integer sc: unlockedStates) {
                stateList.add(allStatesList.get(sc));
                positionStateCodeMap.put(counter, sc);
                counter++;
            }
        }

        for (int i = 1; i < allStatesList.size(); i++){
            if (unlockedStates != null && unlockedStates.size() > 0
            && unlockedStates.contains(i)){
                continue;
            }

            stateList.add(allStatesList.get(i));
            positionStateCodeMap.put(counter, i);
            counter++;
        }
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