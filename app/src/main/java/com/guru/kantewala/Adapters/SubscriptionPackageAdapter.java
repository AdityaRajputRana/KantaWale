package com.guru.kantewala.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.guru.kantewala.InformationActivity;
import com.guru.kantewala.Models.SubscriptionPack;
import com.guru.kantewala.R;
import com.guru.kantewala.rest.response.SubscriptionPackagesRP;

public class SubscriptionPackageAdapter extends RecyclerView.Adapter<SubscriptionPackageAdapter.SubPackageViewHolder> {

    SubscriptionPackagesRP subscriptionPackagesRP;
    Activity activity;
    int selectedPackageIndex;

    public SubscriptionPack getSelectedPack(){
        return subscriptionPackagesRP.getSubscriptionPackages().get(selectedPackageIndex);
    }

    public SubscriptionPackageAdapter(SubscriptionPackagesRP subscriptionPackagesRP, Activity activity) {
        this.subscriptionPackagesRP = subscriptionPackagesRP;
        this.selectedPackageIndex = 0;
        this.activity = activity;
    }

    @NonNull
    @Override
    public SubPackageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SubPackageViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subscrition_package, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SubPackageViewHolder holder, int position) {
        if (position == subscriptionPackagesRP.getSubscriptionPackages().size()){
            holder.titleTxt.setText("Recommended Listing");
            holder.bodyTxt.setText("List your company as recommended company on the first page for Rs. 99,900 per 6 months");
            holder.radioButton.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(view->{
                activity.startActivity(new Intent(activity, InformationActivity.class));
            });
        } else {
            SubscriptionPack pack = subscriptionPackagesRP.getSubscriptionPackages().get(position);
            holder.itemView.setSelected(selectedPackageIndex == position);
            holder.radioButton.setChecked(selectedPackageIndex == position);
            holder.titleTxt.setText(pack.getTitle());
            holder.bodyTxt.setText(pack.getBody());
            holder.radioButton.setOnClickListener(v -> select(holder.getAdapterPosition()));
            holder.itemView.setOnClickListener(view -> {
                select(holder.getAdapterPosition());
            });
        }
    }

    private void select(int newSelectedPack) {
        int oldPack = selectedPackageIndex;
        selectedPackageIndex = newSelectedPack;
        notifyItemChanged(oldPack);
        notifyItemChanged(selectedPackageIndex);
    }

    @Override
    public int getItemCount() {
        return subscriptionPackagesRP.getSubscriptionPackages().size() + 1;
    }

    class SubPackageViewHolder extends RecyclerView.ViewHolder{
        TextView titleTxt;
        TextView bodyTxt;
        RadioButton radioButton;

        public SubPackageViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            bodyTxt = itemView.findViewById(R.id.bodyTxt);
            radioButton = itemView.findViewById(R.id.radioBtn);
        }
    }
}
