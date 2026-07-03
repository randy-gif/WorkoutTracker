package com.rvilleda.workouttracker.ui.screens.exercises.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rvilleda.workouttracker.model.Exercise
import com.rvilleda.workouttracker.model.MuscleGroup
import com.rvilleda.workouttracker.ui.components.ExerciseCard
import com.rvilleda.workouttracker.ui.components.CreateExerciseCard

object ExercisesTabContent {

    @Composable
    private fun ExerciseList(
        filteredExercises: List<Exercise>,
        selectedExercises: Set<Exercise>,
        onToggleSelection: (Exercise) -> Unit,
        selectionMode: Boolean,
        onToggleSelectionMode: () -> Unit,
        onCreateCustomExercise: (() -> Unit)? = null
    ) {


        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (filteredExercises.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No exercises found",
                        )
                    }
                }
                item {
                    CreateExerciseCard(
                        onClick = {
                            onCreateCustomExercise?.invoke()
                        }
                    )
                }
            } else {
                items(filteredExercises, key = { it.id }) { exercise ->
                    ExerciseCard(
                        exercise = exercise,
                        // Checks if the whole object is in the Set!
                        isSelected = selectedExercises.contains(exercise),
                        onItemLongClick = {
                            onToggleSelectionMode()
                            if (!selectedExercises.contains(exercise)) {
                                onToggleSelection(exercise)
                            }
                        },
                        isSelectionModeActive = selectionMode,
                        onItemClick = {
                            if (selectionMode) {
                                onToggleSelection(exercise)
                            } else {
                                // Go to exercise details screen
                            }
                        }
                    )
                }
            }

        }
    }


    @Composable
    private fun ExercisesListBySearch(
        allExercises: List<Exercise>,
        searchQuery: String,
        selectedExercises: Set<Exercise>,
        onToggleSelection: (Exercise) -> Unit,
        selectionMode: Boolean,
        onToggleSelectionMode: () -> Unit,
        onCreateCustomExercise: () -> Unit
    ) {
        val filteredExercises = allExercises.filter { exercise ->
            exercise.name.contains(searchQuery, ignoreCase = true) ||
                    exercise.muscleGroup.displayName.contains(searchQuery, ignoreCase = true) ||
                    exercise.equipment.displayName.contains(searchQuery, ignoreCase = true) ||
                    exercise.movementType.displayName.contains(searchQuery, ignoreCase = true)
        }
        ExerciseList(filteredExercises, selectedExercises, onToggleSelection, selectionMode, onToggleSelectionMode, onCreateCustomExercise)
    }

    // Update all the public functions to accept the new state and action
    @Composable
    fun Chest(chestExercises: List<Exercise>, selectedExercises: Set<Exercise>, onToggleSelection: (Exercise) -> Unit, selectionMode: Boolean, onToggleSelectionMode: () -> Unit) =
        ExerciseList(chestExercises, selectedExercises, onToggleSelection, selectionMode, onToggleSelectionMode)

    @Composable
    fun Back(backExercises: List<Exercise>, selectedExercises: Set<Exercise>, onToggleSelection: (Exercise) -> Unit, selectionMode: Boolean, onToggleSelectionMode: () -> Unit) =
        ExerciseList(backExercises, selectedExercises, onToggleSelection, selectionMode, onToggleSelectionMode)

    @Composable
    fun Legs(legsExercises: List<Exercise>, selectedExercises: Set<Exercise>, onToggleSelection: (Exercise) -> Unit, selectionMode: Boolean, onToggleSelectionMode: () -> Unit) =
        ExerciseList(legsExercises, selectedExercises, onToggleSelection, selectionMode, onToggleSelectionMode)

    @Composable
    fun Shoulders(shouldersExercises: List<Exercise>, selectedExercises: Set<Exercise>, onToggleSelection: (Exercise) -> Unit, selectionMode: Boolean, onToggleSelectionMode: () -> Unit) =
        ExerciseList(shouldersExercises, selectedExercises, onToggleSelection, selectionMode, onToggleSelectionMode)

    @Composable
    fun Arms(armsExercises: List<Exercise>, selectedExercises: Set<Exercise>, onToggleSelection: (Exercise) -> Unit, selectionMode: Boolean, onToggleSelectionMode: () -> Unit) =
        ExerciseList(armsExercises, selectedExercises, onToggleSelection, selectionMode, onToggleSelectionMode)

    @Composable
    fun Core(coreExercises: List<Exercise>, selectedExercises: Set<Exercise>, onToggleSelection: (Exercise) -> Unit, selectionMode: Boolean, onToggleSelectionMode: () -> Unit) =
        ExerciseList(coreExercises, selectedExercises, onToggleSelection, selectionMode, onToggleSelectionMode)

    @Composable
    fun Search(
        allExercises: List<Exercise>,
        searchQuery: String,
        selectedExercises: Set<Exercise>,
        onToggleSelection: (Exercise) -> Unit,
        selectionMode: Boolean,
        onToggleSelectionMode: () -> Unit,
        onCreateCustomExercise: () -> Unit
    ) =
        ExercisesListBySearch(
            allExercises,
            searchQuery,
            selectedExercises,
            onToggleSelection,
            selectionMode,
            onToggleSelectionMode,
            onCreateCustomExercise
        )
}