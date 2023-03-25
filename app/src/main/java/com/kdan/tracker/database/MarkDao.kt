package com.kdan.tracker.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface MarkDao {

    @Upsert
    suspend fun upsertMark(mark: Mark)

    @Delete
    suspend fun deleteMark(mark: Mark)

    @Query("SELECT * FROM local_marks")
    fun getAllMarks(): List<Mark>
}