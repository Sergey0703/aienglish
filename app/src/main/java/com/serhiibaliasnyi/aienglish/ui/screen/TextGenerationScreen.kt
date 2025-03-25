package com.serhiibaliasnyi.aienglish.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.serhiibaliasnyi.aienglish.ui.viewmodel.MainViewModel

@Composable
fun TextGenerationScreen(
    paddingValues: PaddingValues,
    viewModel: MainViewModel = hiltViewModel()
) {
    val generatedText by viewModel.generatedText.collectAsState()
    val selectedWords by viewModel.selectedWords.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        Button(
            onClick = { viewModel.generateDailyPractice() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text(if (isLoading) "Generating..." else "Generate Daily Practice")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedWords.isNotEmpty()) {
            Text(
                text = "Selected words:",
                style = MaterialTheme.typography.titleMedium
            )
            LazyColumn(
                modifier = Modifier
                    .height(100.dp)
            ) {
                items(selectedWords) { word ->
                    Text(text = "${word.word} - ${word.translation}")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!generatedText.isNullOrEmpty()) {
            Text(
                text = "Generated text:",
                style = MaterialTheme.typography.titleMedium
            )
            Text(text = generatedText ?: "")
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
} 