package com.example.bryanchen.formations;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ImageView imageView = (ImageView) findViewById(R.id.titlePage);
        imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.logo));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent startPageIntent = new Intent(SplashScreen.this, StartPage.class);
                startActivity(startPageIntent);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
