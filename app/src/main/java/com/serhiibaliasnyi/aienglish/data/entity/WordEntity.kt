package com.serhiibaliasnyi.aienglish.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val word: String,
    val translation: String,
    val transcription: String? = null,
    val lastReviewed: LocalDateTime? = null,
    val reviewCount: Int = 0,
    val difficulty: Int = 0 // 0 - неизвестно, 1 - легко, 2 - средне, 3 - сложно
) 