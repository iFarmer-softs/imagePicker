package com.example.iimagepicker.views;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.READ_MEDIA_VIDEO;
import static android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED;
import static android.Manifest.permission.RECORD_AUDIO;

import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

import static com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
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
    //    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
//    //private ActivityResultLauncher<String[]> permissionLauncher;
//    private ActivityResultLauncher<Intent> startActivityForResult;
    private ImageRvHorizontalAdapter imageRvHorizontalAdapter;
    private ImageRvGridAdapter imageRvGridAdapter;
    private int count = 0;

    public static LinkedHashMap<String, String> selectedImages = new LinkedHashMap<>();
    public static Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        requestPermissions();

        binding.camera.setLifecycleOwner(this);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        sheetBehavior = BottomSheetBehavior.from(binding.flDrag);
        imageRvHorizontalAdapter = new ImageRvHorizontalAdapter(this, images);
        imageRvGridAdapter = new ImageRvGridAdapter(this, images);
//
        binding.rvImageHorizontal.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        binding.rvImageHorizontal.setAdapter(imageRvHorizontalAdapter);

        binding.rvImageGrid.setLayoutManager(new GridLayoutManager(this, 3));
        binding.rvImageGrid.setAdapter(imageRvGridAdapter);

        //binding.camera.setPreview(Preview.GL_SURFACE);
//        binding.camera.post(new Runnable() {
//            @Override
//            public void run() {
//                binding.camera.setPictureSize(SizeSelectors.maxHeight(binding.camera.getHeight()));
//                binding.camera.setPictureSize(SizeSelectors.minHeight(binding.camera.getHeight()));
//                binding.camera.setPictureSize(SizeSelectors.minWidth(binding.camera.getWidth()));
//                binding.camera.setPictureSize(SizeSelectors.maxWidth(binding.camera.getWidth()));
//            }
//        });



        binding.ivSwitchCamera.setOnClickListener(view -> {
            binding.camera.toggleFacing();
        });

        binding.camera.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(PictureResult result) {
//                AspectRatio.of(result.getSize());
                result.getSize().flip();
                bitmap = BitmapFactory.decodeByteArray(result.getData(), 0, result.getData().length);
//                bitmap = Bitmap.createScaledBitmap(
//                        bitmap, binding.camera.getPictureSize().getWidth(), binding.camera.getPictureSize().getWidth(), false);

                startActivity(new Intent(MainActivity.this, ImagePreviewActivity.class));
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
                //Preview p = binding.camera.getPreview();
//                bitmap = loadBitmapFromView(binding.camera);
//                Log.e("TAG", "onClick: "+bitmap.getByteCount());
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        startActivity(new Intent(MainActivity.this, ImagePreviewActivity.class));
//                    }
//                },500);
                //binding.camera.takePicture();
                binding.camera.takePictureSnapshot();
                //Toast.makeText(MainActivity.this, "asdjhfgjhadsgf", Toast.LENGTH_SHORT).show();
            }
        });

        binding.ivGallery.setOnClickListener(view -> {
            sheetBehavior.setState(STATE_EXPANDED);
            //imageRvGridAdapter.notifyDataSetChanged();
        });

        checkPermission();
    }

    private String getFileFromBitMap(Bitmap bitmap) {
        File filesDir = getCacheDir();
        File imageFile = new File(filesDir, System.currentTimeMillis() + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            //bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
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
            Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy + " desc");

            // below line is to get total number of images
            int count = cursor.getCount();
            Log.e("TAG", "getImagePath: " + count);
            // on below line we are running a loop to add
            // the image file path in our array list.
            for (int i = 0; i < count; i++) {

                // on below line we are moving our cursor position
                cursor.moveToPosition(i);

                // on below line we are getting image file path
                int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);

                // after that we are getting the image file path
                // and adding that path in our array list.
                images.add(new Image(cursor.getString(dataColumnIndex)));
            }
            imageRvHorizontalAdapter.notifyDataSetChanged();
            imageRvGridAdapter.notifyDataSetChanged();
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

    public static Bitmap loadBitmapFromView(View v) {
        Log.e("TAG", "loadBitmapFromView: "+v.getHeight());
        Log.e("TAG", "loadBitmapFromView: "+v.getWidth());
        Bitmap b = Bitmap.createBitmap(v.getWidth() , v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);
        return b;
    }

}