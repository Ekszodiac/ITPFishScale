package com.example.ekszodiac.itp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;


public class MainActivity extends Activity {

    Button FSButton;
    Button AnaFish;
    ImageView imgView;
    static final int CAM_REQUEST = 1;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        useCam();
        anaFish();
        Log.d(TAG, "OpenCV Loaded");
    }

    //Save Picture
    private File getFile(){
        File FSFolder = new File("sdcard/fishscale");

        if(!FSFolder.exists()){
            FSFolder.mkdir();
        }

        File imgFile = new File(FSFolder, "test.jpg");

        return imgFile;
    }

    //Return from Camera
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String path = "sdcard/fishscale/test.png";
        imgView.setImageDrawable(Drawable.createFromPath(path));
    }

    private void useCam(){
        FSButton = (Button) findViewById(R.id.useBut);
        imgView = (ImageView) findViewById(R.id.imgView);
        FSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = getFile();
                camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(camera_intent, CAM_REQUEST);
            }
        });
    }

    private void anaFish(){
        AnaFish = (Button) findViewById(R.id.analyzeFish);
        AnaFish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent analyzeFish = new Intent (MainActivity.this, AnalyzeFish.class);
                startActivity(analyzeFish);
            }
        });
    }
}
