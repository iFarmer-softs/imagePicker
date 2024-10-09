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

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;
import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN;

import android.app.Activity;
import android.app.AlertDialog;
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

            binding.camera.addCameraListener(new CameraListener() {
                @Override
                public void onPictureTaken(PictureResult result) {
                    result.getSize().flip();
                    bitmap = BitmapFactory.decodeByteArray(result.getData(), 0, result.getData().length);
                    Image image = new Image(getFileFromBitMap(bitmap));
                    image.setSelected(true);
                    images.add(0, image);
                    selectedImages.put(image.getImagePath(), selectedImages.size() + 1 + "");
                    binding.cvCount.setVisibility(View.VISIBLE);
                    binding.tvCount.setText(selectedImages.size() + "");
                    imageRvHorizontalAdapter.notifyDataSetChanged();
                    imageRvGridAdapter.notifyDataSetChanged();
                    //startActivity(new Intent(MainActivity.this, ImagePreviewActivity.class));
                }

                @Override
                public void onVideoTaken(VideoResult result) {
                    // A Video was taken!
                }

                // And much more
            });

            sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View view, int newState) {
                    if (newState == STATE_EXPANDED) {

                    } else if (newState == STATE_HIDDEN) {

                    }
                }

                @Override
                public void onSlide(@NonNull View view, float v) {
                    for (int i = 0; i < selectedImages.size(); i++) {
                        imageRvGridAdapter.notifyItemChanged(i);
                    }

                    binding.rvImageHorizontal.setAlpha(1.0f - v);
                    binding.llCameraControll.setAlpha(1.0f - v);
                    binding.llGrid.setAlpha(v);
                    //binding.rvImageGrid.setAlpha(v);
                }
            });


            binding.clickImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.camera.takePictureSnapshot();
                }
            });

            binding.ivGallery.setOnClickListener(view -> {
                checkPermission();
                sheetBehavior.setState(STATE_EXPANDED);
            });

            checkPermission();
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
        //binding.mainContent.camera.onResume();
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
                    Log.e("TAG", "onRequestPermissionsResult: emmmm");
                    new AlertDialog.Builder(this)
                            .setTitle("Permission")
                            .setMessage("")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    someActivityResultLauncher.launch(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                            .setData(Uri.fromParts("package", getPackageName(), null)));
                                }
                            })
                            .show();
                    //requestPermissions(new String[]{Manifest.permission.SEND_SMS}, PERMISSIONS_CODE);
                }
            }
            //if (permission.equals(CAMERA))
        }
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
        //checkPermission();
        //getImagePath();
    });

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    checkPermission();
//                    if (result.getResultCode() == Activity.RESULT_OK) {
//                        checkPermission();
//                    } else {
//                        new AlertDialog.Builder(MainActivity.this)
//                                .setTitle("Permission")
//                                .setMessage("")
//                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                        dialogInterface.dismiss();
//                                        someActivityResultLauncher.launch(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//                                                .setData(Uri.fromParts("package", getPackageName(), null)));
//                                    }
//                                }).show();
//                    }
                }
            });


    private void checkPermission() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                    && (ContextCompat.checkSelfPermission(this, READ_MEDIA_IMAGES) == PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, READ_MEDIA_VIDEO) == PERMISSION_GRANTED
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

                if (ContextCompat.checkSelfPermission(this, CAMERA) == PERMISSION_DENIED || ContextCompat.checkSelfPermission(this, CAMERA) == PERMISSION_DENIED_APP_OP) {
                    new AlertDialog.Builder(this)
                            .setTitle("Permission")
                            .setMessage("Camera")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    someActivityResultLauncher.launch(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                            .setData(Uri.fromParts("package", getPackageName(), null)));
                                }
                            }).show();
                } else if (ContextCompat.checkSelfPermission(this, RECORD_AUDIO) == PERMISSION_DENIED || ContextCompat.checkSelfPermission(this, RECORD_AUDIO) == PERMISSION_DENIED_APP_OP) {
                    new AlertDialog.Builder(this)
                            .setTitle("Permission")
                            .setMessage("Audio")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    someActivityResultLauncher.launch(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                            .setData(Uri.fromParts("package", getPackageName(), null)));
                                }
                            }).show();
                } else if (ContextCompat.checkSelfPermission(this, READ_MEDIA_IMAGES) == PERMISSION_DENIED
                        || ContextCompat.checkSelfPermission(this, READ_MEDIA_IMAGES) == PERMISSION_DENIED_APP_OP
                        || ContextCompat.checkSelfPermission(this, READ_MEDIA_VIDEO) == PERMISSION_DENIED
                        || ContextCompat.checkSelfPermission(this, READ_MEDIA_VIDEO) == PERMISSION_DENIED_APP_OP
                        || ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PERMISSION_DENIED
                        || ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PERMISSION_DENIED_APP_OP
                        || ContextCompat.checkSelfPermission(this, READ_MEDIA_VISUAL_USER_SELECTED) == PERMISSION_DENIED
                        || ContextCompat.checkSelfPermission(this, READ_MEDIA_VISUAL_USER_SELECTED) == PERMISSION_DENIED_APP_OP

                ) {
                    new AlertDialog.Builder(this)
                            .setTitle("Permission")
                            .setMessage("Storage")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    someActivityResultLauncher.launch(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                            .setData(Uri.fromParts("package", getPackageName(), null)));
                                }
                            }).show();
                }
//
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

                //checkPermission();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void requestPermissions() {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
            //checkPermission();
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
//                    })
//                    .show();
        }

    }

    public static Bitmap loadBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);
        return b;
    }

}