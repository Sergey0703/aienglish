package com.serhiibaliasnyi.aienglish.data.repository

import com.serhiibaliasnyi.aienglish.data.dao.WordDao
import com.serhiibaliasnyi.aienglish.data.entity.WordEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WordRepository @Inject constructor(
    private val wordDao: WordDao
) {
    fun getAllWords(): Flow<List<WordEntity>> = wordDao.getAllWords()
    
    suspend fun getRandomWords(limit: Int): List<WordEntity> = wordDao.getRandomWords(limit)
    
    suspend fun insertWords(words: List<WordEntity>) = wordDao.insertWords(words)
    
    suspend fun updateWord(word: WordEntity) = wordDao.updateWord(word)
} 