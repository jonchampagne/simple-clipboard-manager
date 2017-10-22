package com.jayseeofficial.clipboardmanager.ui.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
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
    }
}
