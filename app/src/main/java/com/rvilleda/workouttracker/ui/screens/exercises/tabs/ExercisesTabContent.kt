package com.rvilleda.workouttracker.ui.screens.exercises.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rvilleda.workouttracker.model.Exercise
import com.rvilleda.workouttracker.model.MuscleGroup
import com.rvilleda.workouttracker.model.allExercises
import com.rvilleda.workouttracker.ui.components.ExerciseCard

object ExercisesTabContent {

    @Composable
    private fun ExerciseList(filteredExercises: List<Exercise>, onExerciseSelected: (String, String) -> Unit) {
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
    private fun ExerciseListByGroup(
        muscleGroupName: MuscleGroup,
        onExerciseSelected: (String, String) -> Unit
    ) {
        val filteredExercises = allExercises.filter { exercise ->
            exercise.muscleGroup == muscleGroupName
        }

        ExerciseList(filteredExercises, onExerciseSelected)

    }



    @Composable
    private fun ExercisesListBySearch(
        searchQuery: String,
        onExerciseSelected: (String, String) -> Unit
    ) {
        val filteredExercises = allExercises.filter { exercise ->
            exercise.name.contains(searchQuery, ignoreCase = true) ||
                    exercise.muscleGroup.displayName.contains(searchQuery, ignoreCase = true) ||
                    exercise.equipment.displayName.contains(searchQuery, ignoreCase = true) ||
                    exercise.movementType.displayName.contains(searchQuery, ignoreCase = true)
        }

        ExerciseList(filteredExercises, onExerciseSelected)
    }


    @Composable
    fun Chest( onNavigate: (String, String) -> Unit) =
        ExerciseListByGroup(MuscleGroup.CHEST, onNavigate)

    @Composable
    fun Back( onNavigate: (String, String) -> Unit) =
        ExerciseListByGroup(MuscleGroup.BACK,  onNavigate)

    @Composable
    fun Legs( onNavigate: (String, String) -> Unit) =
        ExerciseListByGroup(MuscleGroup.LEGS, onNavigate)

    @Composable
    fun Shoulders( onNavigate: (String, String) -> Unit) =
        ExerciseListByGroup(MuscleGroup.SHOULDERS, onNavigate)

    @Composable
    fun Arms( onNavigate: (String, String) -> Unit) =
        ExerciseListByGroup(MuscleGroup.ARMS,  onNavigate)

    @Composable
    fun Core( onNavigate: (String, String) -> Unit) =
        ExerciseListByGroup(MuscleGroup.CORE,  onNavigate)

    @Composable
    fun Search (searchQuery: String, onNavigate: (String, String) -> Unit) =
        ExercisesListBySearch(searchQuery, onNavigate)
}
