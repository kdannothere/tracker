package com.kdan.coredatabase.markmap

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kdan.coredatabase.AppDatabase

@Entity(tableName = AppDatabase.tableLocalMarksMap)
data class MarkMap(
    @PrimaryKey val time: String,
    @ColumnInfo val email: String,
    @ColumnInfo val latitude: String,
    @ColumnInfo val longitude: String,
)