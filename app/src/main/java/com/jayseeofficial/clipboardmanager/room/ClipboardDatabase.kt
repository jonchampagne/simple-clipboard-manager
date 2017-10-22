package com.jayseeofficial.clipboardmanager.room

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = arrayOf(ClipboardData::class), version = 1)
abstract class ClipboardDatabase : RoomDatabase() {
    abstract fun clipboardDao(): ClipboardDao

    companion object Singleton {
        lateinit private var reference: ClipboardDatabase
        private var initialized = false
        fun get(context: Context): ClipboardDatabase {
            if (!initialized || !reference.isOpen) {
                reference = Room.databaseBuilder(context, ClipboardDatabase::class.java, "clipboard.sqlite").build()
                initialized = true
            }
            return reference
        }
    }

}