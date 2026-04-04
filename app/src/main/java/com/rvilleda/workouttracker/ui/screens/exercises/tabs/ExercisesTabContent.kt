package com.rvilleda.workouttracker.ui.screens.exercises.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rvilleda.workouttracker.model.MuscleGroup
import com.rvilleda.workouttracker.model.allExercises
import com.rvilleda.workouttracker.ui.components.ExerciseCard

object ExercisesTabContent {

    @Composable
    private fun ExerciseListByGroup(
        muscleGroupName: MuscleGroup,
        searchQuery: String,
        onExerciseSelected: (String, String) -> Unit
    ) {
        val filteredExercises = allExercises.filter { exercise ->
            exercise.muscleGroup == muscleGroupName &&
                    exercise.name.contains(searchQuery, ignoreCase = true)
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {

            items(filteredExercises, key = { it.id }) { exercise ->
                ExerciseCard(
                    exercise = exercise,
                    onClick = {
                        onExerciseSelected(exercise.id, exercise.name)
                    }
                )
            }
        }
    }

    @Composable
    fun Chest(searchQuery: String, onNavigate: (String, String) -> Unit) =
        ExerciseListByGroup(MuscleGroup.CHEST, searchQuery, onNavigate)

    @Composable
    fun Back(searchQuery: String, onNavigate: (String, String) -> Unit) =
        ExerciseListByGroup(MuscleGroup.BACK, searchQuery, onNavigate)

    @Composable
    fun Legs(searchQuery: String, onNavigate: (String, String) -> Unit) =
        ExerciseListByGroup(MuscleGroup.LEGS, searchQuery, onNavigate)

    @Composable
    fun Shoulders(searchQuery: String, onNavigate: (String, String) -> Unit) =
        ExerciseListByGroup(MuscleGroup.SHOULDERS, searchQuery, onNavigate)

    @Composable
    fun Arms(searchQuery: String, onNavigate: (String, String) -> Unit) =
        ExerciseListByGroup(MuscleGroup.ARMS, searchQuery, onNavigate)

    @Composable
    fun Core(searchQuery: String, onNavigate: (String, String) -> Unit) =
        ExerciseListByGroup(MuscleGroup.CORE, searchQuery, onNavigate)
}