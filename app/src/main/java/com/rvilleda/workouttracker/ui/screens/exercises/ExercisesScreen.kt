package com.rvilleda.workouttracker.ui.screens.exercises

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.rvilleda.workouttracker.R
import com.rvilleda.workouttracker.model.Exercise
import com.rvilleda.workouttracker.ui.screens.exercises.components.ExercisesTabs
import com.rvilleda.workouttracker.ui.screens.exercises.components.TabRowHeader
import androidx.compose.ui.graphics.RectangleShape
import com.rvilleda.workouttracker.model.MuscleGroup
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.Alignment
import com.rvilleda.workouttracker.ui.components.CreateExerciseCard
import com.rvilleda.workouttracker.ui.components.ExerciseCard
import androidx.compose.foundation.lazy.items

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisesScreen(
    onAddToWorkout: (Set<Exercise>) -> Unit,
    onCreateWorkout: (Set<Exercise>) -> Unit,
    isWorkoutActive: Boolean,
    onCreateCustomExercise: () -> Unit,
    onBack: () -> Unit,
    viewModel: ExerciseViewModel
) {
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topBarState)

    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(ExercisesTabs.CHEST) }

    val selectedExercises by viewModel.selectedExercises.collectAsState()
    val selectionMode by viewModel.selectionMode.collectAsState()
    val allDbExercises by viewModel.exercises.collectAsState()

    val displayedExercises = remember(searchQuery, selectedTab, allDbExercises) {
        if (searchQuery.isNotEmpty()) {
            allDbExercises.filter { exercise ->
                exercise.name.contains(searchQuery, ignoreCase = true) ||
                        exercise.muscleGroup.displayName.contains(searchQuery, ignoreCase = true) ||
                        exercise.equipment.displayName.contains(searchQuery, ignoreCase = true) ||
                        exercise.movementType.displayName.contains(searchQuery, ignoreCase = true)
            }
        } else {
            when (selectedTab) {
                ExercisesTabs.CHEST -> allDbExercises.filter { it.muscleGroup == MuscleGroup.CHEST }
                ExercisesTabs.BACK -> allDbExercises.filter { it.muscleGroup == MuscleGroup.BACK }
                ExercisesTabs.LEGS -> allDbExercises.filter { it.muscleGroup == MuscleGroup.LEGS }
                ExercisesTabs.SHOULDERS -> allDbExercises.filter { it.muscleGroup == MuscleGroup.SHOULDERS }
                ExercisesTabs.ARMS -> allDbExercises.filter { it.muscleGroup == MuscleGroup.ARMS }
                ExercisesTabs.CORE -> allDbExercises.filter { it.muscleGroup == MuscleGroup.CORE }
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.fillMaxWidth().padding(end = 16.dp),
                            placeholder = { Text("Search exercises...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
                if(searchQuery.length == 0) {
                    TabRowHeader(
                        currentTab = selectedTab,
                        onTabSelected = { newTab -> selectedTab = newTab }
                    )
                }
            }
        },
        bottomBar = {
            if (selectedExercises.isNotEmpty() && selectionMode) {
                Button(
                    onClick = {
                        if(isWorkoutActive){
                            onAddToWorkout(selectedExercises)
                        } else {
                            onCreateWorkout(selectedExercises)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RectangleShape, // Removes the rounded corners completely
                    contentPadding = PaddingValues(vertical = 16.dp) // Makes the button a bit taller and easier to tap
                ) {
                    Text("Add ${selectedExercises.size} Exercises to Workout")
                }
            }
        }
    ) { padding ->

        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            ExerciseList(
                exercises = displayedExercises,
                isWorkoutActive = isWorkoutActive,
                selectedExercises = selectedExercises,
                selectionMode = selectionMode,
                onToggleSelection = viewModel::toggleSelection,
                onToggleSelectionMode = viewModel::toggleSelectionMode,
                onCreateCustomExercise = onCreateCustomExercise,
                onAddToWorkout = onAddToWorkout
            )
        }
    }
}

@Composable
fun ExerciseList(
    exercises: List<Exercise>,
    isWorkoutActive: Boolean,
    selectedExercises: Set<Exercise>,
    selectionMode: Boolean,
    onToggleSelection: (Exercise) -> Unit,
    onToggleSelectionMode: () -> Unit,
    onAddToWorkout: (Set<Exercise>) -> Unit,
    onCreateCustomExercise: (() -> Unit)? = null
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (exercises.isEmpty()) {
            item {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("No exercises found")
                }
            }
            item {
                CreateExerciseCard(onClick = { onCreateCustomExercise?.invoke() })
            }
        } else {
            items(exercises, key = { it.id }) { exercise ->
                ExerciseCard(
                    exercise = exercise,
                    isSelected = selectedExercises.contains(exercise),
                    isSelectionModeActive = selectionMode,
                    onItemLongClick = {
                        onToggleSelectionMode()
                        if (!selectedExercises.contains(exercise)) {
                            onToggleSelection(exercise)
                        }
                    },
                    onItemClick = {
                        if (selectionMode) {
                            onToggleSelection(exercise)
                        } else if (isWorkoutActive) {
                            onAddToWorkout(setOf(exercise))
                        } else {
                            // Go to exercise details screen
                        }
                    }
                )
            }
        }
    }
}