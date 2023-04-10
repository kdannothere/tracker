package com.kdan.tracker.database.user_data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface UserDataDao {

    @Upsert
    fun upsertUserData(userData: UserData)

    @Query("SELECT * FROM ${UserDatabase.dbName} WHERE id = 0")
    fun getSavedUserData(): UserData?

    @Query("SELECT email FROM ${UserDatabase.dbName} WHERE id = 0")
    fun getSavedEmail(): String

    @Query("SELECT service_state FROM ${UserDatabase.dbName} WHERE id = 0")
    fun getSavedServiceState(): String
}