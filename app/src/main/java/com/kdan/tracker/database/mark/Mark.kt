package com.kdan.tracker.database.mark

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kdan.tracker.database.AppDatabase

@Entity(tableName = AppDatabase.tableMarks)
data class Mark(
    @PrimaryKey val time: String,
    @ColumnInfo val email: String,
    @ColumnInfo val latitude: String,
    @ColumnInfo val longitude: String,
)