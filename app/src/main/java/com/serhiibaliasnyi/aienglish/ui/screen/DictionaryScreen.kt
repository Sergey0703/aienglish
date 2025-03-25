package com.serhiibaliasnyi.aienglish.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.serhiibaliasnyi.aienglish.ui.viewmodel.MainViewModel
import com.serhiibaliasnyi.aienglish.MainScreen

@Composable
fun DictionaryScreen(
    paddingValues: PaddingValues,
    viewModel: MainViewModel = hiltViewModel()
) {
    Box(modifier = Modifier.padding(paddingValues)) {
        MainScreen(viewModel)
    }
} 