package com.kdan.coredatabase.mark

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.kdan.coredatabase.AppDatabase

@Dao
interface MarkDao {

    @Upsert
    suspend fun upsertMark(mark: Mark)

    @Delete
    suspend fun deleteMark(mark: Mark)

    @Query("SELECT * FROM ${AppDatabase.tableLocalMarksTracker} WHERE email = :email")
    suspend fun getMarks(email: String): List<Mark>
}