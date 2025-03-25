package com.serhiibaliasnyi.aienglish.data.repository

import com.serhiibaliasnyi.aienglish.data.entity.WordEntity
import com.serhiibaliasnyi.aienglish.data.service.DeepSeekRequest
import com.serhiibaliasnyi.aienglish.data.service.DeepSeekService
import com.serhiibaliasnyi.aienglish.data.service.Message
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TextGenerationRepository @Inject constructor(
    private val wordRepository: WordRepository,
    private val deepSeekService: DeepSeekService
) {
    suspend fun getRandomWords(count: Int): List<WordEntity> {
        return wordRepository.getRandomWords(count)
    }

    suspend fun generateText(words: List<WordEntity>): String {
        val prompt = buildPrompt(words)
        val request = DeepSeekRequest(
            messages = listOf(Message(role = "user", content = prompt))
        )
        
        return try {
            val response = deepSeekService.generateText(
                apiKey = "Bearer YOUR_API_KEY", // Замените на реальный API ключ
                request = request
            )
            response.choices.firstOrNull()?.message?.content ?: "Failed to generate text"
        } catch (e: Exception) {
            "Error generating text: ${e.message}"
        }
    }

    private fun buildPrompt(words: List<WordEntity>): String {
        val wordsList = words.joinToString("\n") { "${it.word} - ${it.translation}" }
        return """
            I want you to create a story or text using the following English words. 
            Try to use as many words from the list as possible, but make the text natural and coherent.
            Here are the words with their translations:
            
            $wordsList
            
            Please create an engaging text that incorporates these words naturally.
            The text should be suitable for English language learners.
        """.trimIndent()
    }
} 