package asia.ifarmer.imagepicker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;


import com.example.iimagepicker.databinding.ActivityImagePreviewBinding;

import java.util.ArrayList;

import asia.ifarmer.imagepicker.views.MainActivity;

public class ImagePreviewActivity extends AppCompatActivity {

    private ActivityImagePreviewBinding binding;
    //private ImagePreviewRVAdapter imagePreviewRVAdapter;
    private ArrayList<String> images = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImagePreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //imagePreviewRVAdapter = new ImagePreviewRVAdapter(this, images);

        binding.rvImageGrid.setLayoutManager(new GridLayoutManager(this, 3));
        //binding.rvImageGrid.setAdapter(imagePreviewRVAdapter);
        //binding.rvImageGrid.addItemDecoration(new GridImageDecoration(6,3));

        binding.btTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("donme", "done");
                startActivity(new Intent(ImagePreviewActivity.this, MainActivity.class));
            }
        });

//        Glide.with(this)
//                .load(MainActivity.bitmap)
//                .into(binding.ivImage);
    }

    @Override
    protected void onResume() {
        super.onResume();
        images.clear();
        //images.addAll(MainActivity.selectedImages.keySet());
//        if (imagePreviewRVAdapter!=null){
//            imagePreviewRVAdapter.notifyDataSetChanged();
//        }
    }
}