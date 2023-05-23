package com.guru.kantewala.Adapters;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.guru.kantewala.CompanyActivity;
import com.guru.kantewala.Models.Company;
import com.guru.kantewala.R;
import com.guru.kantewala.rest.response.DashboardRP;
import com.squareup.picasso.Picasso;

public class RecommendationRVAdapter extends RecyclerView.Adapter<RecommendationRVAdapter.CompanyViewHolder> {

    DashboardRP dashboardRP;
    Context context;

    public RecommendationRVAdapter(DashboardRP dashboardRP, Context context) {
        this.dashboardRP = dashboardRP;
        this.context = context;
    }

    @NonNull
    @Override
    public CompanyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CompanyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_company, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CompanyViewHolder holder, int position) {
        Company company = dashboardRP.getRecommendedCompanies().get(position);
        holder.nameTxt.setText(company.getName());
        if (!company.isLocked() && company.getLocation() != null && !company.getLocation().isEmpty())
            holder.locationTxt.setText(company.getLocation());
        Picasso.get()
                .load(company.getLogoUrl())
                .into(holder.logoImg);

        //Todo: Replace Later by chips
        String tagsTxt = "";
        for (String tag: company.getTags()){
            tagsTxt = tagsTxt + " " + tag;
        }
        holder.tagsTxt.setText(tagsTxt);

        holder.itemView.setOnClickListener(view->{
            handleCompanyClick(holder.getAdapterPosition());
        });
        //Todo: Implement onClick
    }

    private void handleCompanyClick(int index) {
        Log.i("KW-QL-Index", String.valueOf(index));
        Company company = dashboardRP.getRecommendedCompanies().get(index);
        if (company.isLocked()){
            Toast.makeText(context, "Buy premium to unlock!", Toast.LENGTH_SHORT).show();
            return;
        } else {

            Intent intent = new Intent(context, CompanyActivity.class);
            intent.putExtra("companyId", company.getId());
            intent.putExtra("hasCompanyAttached", true);
            intent.putExtra("company", new Gson().toJson(company));

            context.startActivity(intent);
        }
    }

    @Override
    public int getItemCount() {
        if (dashboardRP == null)
            return 0;
        return dashboardRP.getRecommendedCompanies().size();
    }

    class CompanyViewHolder extends RecyclerView.ViewHolder{

        TextView nameTxt;
        TextView locationTxt;
        TextView tagsTxt;
        ImageView logoImg;

        public CompanyViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTxt = itemView.findViewById(R.id.companyNameTxt);
            locationTxt = itemView.findViewById(R.id.locationTxt);
            tagsTxt = itemView.findViewById(R.id.tagsTxt);
            logoImg = itemView.findViewById(R.id.companyLogoImg);
        }
    }
}
