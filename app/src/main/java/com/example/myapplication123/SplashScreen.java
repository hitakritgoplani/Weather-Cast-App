package com.example.myapplication123;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.airbnb.lottie.LottieAnimationView;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    TextView title;
    LottieAnimationView logo;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        title = findViewById(R.id.tv_title);
        logo = findViewById(R.id.lottie);

        String text = "WEATHERCAST";
        SpannableString ss = new SpannableString(text);
        ss.setSpan(new RelativeSizeSpan(1.5f),0,1,0);
        ss.setSpan(new ForegroundColorSpan(Color.rgb(42,157,244)),0,1,0);
        ss.setSpan(new RelativeSizeSpan(1.5f),7,8,0);
        ss.setSpan(new ForegroundColorSpan(Color.rgb(42,157,244)),7,8,0);
        title.setText(ss);

        logo.animate().translationX(-1500).setDuration(500).setStartDelay(1500);
        title.animate().translationX(1500).setDuration(500).setStartDelay(1500);

        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashScreen.this,MainActivity.class));
            finish();
        },2000);
    }
}