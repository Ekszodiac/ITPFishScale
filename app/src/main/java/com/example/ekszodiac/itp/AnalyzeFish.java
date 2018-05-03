package com.example.ekszodiac.itp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.FeatureDetector;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.io.File;

public class AnalyzeFish extends AppCompatActivity {

    private static final String TAG = "AnalyzeFish";
    File capturedImg;
    Mat capturedMatImg;
    Mat dataMat1;
    Mat dataMat2;
    Mat dataMat3;
    Mat dataMat4;
    ImageView sample;
    FeatureDetector detector;
    DescriptorExtractor surfExtractor;

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

        Drawable d = getResources().getDrawable(R.drawable.zem);
        sample = (ImageView)findViewById(R.id.sample);
        sample.setImageDrawable(d);

        //Get file from gallery and change to Bitmap
        String capImg = "sdcard/fishscale/test.jpg";
        capturedImg = new File(capImg);
        capturedMatImg = Highgui.imread(capturedImg.getAbsolutePath(),Highgui.CV_LOAD_IMAGE_COLOR);
        Bitmap capturedBM = new Bitmap();
        Utils.matToBitmap(capturedMatImg, capturedBM);
        capturedBM = capturedBM.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(capturedBM, capturedMatImg);

        compare1(capturedMatImg);


    }

    public void onResume(){
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_13, this, mLoaderCallback);
    }

    private int compare1(Mat CapturedMatImage){
        //Get dataset1 and change from Bitmap to Mat
        Bitmap dataset1= BitmapFactory.decodeResource(getResources(),R.drawable.zem);
        dataset1 = dataset1.copy(Bitmap.Config.ARGB_8888, true);
        dataMat1 = new Mat();
        Utils.bitmapToMat(dataset1, dataMat1);

        Imgproc.cvtColor(CapturedMatImage, CapturedMatImage, Imgproc.COLOR_BGR2RGB);
        Imgproc.cvtColor(dataMat1, dataMat1, Imgproc.COLOR_BGR2RGB);

        detector = FeatureDetector.create(FeatureDetector.SURF);
        surfExtractor = DescriptorExtractor.create(DescriptorExtractor.SURF);


    }



}
