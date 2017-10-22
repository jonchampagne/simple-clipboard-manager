package com.jayseeofficial.clipboardmanager.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.jayseeofficial.clipboardmanager.Application
import com.jayseeofficial.clipboardmanager.R

class BootReceiver : BroadcastReceiver() {
    private val TAG = BootReceiver::class.java.simpleName
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null && intent.action == "android.intent.action.BOOT_COMPLETED") {
            val prefs = context.getSharedPreferences(context.getString(R.string.sp_file_name), Context.MODE_PRIVATE)
            if (prefs.getBoolean(context.getString(R.string.sp_start_on_boot), false)) {
                Application.startClipboardService(context)
            }
        }
    }
}