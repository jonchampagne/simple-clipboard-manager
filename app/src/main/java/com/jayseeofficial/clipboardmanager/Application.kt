package com.jayseeofficial.clipboardmanager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import com.jayseeofficial.clipboardmanager.service.ClipboardService

class Application : android.app.Application() {
    override fun onCreate() {
        super.onCreate()

        // The user visible description of the channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            // The ID of the channel
            val id: String = getString(R.string.notification_channel_ongoing_id)
            // The user-visible name of the channel
            val name = getString(R.string.notification_channel_ongoing_title)
            val description = getString(R.string.notification_channel_ongoing_description)
            val importance = NotificationManager.IMPORTANCE_LOW
            val mChannel = NotificationChannel(id, name, importance)
            // Configure the channel
            mChannel.description = description
            mChannel.enableLights(false)
            mNotificationManager.createNotificationChannel(mChannel)
        }

        startService(Intent(this, ClipboardService::class.java))
    }
}