package com.kdan.tracker.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_marks")
data class Mark(
@PrimaryKey val time: String,
@ColumnInfo val email: String,
@ColumnInfo val latitude: String,
@ColumnInfo val longitude: String,
)