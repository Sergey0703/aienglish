package com.serhiibaliasnyi.aienglish.ui.viewmodel

import android.app.Application
import android.speech.tts.TextToSpeech
import java.util.Locale
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.serhiibaliasnyi.aienglish.data.entity.WordEntity
import com.serhiibaliasnyi.aienglish.data.repository.WordRepository
import com.serhiibaliasnyi.aienglish.data.service.CsvImportService
import com.serhiibaliasnyi.aienglish.data.repository.TextGenerationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class MainViewModel @Inject constructor(
    private val application: Application,
    private val repository: WordRepository,
    private val csvImportService: CsvImportService,
    private val textGenerationRepository: TextGenerationRepository
) : ViewModel() {

    private val _importState = MutableStateFlow<ImportState>(ImportState.Initial)
    val importState: StateFlow<ImportState> = _importState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    private val allWords = repository.getAllWords()
    
    val filteredWords = combine(allWords, _searchQuery) { words, query ->
        if (query.isBlank()) {
            words
        } else {
            words.filter { word ->
                word.word.contains(query, ignoreCase = true) ||
                word.translation.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val wordCount: StateFlow<Int> = allWords.map { it.size }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    private val _selectedWord = MutableStateFlow<WordEntity?>(null)
    val selectedWord: StateFlow<WordEntity?> = _selectedWord.asStateFlow()

    private val _editMode = MutableStateFlow(false)
    val editMode: StateFlow<Boolean> = _editMode.asStateFlow()

    private val _generatedText = MutableStateFlow<String?>(null)
    val generatedText: StateFlow<String?> = _generatedText

    private val _selectedWords = MutableStateFlow<List<WordEntity>>(emptyList())
    val selectedWords: StateFlow<List<WordEntity>> = _selectedWords

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var textToSpeech: TextToSpeech? = null
    private var isTtsReady = false

    init {
        initTextToSpeech()
    }

    private fun initTextToSpeech() {
        textToSpeech = TextToSpeech(application) { status ->
            isTtsReady = status == TextToSpeech.SUCCESS
            if (isTtsReady) {
                textToSpeech?.language = Locale.US
            }
        }
    }

    fun speakWord(word: String) {
        if (isTtsReady) {
            textToSpeech?.speak(word, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    override fun onCleared() {
        super.onCleared()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }

    fun filterWords(query: String) {
        _searchQuery.value = query
    }

    fun importWords() {
        viewModelScope.launch {
            try {
                _importState.value = ImportState.Loading
                val words = csvImportService.importWordsFromCsv("ling.csv")
                repository.insertWords(words)
                _importState.value = ImportState.Success(words.size)
            } catch (e: Exception) {
                _importState.value = ImportState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun selectWord(word: WordEntity) {
        _selectedWord.value = word
    }

    fun startEditing() {
        _editMode.value = true
    }

    fun cancelEditing() {
        _editMode.value = false
        _selectedWord.value = null
    }

    fun deleteSelectedWord() {
        viewModelScope.launch {
            _selectedWord.value?.let { word ->
                repository.deleteWord(word)
                _selectedWord.value = null
            }
        }
    }

    fun updateWord(
        id: Long,
        word: String,
        translation: String,
        transcription: String
    ) {
        viewModelScope.launch {
            val updatedWord = WordEntity(
                id = id,
                word = word,
                translation = translation,
                transcription = transcription
            )
            repository.updateWord(updatedWord)
            _editMode.value = false
            _selectedWord.value = null
        }
    }

    fun addNewWord(
        word: String,
        translation: String,
        transcription: String
    ) {
        viewModelScope.launch {
            val newWord = WordEntity(
                word = word,
                translation = translation,
                transcription = transcription
            )
            repository.insertWord(newWord)
        }
    }

    fun generateDailyPractice() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val words = textGenerationRepository.getRandomWords(300)
                _selectedWords.value = words
                val text = textGenerationRepository.generateText(words)
                _generatedText.value = text
            } catch (e: Exception) {
                // Обработка ошибок
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearGeneratedText() {
        _generatedText.value = null
        _selectedWords.value = emptyList()
    }
}

sealed class ImportState {
    object Initial : ImportState()
    object Loading : ImportState()
    data class Success(val count: Int) : ImportState()
    data class Error(val message: String) : ImportState()
} 