package com.rvilleda.workouttracker.ui.screens.exercises

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.rvilleda.workouttracker.R
import com.rvilleda.workouttracker.ui.screens.exercises.components.ExercisesTabs
import com.rvilleda.workouttracker.ui.screens.exercises.components.TabRowHeader
import com.rvilleda.workouttracker.ui.screens.exercises.tabs.ExercisesTabContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisesScreen(
    onExerciseSelected: (String, String) -> Unit
) {
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topBarState)

    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(ExercisesTabs.CHEST) }

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
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            placeholder = { Text("Search exercises...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            singleLine = true,
                            shape = MaterialTheme.shapes.medium
                        )
                    },
                    navigationIcon = {
                        Image(
                            painter = painterResource(R.drawable.workout_tracker_logo),
                            contentDescription = "Logo"
                        )
                    },
                    scrollBehavior = scrollBehavior
                )

                TabRowHeader(
                    currentTab = selectedTab,
                    onTabSelected = { newTab -> selectedTab = newTab }
                )
            }
        }
    ) { padding ->

        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (selectedTab) {
                ExercisesTabs.CHEST -> ExercisesTabContent.Chest(searchQuery, onExerciseSelected)
                ExercisesTabs.BACK -> ExercisesTabContent.Back(searchQuery, onExerciseSelected)
                ExercisesTabs.LEGS -> ExercisesTabContent.Legs(searchQuery, onExerciseSelected)
                ExercisesTabs.SHOULDERS -> ExercisesTabContent.Shoulders(searchQuery, onExerciseSelected)
                ExercisesTabs.ARMS -> ExercisesTabContent.Arms(searchQuery, onExerciseSelected)
                ExercisesTabs.CORE -> ExercisesTabContent.Core(searchQuery, onExerciseSelected)
            }
        }
    }
}