package com.kdan.tracker.database.user

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.kdan.tracker.database.AppDatabase

@Dao
interface UserDao {

    @Upsert
    fun upsertUser(user: User)

    @Query("SELECT * FROM ${AppDatabase.tableUser}")
    fun getUser(): User?
}