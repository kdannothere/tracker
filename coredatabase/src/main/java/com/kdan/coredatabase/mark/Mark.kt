package com.kdan.coredatabase.mark

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kdan.coredatabase.AppDatabase
import org.jetbrains.annotations.NotNull

@Entity(tableName = AppDatabase.tableLocalMarksTracker)
data class Mark(
    @PrimaryKey @NotNull val time: String,
    @ColumnInfo val email: String,
    @ColumnInfo val latitude: String,
    @ColumnInfo val longitude: String,
)