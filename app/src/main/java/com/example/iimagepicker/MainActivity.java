package com.example.iimagepicker;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.camerakit.CameraKitView;
import com.example.iimagepicker.databinding.ActivityMainBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.VideoResult;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {


    private BottomSheetBehavior sheetBehavior;
//    private RelativeLayout bottom_sheet;
//    RecyclerView peekRecyclerView , MainRecyclerView ;
//    CameraKitView cameraKitView;
//    RelativeLayout peekView, collapsedView;
    private ActivityMainBinding binding;
    private String[] permission13 = new String[]{
            android.Manifest.permission.READ_MEDIA_IMAGES,
            android.Manifest.permission.READ_MEDIA_VIDEO,
            android.Manifest.permission.RECORD_AUDIO

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        EasyPermissions.requestPermissions(
                new PermissionRequest.Builder(this, RC_CAMERA_AND_LOCATION, {Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION})
                        .setRationale(R.string.camera_and_location_rationale)
                        .setPositiveButtonText(R.string.rationale_ask_ok)
                        .setNegativeButtonText(R.string.rationale_ask_cancel)
                        .setTheme(R.style.my_fancy_style)
                        .build());

        binding.mainContent.camera.setLifecycleOwner(this);

        binding.mainContent.camera.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(PictureResult result) {

            }

            @Override
            public void onVideoTaken(VideoResult result) {
                // A Video was taken!
            }

            // And much more
        });

//        cameraKitView = findViewById(R.id.camera);
//        bottom_sheet = findViewById(R.id.bottom_sheet);
//        peekRecyclerView = findViewById(R.id.peekRecyclerView);
//        peekView = findViewById(R.id.peekView);
//        collapsedView = findViewById(R.id.collapsedView);
//        MainRecyclerView = findViewById(R.id.MainRecyclerView);
        sheetBehavior = BottomSheetBehavior.from(binding.bottomSheet.bottomSheet);

        Log.e("TAG", "onCreate: "+getAllShownImagesPath().size());
//
        binding.bottomSheet.peekRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, RecyclerView.HORIZONTAL, false));
        binding.bottomSheet.peekRecyclerView.setAdapter(new HomeAdapter(MainActivity.this,getAllShownImagesPath()));

        binding.bottomSheet.mainRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 4));
        binding.bottomSheet.mainRecyclerView.setAdapter(new HomeAdapter(MainActivity.this,getAllShownImagesPath()));
        binding.bottomSheet.mainRecyclerView.scrollToPosition(0);


        binding.bottomSheet.mainRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if(!binding.bottomSheet.mainRecyclerView.canScrollVertically(-1)){
                    if(sheetBehavior.isDraggable()== false){
                        sheetBehavior.setDraggable(true);
                    }
                }else{
                    if(sheetBehavior.isDraggable()==true){
                        sheetBehavior.setDraggable(false);
                    }
                }
            }
        });

        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
            }

            @Override
            public void onSlide(@NonNull View view, float v) {
                binding.bottomSheet.peekView.setAlpha(1.0f-v);
                binding.bottomSheet.collapsedView.setAlpha(v);
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        //binding.mainContent.camera.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //binding.mainContent.camera.onResume();
        binding.mainContent.camera.open();
    }

    @Override
    protected void onPause() {
        //binding.mainContent.camera.onPause();
        super.onPause();
        binding.mainContent.camera.close();
    }

    @Override
    protected void onStop() {
        //binding.mainContent.camera.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.mainContent.camera.destroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //binding.mainContent.camera.setRequestPermissions(true);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);

        //binding.mainContent.camera.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private ArrayList<String> getAllShownImagesPath() {
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        String absolutePathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

        cursor = getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(absolutePathOfImage);
        }
        return listOfAllImages;
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }
}