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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisesScreen(
    onAddToWorkout: (Set<Exercise>) -> Unit,
    onBack: () -> Unit,
    viewModel: ExerciseViewModel
) {
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topBarState)

    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(ExercisesTabs.CHEST) }

    val selectedExercises by viewModel.selectedExercises.collectAsState()
    val selectionMode by viewModel.selectionMode.collectAsState()


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
            if(selectedExercises.isNotEmpty() && selectionMode) {
                Button(
                    onClick = {
                        onAddToWorkout(selectedExercises)
                    },
                    modifier = Modifier.fillMaxWidth()
                ){
                    Text("Add ${selectedExercises.size} Exercises to Workout")
                }
            }
        }
    ) { padding ->

        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if(searchQuery.isNotEmpty()) {
                ExercisesTabContent.Search(
                    searchQuery = searchQuery,
                    selectedExercises = selectedExercises,
                    onToggleSelection = { viewModel.toggleSelection(it) },
                    selectionMode = selectionMode,
                    onToggleSelectionMode = { viewModel.toggleSelectionMode() }
                )
            } else {
                when (selectedTab) {
                    // 3. Pass the state and the action into every tab
                    ExercisesTabs.CHEST -> ExercisesTabContent.Chest(
                        selectedExercises = selectedExercises,
                        onToggleSelection = { viewModel.toggleSelection(it) },
                        selectionMode = selectionMode,
                        onToggleSelectionMode = { viewModel.toggleSelectionMode() }
                        )

                    ExercisesTabs.BACK -> ExercisesTabContent.Back(
                        selectedExercises = selectedExercises,
                        onToggleSelection = { viewModel.toggleSelection(it) },
                        selectionMode = selectionMode,
                        onToggleSelectionMode = { viewModel.toggleSelectionMode() }
                    )


                    ExercisesTabs.LEGS -> ExercisesTabContent.Legs(
                        selectedExercises = selectedExercises,
                        onToggleSelection = { viewModel.toggleSelection(it) },
                        selectionMode = selectionMode,
                        onToggleSelectionMode = { viewModel.toggleSelectionMode() }
                    )


                    ExercisesTabs.SHOULDERS -> ExercisesTabContent.Shoulders(
                        selectedExercises = selectedExercises,
                        onToggleSelection = { viewModel.toggleSelection(it) },
                        selectionMode = selectionMode,
                        onToggleSelectionMode = { viewModel.toggleSelectionMode() }
                    )

                    ExercisesTabs.ARMS -> ExercisesTabContent.Arms(
                        selectedExercises = selectedExercises,
                        onToggleSelection = { viewModel.toggleSelection(it) },
                        selectionMode = selectionMode,
                        onToggleSelectionMode = { viewModel.toggleSelectionMode() }
                    )

                    ExercisesTabs.CORE -> ExercisesTabContent.Core(
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