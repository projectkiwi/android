package com.purduecs.kiwi.oneup;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;

public class MediaCaptureActivity extends AppCompatActivity {

    private static String EXTRA_IMAGE_FILE_NAME = "com.purduecs.kiwi.oneup.filenamecrapforimageinactivity";

    public static Intent intentForImage(Context c, File file) {
        Intent i = new Intent(c, MediaCaptureActivity.class);
        Log.d("HEY", file.toString());
        i.putExtra(EXTRA_IMAGE_FILE_NAME, file.toString());
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_capture);
        if (null == savedInstanceState) {
            String uri = getIntent().getStringExtra(EXTRA_IMAGE_FILE_NAME);
            CameraFragment fragment;
            if (uri != null) {
                fragment = CameraFragment.newInstance(uri);
            } else {
                fragment = CameraFragment.newInstance(new File(getExternalFilesDir(null), "pic.jpg").toString());
            }
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }
        try {
            getSupportActionBar().hide();
        } catch (Exception e) {
            Log.e("HEY", String.format("No Action Bar. Error : " + e.getMessage()));
        }
    }
}
