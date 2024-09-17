package com.example.iimagepicker.views;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.iimagepicker.R;
import com.example.iimagepicker.databinding.ActivityImagePreviewBinding;

public class ImagePreviewActivity extends AppCompatActivity {

    private ActivityImagePreviewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImagePreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Glide.with(this)
                .load(MainActivity.bitmap)
                .into(binding.ivImage);
    }
}