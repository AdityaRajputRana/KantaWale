package com.guru.kantewala.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.guru.kantewala.Models.CompanyImages;
import com.guru.kantewala.R;

public class CompanyImagesRVAdapter extends RecyclerView.Adapter<CompanyImagesRVAdapter.ImageBlockViewHolder> {
    CompanyImages companyImages;
    Activity context;
    boolean inEditMode = false;
    EditListener listener;

    public interface EditListener{
        void addImage(CompanyImages.ImageBlock block);
        void deleteBlock(CompanyImages.ImageBlock block);
        void editBlock(CompanyImages.ImageBlock block);
    }

    public CompanyImagesRVAdapter(CompanyImages companyImages, Activity context) {
        this.companyImages = companyImages;
        this.context = context;
    }

    public CompanyImagesRVAdapter(CompanyImages companyImages, Activity context, boolean inEditMode, EditListener listener) {
        this.companyImages = companyImages;
        this.context = context;
        this.inEditMode = inEditMode;
        this.listener = listener;
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
        if (inEditMode){
            holder.deleteBtn.setVisibility(View.VISIBLE);
            holder.editBtn.setVisibility(View.VISIBLE);
            holder.addBtn.setVisibility(View.VISIBLE);

            holder.deleteBtn.setOnClickListener(view -> listener.deleteBlock(block));
            holder.editBtn.setOnClickListener(view -> listener.editBlock(block));
            holder.addBtn.setOnClickListener(view -> listener.addImage(block));
        }
    }

    @Override
    public int getItemCount() {
        return companyImages.getBlocks().size();
    }

    public class ImageBlockViewHolder extends RecyclerView.ViewHolder{

        TextView titleTxt;
        RecyclerView photoRV;

        ImageButton editBtn;
        ImageButton deleteBtn;
        ImageButton addBtn;

        public ImageBlockViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            photoRV = itemView.findViewById(R.id.photoRV);

            editBtn = itemView.findViewById(R.id.editBtn);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            addBtn = itemView.findViewById(R.id.addPhotoBtn);
        }
    }
}
