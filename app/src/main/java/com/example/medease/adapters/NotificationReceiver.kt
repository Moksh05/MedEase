package com.example.medease.adapters

import StopAudioReceiver
import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.medease.Home
import com.example.medease.R
import com.example.medease.notificationacitivity
import java.util.Random

const val title = "Dosage Reminder"
const val ChannelID = "MedEase"



class NotificationReceiver : BroadcastReceiver() {

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent?) {
        Log.d("NotificationReceived", "Received notification")

        // Check for notification permission

        val random = Random()
        val notificationID = random.nextInt()
        // Create an intent to open the Home activity
        val selectedTime = intent?.getStringExtra("SELECTED_TIME")
        val notificationText = intent?.getStringExtra("NOTIFICATION_TEXT")
        val i = Intent(context, notificationacitivity::class.java)

        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, i, 0)

//        only notification right now
//my original code

        val builder = NotificationCompat.Builder(context, ChannelID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(title)
            .setContentText("Its time to take your $notificationText at $selectedTime")
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(notificationID, builder.build())

//        context.startActivity(i)





//        //new code to try
//        val notificationManager = NotificationManagerCompat.from(context)
//        val notificationIntent = Intent(context, notificationacitivity::class.java)
//        val pendingIntent =
//            PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
//
//        // Create an intent to stop the audio
//        val stopIntent = Intent(context, StopAudioReceiver::class.java)
//        val stopPendingIntent = PendingIntent.getBroadcast(
//            context,
//            0,
//            stopIntent,
//            PendingIntent.FLAG_UPDATE_CURRENT
//        )
//
//
//        // Create a MediaStyle for the notification
//        val mediaStyle = androidx.media.app.NotificationCompat.MediaStyle()
//            .setMediaSession(null) // Set to null as there is no media session
//            .setShowCancelButton(true)
//            .setCancelButtonIntent(stopPendingIntent)
//
//        // Create the notification
//        val builder = NotificationCompat.Builder(context, ChannelID)
//            .setSmallIcon(R.drawable.colored_logo)
//            .setContentTitle(title)
//            .setContentText("Its time to take your $notificationText Medicine")
//            .setAutoCancel(true)
//            .setStyle(mediaStyle)
//            .addAction(
//                R.drawable.ic_stop,
//                "Stop Audio",
//                stopPendingIntent
//            )
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setContentIntent(pendingIntent)
//
//        // Issue the notification
//        notificationManager.notify(notificationID, builder.build())
//
//        // Play audio
//        val mp = MediaPlayer.create(context, R.raw.alarm_audio)
//        mp.start()
    }
}
