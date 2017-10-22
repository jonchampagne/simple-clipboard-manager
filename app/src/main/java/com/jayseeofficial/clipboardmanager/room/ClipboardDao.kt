package com.jayseeofficial.clipboardmanager.room

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface ClipboardDao {
    @Insert
    fun insertClipboardData(cd: ClipboardData)

    @Delete
    fun deleteClipboardData(cd: ClipboardData)

    // TODO: LiveData-ify this
    @Query("SELECT * FROM ClipboardData")
    fun getAll(): List<ClipboardData>
}