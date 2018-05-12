package com.example.ekszodiac.itp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    File capturedImg;
    Mat capturedMatImg;
    Bitmap capturedBM;
    Mat dataMat1, dataMat2, dataMat3, dataMat4;
    ImageView sample;
    FeatureDetector detector;
    DescriptorExtractor Extractor;
    DescriptorMatcher matcher;
    MatOfDMatch matches;
    int dist_limit = 500;
    TextView testTV;
    Button FSButton;
    Button AnaFish;
    ImageView imgView;
    static final int CAM_REQUEST = 1;
    private static final String TAG = "MainActivity";
    int comp1 = compare1();
    int comp2 = compare2();
    int comp3 = compare3();
    int comp4 = compare4();

    static {
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV not loaded");
        }
        else{
            Log.d(TAG, "OpenCV loaded");
        }
    }

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
        setContentView(R.layout.activity_main);
        useCam();
        anaFish();
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
        String path = "sdcard/fishscale/test.jpg";
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
                String compString1 = Integer.toString(comp2);


                if(compare1() < compare2() && compare1() < compare3() && compare1() < compare4()){
                    Toast.makeText(getApplicationContext(), "Dataset1", Toast.LENGTH_LONG).show();
                }
                else if (compare2() < compare3() && compare2() < compare4() && compare2() < compare1()){
                    Toast.makeText(getApplicationContext(), "Dataset2", Toast.LENGTH_LONG).show();
                }
                else if (compare3() < compare4() && compare3() < compare1() && compare3() < compare2()){
                    Toast.makeText(getApplicationContext(), "Dataset3", Toast.LENGTH_LONG).show();
                }
                else if (compare4() < compare1() && compare4() < compare2() && compare4() < compare3()){
                    Toast.makeText(getApplicationContext(), "Dataset4", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), compString1, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    int compare1(){
        List<DMatch> matchList;
        List<DMatch> matches_final;
        MatOfDMatch matches_final_mat;
        List<DMatch> finalMatchesList = new ArrayList<DMatch>();
        try{
            capturedMatImg = new Mat();
            dataMat1 = new Mat();

            //Get file from gallery and change to Bitmap
            String capImg = "sdcard/fishscale/test.jpg";
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            capturedBM = BitmapFactory.decodeFile(capImg, options);
            if(capturedBM != null){
                Log.d("Captured Image 1", "Found");
            }

            Utils.bitmapToMat(capturedBM, capturedMatImg);
            if(capturedMatImg != null){
                Log.d("Captured Image Mat 1", "Found");
            }

            //Get dataset1 and change from Bitmap to Mat
            Bitmap dataset1= BitmapFactory.decodeResource(getResources(),R.drawable.dataset1);
            dataset1 = dataset1.copy(Bitmap.Config.ARGB_8888, true);
            if(dataset1 != null){
                Log.d("Dataset Image 1", "Found");
            }
            Utils.bitmapToMat(dataset1, dataMat1);
            if(dataMat1 != null){
                Log.d("Dataset Image Mat 1", "Found");
            }

            Imgproc.cvtColor(capturedMatImg, capturedMatImg, Imgproc.COLOR_BGR2RGB);
            Imgproc.cvtColor(dataMat1, dataMat1, Imgproc.COLOR_BGR2RGB);

            detector = FeatureDetector.create(FeatureDetector.ORB);

            MatOfKeyPoint capturedkeypoints = new MatOfKeyPoint();
            detector.detect(capturedMatImg, capturedkeypoints);
            Log.d("FishScale:",  "Captured Keypoints 1:" + capturedkeypoints.size());

            MatOfKeyPoint keypoints = new MatOfKeyPoint();
            detector.detect(dataMat1, keypoints);
            Log.d("FishScale:",  "Keypoints 1:" + keypoints.size());


            Extractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
            Mat capturedDescriptors = new Mat();
            Mat sourceDescriptors = new Mat();

            Extractor.compute(capturedMatImg, capturedkeypoints, capturedDescriptors);
            Log.d("FishScale:", "Captured Descriptors 1:" + capturedDescriptors.size());
            Extractor.compute(dataMat1, keypoints, sourceDescriptors);
            Log.d("FishScale:", "Source Descriptors 1:" + sourceDescriptors.size());

            matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
            matches = new MatOfDMatch();
            matcher.match(sourceDescriptors,capturedDescriptors,matches);
            Log.d("Fish Scale Data Set 1:", "Size:" + matches.size() );

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

    int compare2(){
        List<DMatch> matchList;
        List<DMatch> matches_final;
        MatOfDMatch matches_final_mat;
        List<DMatch> finalMatchesList = new ArrayList<DMatch>();
        try{
            capturedMatImg = new Mat();
            dataMat2 = new Mat();

            //Get file from gallery and change to Bitmap
            String capImg = "sdcard/fishscale/test.jpg";
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            capturedBM = BitmapFactory.decodeFile(capImg, options);
            if(capturedBM != null){
                Log.d("Captured Image 2", "Found");
            }

            Utils.bitmapToMat(capturedBM, capturedMatImg);
            if(capturedMatImg != null){
                Log.d("Captured Image Mat 2", "Found");
            }

            //Get dataset1 and change from Bitmap to Mat
            Bitmap dataset1= BitmapFactory.decodeResource(getResources(),R.drawable.dataset2);
            dataset1 = dataset1.copy(Bitmap.Config.ARGB_8888, true);
            if(dataset1 != null){
                Log.d("Dataset Image 2", "Found");
            }
            Utils.bitmapToMat(dataset1, dataMat2);
            if(dataMat2 != null){
                Log.d("Dataset Image Mat 2", "Found");
            }

            Imgproc.cvtColor(capturedMatImg, capturedMatImg, Imgproc.COLOR_BGR2RGB);
            Imgproc.cvtColor(dataMat2, dataMat2, Imgproc.COLOR_BGR2RGB);

            detector = FeatureDetector.create(FeatureDetector.ORB);

            MatOfKeyPoint capturedkeypoints = new MatOfKeyPoint();
            detector.detect(capturedMatImg, capturedkeypoints);
            Log.d("FishScale:",  "Captured Keypoints 2:" + capturedkeypoints.size());

            MatOfKeyPoint keypoints = new MatOfKeyPoint();
            detector.detect(dataMat2, keypoints);
            Log.d("FishScale:",  "Keypoints 2:" + keypoints.size());


            Extractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
            Mat capturedDescriptors = new Mat();
            Mat sourceDescriptors = new Mat();

            Extractor.compute(capturedMatImg, capturedkeypoints, capturedDescriptors);
            Log.d("FishScale:", "Captured Descriptors 2:" + capturedDescriptors.size());
            Extractor.compute(dataMat2, keypoints, sourceDescriptors);
            Log.d("FishScale:", "Source Descriptors 2:" + sourceDescriptors.size());

            matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
            matches = new MatOfDMatch();
            matcher.match(sourceDescriptors,capturedDescriptors,matches);
            Log.d("Fish Scale Dataset 2:", "Size:" + matches.size() );

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

    int compare3(){
        List<DMatch> matchList;
        List<DMatch> matches_final;
        MatOfDMatch matches_final_mat;
        List<DMatch> finalMatchesList = new ArrayList<DMatch>();
        try{
            capturedMatImg = new Mat();
            dataMat3 = new Mat();

            //Get file from gallery and change to Bitmap
            String capImg = "sdcard/fishscale/test.jpg";
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            capturedBM = BitmapFactory.decodeFile(capImg, options);
            if(capturedBM != null){
                Log.d("Captured Image 3", "Found");
            }

            Utils.bitmapToMat(capturedBM, capturedMatImg);
            if(capturedMatImg != null){
                Log.d("Captured Image Mat 3", "Found");
            }

            //Get dataset1 and change from Bitmap to Mat
            Bitmap dataset1= BitmapFactory.decodeResource(getResources(),R.drawable.dataset3);
            dataset1 = dataset1.copy(Bitmap.Config.ARGB_8888, true);
            if(dataset1 != null){
                Log.d("Dataset Image 3", "Found");
            }
            Utils.bitmapToMat(dataset1, dataMat3);
            if(dataMat3 != null){
                Log.d("Dataset Image Mat 3", "Found");
            }

            Imgproc.cvtColor(capturedMatImg, capturedMatImg, Imgproc.COLOR_BGR2RGB);
            Imgproc.cvtColor(dataMat3, dataMat3, Imgproc.COLOR_BGR2RGB);

            detector = FeatureDetector.create(FeatureDetector.ORB);

            MatOfKeyPoint capturedkeypoints = new MatOfKeyPoint();
            detector.detect(capturedMatImg, capturedkeypoints);
            Log.d("FishScale:",  "Captured Keypoints 3:" + capturedkeypoints.size());

            MatOfKeyPoint keypoints = new MatOfKeyPoint();
            detector.detect(dataMat3, keypoints);
            Log.d("FishScale:",  "Keypoints 3:" + keypoints.size());


            Extractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
            Mat capturedDescriptors = new Mat();
            Mat sourceDescriptors = new Mat();

            Extractor.compute(capturedMatImg, capturedkeypoints, capturedDescriptors);
            Log.d("FishScale:", "Captured Descriptors 3:" + capturedDescriptors.size());
            Extractor.compute(dataMat3, keypoints, sourceDescriptors);
            Log.d("FishScale:", "Source Descriptors 3:" + sourceDescriptors.size());

            matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
            matches = new MatOfDMatch();
            matcher.match(sourceDescriptors,capturedDescriptors,matches);
            Log.d("Fish Scale Dataset 3:", "Size:" + matches.size() );

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

    int compare4(){
        List<DMatch> matchList;
        List<DMatch> matches_final;
        MatOfDMatch matches_final_mat;
        List<DMatch> finalMatchesList = new ArrayList<DMatch>();
        try{
            capturedMatImg = new Mat();
            dataMat4 = new Mat();

            //Get file from gallery and change to Bitmap
            String capImg = "sdcard/fishscale/test.jpg";
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            capturedBM = BitmapFactory.decodeFile(capImg, options);
            if(capturedBM != null){
                Log.d("Captured Image 4", "Found");
            }

            Utils.bitmapToMat(capturedBM, capturedMatImg);
            if(capturedMatImg != null){
                Log.d("Captured Image Mat 4", "Found");
            }

            //Get dataset1 and change from Bitmap to Mat
            Bitmap dataset1= BitmapFactory.decodeResource(getResources(),R.drawable.dataset4);
            dataset1 = dataset1.copy(Bitmap.Config.ARGB_8888, true);
            if(dataset1 != null){
                Log.d("Dataset Image 4", "Found");
            }
            Utils.bitmapToMat(dataset1, dataMat4);
            if(dataMat4 != null){
                Log.d("Dataset Image Mat 4", "Found");
            }

            Imgproc.cvtColor(capturedMatImg, capturedMatImg, Imgproc.COLOR_BGR2RGB);
            Imgproc.cvtColor(dataMat4, dataMat4, Imgproc.COLOR_BGR2RGB);

            detector = FeatureDetector.create(FeatureDetector.ORB);

            MatOfKeyPoint capturedkeypoints = new MatOfKeyPoint();
            detector.detect(capturedMatImg, capturedkeypoints);
            Log.d("FishScale:",  "Captured Keypoints 4:" + capturedkeypoints.size());

            MatOfKeyPoint keypoints = new MatOfKeyPoint();
            detector.detect(dataMat4, keypoints);
            Log.d("FishScale:",  "Keypoints 4:" + keypoints.size());


            Extractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
            Mat capturedDescriptors = new Mat();
            Mat sourceDescriptors = new Mat();

            Extractor.compute(capturedMatImg, capturedkeypoints, capturedDescriptors);
            Log.d("FishScale:", "Captured Descriptors 4:" + capturedDescriptors.size());
            Extractor.compute(dataMat4, keypoints, sourceDescriptors);
            Log.d("FishScale:", "Source Descriptors 4:" + sourceDescriptors.size());

            matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
            matches = new MatOfDMatch();
            matcher.match(sourceDescriptors,capturedDescriptors,matches);
            Log.d("FishScale Data set 4:", "Size:" + matches.size() );

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
