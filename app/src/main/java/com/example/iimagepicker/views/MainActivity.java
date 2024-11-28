package com.example.iimagepicker.views;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.READ_MEDIA_VIDEO;
import static android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED;
import static android.Manifest.permission.RECORD_AUDIO;

import static androidx.core.content.PermissionChecker.PERMISSION_DENIED;
import static androidx.core.content.PermissionChecker.PERMISSION_DENIED_APP_OP;
import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.iimagepicker.adapters.ImageRvGridAdapter;
import com.example.iimagepicker.adapters.ImageRvHorizontalAdapter;
import com.example.iimagepicker.databinding.ActivityMainBinding;
import com.example.iimagepicker.models.Image;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.VideoResult;
import com.otaliastudios.cameraview.controls.Preview;
import com.otaliastudios.cameraview.frame.Frame;
import com.otaliastudios.cameraview.frame.FrameProcessor;
import com.otaliastudios.cameraview.size.AspectRatio;
import com.otaliastudios.cameraview.size.Size;
import com.otaliastudios.cameraview.size.SizeSelector;
import com.otaliastudios.cameraview.size.SizeSelectors;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private BottomSheetBehavior sheetBehavior;
    private ActivityMainBinding binding;
    private ArrayList<Image> images = new ArrayList<>();
    private ImageRvHorizontalAdapter imageRvHorizontalAdapter;
    private ImageRvGridAdapter imageRvGridAdapter;

    public static LinkedHashMap<String, String> selectedImages = new LinkedHashMap<>();
    public static Bitmap bitmap;

    ArrayList<String> permissionsList = new ArrayList<>();

    int permissionsCount = 0;
    String[] permissionsStr = {};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        try {

            requestPermissions();
            binding.camera.setRequestPermissions(false);
            binding.camera.setLifecycleOwner(this);
            sheetBehavior = BottomSheetBehavior.from(binding.flDrag);
            imageRvHorizontalAdapter = new ImageRvHorizontalAdapter(this, images);
            imageRvGridAdapter = new ImageRvGridAdapter(this, images);
//
            binding.rvImageHorizontal.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
            binding.rvImageHorizontal.setAdapter(imageRvHorizontalAdapter);

            binding.rvImageGrid.setLayoutManager(new GridLayoutManager(this, 3));
            binding.rvImageGrid.setAdapter(imageRvGridAdapter);

            binding.ivSwitchCamera.setOnClickListener(view -> {
                binding.camera.toggleFacing();
            });

            binding.ivBack.setOnClickListener(v -> sheetBehavior.setState(STATE_COLLAPSED));

            binding.camera.addCameraListener(new CameraListener() {
                @Override
                public void onPictureTaken(@NonNull PictureResult result) {
                    result.getSize().flip();
                    bitmap = BitmapFactory.decodeByteArray(result.getData(), 0, result.getData().length);
                    Image image = new Image(getFileFromBitMap(bitmap));
                    image.setSelected(true);
                    images.add(0, image);
                    selectedImages.put(image.getImagePath(), selectedImages.size() + 1 + "");
                    binding.cvCount.setVisibility(View.VISIBLE);
                    binding.tvCount.setText(selectedImages.size()+"");
                    imageRvHorizontalAdapter.notifyDataSetChanged();
                    imageRvGridAdapter.notifyDataSetChanged();
                    //startActivity(new Intent(MainActivity.this, ImagePreviewActivity.class));
                }

                @Override
                public void onVideoTaken(@NonNull VideoResult result) {
                    // A Video was taken!
                }

                // And much more
            });

            sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View view, int newState) {
                    if (newState == STATE_EXPANDED) {

                        //binding.llNav.setVisibility(View.VISIBLE);
                        imageRvGridAdapter.notifyDataSetChanged();
                    } else if (newState == STATE_COLLAPSED) {
                        //binding.llNav.setVisibility(View.INVISIBLE);
                        imageRvHorizontalAdapter.notifyDataSetChanged();
//                        binding.cvCount.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onSlide(@NonNull View view, float v) {
                    if (!selectedImages.isEmpty()){
                        binding.tvCount.setText(selectedImages.size()+"");
                        binding.cvCount.setVisibility(View.VISIBLE);
                    }else {
                        binding.cvCount.setVisibility(View.GONE);
                    }

                    binding.rvImageHorizontal.setAlpha(1.0f - v);
                    binding.llCameraControll.setAlpha(1.0f - v);
                    binding.cvCount.setAlpha(1.0f - v);
                    binding.llGrid.setAlpha(v);
                    binding.llNav.setAlpha(v);
                    //imageRvHorizontalAdapter.notifyDataSetChanged();
                }
            });

            binding.ivRemove.setOnClickListener(v -> finish());


            binding.clickImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.camera.takePictureSnapshot();
                }
            });

            binding.ivGallery.setOnClickListener(view -> {
                requestPermissions();
                sheetBehavior.setState(STATE_EXPANDED);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private String getFileFromBitMap(Bitmap bitmap) {
        File imageFile = null;
        try {
            File filesDir = getCacheDir();
            imageFile = new File(filesDir, System.currentTimeMillis() + ".jpg");
            OutputStream os;
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
        }
        return imageFile.getPath();
    }


    @Override
    protected void onStart() {
        super.onStart();
        //binding.mainContent.camera.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.camera.open();
    }

    @Override
    protected void onPause() {
        //binding.mainContent.camera.onPause();
        super.onPause();
        binding.camera.close();
    }

    @Override
    protected void onStop() {
        //binding.mainContent.camera.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.camera.destroy();
    }

    private void getImagePath() {
        try {
            boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);

            if (isSDPresent) {
                final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};

                final String orderBy = MediaStore.Images.Media._ID;

                Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy + " desc");

                int count = cursor.getCount();
                for (int i = 0; i < count; i++) {

                    cursor.moveToPosition(i);
                    int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    images.add(new Image(cursor.getString(dataColumnIndex)));
                }
                imageRvHorizontalAdapter.notifyDataSetChanged();
                imageRvGridAdapter.notifyDataSetChanged();
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private final ActivityResultLauncher<String[]> permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
        try {
            ArrayList<Boolean> list = new ArrayList<>(result.values());
            permissionsList = new ArrayList<>();
            permissionsCount = 0;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                permissionsStr = new String[]{CAMERA, RECORD_AUDIO, READ_MEDIA_IMAGES, READ_MEDIA_VIDEO};
            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
                permissionsStr = new String[]{CAMERA, RECORD_AUDIO, READ_MEDIA_IMAGES, READ_MEDIA_VIDEO};
            } else {
                permissionsStr = new String[]{CAMERA, RECORD_AUDIO, READ_EXTERNAL_STORAGE};
            }

            for (int i = 0; i < list.size(); i++) {
                if (shouldShowRequestPermissionRationale(permissionsStr[i])) {
                    permissionsList.add(permissionsStr[i]);
                } else if (!hasPermission(permissionsStr[i])) {
                    permissionsCount++;
                }
            }
            if (!permissionsList.isEmpty()) {
                //Some permissions are denied and can be asked again.
                askForPermissions(permissionsList);
            } else if (permissionsCount > 0) {
                new AlertDialog.Builder(this)
                        .setTitle("Permission")
                        .setMessage("This app needs camera, gallery and audio permission")
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                someActivityResultLauncher.launch(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                        .setData(Uri.fromParts("package", getPackageName(), null)));
                            }
                        }).show();
            } else {
                getImagePath();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    });

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    requestPermissions();
                }
            });


    private void checkPermission() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                    && (ContextCompat.checkSelfPermission(this, READ_MEDIA_IMAGES) == PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, READ_MEDIA_VIDEO) == PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, CAMERA) == PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, RECORD_AUDIO) == PERMISSION_GRANTED
            )
            ) {

                getImagePath();
            } else if (
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE &&
                            ContextCompat.checkSelfPermission(this, READ_MEDIA_VISUAL_USER_SELECTED) == PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(this, CAMERA) == PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(this, RECORD_AUDIO) == PERMISSION_GRANTED
            ) {

                getImagePath();
            } else if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, CAMERA) == PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this, RECORD_AUDIO) == PERMISSION_GRANTED
            ) {
                getImagePath();
            } else {
                askForPermissions(permissionsList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void requestPermissions() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                permissionsList.clear();
                permissionsList.addAll(Arrays.asList(CAMERA, READ_MEDIA_IMAGES,
                        RECORD_AUDIO,
                        READ_MEDIA_VIDEO));

            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
                permissionsList.clear();
                permissionsList.addAll(Arrays.asList(CAMERA, RECORD_AUDIO, READ_MEDIA_IMAGES, READ_MEDIA_VIDEO));

            } else {
                permissionsList.clear();
                permissionsList.addAll(Arrays.asList(CAMERA, RECORD_AUDIO, READ_EXTERNAL_STORAGE));
            }
            askForPermissions(permissionsList);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void askForPermissions(ArrayList<String> permissionsList) {
        try {
            String[] newPermissionStr = new String[permissionsList.size()];
            for (int i = 0; i < newPermissionStr.length; i++) {
                newPermissionStr[i] = permissionsList.get(i);
            }
            if (newPermissionStr.length > 0) {
                //binding.txtStatus.setText("Asking for permissions");
                permissionLauncher.launch(newPermissionStr);
            } else {
            /* User has pressed 'Deny & Don't ask again' so we have to show the enable permissions dialog
            which will lead them to app details page to enable permissions from there. */
                new AlertDialog.Builder(this)
                        .setTitle("Permission")
                        .setMessage("This app needs camera, gallery and audio permission")
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                someActivityResultLauncher.launch(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                        .setData(Uri.fromParts("package", getPackageName(), null)));
                            }
                        }).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);
        return b;
    }

    private boolean hasPermission(String permissionStr) {
        return ContextCompat.checkSelfPermission(this, permissionStr) == PackageManager.PERMISSION_GRANTED;
    }

    public void setImageCount(){
        if (!selectedImages.isEmpty()){
            binding.tvCount.setText(selectedImages.size()+"");
            binding.cvCount.setVisibility(View.VISIBLE);
        }else {
            binding.cvCount.setVisibility(View.GONE);
        }
    }

}