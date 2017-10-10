package com.jayseeofficial.clipboardmanager.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.jayseeofficial.clipboardmanager.R
import com.jayseeofficial.clipboardmanager.broadcastreceiver.ClipboardCopyReceiver
import kotlin.collections.ArrayList

class ClipboardService : Service() {
    val TAG = ClipboardService::javaClass.name

    val CLIPBOARD_NOTIFICATION_ID = 3191

    lateinit var history: ArrayList<ClipData?>

    override fun onBind(intent: Intent): IBinder? {
        // TODO: Return the communication channel to the service.
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        setup()

        // Show the notification
        startForeground(CLIPBOARD_NOTIFICATION_ID, createNotification())

        return START_NOT_STICKY
    }

    fun setup() {
        val historyLength = 3 // Seemingly max allowed by the system

        history = ArrayList(historyLength)

        // Set up listening to the clipboard
        val cb = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        cb.addPrimaryClipChangedListener {
            history.add(cb.primaryClip)
            if (history.size > historyLength) {
                Log.d(TAG, "Removed clip data: " + history.removeAt(0))
            }
            mNotificationManager.notify(CLIPBOARD_NOTIFICATION_ID, createNotification())
        }
    }

    fun createNotification(): Notification {
        val notification = NotificationCompat.Builder(this, getString(R.string.notification_channel_ongoing_id))
                .setSmallIcon(R.drawable.ic_ongoing)
                .setContentTitle(getString(R.string.clipboard_history))
                .setContentText("Tap below to copy")
                .setOngoing(true)
        history.forEach({
            if (it != null) {
                val intent = Intent(this, ClipboardCopyReceiver::class.java)
                intent.putExtra(ClipboardCopyReceiver.KEY_CLIPDATA, it)
                val pendingIntent = PendingIntent.getBroadcast(this, CLIPBOARD_NOTIFICATION_ID, intent, 0)
                notification.addAction(R.drawable.ic_ongoing, it.getItemAt(0).text, pendingIntent)
            }
        })
        return notification.build()
    }
}