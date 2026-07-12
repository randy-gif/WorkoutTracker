package com.rvilleda.workouttracker.ui.screens.createroutine

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.style.TextAlign
import com.rvilleda.workouttracker.model.ExerciseInSession
import com.rvilleda.workouttracker.model.ExerciseSet
import com.rvilleda.workouttracker.model.WeightUnit
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRoutineScreen(
    globalUnit: WeightUnit,
    onNavigateToExerciseSelection: () -> Unit,
    onSaveRoutine: (name: String) -> Unit, // Passes the routine name back to be saved
    onBack: () -> Unit,
    viewModel: CreateRoutineViewModel // You will need to create this ViewModel!
) {
    val routineExercises by viewModel.routineExercises.collectAsState()
    val routineName by viewModel.routineName.collectAsState()

    var showDiscardDialog by remember { mutableStateOf(false) }

    val lazyListState = rememberLazyListState()
    val reorderState = rememberReorderableLazyListState(lazyListState) { from, to ->
        val fromKey = from.key as? String ?: return@rememberReorderableLazyListState
        val toKey = to.key as? String ?: return@rememberReorderableLazyListState
        viewModel.moveExerciseByKey(fromKey, toKey)
    }

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text(text = "Discard Routine?") },
            text = { Text(text = "Are you sure you want to discard this routine? Any unsaved changes will be lost.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onBack()
                        showDiscardDialog = false
                    }
                ) {
                    Text("Discard", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Routine") },
                actions = {
                    TextButton(
                        onClick = {
                            val finalName = if (routineName.isNotBlank()) routineName.trim() else "My Routine"
                            onSaveRoutine(finalName)
                        },
                        // Disable save if there are no exercises
                        enabled = routineExercises.isNotEmpty()
                    ) {
                        Text("Save")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { showDiscardDialog = true }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.padding(padding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {

            // 1. Routine Name Input at the top
            item {
                OutlinedTextField(
                    value = routineName,
                    onValueChange = { viewModel.updateRoutineName(it) },
                    label = { Text("Routine Name") },
                    placeholder = { Text("e.g. Push Day, Full Body") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // 2. The Reorderable List of Exercises
            items(routineExercises, key = { it.id }) { exercise ->
                ReorderableItem(reorderState, key = exercise.id) { isDragging ->
                    RoutineExerciseCard(
                        exercise = exercise,
                        isDragging = isDragging,
                        dragModifier = Modifier.draggableHandle(),
                        onDeleteExercise = { viewModel.removeExercise(exercise.id) },
                        onAddSet = { viewModel.addSetToExercise(exercise.id, globalUnit) },
                        onUpdateSetWeight = { setId, weight -> viewModel.updateSetWeight(exercise.id, setId, weight) },
                        onUpdateSetReps = { setId, reps -> viewModel.updateSetReps(exercise.id, setId, reps) },
                        onToggleExerciseUnit = { viewModel.toggleExerciseUnit(exercise.id) },
                        onRemoveSet = { setId -> viewModel.removeSet(exercise.id, setId) },
                        onToggleAutoRest = { viewModel.toggleAutoRest(exercise.id) },
                        onUpdateRestTime = { seconds -> viewModel.updateRestTime(exercise.id, seconds) },
                    )
                }
            }

            // 3. Add Exercise Button
            item {
                OutlinedButton(
                    onClick = onNavigateToExerciseSelection,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("+ Add Exercise")
                }
            }
        }
    }
}

@Composable
fun RoutineExerciseCard(
    exercise: ExerciseInSession,
    isDragging: Boolean,
    dragModifier: Modifier = Modifier,
    onDeleteExercise: () -> Unit,
    onAddSet: () -> Unit,
    onToggleExerciseUnit: () -> Unit,
    onUpdateSetWeight: (String, String) -> Unit,
    onUpdateSetReps: (String, String) -> Unit,
    onRemoveSet: (String) -> Unit,
    onToggleAutoRest: () -> Unit,
    onUpdateRestTime: (Int) -> Unit,
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var showRestDialog by remember { mutableStateOf(false) }

    if (showRestDialog) {
        var selectedTime by remember { mutableStateOf(exercise.restTimeSeconds) }
        val timeOptions = (5..300 step 5).toList()
        val listState = rememberLazyListState()

        LaunchedEffect(Unit) {
            val initialIndex = timeOptions.indexOf(selectedTime).coerceAtLeast(0)
            listState.scrollToItem(initialIndex)
        }

        AlertDialog(
            onDismissRequest = { showRestDialog = false },
            title = { Text("Default Rest Time") },
            text = {
                Box(modifier = Modifier.heightIn(max = 350.dp)) {
                    LazyColumn(state = listState) {
                        items(timeOptions) { timeInSeconds ->
                            val minutes = timeInSeconds / 60
                            val seconds = timeInSeconds % 60
                            val displayTime = String.format("%dmin %02dsec", minutes, seconds)

                            Text(
                                text = displayTime,
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (selectedTime == timeInSeconds) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (selectedTime == timeInSeconds) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
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
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showRestDialog = false }) { Text("Cancel") }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDragging) 8.dp else 2.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 16.dp)) {
            // Header Row
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

                IconButton(
                    onClick = { },
                    modifier = dragModifier
                ) {
                    Icon(Icons.Default.Menu, contentDescription = "Drag to reorder")
                }
                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Options")
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        val minutes = exercise.restTimeSeconds / 60
                        val seconds = exercise.restTimeSeconds % 60
                        val displayTime = String.format("%dmin %02dsec", minutes, seconds)

                        DropdownMenuItem(
                            text = { Text(if (exercise.autoRestEnabled) "Disable Auto-Rest" else "Enable Auto-Rest") },
                            onClick = {
                                onToggleAutoRest()
                                menuExpanded = false
                            }
                        )

                        DropdownMenuItem(
                            text = { Text("Default Rest ($displayTime)") },
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

                        DropdownMenuItem(
                            text = { Text("Remove Exercise", color = MaterialTheme.colorScheme.error) },
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
                RoutineSetInputRow(
                    setNumber = index + 1,
                    set = set,
                    onWeightChange = { weight -> onUpdateSetWeight(set.id, weight) },
                    onRepsChange = { reps -> onUpdateSetReps(set.id, reps) },
                    onDelete = { onRemoveSet(set.id) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = onAddSet,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text("+ Add Target Set")
            }
        }
    }
}

@Composable
fun RoutineSetInputRow(
    setNumber: Int,
    set: ExerciseSet,
    onWeightChange: (String) -> Unit,
    onRepsChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Set Number
        Text(
            text = "$setNumber",
            modifier = Modifier.width(24.dp),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Target Weight Field
        OutlinedTextField(
            value = set.weight,
            onValueChange = onWeightChange,
            placeholder = {
                Text("0", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            },
            trailingIcon = {
                Text(
                    text = set.weightUnit.displayName,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(end = 12.dp)
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
            modifier = Modifier.weight(1f)
        )

        // Target Reps Field
        OutlinedTextField(
            value = set.reps,
            onValueChange = onRepsChange,
            placeholder = {
                Text("0", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            },
            trailingIcon = {
                Text(
                    text = "Reps",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(end = 12.dp)
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
            modifier = Modifier.weight(1f)
        )

        // Delete Button
        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(36.dp) // Slightly smaller touch target to save horizontal space
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Set",
                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f) // Slightly faded error color
            )
        }
    }
}