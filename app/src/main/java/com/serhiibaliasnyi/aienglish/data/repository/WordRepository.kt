package com.serhiibaliasnyi.aienglish.data.repository

import com.serhiibaliasnyi.aienglish.data.dao.WordDao
import com.serhiibaliasnyi.aienglish.data.entity.WordEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordRepository @Inject constructor(
    private val wordDao: WordDao
) {
    fun getAllWords(): Flow<List<WordEntity>> = wordDao.getAllWords()
    
    fun searchWords(query: String): Flow<List<WordEntity>> = wordDao.searchWords(query)
    
    suspend fun getWordById(id: Long): WordEntity? = wordDao.getWordById(id)
    
    suspend fun insertWord(word: WordEntity): Long = wordDao.insertWord(word)
    
    suspend fun updateWord(word: WordEntity) = wordDao.updateWord(word)
    
    suspend fun deleteWord(word: WordEntity) = wordDao.deleteWord(word)

    suspend fun insertWords(words: List<WordEntity>) = wordDao.insertWords(words)

    suspend fun getRandomWords(count: Int): List<WordEntity> {
        return wordDao.getRandomWords(count)
    }
} 