package com.jayseeofficial.clipboardmanager.room

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.content.ClipData

@Entity(tableName = "ClipboardData")
class ClipboardData(@PrimaryKey var timestamp: Long = System.currentTimeMillis(), var text: String = "") {
    constructor(cd: ClipData) : this(text = cd.getItemAt(0).text.toString())
}