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
    int dist_limit = 80;
    TextView testTV;
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
                if(compare1() > 0){
                    Toast.makeText(getApplicationContext(), "Naa", Toast.LENGTH_LONG).show();
                }
                else if(compare1() == 0){
                    Toast.makeText(getApplicationContext(), "Equals", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Wa gyud", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private int compare1(){
        List<DMatch> matchList;
        List<DMatch> matches_final;
        MatOfDMatch matches_final_mat;
        List<DMatch> finalMatchesList = new ArrayList<DMatch>();
        try{
            //Get file from gallery and change to Bitmap
            File root = Environment.getExternalStorageDirectory();
            String capImg = "sdcard/fishscale/test.jpg";
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
