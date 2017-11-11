package com.jayseeofficial.clipboardmanager.data

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface ClipboardHistoryDao {
    @Insert
    fun insertClipboardData(cd: ClipboardData)

    @Delete
    fun deleteClipboardData(cd: ClipboardData)

    @Query("DELETE FROM ClipboardData")
    fun deleteAll()

    @Query("SELECT * FROM ClipboardData")
    fun getAll(): LiveData<List<ClipboardData>>
}