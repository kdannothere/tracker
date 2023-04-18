package com.kdan.coredatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kdan.coredatabase.mark.Mark
import com.kdan.coredatabase.mark.MarkDao
import com.kdan.coredatabase.markmap.MarkMap
import com.kdan.coredatabase.markmap.MarkMapDao
import com.kdan.coredatabase.user.User
import com.kdan.coredatabase.user.UserDao

@Database(
    entities = [Mark::class, MarkMap::class, User::class],
    version = 4,
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {

    abstract fun getMarkDao(): MarkDao
    abstract fun getMarkMapDao(): MarkMapDao
    abstract fun getUserDao(): UserDao

    companion object {

        private const val dbName = "app_database"
        const val tableLocalMarksTracker = "local_marks"
        const val tableLocalMarksMap = "local_marks_map"
        const val tableUser = "local_user_data"
        const val tableRemoteMarks = "remote_marks"

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
                    .addMigrations(
                        Migrations.from1To2,
                        Migrations.from2To3,
                        Migrations.from3To4
                    )
                    .build()
                INSTANCE = instance
                return instance
            }

        }
    }
}
