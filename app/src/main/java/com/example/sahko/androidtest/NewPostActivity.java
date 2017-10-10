package com.example.sahko.androidtest;

import android.*;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class NewPostActivity extends BaseActivity implements
        EasyPermissions.PermissionCallbacks,
        NewPostUploadTaskFragment.TaskCallbacks {

    public static final String TAG = "NewPostActivity";
    public static final String TAG_TASK_FRAGMENT = "newPostUploadTaskFragment";
    private static final int THUMBNAIL_MAX_DIMENSION = 640;
    private static final int FULL_SIZE_MAX_DIMENSION = 1280;
    private Button submitButton;

    private ImageView imageView;
    private Uri fileUri;
    private Bitmap resizedBitmap;
    private Bitmap thumbnail;

    private NewPostUploadTaskFragment taskFragment;

    private static final int TC_PICK_IMAGE = 101;
    private static final int RC_CAMERA_PERMISSIONS = 102;

    private static final String[] cameraPerms = new String[]{
            android.Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        FragmentManager fm = getSupportFragmentManager();
        taskFragment = (NewPostUploadTaskFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);

        if (taskFragment == null) {
            // add the fragment
            taskFragment = new NewPostUploadTaskFragment();
            fm.beginTransaction().add(taskFragment, TAG_TASK_FRAGMENT).commit();
        }

        imageView = findViewById(R.id.new_post_picture);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePicker();
            }
        });

        Bitmap selectedBitmap = taskFragment.getSelectedBitmap();
        Bitmap thumbnail = taskFragment.getThumbnail();
        if (selectedBitmap != null) {
            imageView.setImageBitmap(selectedBitmap);
            resizedBitmap = selectedBitmap;
        }
        if (thumbnail != null) {
            this.thumbnail = thumbnail;
        }

        submitButton = findViewById(R.id.new_post_submit);
    }

    public void uploadPost(View v) {

        if (resizedBitmap == null) {
            Toast.makeText(NewPostActivity.this, "Select an image first.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        EditText descriptionText = findViewById(R.id.new_post_text);
        String postText = descriptionText.getText().toString();
        if (TextUtils.isEmpty(postText)) {
            descriptionText.setError(getString(R.string.error_required_field));
            return;
        }

        showProgressDialog(getString(R.string.post_upload_progress_message));
        submitButton.setEnabled(false);

        Long timestamp = System.currentTimeMillis();

        String bitmapPath = "/" + FirebaseUtil.getCurrentUserId() + "/full/" + timestamp.toString() + "/";
        String thumbnailPath = "/" + FirebaseUtil.getCurrentUserId() + "/thumb/" + timestamp.toString() + "/";
        taskFragment.uploadPost(resizedBitmap, bitmapPath, this.thumbnail, thumbnailPath, fileUri.getLastPathSegment(),
                postText);
    }

    @Override
    public void onPostUploaded(final String error) {
        NewPostActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                submitButton.setEnabled(true);
                dismissProgressDialog();
                if (error == null) {
                    Toast.makeText(NewPostActivity.this, "Post created!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(NewPostActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @AfterPermissionGranted(RC_CAMERA_PERMISSIONS)
    private void showImagePicker() {
        if (!EasyPermissions.hasPermissions(this, cameraPerms)) {
            EasyPermissions.requestPermissions(this, "This sample will upload a picture from your Camera",
                    RC_CAMERA_PERMISSIONS, cameraPerms);
            return;
        }

        File file = new File(getExternalCacheDir(), UUID.randomUUID().toString());
        fileUri = Uri.fromFile(file);

        final List<Intent> cameraIntents = new ArrayList<>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            cameraIntents.add(intent);
        }

        // Image Picker
        Intent pickerIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        Intent chooserIntent = Intent.createChooser(pickerIntent,
                getString(R.string.picture_chooser_title));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                cameraIntents.toArray(new Parcelable[cameraIntents.size()]));
        startActivityForResult(chooserIntent, TC_PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TC_PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                final boolean isCamera = data.getData() == null ||
                                         MediaStore.ACTION_IMAGE_CAPTURE.equals(data.getAction());

                if (!isCamera) {
                    fileUri = data.getData();
                }
                taskFragment.resizeBitmap(fileUri, THUMBNAIL_MAX_DIMENSION);
                taskFragment.resizeBitmap(fileUri, FULL_SIZE_MAX_DIMENSION);
            }
        }
    }

    @Override
    public void onDestroy() {
        if (resizedBitmap != null) {
            taskFragment.setSelectedBitmap(resizedBitmap);
        }
        super.onDestroy();
    }

    @Override
    public void onBitmapResized(Bitmap resizedBitmap, int mMaxDimension) {
        if (resizedBitmap == null) {
            Toast.makeText(getApplicationContext(), "Couldn't resize bitmap.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (mMaxDimension == THUMBNAIL_MAX_DIMENSION) {
            thumbnail = resizedBitmap;
        } else if (mMaxDimension == FULL_SIZE_MAX_DIMENSION) {
            this.resizedBitmap = resizedBitmap;
            imageView.setImageBitmap(resizedBitmap);
        }

        if (thumbnail != null && resizedBitmap != null) {
            submitButton.setEnabled(true);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {}

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
    }
}
