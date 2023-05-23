package com.guru.kantewala.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.guru.kantewala.Models.Category;
import com.guru.kantewala.R;
import com.guru.kantewala.SearchActivity;
import com.guru.kantewala.rest.response.CategoryRP;
import com.squareup.picasso.Picasso;

public class CategoryRVAdapter extends RecyclerView.Adapter<CategoryRVAdapter.CategoryViewHolder> {

    CategoryRP categoryRP;
    Activity context;

    public CategoryRVAdapter(CategoryRP categoryRP, Activity context) {
        this.categoryRP = categoryRP;
        this.context = context;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_category, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryRP.getCategories().get(position);
        holder.titleTxt.setText(category.getName());
        Picasso.get()
                .load(category.getLogoUrl())
                .placeholder(R.drawable.ic_laucher_foreground)
                .into(holder.imageView);

        holder.itemView.setOnClickListener(view->handleCategoryClick(holder.getAdapterPosition()));
    }

    private void handleCategoryClick(int index) {
        Gson gson = new Gson();
        Category category = categoryRP.getCategories().get(index);
        Intent i = new Intent(context, SearchActivity.class);
        i.putExtra("categoryId", category.getId());
        i.putExtra("isCategoryAttached", true);
        i.putExtra("category", gson.toJson(category));
        i.putExtra("isCategoryRPAttached", true);
        i.putExtra("categoryRP", gson.toJson(categoryRP));

        context.startActivity(i);
    }

    @Override
    public int getItemCount() {
        return categoryRP.getCategories().size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder{

        TextView titleTxt;
        ImageView imageView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTxt = itemView.findViewById(R.id.titleTxt);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
