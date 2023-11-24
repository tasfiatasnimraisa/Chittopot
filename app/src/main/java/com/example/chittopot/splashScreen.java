package com.example.chittopot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class splashScreen extends AppCompatActivity {

    TextView appname;
    TextView slogan;
    LottieAnimationView lottie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        appname = findViewById(R.id.app_name);
        lottie = findViewById(R.id.lottie);
        slogan = findViewById(R.id.slogan);
        slogan.setAlpha(0f);

        appname.animate().translationY(-1250).setDuration(1800).setStartDelay(10);
        lottie.animate().translationY(250).setDuration(1000).setStartDelay(500);
        slogan.animate().alpha(1f).translationY(-100).setDuration(1000).setStartDelay(900);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        },5000);
    }
}