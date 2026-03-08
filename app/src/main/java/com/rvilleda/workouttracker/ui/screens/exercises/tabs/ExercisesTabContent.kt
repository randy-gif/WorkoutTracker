package com.rvilleda.workouttracker.ui.screens.exercises.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rvilleda.workouttracker.model.allExercises
import com.rvilleda.workouttracker.ui.components.ExerciseCard

object ExercisesTabContent {
    @Composable
    private fun ExerciseListByGroup(muscleGroupName: String) {
        val filteredExercises = allExercises.filter { it.muscleGroup == muscleGroupName }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(filteredExercises.size) { index ->
                ExerciseCard(
                    exercise = filteredExercises[index],
                    onClick = { /* Handle exercise card click */ }
                )
            }
        }
    }
    @Composable
    fun Chest() = ExerciseListByGroup("Chest")

    @Composable
    fun Back() = ExerciseListByGroup("Back")

    @Composable
    fun Legs() = ExerciseListByGroup("Legs")

    @Composable
    fun Shoulders() = ExerciseListByGroup("Shoulders")

    @Composable
    fun Arms() = ExerciseListByGroup("Arms")

    @Composable
    fun Core() = ExerciseListByGroup("Core")

    @Composable
    fun Cardio() = ExerciseListByGroup("Cardio")
}
