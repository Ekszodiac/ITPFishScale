package com.example.ekszodiac.itp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgproc.Imgproc;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AnalyzeFish extends AppCompatActivity {

    private static final String TAG = "AnalyzeFish";
    File capturedImg;
    Mat capturedMatImg;
    Bitmap capturedBM;
    Mat dataMat1, dataMat2, dataMat3, dataMat4;
    ImageView sample;
    FeatureDetector detector;
    DescriptorExtractor Extractor;
    DescriptorMatcher matcher;
    MatOfDMatch matches;
    int dist_limit = 80;
    TextView testTV;

    BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");

                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze_fish);
    }

    public void onResume(){
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_13, this, mLoaderCallback);
    }

    @Override
    protected void onStart() {
        super.onStart();
        testTV = (TextView) findViewById(R.id.testTV);

    }





}
