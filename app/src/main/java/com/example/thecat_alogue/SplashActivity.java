package com.example.thecat_alogue;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;

import androidx.appcompat.app.AppCompatActivity;

import com.example.thecat_alogue.databinding.ActivitySplashBinding;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY_MS = 3000; // 3 seconds splash

    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // --- Animated Entrance ---
        startSplashAnimations();

        // --- Transition to MainActivity after 3 seconds ---
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_DELAY_MS);
    }

    private void startSplashAnimations() {
        // Logo Scale + Alpha Animation
        AnimationSet logoAnim = new AnimationSet(true);
        logoAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        logoAnim.setDuration(1200);

        ScaleAnimation scale = new ScaleAnimation(
                0.3f, 1.0f,
                0.3f, 1.0f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f
        );
        AlphaAnimation fade = new AlphaAnimation(0.0f, 1.0f);

        logoAnim.addAnimation(scale);
        logoAnim.addAnimation(fade);
        binding.imgSplashLogo.startAnimation(logoAnim);

        // Text Title Fade Animation
        AlphaAnimation textFade = new AlphaAnimation(0.0f, 1.0f);
        textFade.setDuration(1500);
        textFade.setStartOffset(400);
        binding.txtSplashTitle.startAnimation(textFade);
        binding.txtSplashSubtitle.startAnimation(textFade);
    }
}
