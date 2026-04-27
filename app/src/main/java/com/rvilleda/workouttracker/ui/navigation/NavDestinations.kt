package com.rvilleda.workouttracker.ui.navigation

import com.rvilleda.workouttracker.R

enum class AppDestinations(
    val label: String,
    val icon: Int,
) {
    HOME("Home", R.drawable.ic_home),
    EXERCISES("Exercises", R.drawable.ic_exercises),
    EXERCISES_DATA("Chart", R.drawable.ic_exercises_data),
    SETTINGS("Settings", R.drawable.ic_settings)
}
