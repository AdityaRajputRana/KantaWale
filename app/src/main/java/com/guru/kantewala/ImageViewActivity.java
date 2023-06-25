package com.guru.kantewala;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.guru.kantewala.databinding.ActivityImageViewBinding;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ImageViewActivity extends AppCompatActivity {

    ActivityImageViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImageViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(view->onBackPressed());
        String thumbUrl = getIntent().getStringExtra("thumbUrl");
        String url = getIntent().getStringExtra("url");
        Picasso.get()
                .load(thumbUrl) // thumbnail url goes here
                .into(binding.imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        Picasso.get()
                                .load(url) // image url goes here
                                .placeholder(binding.imageView.getDrawable())
                                .into(binding.imageView);
                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get()
                                .load(url)
                                .placeholder(R.drawable.ic_laucher_foreground)
                                .into(binding.imageView);
                    }
                });
    }
}