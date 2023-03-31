package com.kdan.tracker.database

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_marks")
data class Mark(
@PrimaryKey val time: String,
@NonNull @ColumnInfo val email: String,
@NonNull @ColumnInfo val latitude: String,
@NonNull @ColumnInfo val longitude: String,
)