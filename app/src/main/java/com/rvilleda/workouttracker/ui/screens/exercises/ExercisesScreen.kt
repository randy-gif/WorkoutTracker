package com.rvilleda.workouttracker.ui.screens.exercises

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rvilleda.workouttracker.R
import com.rvilleda.workouttracker.ui.screens.exercises.components.ExercisesTabs
import com.rvilleda.workouttracker.ui.screens.exercises.components.TabRowHeader
import com.rvilleda.workouttracker.ui.screens.exercises.tabs.ExercisesTabContent


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisesScreen() {
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topBarState)

    var searchQuery by remember { mutableStateOf("") }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: ExercisesTabs.CHEST.route


    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .padding(top = 8.dp),
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            placeholder = { Text("Search exercises or muscles...") },
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
                TabRowHeader(navController = navController, currentRoute = currentRoute)
            }
        }, content = { padding ->
            NavHost(
                navController = navController,
                startDestination = ExercisesTabs.CHEST.route,
                modifier = Modifier.padding(padding)
            ) {
                composable(ExercisesTabs.CHEST.route) {
                    ExercisesTabContent.Chest()
                }
                composable(ExercisesTabs.BACK.route) {
                    ExercisesTabContent.Back()
                }
                composable(ExercisesTabs.LEGS.route) {
                    ExercisesTabContent.Legs()
                }
                composable(ExercisesTabs.SHOULDERS.route) {
                    ExercisesTabContent.Shoulders()
                }
                composable(ExercisesTabs.ARMS.route) {
                    ExercisesTabContent.Arms()
                }
                composable(ExercisesTabs.CORE.route) {
                    ExercisesTabContent.Core()
                }

            }
        }
    )
}

