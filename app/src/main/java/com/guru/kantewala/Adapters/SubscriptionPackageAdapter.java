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
import com.guru.kantewala.PlanDetailsActivity;
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
        if (subscriptionPackagesRP.isPremiumUser()){
            position--;
        }

        if (position == -1){
            holder.titleTxt.setVisibility(View.GONE);
            holder.itemView.setBackground(null);
            holder.titleTxt.setText("View Your Plans");
            holder.bodyTxt.setText("You have one or more active plans. Tap here to view.");
            holder.radioButton.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(view->{
                activity.startActivity(new Intent(activity, PlanDetailsActivity.class));
            });
            holder.radioButton.setVisibility(View.GONE);
        } else if (position == subscriptionPackagesRP.getSubscriptionPackages().size()){
            holder.titleTxt.setVisibility(View.VISIBLE);
            holder.itemView.setBackground(activity.getResources().getDrawable(R.drawable.bg_subsciption_package));
            holder.titleTxt.setText("Recommended Listing");
            holder.bodyTxt.setText("List your company as recommended company on the first page for Rs. 99,900 per 6 months");
            holder.radioButton.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(view->{
                activity.startActivity(new Intent(activity, InformationActivity.class));
            });
        } else {
            holder.titleTxt.setVisibility(View.VISIBLE);
            if (activity != null)
                holder.itemView.setBackground(activity.getResources().getDrawable(R.drawable.bg_subsciption_package));
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
        if (subscriptionPackagesRP.isPremiumUser())
            newSelectedPack --;
        int oldPack = selectedPackageIndex;
        selectedPackageIndex = newSelectedPack;

        int oldNotifIndex = oldPack;
        int newNotifIndex = selectedPackageIndex;

        if (subscriptionPackagesRP.isPremiumUser()){
            oldNotifIndex++;
            newNotifIndex++;
        }
        notifyItemChanged(oldNotifIndex);
        notifyItemChanged(newNotifIndex);
    }

    @Override
    public int getItemCount() {
        int adder = 1;
        if (subscriptionPackagesRP.isPremiumUser())
            adder++;
        return subscriptionPackagesRP.getSubscriptionPackages().size() + adder;
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
