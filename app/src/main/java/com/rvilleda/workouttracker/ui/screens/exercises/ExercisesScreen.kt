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
import com.rvilleda.workouttracker.ui.screens.exercises.tabs.ExercisesTabContent
import androidx.compose.ui.graphics.RectangleShape
import com.rvilleda.workouttracker.model.MuscleGroup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisesScreen(
    onAddToWorkout: (Set<Exercise>) -> Unit,
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
    val chestExercises = allDbExercises.filter { it.muscleGroup == MuscleGroup.CHEST }
    val backExercises = allDbExercises.filter { it.muscleGroup == MuscleGroup.BACK }
    val legsExercises = allDbExercises.filter { it.muscleGroup == MuscleGroup.LEGS }
    val shouldersExercises = allDbExercises.filter { it.muscleGroup == MuscleGroup.SHOULDERS }
    val armsExercises = allDbExercises.filter { it.muscleGroup == MuscleGroup.ARMS }
    val coreExercises = allDbExercises.filter { it.muscleGroup == MuscleGroup.CORE }

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
                        onAddToWorkout(selectedExercises)
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
            if(searchQuery.isNotEmpty()) {
                ExercisesTabContent.Search(
                    allExercises = allDbExercises,
                    searchQuery = searchQuery,
                    selectedExercises = selectedExercises,
                    onToggleSelection = { viewModel.toggleSelection(it) },
                    selectionMode = selectionMode,
                    onToggleSelectionMode = { viewModel.toggleSelectionMode() },
                    onCreateCustomExercise = onCreateCustomExercise
                )
            } else {
                when (selectedTab) {
                    ExercisesTabs.CHEST -> ExercisesTabContent.Chest(
                        chestExercises = chestExercises,
                        selectedExercises = selectedExercises,
                        onToggleSelection = { viewModel.toggleSelection(it) },
                        selectionMode = selectionMode,
                        onToggleSelectionMode = { viewModel.toggleSelectionMode() }
                        )

                    ExercisesTabs.BACK -> ExercisesTabContent.Back(
                        backExercises = backExercises,
                        selectedExercises = selectedExercises,
                        onToggleSelection = { viewModel.toggleSelection(it) },
                        selectionMode = selectionMode,
                        onToggleSelectionMode = { viewModel.toggleSelectionMode() }
                    )


                    ExercisesTabs.LEGS -> ExercisesTabContent.Legs(
                        legsExercises = legsExercises,
                        selectedExercises = selectedExercises,
                        onToggleSelection = { viewModel.toggleSelection(it) },
                        selectionMode = selectionMode,
                        onToggleSelectionMode = { viewModel.toggleSelectionMode() }
                    )


                    ExercisesTabs.SHOULDERS -> ExercisesTabContent.Shoulders(
                        shouldersExercises = shouldersExercises,
                        selectedExercises = selectedExercises,
                        onToggleSelection = { viewModel.toggleSelection(it) },
                        selectionMode = selectionMode,
                        onToggleSelectionMode = { viewModel.toggleSelectionMode() }
                    )

                    ExercisesTabs.ARMS -> ExercisesTabContent.Arms(
                        armsExercises = armsExercises,
                        selectedExercises = selectedExercises,
                        onToggleSelection = { viewModel.toggleSelection(it) },
                        selectionMode = selectionMode,
                        onToggleSelectionMode = { viewModel.toggleSelectionMode() }
                    )

                    ExercisesTabs.CORE -> ExercisesTabContent.Core(
                        coreExercises = coreExercises,
                        selectedExercises = selectedExercises,
                        onToggleSelection = { viewModel.toggleSelection(it) },
                        selectionMode = selectionMode,
                        onToggleSelectionMode = { viewModel.toggleSelectionMode() }
                    )
                }
            }
        }
    }
}