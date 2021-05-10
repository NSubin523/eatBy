package com.example.eatby.Home;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;

import androidx.appcompat.app.AlertDialog;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;


public class PermissionHelper {
    private PermissionListener listener;
    private Activity activity;
    private Context context;

    public PermissionHelper(Context context) {
        this.context = context;
    }

    public void checkPermission(String permission, final String title, final String message, final PermissionGrant permissionGrant) {
        listener = new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {
                permissionGrant.permissionGranted();
                listener = null;
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {
                DialogOnDeniedPermissionListener.Builder.withContext(context)
                        .withTitle(title)
                        .withMessage(message)
                        .withButtonText(android.R.string.ok)
                        .build();
                listener = null;
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                showPermissionRationale(token, title, message);
                listener = null;
            }
        };
        if (this.activity == null) throw new RuntimeException();
        Dexter.withActivity(activity).withPermission(permission).withListener(listener).check();
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void showPermissionRationale(final PermissionToken token, String title, String message) {
        new AlertDialog.Builder(activity).setTitle(title).setMessage(message).setNegativeButton(android.R.string.cancel, (dialog, which) -> {
            dialog.dismiss();
            token.cancelPermissionRequest();
        }).setPositiveButton(android.R.string.ok, (dialog, which) -> {
            dialog.dismiss();
            token.continuePermissionRequest();
        }).setOnDismissListener(dialog -> token.cancelPermissionRequest()).show();
    }

    public interface PermissionGrant {
        void permissionGranted();
    }
}