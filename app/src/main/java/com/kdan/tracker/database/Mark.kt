package com.kdan.tracker.database

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_marks")
data class Mark(
@PrimaryKey(autoGenerate = true) val id: Int = 0,
@NonNull @ColumnInfo val latitude: String,
@NonNull @ColumnInfo val longitude: String,
@NonNull @ColumnInfo val time: String,
)