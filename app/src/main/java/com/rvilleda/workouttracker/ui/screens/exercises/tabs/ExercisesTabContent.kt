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
import com.rvilleda.workouttracker.model.MuscleGroup

object ExercisesTabContent {
    @Composable
    private fun ExerciseListByGroup(muscleGroupName: MuscleGroup) {
        val filteredExercises = allExercises.filter { it.muscleGroup == muscleGroupName }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
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
    fun Chest() = ExerciseListByGroup(MuscleGroup.CHEST)

    @Composable
    fun Back() = ExerciseListByGroup(MuscleGroup.BACK)

    @Composable
    fun Legs() = ExerciseListByGroup(MuscleGroup.LEGS)

    @Composable
    fun Shoulders() = ExerciseListByGroup(MuscleGroup.SHOULDERS)

    @Composable
    fun Arms() = ExerciseListByGroup(MuscleGroup.ARMS)

    @Composable
    fun Core() = ExerciseListByGroup(MuscleGroup.CORE)
}
