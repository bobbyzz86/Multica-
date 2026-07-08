package com.multica.app.di

import android.content.Context
import androidx.room.Room
import com.multica.app.data.local.MulticaDatabase
import com.multica.app.data.local.dao.IssueDao
import com.multica.app.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MulticaDatabase {
        return Room.databaseBuilder(
            context,
            MulticaDatabase::class.java,
            "multica_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideIssueDao(database: MulticaDatabase): IssueDao = database.issueDao()

    @Provides
    fun provideUserDao(database: MulticaDatabase): UserDao = database.userDao()
}
