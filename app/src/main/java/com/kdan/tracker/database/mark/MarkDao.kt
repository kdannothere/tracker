package com.kdan.tracker.database.mark

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface MarkDao {

    @Upsert
    suspend fun upsertMark(mark: Mark)

    @Delete
    suspend fun deleteMark(mark: Mark)

    @Query("SELECT * FROM ${MarkDatabase.dbName}")
    fun getAllMarks(): List<Mark>
}