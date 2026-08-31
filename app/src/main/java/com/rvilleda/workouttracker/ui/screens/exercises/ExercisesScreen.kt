package com.rvilleda.workouttracker.ui.screens.exercises

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
import androidx.compose.material.icons.filled.Add
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisesScreen(
    onConfirmSelection: (Set<Exercise>) -> Unit,
    onCreateCustomExercise: () -> Unit,
    onBack: () -> Unit,
    viewModel: ExerciseViewModel,
    allowSelection: Boolean = true
) {
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topBarState)

    var searchQuery by remember { mutableStateOf("") }

    val pagerState = rememberPagerState(pageCount = { MuscleGroup.entries.size })
    val coroutineScope = rememberCoroutineScope()

    val selectedExercises by viewModel.selectedExercises.collectAsState()
    val selectionMode by viewModel.selectionMode.collectAsState()
    val allDbExercises by viewModel.exercises.collectAsState()

    val categorizedExercisesList: List<List<Exercise>?> = remember(allDbExercises) {
        MuscleGroup.entries.map { group ->
            allDbExercises?.filter { exercise -> exercise.primaryMuscle.group == group }
        }
    }

    val searchFilteredExercises = remember(searchQuery, allDbExercises) {
        if (searchQuery.isNotEmpty()) {
            allDbExercises?.filter { exercise ->
                exercise.name.contains(searchQuery, ignoreCase = true) ||
                        exercise.primaryMuscle.displayName.contains(searchQuery, ignoreCase = true) ||
                        exercise.secondaryMuscles.any { it.displayName.contains(searchQuery, ignoreCase = true) } ||
                        exercise.equipment.displayName.contains(searchQuery, ignoreCase = true) ||
                        exercise.movementPattern.name.contains(searchQuery, ignoreCase = true) ||
                        exercise.fatigueTier.name.contains(searchQuery, ignoreCase = true)
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

    ) { padding ->
        if (searchQuery.isNotEmpty() && searchFilteredExercises != null) {
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                ExerciseList(
                    exercises = searchFilteredExercises,
                    selectedExercises = selectedExercises,
                    selectionMode = selectionMode,
                    onToggleSelection = viewModel::toggleSelection,
                    onToggleSelectionMode = viewModel::toggleSelectionMode,
                    onCreateCustomExercise = onCreateCustomExercise,
                    onConfirmSelection = onConfirmSelection,
                    allowSelection = allowSelection
                )
            }
        } else {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) { page ->
                ExerciseList(
                    exercises = categorizedExercisesList[page],
                    selectedExercises = selectedExercises,
                    selectionMode = selectionMode,
                    onToggleSelection = viewModel::toggleSelection,
                    onToggleSelectionMode = viewModel::toggleSelectionMode,
                    onCreateCustomExercise = onCreateCustomExercise,
                    onConfirmSelection = onConfirmSelection,
                    allowSelection = allowSelection
                )
            }
        }
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (selectedExercises.isNotEmpty() && selectionMode && allowSelection) {
                ExtendedFloatingActionButton(
                    onClick = {onConfirmSelection(selectedExercises)},
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    icon = { Icon(Icons.Filled.Add, contentDescription = "Add Exercises") },
                    text = { Text("Add ${selectedExercises.size} Exercises to Workout") },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}
@Composable
fun ExerciseList(
    exercises: List<Exercise>?,
    selectedExercises: Set<Exercise>,
    selectionMode: Boolean,
    onToggleSelection: (Exercise) -> Unit,
    onToggleSelectionMode: () -> Unit,
    onConfirmSelection: (Set<Exercise>) -> Unit,
    onCreateCustomExercise: (() -> Unit)? = null,
    allowSelection: Boolean
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (exercises == null) {
            item {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            }
        } else if(exercises.isEmpty()) {
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
                        if (!allowSelection){
                            onConfirmSelection(setOf(exercise))
                        } else {
                            if (selectionMode) {
                                onToggleSelection(exercise)
                            } else {
                                onConfirmSelection(setOf(exercise))
                            }
                        }

                    }
                )
            }
        }
    }
}