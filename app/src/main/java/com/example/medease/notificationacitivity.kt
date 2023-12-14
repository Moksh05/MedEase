package com.example.medease

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class notificationacitivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notificationacitivity)


        Log.d("try1",intent.getStringExtra("SELECTED_TIME").toString())
        if (intent.hasExtra("SELECTED_TIME")) {
            val selectedTime = intent?.getStringExtra("SELECTED_TIME")
            val notificationText = intent?.getStringExtra("NOTIFICATION_TEXT")


            findViewById<TextView>(R.id.notification_text).append("$selectedTime + $notificationText")

            findViewById<Button>(R.id.stop_audio_button).setOnClickListener {
                finish()
                onBackPressed()
            }
        }


    }
}