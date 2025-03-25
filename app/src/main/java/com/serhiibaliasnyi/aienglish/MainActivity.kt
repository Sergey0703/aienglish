package com.serhiibaliasnyi.aienglish

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.serhiibaliasnyi.aienglish.ui.theme.AiEnglishTheme
import com.serhiibaliasnyi.aienglish.ui.viewmodel.ImportState
import com.serhiibaliasnyi.aienglish.ui.viewmodel.MainViewModel
import com.serhiibaliasnyi.aienglish.data.entity.WordEntity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material.icons.filled.Create
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.serhiibaliasnyi.aienglish.navigation.Screen
import com.serhiibaliasnyi.aienglish.ui.screen.DictionaryScreen
import com.serhiibaliasnyi.aienglish.ui.screen.TextGenerationScreen
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.material.icons.filled.VolumeUp

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AiEnglishTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                androidx.compose.material3.Scaffold(
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                icon = { Icon(Icons.Filled.List, contentDescription = "Dictionary") },
                                label = { Text("Dictionary") },
                                selected = currentRoute == Screen.Dictionary.route,
                                onClick = { navController.navigate(Screen.Dictionary.route) }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Filled.Create, contentDescription = "Generate Text") },
                                label = { Text("Generate") },
                                selected = currentRoute == Screen.TextGeneration.route,
                                onClick = { navController.navigate(Screen.TextGeneration.route) }
                            )
                        }
                    }
                ) { paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Dictionary.route
                    ) {
                        composable(Screen.Dictionary.route) {
                            DictionaryScreen(paddingValues = paddingValues)
                        }
                        composable(Screen.TextGeneration.route) {
                            TextGenerationScreen(paddingValues = paddingValues)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
    var showAddDialog by remember { mutableStateOf(false) }
    val selectedWord by viewModel.selectedWord.collectAsState()
    val editMode by viewModel.editMode.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val words by viewModel.filteredWords.collectAsState()
    val importState by viewModel.importState.collectAsState()
    val wordCount by viewModel.wordCount.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "AI English",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Поисковая строка
        TextField(
            value = searchQuery,
            onValueChange = { 
                searchQuery = it
                viewModel.filterWords(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            placeholder = { Text("Поиск слов...") },
            singleLine = true
        )

        // Статистика и кнопка импорта
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Слов в базе данных: $wordCount")
            if (importState is ImportState.Initial) {
                Button(onClick = { viewModel.importWords() }) {
                    Text("Импортировать")
                }
            }
        }

        // Кнопка добавления нового слова
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Добавить слово")
        }

        // Диалог добавления/редактирования
        if (showAddDialog || editMode) {
            WordDialog(
                word = if (editMode) selectedWord else null,
                isNew = !editMode,
                onDismiss = {
                    showAddDialog = false
                    viewModel.cancelEditing()
                },
                onSave = { word, translation, transcription ->
                    if (editMode) {
                        selectedWord?.id?.let { id ->
                            viewModel.updateWord(id, word, translation, transcription)
                        }
                    } else {
                        viewModel.addNewWord(word, translation, transcription)
                    }
                }
            )
        }

        // Список слов
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(words) { word ->
                WordCard(
                    word = word,
                    onEdit = {
                        viewModel.selectWord(word)
                        viewModel.startEditing()
                    },
                    onDelete = {
                        viewModel.selectWord(word)
                        viewModel.deleteSelectedWord()
                    },
                    onSpeak = {
                        viewModel.speakWord(word.word)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordDialog(
    word: WordEntity?,
    isNew: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var wordText by remember { mutableStateOf(word?.word ?: "") }
    var translationText by remember { mutableStateOf(word?.translation ?: "") }
    var transcriptionText by remember { mutableStateOf(word?.transcription?.removeSurrounding("[", "]") ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isNew) "Добавить слово" else "Редактировать слово") },
        text = {
            Column {
                TextField(
                    value = wordText,
                    onValueChange = { wordText = it },
                    label = { Text("Слово") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = translationText,
                    onValueChange = { translationText = it },
                    label = { Text("Перевод") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = transcriptionText,
                    onValueChange = { transcriptionText = it },
                    label = { Text("Транскрипция") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(wordText, translationText, transcriptionText)
                    onDismiss()
                }
            ) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

@Composable
fun WordCard(
    word: WordEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSpeak: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = word.word,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = word.transcription ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = word.translation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row {
                IconButton(onClick = onSpeak) {
                    Icon(Icons.Default.VolumeUp, contentDescription = "Воспроизвести")
                }
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Редактировать")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Удалить")
                }
            }
        }
    }
}