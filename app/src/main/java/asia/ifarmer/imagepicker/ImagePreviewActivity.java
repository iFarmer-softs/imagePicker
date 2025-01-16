package asia.ifarmer.imagepicker;

import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.viewpager.widget.PagerAdapter;


import com.bumptech.glide.Glide;
import com.example.iimagepicker.databinding.ActivityImagePreviewBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import asia.ifarmer.imagepicker.events.ImageSelectionListener;
import asia.ifarmer.imagepicker.views.ImagerActivity;

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
        binding.rvImageGrid.addItemDecoration(new GridImageDecoration(6, 3));

        imagePreviewRVAdapter.setOnItemCLickedListener(new ImagePreviewRVAdapter.OnItemCLickedListener() {
            @Override
            public void onItemClick(int position) {
                binding.viewPager.setVisibility(VISIBLE);
                Log.e("daf", "dasf 3");
                ImagePagerAdapter imagePagerAdapter = new ImagePagerAdapter();
                binding.viewPager.setAdapter(imagePagerAdapter);
            }
        });

        binding.btTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("donme", "done");
                ImagePicker.with(ImagePreviewActivity.this)
                        .start(new ImageSelectionListener() {
                            @Override
                            public void onImageSelected(LinkedHashMap<String, String> selectedImages) {
                                Log.e("size", selectedImages.size() + "");
                                images.addAll(selectedImages.keySet());
                                if (imagePreviewRVAdapter != null) {
                                    imagePreviewRVAdapter.notifyDataSetChanged();
                                }
                                for (String s : selectedImages.keySet()) {
                                    Log.e("size", s + " " + new File(s).length());
                                }
                            }
                        });
            }
        });


    }


    public class ImagePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // Inflate the layout and set the image
            ImageView imageView = new ImageView(ImagePreviewActivity.this);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));

            //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(ImagePreviewActivity.this)
                            .load(images.get(position))
                                    .into(imageView);

            // Add the image view to the container
            container.addView(imageView);

            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}