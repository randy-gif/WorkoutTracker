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
import androidx.compose.ui.unit.dp
import com.rvilleda.workouttracker.model.Exercise
import com.rvilleda.workouttracker.ui.screens.exercises.components.ExercisesTopTabs
import com.rvilleda.workouttracker.ui.screens.exercises.components.TabRowHeader
import androidx.compose.ui.graphics.RectangleShape
import com.rvilleda.workouttracker.model.MuscleGroup
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.Alignment
import com.rvilleda.workouttracker.ui.components.CreateExerciseCard
import com.rvilleda.workouttracker.ui.components.ExerciseCard
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import kotlinx.coroutines.launch

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

    val pagerState = rememberPagerState(pageCount = { ExercisesTopTabs.entries.size })
    val coroutineScope = rememberCoroutineScope()

    val selectedExercises by viewModel.selectedExercises.collectAsState()
    val selectionMode by viewModel.selectionMode.collectAsState()
    val allDbExercises by viewModel.exercises.collectAsState()

    val searchFilteredExercises = remember(searchQuery, allDbExercises) {
        if (searchQuery.isNotEmpty()) {
            allDbExercises.filter { exercise ->
                exercise.name.contains(searchQuery, ignoreCase = true) ||
                        exercise.muscleGroup.displayName.contains(searchQuery, ignoreCase = true) ||
                        exercise.equipment.displayName.contains(searchQuery, ignoreCase = true) ||
                        exercise.movementType.displayName.contains(searchQuery, ignoreCase = true)
            }
        } else {
            allDbExercises // If no search, pass the whole DB down to the Pager
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 16.dp),
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
                // 2. Hide tabs when searching, as search results span all muscle groups
                if (searchQuery.isEmpty()) {
                    TabRowHeader(
                        selectedTabIndex = pagerState.currentPage,
                        onTabSelected = { index ->
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                    )
                }
            }
        },
        bottomBar = {
            if (selectedExercises.isNotEmpty() && selectionMode) {
                Button(
                    onClick = {
                        if (isWorkoutActive) onAddToWorkout(selectedExercises)
                        else onCreateWorkout(selectedExercises)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RectangleShape,
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    Text("Add ${selectedExercises.size} Exercises to Workout")
                }
            }
        }
    ) { padding ->
        if (searchQuery.isNotEmpty()) {
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                ExerciseList(
                    exercises = searchFilteredExercises,
                    isWorkoutActive = isWorkoutActive,
                    selectedExercises = selectedExercises,
                    selectionMode = selectionMode,
                    onToggleSelection = viewModel::toggleSelection,
                    onToggleSelectionMode = viewModel::toggleSelectionMode,
                    onCreateCustomExercise = onCreateCustomExercise,
                    onAddToWorkout = onAddToWorkout
                )
            }
        } else {
            // 4. If NOT searching, use the Pager and filter dynamically per page
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) { page ->
                val pageSpecificExercises = remember(allDbExercises, page) {
                    when (page) {
                        0 -> allDbExercises.filter { it.muscleGroup == MuscleGroup.CHEST }
                        1 -> allDbExercises.filter { it.muscleGroup == MuscleGroup.BACK }
                        2 -> allDbExercises.filter { it.muscleGroup == MuscleGroup.LEGS }
                        3 -> allDbExercises.filter { it.muscleGroup == MuscleGroup.SHOULDERS }
                        4 -> allDbExercises.filter { it.muscleGroup == MuscleGroup.ARMS }
                        5 -> allDbExercises.filter { it.muscleGroup == MuscleGroup.CORE }
                        else -> emptyList()
                    }
                }

                ExerciseList(
                    exercises = pageSpecificExercises,
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