package com.kdan.tracker.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Mark::class], version = 2)
abstract class TrackerDatabase : RoomDatabase() {

    abstract val dao: MarkDao

    companion object{

        @Volatile
        private var INSTANCE : TrackerDatabase? = null

        fun getDatabase(context: Context): TrackerDatabase{

            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TrackerDatabase::class.java,
                    "local_marks"
                )
                    .fallbackToDestructiveMigrationFrom(1)
                    .build()
                INSTANCE = instance
                return instance
            }

        }

    }
}