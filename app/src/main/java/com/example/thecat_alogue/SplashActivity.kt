package com.example.thecat_alogue

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import androidx.appcompat.app.AppCompatActivity
import com.example.thecat_alogue.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startSplashAnimations()

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, SPLASH_DELAY_MS)
    }

    private fun startSplashAnimations() {
        val logoAnim = AnimationSet(true).apply {
            interpolator = AccelerateDecelerateInterpolator()
            duration = 1200
        }

        val scale = ScaleAnimation(
            0.3f, 1.0f,
            0.3f, 1.0f,
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
            ScaleAnimation.RELATIVE_TO_SELF, 0.5f
        )
        val fade = AlphaAnimation(0.0f, 1.0f)

        logoAnim.addAnimation(scale)
        logoAnim.addAnimation(fade)
        binding.imgSplashLogo.startAnimation(logoAnim)

        val textFade = AlphaAnimation(0.0f, 1.0f).apply {
            duration = 1500
            startOffset = 400
        }
        binding.txtSplashTitle.startAnimation(textFade)
        binding.txtSplashSubtitle.startAnimation(textFade)
    }

    companion object {
        private const val SPLASH_DELAY_MS = 2500L
    }
}
