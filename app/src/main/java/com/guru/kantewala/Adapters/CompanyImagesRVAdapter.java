package com.guru.kantewala.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.guru.kantewala.Models.CompanyImages;
import com.guru.kantewala.R;

public class CompanyImagesRVAdapter extends RecyclerView.Adapter<CompanyImagesRVAdapter.ImageBlockViewHolder> {
    CompanyImages companyImages;
    Activity context;

    public CompanyImagesRVAdapter(CompanyImages companyImages, Activity context) {
        this.companyImages = companyImages;
        this.context = context;
    }

    @NonNull
    @Override
    public ImageBlockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImageBlockViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_image_block, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ImageBlockViewHolder holder, int position) {
        CompanyImages.ImageBlock block = companyImages.getBlocks().get(position);
        holder.titleTxt.setText(block.getTitle());

        holder.photoRV.setAdapter(new PhotosRVAdapter(block, context));
        holder.photoRV.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
    }

    @Override
    public int getItemCount() {
        return companyImages.getBlocks().size();
    }

    public class ImageBlockViewHolder extends RecyclerView.ViewHolder{

        TextView titleTxt;
        RecyclerView photoRV;

        public ImageBlockViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            photoRV = itemView.findViewById(R.id.photoRV);
        }
    }
}
