package com.example.medease.adapters

import android.app.Notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.medease.R
import com.example.medease.addedit_medication
import java.lang.Integer.hashCode


const val tittle = "Medicine Reminder"
const val ChannelID = "MedEase"
const val Notificationid = 3467
const val message = "Medicine Name at 9:00pm"


class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        Log.d("NotificationReceived", "Received notification")

        val i = Intent(context, addedit_medication::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_IMMUTABLE)

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
