package com.rvilleda.workouttracker.ui.navigation

import com.rvilleda.workouttracker.R

enum class AppDestinations(
    val label: String,
    val icon: Int,
) {
    HOME("Home", R.drawable.ic_home),
    EXERCISES("Exercises", R.drawable.ic_exercises),
    HISTORY("History", R.drawable.ic_history),
    SETTINGS("Settings", R.drawable.ic_settings)
}
