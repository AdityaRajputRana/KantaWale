package com.guru.kantewala.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.guru.kantewala.CompanyActivity;
import com.guru.kantewala.Helpers.SubscriptionInterface;
import com.guru.kantewala.Models.Company;
import com.guru.kantewala.R;
import com.guru.kantewala.Tools.BlurUtils;
import com.guru.kantewala.databinding.SheetSubscribeBinding;
import com.guru.kantewala.rest.response.SearchRP;

public class SearchRVAdapter extends RecyclerView.Adapter<SearchRVAdapter.CompanyViewHolder> {

    SearchRP searchRP;
    Activity context;

    public SearchRVAdapter(SearchRP searchRP, Activity context) {
        this.searchRP = searchRP;
        this.context = context;
    }

    @NonNull
    @Override
    public CompanyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CompanyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_search, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CompanyViewHolder holder, int position) {
        Company company = searchRP.getRecommendedCompanies().get(position);
        holder.nameTxt.setText(company.getName());
        if (!company.isLocked() && company.getLocation() != null && !company.getLocation().isEmpty())
            holder.locationTxt.setText(company.getLocation());


        for (String tag: company.getTags()){
            Chip chip = new Chip(context);
            chip.setText(tag);
            chip.setClickable(false);
            chip.setCheckable(false);
            chip.setChipStrokeColor(ColorStateList.valueOf(context.getResources().getColor(R.color.color_cta)));
            chip.setChipStrokeWidth(5);
            chip.setChipBackgroundColor(ColorStateList.valueOf(Color.TRANSPARENT));
            chip.setRippleColor(ColorStateList.valueOf(context
                    .getResources().getColor(R.color.color_bg)));
            chip.setTextColor(context.getResources().getColor(R.color.color_cta));
            holder.tagsGroup.addView(chip);
        }

        holder.itemView.setOnClickListener(view->{
            handleCompanyClick(holder.getAdapterPosition());
        });

        if (company.isLocked()){
            BlurUtils.blur(holder.locationTxt, 20f);
        } else {
            BlurUtils.blur(holder.locationTxt, 0f);
        }
    }

    private void handleCompanyClick(int index) {
        Log.i("KW-QL-Index", String.valueOf(index));
        Company company = searchRP.getRecommendedCompanies().get(index);
        if (company.isLocked()){
            askToSubscribe();
            return;
        } else {

            Intent intent = new Intent(context, CompanyActivity.class);
            intent.putExtra("companyId", company.getId());
            intent.putExtra("hasCompanyAttached", true);
            intent.putExtra("company", new Gson().toJson(company));

            context.startActivity(intent);
        }
    }

    private void askToSubscribe() {

        SheetSubscribeBinding sheet = SheetSubscribeBinding.inflate(context.getLayoutInflater());
        BottomSheetDialog dialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
        //Todo: handle Starting from price
        //Todo: handle subscribed already click
        sheet.continueBtn.setOnClickListener(view->{
            if (context instanceof SubscriptionInterface.AskToSubListener)
                ((SubscriptionInterface.AskToSubListener) context).redirectToSubscribeFragment();
            dialog.dismiss();
        });
        dialog.setContentView(sheet.getRoot());
        dialog.show();
    }

    @Override
    public int getItemCount() {
        if (searchRP == null)
            return 0;
        return searchRP.getRecommendedCompanies().size();
    }

    class CompanyViewHolder extends RecyclerView.ViewHolder{

        TextView nameTxt;
        TextView locationTxt;
        ChipGroup tagsGroup;

        public CompanyViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTxt = itemView.findViewById(R.id.companyNameTxt);
            locationTxt = itemView.findViewById(R.id.locationTxt);
            tagsGroup = itemView.findViewById(R.id.tagsGroup);
        }
    }
}
