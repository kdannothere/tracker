package com.kdan.coredatabase.user

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.kdan.coredatabase.AppDatabase

@Dao
interface UserDao {

    @Upsert
    suspend fun upsertUser(user: User)

    @Query("SELECT * FROM ${AppDatabase.tableUser}")
    suspend fun getUser(): User?
}