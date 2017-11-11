/*
 * The MIT License (MIT) (Modified)
 *
 * Copyright (c) 2017 Jon Champagne
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * The copyright owner is notified when the Software is published, distributed, sublicensed, and/or
 * copies are sold.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
import com.jayseeofficial.clipboardmanager.receiver.ClipboardCopyReceiver
import com.jayseeofficial.clipboardmanager.data.ClipboardData
import com.jayseeofficial.clipboardmanager.data.ClipboardHistoryDatabase
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

class ClipboardService : Service() {
    private val TAG = ClipboardService::javaClass.name

    private val CLIPBOARD_NOTIFICATION_ID = 3191

    private val NOTIFICATION_HISTORY_LENGTH = 3

    lateinit var history: ArrayList<ClipData?>

    override fun onBind(intent: Intent): IBinder? {
        // TODO: Return the communication channel to the service.
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
        running = true
    }

    override fun onDestroy() {
        running = false
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        setup()

        // Show the notification
        startForeground(CLIPBOARD_NOTIFICATION_ID, createNotification())

        return START_STICKY
    }

    fun setup() {
        history = ArrayList(NOTIFICATION_HISTORY_LENGTH)

        // Set up listening to the clipboard
        val cb = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cb.addPrimaryClipChangedListener { onPrimaryClipChanged() }
    }

    private fun createNotification(): Notification {
        val notification = NotificationCompat.Builder(this, getString(R.string.notification_channel_ongoing_id))
                .setSmallIcon(R.drawable.ic_ongoing)
                .setContentTitle(getString(R.string.clipboard_history))
                .setContentText(getString(R.string.notification_history_text))
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

    private fun onPrimaryClipChanged() {
        updateNotification()
        logPrimaryClipChanged()
    }

    private fun logPrimaryClipChanged() {
        thread {
            val cb = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

            val db = ClipboardHistoryDatabase.get(this)
            db.clipboardHistoryDao().insertClipboardData(ClipboardData(cb.primaryClip))
        }
    }

    private fun updateNotification() {
        val cb = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        history.add(cb.primaryClip)
        if (history.size > NOTIFICATION_HISTORY_LENGTH) {
            Log.d(TAG, "Removed clip data: " + history.removeAt(0))
        }

        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.notify(CLIPBOARD_NOTIFICATION_ID, createNotification())
    }

    companion object {
        private var running: Boolean = false

        fun isRunning(): Boolean {
            return running
        }
    }
}