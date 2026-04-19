package com.rvilleda.workouttracker.ui.screens.activeworkout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rvilleda.workouttracker.model.ExerciseInSession
import com.rvilleda.workouttracker.model.ExerciseSet
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.ui.res.painterResource
import com.rvilleda.workouttracker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveWorkoutScreen(
    onNavigateToExerciseSelection: () -> Unit,
    onFinishWorkout: () -> Unit,
    onDiscardWorkout: () -> Unit,
    onBack: () -> Unit,
    viewModel: ActiveWorkoutViewModel = viewModel()
) {
    val activeExercises by viewModel.activeExercises.collectAsState()
    val timerText by viewModel.elapsedTime.collectAsState()
    val preferredUnit by viewModel.preferredUnit.collectAsState()

    val restTime by viewModel.restTimeRemaining.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = timerText,
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                },
                actions = {
                    TextButton(onClick = { viewModel.toggleUnit() }) {
                        Text(preferredUnit.displayName, style = MaterialTheme.typography.titleMedium)
                    }
                    Button(onClick = onFinishWorkout) {
                        Text("Save")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        bottomBar = {
                AnimatedVisibility(
                    visible = restTime > 0,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    RestTimerBanner(
                        timeRemaining = restTime,
                        onSkip = { viewModel.skipRestTimer() },
                        onAdd30s = { viewModel.addRestTime(30) }
                    )
                }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(activeExercises, key = { it.id }) { exercise ->
                ActiveExerciseCard(
                    exercise = exercise,
                    onMoveUp = { viewModel.moveExerciseUp(exercise.id) },
                    onMoveDown = { viewModel.moveExerciseDown(exercise.id) },
                    onDeleteExercise = { viewModel.removeExerciseFromSession(exercise.id) },
                    onAddSet = { viewModel.addSetToExercise(exercise.id) },
                    onUpdateSet = { setId, weight, reps ->
                        viewModel.updateSet(exercise.id, setId, weight, reps)
                    },
                    onRemoveSet = { setId -> viewModel.removeSet(exercise.id, setId) },
                    onToggleComplete = { setId -> viewModel.toggleSetCompletion(exercise.id, setId) },
                )
            }
            item {
                Row(Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = onDiscardWorkout,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp)
                    ) {
                        Text("Discard Workout")
                    }
                    OutlinedButton(
                        onClick = onNavigateToExerciseSelection,
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp)
                    ) {
                        Text("+ Add Exercise")
                    }
                }

            }
        }
    }
}

@Composable
fun RestTimerBanner(
    timeRemaining: Int,
    onSkip: () -> Unit,
    onAdd30s: () -> Unit
) {

    val minutes = timeRemaining / 60
    val seconds = timeRemaining % 60
    val timeString = String.format("%d:%02d", minutes, seconds)

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left side: Icon and Clock
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painterResource(R.drawable.ic_timer), contentDescription = "Rest Timer")
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = timeString,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Row {
                TextButton(onClick = onAdd30s) {
                    Text("+30s")
                }
                TextButton(onClick = onSkip) {
                    Text("Skip")
                }
            }
        }
    }
}

@Composable
fun ActiveExerciseCard(
    exercise: ExerciseInSession,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onDeleteExercise: () -> Unit,
    onAddSet: () -> Unit,
    onUpdateSet: (String, String, String) -> Unit,
    onRemoveSet: (String) -> Unit,
    onToggleComplete: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 16.dp)) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = exercise.exerciseName,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = onMoveUp) { Icon(Icons.Default.KeyboardArrowUp, "Move Up") }
                IconButton(onClick = onMoveDown) { Icon(Icons.Default.KeyboardArrowDown, "Move Down") }
                IconButton(onClick = onDeleteExercise) {
                    Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            exercise.sets.forEachIndexed { index, set ->
                SetInputRow(
                    setNumber = index + 1,
                    set = set,
                    onWeightChange = { weight -> onUpdateSet(set.id, weight, set.reps) },
                    onRepsChange = { reps -> onUpdateSet(set.id, set.weight, reps) },
                    onDelete = { onRemoveSet(set.id) },
                    onToggleComplete = { onToggleComplete(set.id) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = onAddSet,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
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
    onDelete: () -> Unit,
    onToggleComplete: () -> Unit
) {
    val rowColor = if (set.isCompleted) Color.Green.copy(alpha = 0.15f) else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(rowColor) // Colors the entire width of the card edge-to-edge!
            .padding(vertical = 4.dp, horizontal = 16.dp), // Pushes the contents inward to align with the header
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Set Number
        Text(
            text = "$setNumber",
            modifier = Modifier.width(24.dp),
            style = MaterialTheme.typography.titleMedium
        )

        // Weight Text Field
        OutlinedTextField(
            value = set.weight,
            onValueChange = onWeightChange,
            label = { Text(set.weightUnit.displayName) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.weight(1f)
        )

        // Reps Text Field
        OutlinedTextField(
            value = set.reps,
            onValueChange = onRepsChange,
            label = { Text("Reps") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.weight(1f)
        )

        // Checkmark Button
        IconButton(onClick = onToggleComplete) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Complete Set",
                tint = if (set.isCompleted) Color.Green else Color.Gray
            )
        }

        // Delete Button
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Set",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}