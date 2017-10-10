package com.jayseeofficial.clipboardmanager.broadcastreceiver

import android.content.*
import android.util.Log

/**
 * Created by jon2 on 2017-10-02.
 */
class ClipboardCopyReceiver : BroadcastReceiver() {
    val TAG = ClipboardCopyReceiver::class.java.canonicalName

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            val mClipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            // Get the clipdata we were given and if we can, stick it on the clipboard
            mClipboardManager.primaryClip = intent.getParcelableExtra<ClipData>(KEY_CLIPDATA) ?: return
            Log.d(TAG,"Copied to clipboard!")
        }
    }

    companion object {
        val KEY_CLIPDATA = "clipdata"
    }
}