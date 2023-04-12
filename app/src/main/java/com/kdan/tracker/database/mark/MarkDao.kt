package com.kdan.tracker.database.mark

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.kdan.tracker.database.AppDatabase

@Dao
interface MarkDao {

    @Upsert
    suspend fun upsertMark(mark: Mark)

    @Delete
    suspend fun deleteMark(mark: Mark)

    @Query("SELECT * FROM ${AppDatabase.tableMarks}")
    fun getAllMarks(): List<Mark>
}