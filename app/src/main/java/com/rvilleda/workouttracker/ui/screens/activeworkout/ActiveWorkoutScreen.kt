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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.ui.res.painterResource
import com.rvilleda.workouttracker.R
import com.rvilleda.workouttracker.model.WeightUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveWorkoutScreen(
    globalUnit: WeightUnit,
    onNavigateToExerciseSelection: () -> Unit,
    onFinishWorkout: () -> Unit,
    onDiscardWorkout: () -> Unit,
    onBack: () -> Unit,
    viewModel: ActiveWorkoutViewModel = viewModel()
) {
    val activeExercises by viewModel.activeExercises.collectAsState()
    val timerText by viewModel.elapsedTime.collectAsState()

    val restTime by viewModel.restTimeRemaining.collectAsState()

    var showDiscardDialog by remember { mutableStateOf(false) }

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = {
                Text(text = "Discard Workout?")
            },
            text = {
                Text(text = "Are you sure you want to discard this workout? This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDiscardDialog = false
                        onDiscardWorkout()
                    }
                ) {
                    Text("Discard", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDiscardDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

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
                    Button(onClick = onFinishWorkout) {
                        Text("Save")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.KeyboardArrowDown, "Back")
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
                        onAdd30s = { viewModel.addRestTime(30) },
                        onSubtract30s = { viewModel.subtractRestTime(30) }
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
                    onAddSet = { viewModel.addSetToExercise(exercise.id, globalUnit) },
                    onUpdateSetWeight = { setId, weight ->
                        viewModel.updateSetWeight(exercise.id, setId, weight)
                    },
                    onUpdateSetReps = { setId, reps ->
                        viewModel.updateSetReps(exercise.id, setId, reps)
                    },
                    onToggleExerciseUnit = {  viewModel.toggleExerciseUnit(exercise.id) },
                    onRemoveSet = { setId -> viewModel.removeSet(exercise.id, setId) },
                    onToggleComplete = { setId -> viewModel.toggleSetCompletion(exercise.id, setId) },
                    onToggleAutoRest = { viewModel.toggleAutoRest(exercise.id) },
                    onUpdateRestTime = { seconds -> viewModel.updateRestTime(exercise.id, seconds) },
                )
            }
            item {
                Row(Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { showDiscardDialog = true },
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
    onAdd30s: () -> Unit,
    onSubtract30s: () -> Unit
) {

    val minutes = timeRemaining / 60
    val seconds = timeRemaining % 60
    val timeString = String.format("%d:%02d", minutes, seconds)

    Card(
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

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(painterResource(R.drawable.ic_timer), contentDescription = "Rest Timer")
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = timeString,
                    style = MaterialTheme.typography.headlineSmall,
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {

                TextButton(onClick = onSubtract30s) {
                    Text("-30s")
                }
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
    onToggleExerciseUnit: () -> Unit,
    onUpdateSetWeight: (String, String) -> Unit,
    onUpdateSetReps: (String, String) -> Unit,
    onRemoveSet: (String) -> Unit,
    onToggleComplete: (String) -> Unit,
    onToggleAutoRest: () -> Unit,
    onUpdateRestTime: (Int) -> Unit,
) {

    var menuExpanded by remember { mutableStateOf(false) }
    var showRestDialog by remember { mutableStateOf(false) }

    if (showRestDialog) {
        // 1. Track the selected time as an Integer instead of a String
        var selectedTime by remember { mutableStateOf(exercise.restTimeSeconds) }

        // 2. Generate a list of times: from 5 seconds to 300 seconds (5 mins) in steps of 5
        val timeOptions = (5..300 step 5).toList()

        // 3. Pro Feature: Remember list state so we can auto-scroll to their current time
        val listState = rememberLazyListState()

        LaunchedEffect(Unit) {
            // Find where their current time is in the list, and scroll there immediately
            val initialIndex = timeOptions.indexOf(selectedTime).coerceAtLeast(0)
            listState.scrollToItem(initialIndex)
        }

        AlertDialog(
            onDismissRequest = { showRestDialog = false },
            title = { Text("Set Rest Time") },
            text = {
                // Limit the height so the dialog doesn't take up the whole screen
                Box(modifier = Modifier.heightIn(max = 350.dp)) {
                    LazyColumn(state = listState) {
                        items(timeOptions) { timeInSeconds ->

                            // 4. Formatting Math: Convert "150" into "2 min 30 sec"
                            val minutes = timeInSeconds / 60
                            val seconds = timeInSeconds % 60
                            val displayTime = String.format("%dmin %02dsec", minutes, seconds)

                            // 5. The selectable row
                            Text(
                                text = displayTime,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (selectedTime == timeInSeconds)
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                else
                                    MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    // Highlight it if it's the currently selected option
                                    .background(
                                        if (selectedTime == timeInSeconds)
                                            MaterialTheme.colorScheme.primaryContainer
                                        else
                                            Color.Transparent
                                    )
                                    .clickable { selectedTime = timeInSeconds }
                                    .padding(vertical = 12.dp, horizontal = 16.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onUpdateRestTime(selectedTime)
                        showRestDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 16.dp)) {
            // The Header Row
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
                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Exercise Options")
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        val minutes = exercise.restTimeSeconds / 60
                        val seconds = exercise.restTimeSeconds % 60
                        val displayTime = String.format("%dmin %02dsec", minutes, seconds)
                        // Option 1: Toggle Auto Rest
                        DropdownMenuItem(
                            text = {
                                Text(if (exercise.autoRestEnabled) "Disable Auto-Rest" else "Enable Auto-Rest")
                            },
                            onClick = {
                                onToggleAutoRest()
                                menuExpanded = false
                            }
                        )

                        // Option 2: Adjust Time
                        DropdownMenuItem(
                            text = { Text("Adjust Rest Time (${displayTime})") },
                            onClick = {
                                menuExpanded = false
                                showRestDialog = true
                            }
                        )

                        DropdownMenuItem(
                            text = { Text(exercise.sets[0].weightUnit.displayName) },
                            onClick = {
                                onToggleExerciseUnit()
                                menuExpanded = false
                            }
                        )

                        // Option 3: Delete
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "Delete Exercise",
                                    color = MaterialTheme.colorScheme.error
                                )
                            },
                            onClick = {
                                onDeleteExercise()
                                menuExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            exercise.sets.forEachIndexed { index, set ->
                SetInputRow(
                    setNumber = index + 1,
                    set = set,
                    onWeightChange = { weight -> onUpdateSetWeight(set.id, weight) },
                    onRepsChange = { reps -> onUpdateSetReps(set.id, reps) },
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