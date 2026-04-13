package com.rvilleda.workouttracker.ui.screens.activeworkout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rvilleda.workouttracker.model.ExerciseInSession
import com.rvilleda.workouttracker.model.ExerciseSet

@Composable
fun ActiveWorkoutScreen(
    exerciseId: String,
    exerciseName: String,
    onNavigateToExerciseSelection: () -> Unit,
    onFinishWorkout: () -> Unit,
    viewModel: ActiveWorkoutViewModel = viewModel()
) {
    val activeExercises by viewModel.activeExercises.collectAsState()
    val timerText by viewModel.elapsedTime.collectAsState()

    LaunchedEffect(Unit) {
        if (viewModel.activeExercises.value.isEmpty()) {
            viewModel.addExerciseToSession(
                baseExerciseId = exerciseId,
                exerciseName = exerciseName
            )
        }
    }
    Scaffold(
        modifier = Modifier,

    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Text(
                text = timerText,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
            )
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                items(activeExercises, key = { it.id }) { exercise ->
                    ActiveExerciseCard(
                        exercise = exercise,
                        onAddSet = { viewModel.addSetToExercise(exercise.id) },
                        onUpdateSet = { setId, weight, reps ->
                            viewModel.updateSet(exercise.id, setId, weight, reps)
                        },
                        onRemoveSet = { setId ->
                            viewModel.removeSet(exercise.id, setId)
                        }
                    )
                }

                item {
                    OutlinedButton(
                        onClick = onNavigateToExerciseSelection,
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                    ) {
                        Text("+ Add Another Exercise")
                    }
                }
            }

            Button(
                onClick = onFinishWorkout,
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ){
                Text("Finish & Save Workout")
            }
        }
    }
}


@Composable
fun ActiveExerciseCard(
    exercise: ExerciseInSession,
    onAddSet: () -> Unit,
    onUpdateSet: (String, String, String) -> Unit,
    onRemoveSet: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = exercise.exerciseName,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Standard Column for sets (Do NOT use LazyColumn here)
            exercise.sets.forEachIndexed { index, set ->
                SetInputRow(
                    setNumber = index + 1,
                    set = set,
                    onWeightChange = { weight -> onUpdateSet(set.id, weight, set.reps) },
                    onRepsChange = { reps -> onUpdateSet(set.id, set.weight, reps) },
                    onDelete = { onRemoveSet(set.id) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            TextButton(onClick = onAddSet) {
                Text("+ Add Set")
            }
        }
    }
}

@Composable
fun SetInputRow(
    setNumber: Int,
    set: ExerciseSet,
    onWeightChange: (String) -> Unit,
    onRepsChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "$setNumber", modifier = Modifier.width(24.dp))

        OutlinedTextField(
            value = set.weight,
            onValueChange = onWeightChange,
            label = { Text("Lbs/Kg") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f)
        )

        OutlinedTextField(
            value = set.reps,
            onValueChange = onRepsChange,
            label = { Text("Reps") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f)
        )

        IconButton(onClick = onDelete) {
            Text("X", color = MaterialTheme.colorScheme.error)
        }
    }
}