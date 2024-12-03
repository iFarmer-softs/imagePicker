package com.example.iimagepicker.views;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.example.iimagepicker.R;
import com.example.iimagepicker.adapters.ImagePreviewRVAdapter;
import com.example.iimagepicker.adapters.ImageRvGridAdapter;
import com.example.iimagepicker.databinding.ActivityImagePreviewBinding;
import com.example.iimagepicker.models.Image;

import java.util.ArrayList;

public class ImagePreviewActivity extends AppCompatActivity {

    private ActivityImagePreviewBinding binding;
    private ImagePreviewRVAdapter imagePreviewRVAdapter;
    private ArrayList<String> images = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImagePreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        imagePreviewRVAdapter = new ImagePreviewRVAdapter(this, images);

        binding.rvImageGrid.setLayoutManager(new GridLayoutManager(this, 3));
        binding.rvImageGrid.setAdapter(imagePreviewRVAdapter);

        binding.btTakePicture.setOnClickListener(v -> startActivity(new Intent(this, MainActivity.class)));

//        Glide.with(this)
//                .load(MainActivity.bitmap)
//                .into(binding.ivImage);
    }

    @Override
    protected void onResume() {
        super.onResume();
        images.clear();
        images.addAll(MainActivity.selectedImages.keySet());
        if (imagePreviewRVAdapter!=null){
            imagePreviewRVAdapter.notifyDataSetChanged();
        }
    }
}