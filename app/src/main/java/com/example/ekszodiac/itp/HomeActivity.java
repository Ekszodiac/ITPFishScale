package com.example.ekszodiac.itp;

import android.content.Intent;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class HomeActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 4000;
    private ImageView splashscreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        splashscreen = (ImageView) findViewById(R.id.splashscreen);
        Animation myanim = AnimationUtils.loadAnimation(this, R.anim.animation);
        splashscreen.startAnimation(myanim);
        final Intent mainIntent = new Intent(this,MainActivity.class);
        Thread timer = new Thread(){
            public void run(){
                try{
                    sleep(4000);
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
                finally {
                    startActivity(mainIntent);
                    finish();
                }
            }
        };
        timer.start();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                /* Create an Intent that will start the Menu-Activity. */
//                Intent mainIntent = new Intent(HomeActivity.this,MainActivity.class);
//                startActivity(mainIntent);
//                finish();
//            }
//        }, SPLASH_DISPLAY_LENGTH);
    }
}
