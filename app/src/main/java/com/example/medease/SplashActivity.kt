package com.example.medease

import android.R
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.medease.databinding.ActivitySplashBinding


class SplashActivity : AppCompatActivity() {
    private val SPLASH_DELAY:Long = 200
    private lateinit var binding:ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySplashBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

//        val fadeOut: Animation = AnimationUtils.loadAnimation(this, R.anim.fade_out)
//
//        // Apply the exit animation to your splash content
//
//        // Apply the exit animation to your splash content
//        binding.splashLayout.startAnimation(fadeOut)

        Handler().postDelayed({
            // Start the main activity after the splash delay
            val mainIntent = Intent(this@SplashActivity, SignInActivity::class.java)
            startActivity(mainIntent)
            finish() // Close the splash activity
        }, SPLASH_DELAY)
    }
}