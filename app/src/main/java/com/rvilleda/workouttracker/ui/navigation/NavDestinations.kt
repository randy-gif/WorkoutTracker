package com.rvilleda.workouttracker.ui.navigation

import com.rvilleda.workouttracker.R

enum class AppDestinations(
    val label: String,
    val icon: Int,
) {
    HOME("Home", R.drawable.ic_home),
    EXERCISES("Exercises", R.drawable.ic_exercises),
    EXERCISES_DATA("Chart", R.drawable.ic_exercises_data),
}

// STANDARD ROUTES (For full-screens, pop-ups, and sub-menus)
object AppRoutes {

    // The base route that the NavHost looks for
    const val ACTIVE_WORKOUT = "active_workout/{exerciseId}/{exerciseName}"

    // A helper function so you don't have to manually type the slashes everywhere
    fun createActiveWorkoutRoute(exerciseId: String, exerciseName: String): String {
        return "active_workout/$exerciseId/$exerciseName"
    }
}
