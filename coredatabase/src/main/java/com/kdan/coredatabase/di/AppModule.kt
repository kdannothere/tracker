package com.kdan.coredatabase.di

import android.app.Application
import com.kdan.coredatabase.AppDatabase
import com.kdan.coredatabase.mark.MarkDao
import com.kdan.coredatabase.markmap.MarkMapDao
import com.kdan.coredatabase.user.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun getAppDatabase(application: Application): AppDatabase {
        return AppDatabase.getDatabase(application)
    }

    @Singleton
    @Provides
    fun getMarkDao(appDatabase: AppDatabase): MarkDao {
        return appDatabase.getMarkDao()
    }

    @Singleton
    @Provides
    fun getMarkMapDao(appDatabase: AppDatabase): MarkMapDao {
        return appDatabase.getMarkMapDao()
    }

    @Singleton
    @Provides
    fun getUserDao(appDatabase: AppDatabase): UserDao {
        return appDatabase.getUserDao()
    }
}