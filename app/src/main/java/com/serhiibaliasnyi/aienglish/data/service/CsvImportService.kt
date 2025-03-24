package com.serhiibaliasnyi.aienglish.data.service

import android.content.Context
import com.serhiibaliasnyi.aienglish.data.entity.WordEntity
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject

class CsvImportService @Inject constructor(
    private val context: Context
) {
    fun importWordsFromCsv(fileName: String): List<WordEntity> {
        val words = mutableListOf<WordEntity>()
        try {
            context.assets.open(fileName).use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    // Пропускаем заголовок, если он есть
                    var line = reader.readLine()
                    
                    while (reader.readLine()?.also { line = it } != null) {
                        val columns = line.split(";")
                        if (columns.size >= 4) {
                            // Удаляем кавычки из всех полей
                            val word = columns[0].trim().removeSurrounding("\"")
                            val translation = columns[1].trim().removeSurrounding("\"")
                            val transcription = columns[3].trim().removeSurrounding("\"")
                                .removeSurrounding("[") // Если транскрипция в квадратных скобках
                                .removeSurrounding("]") // удаляем и их тоже
                            
                            words.add(
                                WordEntity(
                                    word = word,
                                    translation = translation,
                                    transcription = "[$transcription]", // Добавляем скобки обратно для красивого отображения
                                    lastReviewed = null,
                                    reviewCount = 0,
                                    difficulty = 0
                                )
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return words
    }
} 