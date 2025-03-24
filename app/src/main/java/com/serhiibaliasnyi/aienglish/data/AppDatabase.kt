package com.serhiibaliasnyi.aienglish.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.serhiibaliasnyi.aienglish.data.dao.WordDao
import com.serhiibaliasnyi.aienglish.data.entity.WordEntity
import com.serhiibaliasnyi.aienglish.data.converter.DateTimeConverter

@Database(
    entities = [WordEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(DateTimeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
} 