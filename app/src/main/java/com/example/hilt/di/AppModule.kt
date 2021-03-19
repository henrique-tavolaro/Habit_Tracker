package com.example.hilt.di

import android.content.Context
import androidx.room.Room
import com.example.hilt.db.UserDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

const val DATABASE = "Zeca Baleiro"

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideUserDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        UserDatabase::class.java,
        DATABASE
    ).build()

    @Singleton
    @Provides
    fun provideUserDao(db: UserDatabase) = db.getDao()

}