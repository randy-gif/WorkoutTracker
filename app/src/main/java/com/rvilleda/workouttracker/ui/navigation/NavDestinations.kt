package com.rvilleda.workouttracker.ui.navigation

import com.rvilleda.workouttracker.R

enum class TopTab(val route: String, val label: String) {
    FOR_YOU("for_you", "For You"),
    TOP_EXERCISES("top_exercises", "Top Exercises"),
    TOP_ROUTINES("top_routines", "Top Routines"),
    TUTORIALS("tutorials", "Tutorials")
}

enum class AppDestinations(
    val label: String,
    val icon: Int,
) {
    HOME("Home", R.drawable.ic_home),
    EXERCISES("Exercises", R.drawable.ic_exercises),
    EXERCISES_DATA("Chart", R.drawable.ic_exercises_data),
}
