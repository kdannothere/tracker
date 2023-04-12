package com.kdan.tracker.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kdan.tracker.database.mark.Mark
import com.kdan.tracker.database.mark.MarkDao
import com.kdan.tracker.database.user.User
import com.kdan.tracker.database.user.UserDao

@Database(
    entities = [Mark::class, User::class],
    version = 3
)

abstract class AppDatabase : RoomDatabase() {

    abstract val markDao: MarkDao
    abstract val userDao: UserDao

    companion object {

        private const val dbName = "app_database"
        const val tableMarks = "local_marks"
        const val tableUser = "local_user_data"

        private val migrationFrom1To2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                val tempTable = "${tableMarks}_new"
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
                    "INSERT INTO $tempTable (email) VALUES (null)"
                )
                // Remove the old table
                database.execSQL("DROP TABLE $tableMarks")
                // Change the table name to the correct one
                database.execSQL("ALTER TABLE $tempTable RENAME TO $tableMarks")
            }
        }

        private val migrationFrom2To3: Migration = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create the new table
                database.execSQL(
                    "CREATE TABLE $tableUser (id INTEGER, email TEXT, service_state TEXT PRIMARY KEY(id))"
                )
                // Insert the data
//                database.execSQL(
//                    "INSERT INTO $tableUser (id, email, service_state) VALUES (0, empty, off)"
//                )
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {

            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    name = dbName
                )
                    .addMigrations(migrationFrom1To2, migrationFrom2To3)
                    .build()
                INSTANCE = instance
                return instance
            }

        }
    }
}
