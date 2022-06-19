package com.example.glide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.util.SimpleTimeZone;

public class MainActivity extends AppCompatActivity {

    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = findViewById(R.id.iv_image);

        if (checkForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_CODE)) {
            startGlide();
        }
    }

    private static final int REQUEST_CODE = 100;

    private boolean checkForPermission(String permission, int requestCode) {
        // Check if permission is already granted.
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Permission has not been granted. Check if a rationale AlertDialog should be shown.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {

                // Show rationale AlertDialog explaining why this permission is needed.
                new AlertDialog.Builder(this)
                        .setTitle("请求权限")
                        .setMessage("拥有写入权限才能保存图片")
                        .setPositiveButton(android.R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        // Request permission again.
                                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
                                    }
                                })
                        .create()
                        .show();
            }

            // Do not show rationale AlertDialog. Just request permission again.
            else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
            }
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (REQUEST_CODE == requestCode) {
            boolean allowed = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allowed = false;
                    break;
                }
            }
            if (allowed) startGlide();
        }
    }

    private static final String IMAGE_URL = "http://pic2.zhimg.com/v2-a5b909e499c3b0437b44785bd7512d39_r.jpg";
    private static final String IMAGE_URL2 = "http://hbimg.b0.upaiyun.com/9c04c8c0ac4ba85b78bc139a5a120d7169f6348135a1e-rCzooD_fw658";
    private static final String IMAGE_URL3 = "http://img.8794.cn/2017%2F0311%2F20170311015414975.jpg";

    private void startGlide() {
        Glide.with(this/*getApplicationContext()*/)
                .load(Uri.parse(IMAGE_URL3))
                .into(mImageView);

//        Glide.with(this)
//                .asBitmap()
//                .load(Uri.parse(IMAGE_URL2))
//                .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
    }
}