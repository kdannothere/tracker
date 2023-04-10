package com.kdan.tracker.database.user_data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_user")
data class UserData(
    @PrimaryKey val id: Int = 0,
    @ColumnInfo var email: String = "",
    @ColumnInfo(name = "service_state") var serviceState: String = "off",
)
