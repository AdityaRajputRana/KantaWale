package com.guru.kantewala.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.guru.kantewala.Models.PlanDetails;
import com.guru.kantewala.Models.SubscriptionPack;
import com.guru.kantewala.R;
import com.guru.kantewala.databinding.ItemPlanDetailsBinding;
import com.guru.kantewala.rest.response.PackHistoryRP;

import java.util.ArrayList;

public class PlanDetailsAdapter extends RecyclerView.Adapter<PlanDetailsAdapter.PlanViewHolder> {

    PackHistoryRP historyRP;
    Activity activity;

    public PlanDetailsAdapter(PackHistoryRP historyRP, Activity activity) {
        this.historyRP = historyRP;
        this.activity = activity;
    }

    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PlanViewHolder(ItemPlanDetailsBinding.inflate(LayoutInflater.from(parent.getContext())));
    }

    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
        PlanDetails myDetails = historyRP.getHistory().get(position);

        holder.binding.planTitle.setText(myDetails.getTitle());
        holder.binding.planBody.setText(myDetails.getBody());
        holder.binding.statesTxt.setText(myDetails.getStatesAsString());
        holder.binding.subscriptionId.setText(
                "Id                         : " + myDetails.getId()
        );

        holder.binding.orderIdTxt.setText(
                "Order Id             : " + myDetails.getOrder_id()
        );

        holder.binding.validTillTxt.setText(
               "Valid Till            : " + myDetails.getValidOn()
        );

        holder.binding.purchasedOnTxt.setText(
                "Purchased On : " + myDetails.getPurchasedOn()
        );

        holder.binding.myPackDetailsLayout.setVisibility(View.VISIBLE);

        if (!myDetails.isActive()){
            holder.binding.statusTxt.setTextColor(activity.getResources().getColor(R.color.inactiveColorFg));
            holder.binding.statusTxt.setBackgroundColor(activity.getResources().getColor(R.color.inactiveColorBG));
            holder.binding.statusTxt.setText("INACTIVE");
            if (myDetails.getPurchasedOn() == null){
                holder.binding.statusTxt.setText("FAILED");
            }
        } else if (myDetails.isExpired()){
            holder.binding.statusTxt.setTextColor(activity.getResources().getColor(R.color.expiredColorFg));
            holder.binding.statusTxt.setBackgroundColor(activity.getResources().getColor(R.color.expiredColorBG));
            holder.binding.statusTxt.setText("EXPIRED");
        } else {
            holder.binding.statusTxt.setTextColor(activity.getResources().getColor(R.color.activeColorFg));
            holder.binding.statusTxt.setBackgroundColor(activity.getResources().getColor(R.color.activeColorBG));
            holder.binding.statusTxt.setText("ACTIVE");
        }

    }

    @Override
    public int getItemCount() {
        return historyRP.getHistory().size();
    }

    class PlanViewHolder extends RecyclerView.ViewHolder{

        ItemPlanDetailsBinding binding;

        public PlanViewHolder(ItemPlanDetailsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
