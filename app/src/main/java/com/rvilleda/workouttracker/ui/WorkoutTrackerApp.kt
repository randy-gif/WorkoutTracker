package com.rvilleda.workouttracker.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rvilleda.workouttracker.ui.navigation.AppDestinations
import com.rvilleda.workouttracker.ui.navigation.AppRoutes
import com.rvilleda.workouttracker.ui.screens.activeworkout.ActiveWorkoutScreen
import com.rvilleda.workouttracker.ui.screens.data.ExercisesDataScreen
import com.rvilleda.workouttracker.ui.screens.exercises.ExercisesScreen
import com.rvilleda.workouttracker.ui.screens.home.HomeScreen
@Preview(showBackground = true)
@Composable
fun WorkoutTrackerApp() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "main_bottom_nav_flow"
    ) {

        composable("main_bottom_nav_flow") {

            // Your state management for the bottom bar stays safely inside here
            var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

            NavigationSuiteScaffold(
                navigationSuiteItems = {
                    AppDestinations.entries.forEach {
                        item(
                            icon = { Icon(painterResource(it.icon), contentDescription = it.label) },
                            label = { Text(it.label) },
                            selected = it == currentDestination,
                            onClick = { currentDestination = it }
                        )
                    }
                }
            ) {
                // Switching between the main rooms
                when (currentDestination) {
                    AppDestinations.HOME -> HomeScreen()

                    AppDestinations.EXERCISES -> ExercisesScreen(
                        // The Waiter passes the order to the Building Manager!
                        onExerciseSelected = { exerciseId, exerciseName ->
                            navController.navigate(
                                AppRoutes.createActiveWorkoutRoute(exerciseId, exerciseName)
                            )
                        }
                    )

                    AppDestinations.EXERCISES_DATA -> ExercisesDataScreen()
                }
            }
        }

        composable(
            route = AppRoutes.ACTIVE_WORKOUT,
            arguments = listOf(
                navArgument("exerciseId") { type = NavType.StringType },
                navArgument("exerciseName") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val exerciseId = backStackEntry.arguments?.getString("exerciseId") ?: ""
            val exerciseName = backStackEntry.arguments?.getString("exerciseName") ?: ""

            ActiveWorkoutScreen(
                exerciseId = exerciseId,
                exerciseName = exerciseName,
                onNavigateToExerciseSelection = {
                    navController.popBackStack()
                },
                onFinishWorkout = {
                    navController.popBackStack("main_bottom_nav_flow", inclusive = false)
                }
            )
        }
    }
}