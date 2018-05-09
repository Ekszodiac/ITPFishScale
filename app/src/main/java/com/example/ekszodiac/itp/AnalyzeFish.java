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
        if(compare1() > 0){
            testTV.setText(compare1());
        }
    }

    private int compare1(){
        List<DMatch> matchList;
        List<DMatch> matches_final;
        MatOfDMatch matches_final_mat;
        List<DMatch> finalMatchesList = new ArrayList<DMatch>();
        try{
            //Get file from gallery and change to Bitmap
            File root = Environment.getExternalStorageDirectory();
            String capImg = root.getAbsolutePath() + "fishscale/test.jpg";
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            capturedBM = BitmapFactory.decodeFile(capImg, options);
            Utils.bitmapToMat(capturedBM, capturedMatImg);

            //Get dataset1 and change from Bitmap to Mat
            Bitmap dataset1= BitmapFactory.decodeResource(getResources(),R.drawable.dataset2);
            dataset1 = dataset1.copy(Bitmap.Config.ARGB_8888, true);
            dataMat1 = new Mat();
            Utils.bitmapToMat(dataset1, dataMat1);

            Imgproc.cvtColor(capturedMatImg, capturedMatImg, Imgproc.COLOR_BGR2RGB);
            Imgproc.cvtColor(dataMat1, dataMat1, Imgproc.COLOR_BGR2RGB);

            detector = FeatureDetector.create(FeatureDetector.ORB);

            MatOfKeyPoint capturedkeypoints = new MatOfKeyPoint();
            detector.detect(capturedMatImg, capturedkeypoints);
            Log.d("FishScale:",  "Captured Keypoints:" + capturedkeypoints.size());

            MatOfKeyPoint keypoints = new MatOfKeyPoint();
            detector.detect(dataMat1, keypoints);
            Log.d("FishScale:",  "Keypoints:" + keypoints.size());


            Extractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
            Mat capturedDescriptors = new Mat();
            Mat sourceDescriptors = new Mat();

            Extractor.compute(capturedMatImg, capturedkeypoints, capturedDescriptors);
            Log.d("FishScale:", "Captured Descriptors:" + capturedDescriptors.size());
            Extractor.compute(dataMat1, keypoints, sourceDescriptors);
            Log.d("FishScale:", "Source Descriptors:" + sourceDescriptors.size());

            matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE);
            matches = new MatOfDMatch();
            matcher.match(sourceDescriptors,capturedDescriptors,matches);
            Log.d("FishScale:", "Size:" + matches.size() );

            matchList = matches.toList();
            matches_final = new ArrayList<DMatch>();

            for(int i = 0; i < matchList.size(); i++){
                if(matchList.get(i).distance <= dist_limit){
                    matches_final.add(matches.toList().get(i));
                }
            }

            matches_final_mat = new MatOfDMatch();
            matches_final_mat.fromList(matches_final);
            finalMatchesList = matches_final_mat.toList();


        }
        catch(Exception e){
            e.printStackTrace();
        }

        return finalMatchesList.size();

    }



}
