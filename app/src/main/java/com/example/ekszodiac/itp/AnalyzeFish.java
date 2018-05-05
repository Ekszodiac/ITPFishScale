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
import org.opencv.features2d.Features2d;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

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
    ArrayList matches_final;
    int min_dist = 500;
    TextView testTV;
    List finalMatchesList;
    Mat mlast;

    BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    compare1();
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

    private Mat compare1(){
        try{
            //Get file from gallery and change to Bitmap
            File root = Environment.getExternalStorageDirectory();
            String capImg = root.getAbsolutePath() + "fishscale/test.jpg";
            capturedImg = new File(capImg);
            capturedMatImg = Highgui.imread(capturedImg.getAbsolutePath());

            Utils.matToBitmap(capturedMatImg, capturedBM);
            capturedBM = capturedBM.copy(Bitmap.Config.ARGB_8888, true);
            Utils.bitmapToMat(capturedBM, capturedMatImg);

            //Get dataset1 and change from Bitmap to Mat
            Bitmap dataset1= BitmapFactory.decodeResource(getResources(),R.drawable.dataset2);
            dataset1 = dataset1.copy(Bitmap.Config.ARGB_8888, true);
            dataMat1 = new Mat();
            Utils.bitmapToMat(dataset1, dataMat1);

            Imgproc.cvtColor(capturedMatImg, capturedMatImg, Imgproc.COLOR_BGR2RGB);
            Imgproc.cvtColor(dataMat1, dataMat1, Imgproc.COLOR_BGR2RGB);

            detector = FeatureDetector.create(FeatureDetector.ORB);
            //surfExtractor = DescriptorExtractor.create(DescriptorExtractor.SURF);

            MatOfKeyPoint capturedkeypoints = new MatOfKeyPoint();
            detector.detect(capturedMatImg, capturedkeypoints);
            Log.d("LOG!",  "number of query Captured Keypoints:" + capturedkeypoints.size());

            MatOfKeyPoint keypoints = new MatOfKeyPoint();
            detector.detect(dataMat1, keypoints);
            Log.d("LOG!",  "number of query Keypoints:" + keypoints.size());


            Extractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
            Mat capturedDescriptors = new Mat();
            Mat sourceDescriptors = new Mat();

            Extractor.compute(capturedMatImg, capturedkeypoints, capturedDescriptors);
            Log.d("LOG!", "number of Captured Descriptors:" + capturedDescriptors.size());
            Extractor.compute(dataMat1, keypoints, sourceDescriptors);
            Log.d("LOG!", "number of Source Descriptors:" + sourceDescriptors.size());

            matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE);
            matches = new MatOfDMatch();
            matcher.match(sourceDescriptors,capturedDescriptors,matches);

            Features2d.drawMatches(capturedMatImg, capturedkeypoints, dataMat1, keypoints, matches, mlast );

            testTV = (TextView) findViewById(R.id.testTV);
            testTV.setText("Bulowk");

            return mlast;
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return capturedMatImg;

    }



}
