package com.serhiibaliasnyi.aienglish.di

import android.content.Context
import androidx.room.Room
import com.serhiibaliasnyi.aienglish.data.AppDatabase
import com.serhiibaliasnyi.aienglish.data.dao.WordDao
import com.serhiibaliasnyi.aienglish.data.repository.WordRepository
import com.serhiibaliasnyi.aienglish.data.service.CsvImportService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "ai_english_db"
        )
        .fallbackToDestructiveMigration()
        .allowMainThreadQueries()
        .build()
    }

    @Provides
    @Singleton
    fun provideWordDao(database: AppDatabase): WordDao = database.wordDao()

    @Provides
    @Singleton
    fun provideWordRepository(wordDao: WordDao): WordRepository {
        return WordRepository(wordDao)
    }

    @Provides
    @Singleton
    fun provideCsvImportService(@ApplicationContext context: Context): CsvImportService {
        return CsvImportService(context)
    }
} 