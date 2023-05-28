package com.guru.kantewala.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.guru.kantewala.Models.SubscriptionPackage;
import com.guru.kantewala.R;
import com.guru.kantewala.rest.response.SubscriptionPackagesRP;

import org.w3c.dom.Text;

public class SubscriptionPackageAdapter extends RecyclerView.Adapter<SubscriptionPackageAdapter.SubPackageViewHolder> {

    SubscriptionPackagesRP subscriptionPackagesRP;
    int selectedPackageIndex;

    public SubscriptionPackage getSelectedPack(){
        return subscriptionPackagesRP.getSubscriptionPackages().get(selectedPackageIndex);
    }

    public SubscriptionPackageAdapter(SubscriptionPackagesRP subscriptionPackagesRP) {
        this.subscriptionPackagesRP = subscriptionPackagesRP;
        this.selectedPackageIndex = 0;
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
        SubscriptionPackage pack = subscriptionPackagesRP.getSubscriptionPackages().get(position);
        holder.itemView.setSelected(selectedPackageIndex == position);
        holder.radioButton.setChecked(selectedPackageIndex == position);
        holder.titleTxt.setText(pack.getTitle());
        holder.bodyTxt.setText(pack.getBody());
        holder.itemView.setOnClickListener(view->{
            select(holder.getAdapterPosition());
        });
    }

    private void select(int newSelectedPack) {
        int oldPack = selectedPackageIndex;
        selectedPackageIndex = newSelectedPack;
        notifyItemChanged(oldPack);
        notifyItemChanged(selectedPackageIndex);
    }

    @Override
    public int getItemCount() {
        return subscriptionPackagesRP.getSubscriptionPackages().size();
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
