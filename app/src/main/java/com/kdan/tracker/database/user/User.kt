package com.kdan.tracker.database.user

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kdan.tracker.database.AppDatabase

@Entity(tableName = AppDatabase.tableUser)
data class User(
    @PrimaryKey val id: Int = 0,
    @ColumnInfo var email: String = "",
    @ColumnInfo(name = "service_state") var serviceState: String = "off",
)
