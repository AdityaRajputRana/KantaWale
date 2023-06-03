package com.guru.kantewala.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.guru.kantewala.ImageViewActivity;
import com.guru.kantewala.Models.CompanyImages;
import com.guru.kantewala.R;
import com.guru.kantewala.Tools.Transformations.RoundedCornerTransformation;
import com.squareup.picasso.Picasso;

public class PhotosRVAdapter extends RecyclerView.Adapter<PhotosRVAdapter.PhotoViewHolder> {

    CompanyImages.ImageBlock block;
    Context context;

    CompanyImagesRVAdapter.EditListener listener;
    boolean inEditMode = false;

    public PhotosRVAdapter(CompanyImages.ImageBlock block, Context context) {
        this.block = block;
        this.context = context;
    }

    public PhotosRVAdapter(CompanyImages.ImageBlock block, Context context, CompanyImagesRVAdapter.EditListener listener, boolean inEditMode) {
        this.block = block;
        this.context = context;
        this.listener = listener;
        this.inEditMode = inEditMode;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PhotoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_photo, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        if (block.getImages() == null) {
            Picasso.get()
                    .load(block.getPhotos().get(position))
                    .into(holder.imageView);
        } else {
            Picasso.get()
                    .load(block.getImages().get(position).getThumbUrl())
                    .into(holder.imageView);
        }

        holder.actionImageView.setVisibility(View.GONE);
        if (inEditMode){
            holder.actionImageView.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(view->{
            handleClick(holder.getAdapterPosition());
        });

    }

    private void handleClick(int adapterPosition) {
        if (inEditMode)
            confirmDelete(adapterPosition);
        else
            enlarge(adapterPosition);
    }

    private void enlarge(int adapterPosition) {
        String thumbUrl;
        String url;

        if (block.getImages() == null){
            thumbUrl = url = block.getPhotos().get(adapterPosition);
        } else {
            thumbUrl = block.getImages().get(adapterPosition).getThumbUrl();
            url = block.getImages().get(adapterPosition).getUrl();
        }

        Intent i = new Intent(context, ImageViewActivity.class);
        i.putExtra("thumbUrl", thumbUrl);
        i.putExtra("url", url);
        context.startActivity(i);
    }

    private void confirmDelete(int adapterPosition) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Image")
                .setMessage("Are you sure you want to remove this image from the the block")
                .setPositiveButton("Delete", (dialog, which) -> listener.deleteImage(block.getImages().get(adapterPosition)))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }

    @Override
    public int getItemCount() {
        return block.getPhotos().size();
    }

    class PhotoViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        ImageView actionImageView;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            actionImageView = itemView.findViewById(R.id.actionImageView);
        }
    }
}
