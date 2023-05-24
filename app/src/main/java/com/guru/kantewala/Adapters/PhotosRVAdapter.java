package com.guru.kantewala.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.guru.kantewala.Models.CompanyImages;
import com.guru.kantewala.R;
import com.guru.kantewala.Tools.Transformations.RoundedCornerTransformation;
import com.squareup.picasso.Picasso;

public class PhotosRVAdapter extends RecyclerView.Adapter<PhotosRVAdapter.PhotoViewHolder> {

    CompanyImages.ImageBlock block;
    Context context;

    public PhotosRVAdapter(CompanyImages.ImageBlock block, Context context) {
        this.block = block;
        this.context = context;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PhotoViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_photo, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        Picasso.get()
                .load(block.getPhotos().get(position))
                .transform(new RoundedCornerTransformation(16, 0))
                .into(holder.imageView);

        //Todo: can be full screened image on click
    }

    @Override
    public int getItemCount() {
        return block.getPhotos().size();
    }

    class PhotoViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
