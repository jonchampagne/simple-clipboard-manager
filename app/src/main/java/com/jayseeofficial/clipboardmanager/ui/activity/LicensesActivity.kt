package com.jayseeofficial.clipboardmanager.ui.activity

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.damianogiusti.acknowledgements.Acknowledger
import com.jayseeofficial.clipboardmanager.R

import kotlinx.android.synthetic.main.activity_licenses.*

class LicensesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_licenses)

        Acknowledger.with(this).load(R.raw.notices).into(webview)
    }

}
