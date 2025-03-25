package com.serhiibaliasnyi.aienglish.navigation

sealed class Screen(val route: String) {
    object Dictionary : Screen("dictionary")
    object TextGeneration : Screen("text_generation")
} 