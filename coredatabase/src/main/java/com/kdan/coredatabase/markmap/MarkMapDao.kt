package com.kdan.coredatabase.markmap

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.kdan.coredatabase.AppDatabase

@Dao
interface MarkMapDao {

    @Upsert
    suspend fun upsertMark(mark: MarkMap)

    @Delete
    suspend fun deleteMark(mark: MarkMap)

    @Query("SELECT * FROM ${AppDatabase.tableLocalMarksMap} WHERE email = :email")
    suspend fun getMarks(email: String): List<MarkMap>
}