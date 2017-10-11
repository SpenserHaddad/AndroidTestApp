package com.example.sahko.androidtest;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

/**
 * Created by sahko on 10/10/2017.
 */

public class NewGameUploadTaskFragment extends Fragment {
    private static final String TAG = "NewPostTaskFragment";

    public interface TaskCallbacks {
        void onBitmapResized(Bitmap resizedBitmap, int maxDimension);
        void onPostUploaded(String error);
    }

    private Context applicationContext;
    private NewPostUploadTaskFragment.TaskCallbacks callbacks;
    private Bitmap selectedBitmap;
    private Bitmap thumbnail;

    public NewGameUploadTaskFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NewPostUploadTaskFragment.TaskCallbacks) {
            callbacks = (NewPostUploadTaskFragment.TaskCallbacks) context;
        } else {
            throw new RuntimeException(context.toString() + "must implement TaskCallbacks");
        }
        applicationContext = context.getApplicationContext();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }
}
