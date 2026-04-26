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
    private fun ExerciseList(
        filteredExercises: List<Exercise>,
        selectedExercises: Set<Exercise>, // Changed from Set<String>
        onToggleSelection: (Exercise) -> Unit // Changed from (String)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(filteredExercises, key = { it.id }) { exercise ->
                ExerciseCard(
                    exercise = exercise,
                    // Checks if the whole object is in the Set!
                    isSelected = selectedExercises.contains(exercise),
                    onClick = {
                        // Passes the whole object to the ViewModel!
                        onToggleSelection(exercise)
                    }
                )
            }
        }
    }

    @Composable
    private fun ExerciseListByGroup(
        muscleGroupName: MuscleGroup,
        selectedIds: Set<Exercise>,
        onToggleSelection: (Exercise) -> Unit
    ) {
        val filteredExercises = allExercises.filter { exercise ->
            exercise.muscleGroup == muscleGroupName
        }
        ExerciseList(filteredExercises, selectedIds, onToggleSelection)
    }

    @Composable
    private fun ExercisesListBySearch(
        searchQuery: String,
        selectedExercises: Set<Exercise>,
        onToggleSelection: (Exercise) -> Unit
    ) {
        val filteredExercises = allExercises.filter { exercise ->
            exercise.name.contains(searchQuery, ignoreCase = true) ||
                    exercise.muscleGroup.displayName.contains(searchQuery, ignoreCase = true) ||
                    exercise.equipment.displayName.contains(searchQuery, ignoreCase = true) ||
                    exercise.movementType.displayName.contains(searchQuery, ignoreCase = true)
        }
        ExerciseList(filteredExercises, selectedExercises, onToggleSelection)
    }

    // Update all the public functions to accept the new state and action
    @Composable
    fun Chest(selectedExercises: Set<Exercise>, onToggleSelection: (Exercise) -> Unit) =
        ExerciseListByGroup(MuscleGroup.CHEST, selectedExercises, onToggleSelection)

    @Composable
    fun Back(selectedExercises: Set<Exercise>, onToggleSelection: (Exercise) -> Unit) =
        ExerciseListByGroup(MuscleGroup.BACK, selectedExercises, onToggleSelection)

    @Composable
    fun Legs(selectedExercises: Set<Exercise>, onToggleSelection: (Exercise) -> Unit) =
        ExerciseListByGroup(MuscleGroup.LEGS, selectedExercises, onToggleSelection)

    @Composable
    fun Shoulders(selectedExercises: Set<Exercise>, onToggleSelection: (Exercise) -> Unit) =
        ExerciseListByGroup(MuscleGroup.SHOULDERS, selectedExercises, onToggleSelection)

    @Composable
    fun Arms(selectedExercises: Set<Exercise>, onToggleSelection: (Exercise) -> Unit) =
        ExerciseListByGroup(MuscleGroup.ARMS, selectedExercises, onToggleSelection)

    @Composable
    fun Core(selectedExercises: Set<Exercise>, onToggleSelection: (Exercise) -> Unit) =
        ExerciseListByGroup(MuscleGroup.CORE, selectedExercises, onToggleSelection)

    @Composable
    fun Search(searchQuery: String, selectedExercises: Set<Exercise>, onToggleSelection: (Exercise) -> Unit) =
        ExercisesListBySearch(searchQuery, selectedExercises, onToggleSelection)
}