package com.kdan.coredatabase

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migrations {

    val from1To2: Migration = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            val tempTable = "${AppDatabase.tableLocalMarksTracker}_new"
            // Create the new table
            database.execSQL(
                "CREATE TABLE $tempTable (time TEXT, email TEXT, latitude TEXT, longitude TEXT PRIMARY KEY(time))"
            )
            // Copy the data
            database.execSQL(
                "INSERT INTO $tempTable (time, email, latitude, longitude) SELECT time, latitude, longitude FROM users"
            )
            // fill out column email with null
            database.execSQL(
                "INSERT INTO $tempTable (email) VALUES ()"
            )
            // Remove the old table
            database.execSQL("DROP TABLE ${AppDatabase.tableLocalMarksTracker}")
            // Change the table name to the correct one
            database.execSQL("ALTER TABLE $tempTable RENAME TO ${AppDatabase.tableLocalMarksTracker}")
        }
    }

    val from2To3: Migration = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                "CREATE TABLE ${AppDatabase.tableUser} (id INTEGER, email TEXT, service_state TEXT PRIMARY KEY(id))"
            )
        }
    }

    val from3To4: Migration = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                "CREATE TABLE ${AppDatabase.tableLocalMarksMap} (time TEXT NOT NULL, email TEXT NOT NULL, latitude TEXT NOT NULL, longitude TEXT NOT NULL, PRIMARY KEY(time))"
            )
        }
    }

}