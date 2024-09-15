package com.example.iimagepicker;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.READ_MEDIA_VIDEO;
import static android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.webkit.ConsoleMessage.MessageLevel.LOG;

import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.iimagepicker.databinding.ActivityMainBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.VideoResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {


    private BottomSheetBehavior sheetBehavior;
    private ActivityMainBinding binding;
    private ArrayList<String> images = new ArrayList<>();
    //    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
//    //private ActivityResultLauncher<String[]> permissionLauncher;
//    private ActivityResultLauncher<Intent> startActivityForResult;
    private ImageRvAdapter imageRvAdapter;
    private int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //requestPermissions();

        //binding.mainContent.camera.setLifecycleOwner(this);

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

        binding.bottomSheet.ivGallery.setOnClickListener(view -> {
            if (!binding.bottomSheet.mainRecyclerView.canScrollVertically(-1)) {
                if (!sheetBehavior.isDraggable()) {
                    sheetBehavior.setDraggable(true);
                }
            } else {
                if (sheetBehavior.isDraggable()) {
                    sheetBehavior.setDraggable(false);
                }
            }
        });


        sheetBehavior = BottomSheetBehavior.from(binding.bottomSheet.bottomSheet);
        imageRvAdapter = new ImageRvAdapter(MainActivity.this, images);

        binding.bottomSheet.peekRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, RecyclerView.HORIZONTAL, false));
        binding.bottomSheet.peekRecyclerView.setAdapter(imageRvAdapter);

        binding.bottomSheet.mainRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 4));
        binding.bottomSheet.mainRecyclerView.setAdapter(imageRvAdapter);
        binding.bottomSheet.mainRecyclerView.scrollToPosition(0);

        binding.bottomSheet.ivSwitchCamera.setOnClickListener(view -> {
            binding.mainContent.camera.toggleFacing();
        });

        binding.bottomSheet.mainRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (!binding.bottomSheet.mainRecyclerView.canScrollVertically(-1)) {
                    if (!sheetBehavior.isDraggable()) {
                        sheetBehavior.setDraggable(true);
                    }
                } else {
                    if (sheetBehavior.isDraggable()) {
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
                binding.bottomSheet.peekView.setAlpha(1.0f - v);
                binding.bottomSheet.collapsedView.setAlpha(v);
            }
        });

        //binding.mainContent.camera.setRequestPermissions(true);

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
        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            int grantResult = grantResults[i];

            if (permission.equals(CAMERA)) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    //onPPSButtonPress();
                } else {
                    //Log.e("TAG", "onRequestPermissionsResult: emmmm");
                    //requestPermissions(new String[]{Manifest.permission.SEND_SMS}, PERMISSIONS_CODE);
                }
            }
        }
    }
    private void getImagePath() {
        // in this method we are adding all our image paths
        // in our arraylist which we have created.
        // on below line we are checking if the device is having an sd card or not.
        //images.clear();
        boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);

        if (isSDPresent) {

            // if the sd card is present we are creating a new list in
            // which we are getting our images data with their ids.
            final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};

            // on below line we are creating a new
            // string to order our images by string.
            final String orderBy = MediaStore.Images.Media._ID;

            // this method will stores all the images
            // from the gallery in Cursor
            Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);

            // below line is to get total number of images
            int count = cursor.getCount();
            Log.e("TAG", "getImagePath: " + count);
            // on below line we are running a loop to add
            // the image file path in our array list.
            for (int i = 0; i < 10; i++) {

                // on below line we are moving our cursor position
                cursor.moveToPosition(i);

                // on below line we are getting image file path
                int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);

                // after that we are getting the image file path
                // and adding that path in our array list.
                images.add(cursor.getString(dataColumnIndex));
            }
            imageRvAdapter.notifyDataSetChanged();
            // after adding the data to our
            // array list we are closing our cursor.
            cursor.close();
        }
    }

    private final ActivityResultLauncher<String[]> permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
        checkPermission();
        //getImagePath();
    });

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        //requestPermissions();
                    }
                }
            });


    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && (ContextCompat.checkSelfPermission(this, READ_MEDIA_IMAGES) == PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, READ_MEDIA_VIDEO) == PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, CAMERA) == PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, RECORD_AUDIO) == PERMISSION_GRANTED
        )
        ) {
            // Full access on Android 13 (API level 33) or higher
            //cardLayout.visibility = View.GONE
            getImagePath();
        } else if (
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE &&
                        ContextCompat.checkSelfPermission(this, READ_MEDIA_VISUAL_USER_SELECTED) == PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(this, CAMERA) == PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(this, RECORD_AUDIO) == PERMISSION_GRANTED
        ) {
            // Partial access on Android 14 (API level 34) or higher
//            textView.text = "你已授权访问部分相册的照片和视频"
//            button.text = "管理"
//            cardLayout.visibility = View.VISIBLE
            getImagePath();
        } else if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, CAMERA) == PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, RECORD_AUDIO) == PERMISSION_GRANTED
        ) {
            // Full access up to Android 12 (API level 32)
            //cardLayout.visibility = View.GONE
            getImagePath();
        } else {
            Log.e("TAG", "checkPermission: denied");
//            new AlertDialog.Builder(this)
//                    .setTitle("Permission")
//                    .setMessage("")
//                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            dialogInterface.dismiss();
//                            someActivityResultLauncher.launch(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//                                    .setData(Uri.fromParts("package", getPackageName(), null)));
//                        }
//                    }).show();

        }
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            permissionLauncher.launch(
                    new String[]{CAMERA, READ_MEDIA_IMAGES,
                            RECORD_AUDIO,
                            READ_MEDIA_VIDEO}
            );
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(new String[]{CAMERA, RECORD_AUDIO, READ_MEDIA_IMAGES, READ_MEDIA_VIDEO});
        } else {
            permissionLauncher.launch(new String[]{CAMERA, RECORD_AUDIO, READ_EXTERNAL_STORAGE});
        }
    }

}