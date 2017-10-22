package com.jayseeofficial.clipboardmanager.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = arrayOf(ClipboardData::class), version = 1)
abstract class ClipboardHistoryDatabase : RoomDatabase() {
    abstract fun clipboardHistoryDao(): ClipboardHistoryDao

    companion object Singleton {
        lateinit private var reference: ClipboardHistoryDatabase
        private var initialized = false
        fun get(context: Context): ClipboardHistoryDatabase {
            if (!initialized || !reference.isOpen) {
                reference = Room.databaseBuilder(context, ClipboardHistoryDatabase::class.java, "clipboard.sqlite").build()
                initialized = true
            }
            return reference
        }
    }

}