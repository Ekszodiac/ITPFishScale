package com.example.ekszodiac.itp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

public class AnalyzeFish extends AppCompatActivity {

    Mat testImg = Highgui.imread("sdcard/fishscale/test.jpg");
    //Mat FSImage1 =

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze_fish);
    }
}
