package com.example.eatby.Home;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import com.example.eatby.BuildConfig;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;


public class PhotoPickHelper {
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_PICK_IMAGE = 542;
    private final PermissionHelper permissionHelper;
    private final RealPathUtil realPathUtil;
    private Context context;
    private String tempPhotoPath;
    private PhotoPickCallback photoPickCallback = new PhotoPickCallback() {
        @Override
        public void showError(boolean b, Throwable e) {

        }

        @Override
        public void setUpImage(String currentPhotoPath) {

        }
    };
    private Activity activity;

    public PhotoPickHelper(Context context) {
        this.context = context;
        this.permissionHelper = new PermissionHelper(context);
        this.realPathUtil = new RealPathUtil(context);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */);
        tempPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void requestTakePhoto() {
        permissionHelper.checkPermission(Manifest.permission.CAMERA, "Camera Permission", "Permission is needed to take a photo", () -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera context to handle the intent
            if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    photoPickCallback.showError(true, ex);
                    // Error occurred while creating the File
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    if (activity != null) {
                        activity.startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                    }
                }
            }
        });
    }

    public void requestPickPhoto() {
        permissionHelper.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, "Storage Permission",
                "Permission is needed to read the image files", () -> {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    if (activity != null) {
                        activity.startActivityForResult(Intent.createChooser(intent, "Complete action using"), REQUEST_PICK_IMAGE);
                    }
                });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PICK_IMAGE) {
            if (resultCode == RESULT_OK && data.getData() != null) {
                photoPickCallback.setUpImage(realPathUtil.getRealPath(data.getData()));
            }
        } else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            photoPickCallback.setUpImage(tempPhotoPath);
        }
    }

    public void addPhotoPickCallback(PhotoPickCallback photoPickCallback) {
        this.photoPickCallback = photoPickCallback;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
        permissionHelper.setActivity(activity);
    }

    public interface PhotoPickCallback {
        void showError(boolean b, Throwable e);

        void setUpImage(String currentPhotoPath);
    }
}