package com.example.medease.adapters

import android.app.Notification
import android.app.NotificationChannel

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.medease.Home
import com.example.medease.R
import com.example.medease.addedit_medication
import java.lang.Integer.hashCode


const val tittle = "Medicine Reminder"
const val ChannelID = "MedEase"
const val Notificationid = 3467
const val message = "Medicine Name at 9:00pm"


class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // unique id for the channel
            val channelId = "MedEase"

            // name of the channel
            val channelName = "Medicine Reminders"

            // importance level of the channel
            val importance = NotificationManager.IMPORTANCE_HIGH

            // creating channel
            val channel =
                NotificationChannel(channelId, channelName, importance)

            // enabling lights for the channel
            channel.enableLights(true)

            // setting light color for the channel
            channel.lightColor = Color.RED

            // enabling vibration for the channel
            channel.enableVibration(true)
            val notificationManager = context!!.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(
                channel
            ) // registering the channel with the system
        }

        Log.d("NotificationReceived", "Received notification")

        val i = Intent(context, Home::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, i, 0)

        val builder = NotificationCompat.Builder(context!!, "MedEase")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(tittle)
            .setContentText(message)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(Notificationid, builder.build())


    }
}


