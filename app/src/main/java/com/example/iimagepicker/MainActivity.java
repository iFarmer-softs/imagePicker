package com.example.iimagepicker;

import android.Manifest;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

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

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks,
        EasyPermissions.RationaleCallbacks {


    private BottomSheetBehavior sheetBehavior;
    private ActivityMainBinding binding;
    private static final int RC_CAMERA_PERM = 123;
    private static final int RC_STORAGE_PERM = 124;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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

        binding.bottomSheet.clickImage.setOnClickListener(view -> cameraTask());
        binding.bottomSheet.ivGallery.setOnClickListener(view -> Log.e("TAG", "onCreate: " + getAllShownImagesPath()));

        sheetBehavior = BottomSheetBehavior.from(binding.bottomSheet.bottomSheet);

        binding.bottomSheet.peekRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, RecyclerView.HORIZONTAL, false));
        binding.bottomSheet.peekRecyclerView.setAdapter(new HomeAdapter(MainActivity.this, getAllShownImagesPath()));

        binding.bottomSheet.mainRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 4));
        binding.bottomSheet.mainRecyclerView.setAdapter(new HomeAdapter(MainActivity.this, getAllShownImagesPath()));
        binding.bottomSheet.mainRecyclerView.scrollToPosition(0);

        binding.bottomSheet.ivSwitchCamera.setOnClickListener(view -> {
            binding.mainContent.camera.toggleFacing();
        });

        binding.bottomSheet.mainRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (!binding.bottomSheet.mainRecyclerView.canScrollVertically(-1)) {
                    if (sheetBehavior.isDraggable() == false) {
                        sheetBehavior.setDraggable(true);
                    }
                } else {
                    if (sheetBehavior.isDraggable() == true) {
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
        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN
        };

// content:// style URI for the "primary" external storage volume
        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

// Make the query.
        Cursor cur = getContentResolver().query(images,
                projection, // Which columns to return
                null,       // Which rows to return (all rows)
                null,       // Selection arguments (none)
                null        // Ordering
        );

        Log.e("ListingImages", " query count=" + cur.getCount());

        if (cur.moveToFirst()) {
            String bucket;
            String date;
            int bucketColumn = cur.getColumnIndex(
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

            int dateColumn = cur.getColumnIndex(
                    MediaStore.Images.Media.DATE_TAKEN);

            do {
                // Get the field values
                bucket = cur.getString(bucketColumn);
                date = cur.getString(dateColumn);

                // Do something with the values.
                Log.e("ListingImages", " bucket=" + bucket
                        + "  date_taken=" + date);
            } while (cur.moveToNext());
        }
//        String absolutePathOfImage = null;
//        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//
//        String[] projection = { MediaStore.MediaColumns.DATA,
//                MediaStore.Images.Media.BUCKET_DISPLAY_NAME };
//
//        cursor = getContentResolver().query(uri, projection, null,
//                null, null);
//
//        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
//        column_index_folder_name = cursor
//                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
//        while (cursor.moveToNext()) {
//            absolutePathOfImage = cursor.getString(column_index_data);
//
//            listOfAllImages.add(absolutePathOfImage);
//        }

        return listOfAllImages;
    }


    private boolean hasCameraPermission() {
        return EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA);
    }


    private boolean hasStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return EasyPermissions.hasPermissions(
                    this,
                    getString(R.string.rationale_camera),
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_IMAGES
            );
        } else {
            return EasyPermissions.hasPermissions(
                    this,
                    getString(R.string.rationale_camera),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            );
        }
        // return EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    public void onRationaleAccepted(int requestCode) {

    }

    @Override
    public void onRationaleDenied(int requestCode) {

    }

    @AfterPermissionGranted(RC_CAMERA_PERM)
    public void cameraTask() {
        if (hasCameraPermission()) {
            // Have permission, do the thing!
            Toast.makeText(this, "TODO: Camera things", Toast.LENGTH_LONG).show();
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.rationale_camera),
                    RC_CAMERA_PERM,
                    Manifest.permission.CAMERA);
        }
    }

    @AfterPermissionGranted(RC_STORAGE_PERM)
    public void storageTask() {
        if (hasStoragePermission()) {
            // Have permission, do the thing!
            Toast.makeText(this, "TODO: Camera things", Toast.LENGTH_LONG).show();
        } else {
            // Ask for one permission

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                EasyPermissions.requestPermissions(
                        this,
                        getString(R.string.rationale_camera),
                        RC_STORAGE_PERM,
                        Manifest.permission.READ_MEDIA_VIDEO,
                        Manifest.permission.READ_MEDIA_IMAGES
                );
            } else {
                EasyPermissions.requestPermissions(
                        this,
                        getString(R.string.rationale_camera),
                        RC_STORAGE_PERM,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                );
            }
        }
    }
}