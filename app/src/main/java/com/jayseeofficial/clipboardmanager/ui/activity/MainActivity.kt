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

package com.jayseeofficial.clipboardmanager.ui.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.jayseeofficial.clipboardmanager.Application
import com.jayseeofficial.clipboardmanager.R
import com.jayseeofficial.clipboardmanager.data.ClipboardHistoryDatabase
import com.jayseeofficial.clipboardmanager.service.ClipboardService
import com.jayseeofficial.clipboardmanager.ui.adapter.ClipboardHistoryAdapter

import kotlinx.android.synthetic.main.activity_main.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Pop to the background thread to get the database reference and LiveData from that, then
        // back to the main thread to bind the RecyclerView to the LiveData adapter
        thread {
            val history = ClipboardHistoryDatabase.get(this).clipboardHistoryDao().getAll()
            runOnUiThread {
                val cbha = ClipboardHistoryAdapter(history, this)
                cbha.setItemClickedListener {
                    val cd = ClipData.newPlainText("", it.text)
                    val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    cm.primaryClip = cd

                    Toast.makeText(this, "Copied to clipboard:\n" + it.text, Toast.LENGTH_SHORT).show()
                }
                rvHistory.adapter = cbha
                rvHistory.layoutManager = LinearLayoutManager(this)
            }
        }

        // Check the status of the service and display the togglebutton appropriately
        tbServiceOnOff.isChecked = ClipboardService.isRunning()
        tbServiceOnOff.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Application.startClipboardService(this)
                tbServiceOnOff.isChecked = true
            } else {
                Application.stopClipboardService(this)
                tbServiceOnOff.isChecked = false
            }
        }

        val prefs = getSharedPreferences(getString(R.string.sp_file_name), Context.MODE_PRIVATE)
        cbStartOnBoot.isChecked = prefs.getBoolean(getString(R.string.sp_start_on_boot), false)
        cbStartOnBoot.setOnCheckedChangeListener { _, isChecked ->
            val editor = prefs.edit()
            editor.putBoolean(getString(R.string.sp_start_on_boot), isChecked)
            editor.apply()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // Like a switch statement, but infinitely more readable
        return when (item?.itemId) {
            R.id.menuitem_open_source_licenses -> {
                Application.showOpenSourceLicensesDialog(this)
                true
            }
            R.id.menuitem_delete_full_history -> {
                AlertDialog.Builder(this)
                        .setTitle(getString(R.string.dialog_text_delete_full_history))
                        .setPositiveButton(getString(R.string.yes), { _: DialogInterface, _: Int -> deleteHistory() })
                        .setNegativeButton(getString(R.string.no), { _: DialogInterface, _: Int -> /* Do nothing */ })
                        .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun deleteHistory() {
        thread {
            val chd = ClipboardHistoryDatabase.get(this).clipboardHistoryDao()
            chd.deleteAll()
        }
    }
}
