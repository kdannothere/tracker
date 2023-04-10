package com.kdan.tracker.database.mark

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


@Database(
    entities = [Mark::class],
    version = 2
)

abstract class MarkDatabase : RoomDatabase() {

    abstract val dao: MarkDao

    companion object {

        const val dbName = "local_marks"

        private val migrationFrom1To2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                val tempDb = "${dbName}_new"
                // Create the new table
                database.execSQL(
                    "CREATE TABLE $tempDb (time TEXT, email TEXT, latitude TEXT, longitude TEXT PRIMARY KEY(time))"
                )
                // Copy the data
                database.execSQL(
                    "INSERT INTO $tempDb (time, email, latitude, longitude) SELECT time, latitude, longitude FROM users"
                )
                // fill out column email with null
                database.execSQL(
                    "INSERT INTO $tempDb (email) VALUES (null)"
                )
                // Remove the old table
                database.execSQL("DROP TABLE $dbName")
                // Change the table name to the correct one
                database.execSQL("ALTER TABLE $tempDb RENAME TO $dbName")
            }
        }

        @Volatile
        private var INSTANCE: MarkDatabase? = null

        fun getDatabase(context: Context): MarkDatabase {

            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MarkDatabase::class.java,
                    name = dbName
                )
                    .addMigrations(migrationFrom1To2)
                    .build()
                INSTANCE = instance
                return instance
            }

        }
    }
}
