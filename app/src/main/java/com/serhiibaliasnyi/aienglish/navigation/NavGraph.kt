package com.serhiibaliasnyi.aienglish.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Dictionary : Screen("dictionary")
    object Statistics : Screen("statistics")
    object Settings : Screen("settings")
} 