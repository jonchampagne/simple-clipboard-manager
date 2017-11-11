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