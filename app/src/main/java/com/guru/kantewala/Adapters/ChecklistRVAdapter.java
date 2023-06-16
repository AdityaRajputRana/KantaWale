package com.guru.kantewala.Adapters;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.guru.kantewala.Models.State;
import com.guru.kantewala.R;

import java.util.ArrayList;
import java.util.Locale;

public class ChecklistRVAdapter extends RecyclerView.Adapter<ChecklistRVAdapter.ChecklistViewHolder> {

    public interface ChecklistListener{
        void showCheckout(boolean toShow);
    }

    ArrayList<State> allStates;
    ArrayList<State> filteredStates;
    ArrayList<State> selectedStates;
    int maxAllowedCheck = 0;
    EditText searchEditText;
    ChecklistListener listener;
    String filter;
    Activity activity;
    ArrayList<Integer> restrictedStateCodes = new ArrayList<>();

    public ArrayList<State> getSelectedStates() {
        return selectedStates;
    }

    boolean areItemsUpdatedFlag = false;

    public ChecklistRVAdapter(ArrayList<String> states, EditText searchEditText, ChecklistListener listener, int maxAllowedCheck,
                              RecyclerView recyclerView, Activity activity, ArrayList<Integer> unlockedStates) {
        this.restrictedStateCodes = unlockedStates;
        buildStateLists(states);
        this.maxAllowedCheck = maxAllowedCheck;
        selectedStates = new ArrayList<>();
        this.searchEditText = searchEditText;
        this.listener = listener;
        this.activity = activity;
        if (searchEditText != null){
            setUpSearchETListener();
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE && areItemsUpdatedFlag){
                    areItemsUpdatedFlag = false;
                    itemRangeChanged();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    private void buildStateLists(ArrayList<String> states) {
        allStates = new ArrayList<State>();
        filteredStates = new ArrayList<State>();
        for (int i = 0; i < states.size(); i++){
            if (restrictedStateCodes.contains(i+1))
                continue;
            State state = new State(states.get(i), i+1);
            allStates.add(state);
            filteredStates.add(state);
        }
    }

    private void setUpSearchETListener() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter = searchEditText.getText().toString();
                filter();
            }
        });
    }

    private void filter() {
        filteredStates = new ArrayList<State>();
        for (int i = 0; i < allStates.size(); i++){
            State state = allStates.get(i);
            if (state.getName().toLowerCase().contains(filter.toLowerCase())){
                filteredStates.add(state);
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChecklistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChecklistViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_state_checkbox, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChecklistViewHolder holder, int position) {
        State state = filteredStates.get(position);
        holder.checkBox.setText(state.getName());
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(selectedStates.contains(state));
        if (!selectedStates.contains(state)){
            holder.checkBox.setEnabled(selectedStates.size() < maxAllowedCheck);
        } else {
            holder.checkBox.setEnabled(true);
        }

        if (holder.checkBox.isEnabled()){
            holder.checkBox.setTextColor(activity.getResources()
                    .getColor(R.color.primaryText));
        } else {
            holder.checkBox.setTextColor(activity.getResources()
                    .getColor(R.color.tertiaryText));
        }

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateValues(isChecked, holder.getAdapterPosition());
            }
        });
    }

    private void updateValues(boolean isChecked, int index) {
        State sState = filteredStates.get(index);
        if (isChecked){
            selectedStates.add(sState);
        } else {
            selectedStates.remove(sState);
        }

        areItemsUpdatedFlag = true;
        try {
            itemRangeChanged();
        } catch (Exception e){
            e.printStackTrace();
        }
        listener.showCheckout(selectedStates.size() == maxAllowedCheck);
    }

    public void itemRangeChanged(){
        notifyItemRangeChanged(0, filteredStates.size());
        Log.i("KW-L-SS", String.valueOf(selectedStates.size()));
        Log.i("KW-L-MAC", String.valueOf(maxAllowedCheck));
        listener.showCheckout(selectedStates.size() == maxAllowedCheck);
    }

    @Override
    public int getItemCount() {
        return filteredStates.size();
    }

    class ChecklistViewHolder extends RecyclerView.ViewHolder{
        CheckBox checkBox;

        public ChecklistViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox  = itemView.findViewById(R.id.checkbox);
        }
    }
}
