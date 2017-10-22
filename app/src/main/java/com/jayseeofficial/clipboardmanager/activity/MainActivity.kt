package com.jayseeofficial.clipboardmanager.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.jayseeofficial.clipboardmanager.Application
import com.jayseeofficial.clipboardmanager.R

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnStartService.setOnClickListener { Application.startClipboardService(this) }
        btnStopService.setOnClickListener { Application.stopClipboardService(this) }
    }
}
