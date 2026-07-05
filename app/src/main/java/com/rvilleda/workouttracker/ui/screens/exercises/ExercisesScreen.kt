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
import com.rvilleda.workouttracker.ui.screens.home.components.HomeTopTabs
import com.rvilleda.workouttracker.ui.screens.home.tabs.AICoach
import com.rvilleda.workouttracker.ui.screens.home.tabs.DashboardTab
import com.rvilleda.workouttracker.ui.screens.home.tabs.ProgressTab
import com.rvilleda.workouttracker.ui.screens.home.tabs.RoutinesTab
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
    var selectedTab by remember { mutableStateOf(ExercisesTopTabs.CHEST) }

    val pagerState = rememberPagerState(pageCount = { ExercisesTopTabs.entries.size })
    val coroutineScope = rememberCoroutineScope()

    val selectedExercises by viewModel.selectedExercises.collectAsState()
    val selectionMode by viewModel.selectionMode.collectAsState()
    val allDbExercises by viewModel.exercises.collectAsState()

    val displayedExercises : List<Exercise> = remember(searchQuery, pagerState.currentPage, allDbExercises) {
        if (searchQuery.isNotEmpty()) {
            allDbExercises.filter { exercise ->
                exercise.name.contains(searchQuery, ignoreCase = true) ||
                        exercise.muscleGroup.displayName.contains(searchQuery, ignoreCase = true) ||
                        exercise.equipment.displayName.contains(searchQuery, ignoreCase = true) ||
                        exercise.movementType.displayName.contains(searchQuery, ignoreCase = true)
            }
        } else {
            when (pagerState.currentPage) {
                0 -> allDbExercises.filter { it.muscleGroup == MuscleGroup.CHEST }
                1 -> allDbExercises.filter { it.muscleGroup == MuscleGroup.BACK }
                2 -> allDbExercises.filter { it.muscleGroup == MuscleGroup.LEGS }
                3 -> allDbExercises.filter { it.muscleGroup == MuscleGroup.SHOULDERS }
                4 -> allDbExercises.filter { it.muscleGroup == MuscleGroup.ARMS }
                5 -> allDbExercises.filter { it.muscleGroup == MuscleGroup.CORE }
                else -> emptyList()
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
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
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