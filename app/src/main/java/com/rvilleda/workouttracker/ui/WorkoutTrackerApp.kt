package com.rvilleda.workouttracker.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.rvilleda.workouttracker.data.database.WorkoutDao
import com.rvilleda.workouttracker.ui.navigation.AppDestinations
import com.rvilleda.workouttracker.ui.navigation.AppRoutes
import com.rvilleda.workouttracker.ui.screens.activeworkout.ActiveWorkoutScreen
import com.rvilleda.workouttracker.ui.screens.activeworkout.ActiveWorkoutViewModel
import com.rvilleda.workouttracker.ui.screens.data.ExercisesDataScreen
import com.rvilleda.workouttracker.ui.screens.exercises.ExercisesScreen
import com.rvilleda.workouttracker.ui.screens.home.HomeScreen
import com.rvilleda.workouttracker.ui.screens.home.HomeViewModel

@Composable
fun WorkoutTrackerApp(workoutDao: WorkoutDao) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "main_bottom_nav_flow"
    ) {

        // ====================================================
        // FOLDER 1: THE MAIN APP (Bottom Bar)
        // ====================================================
        composable("main_bottom_nav_flow") {

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
                when (currentDestination) {
                    AppDestinations.HOME -> {
                        // 1. Build the HomeViewModel using a factory to pass the DAO
                        val homeViewModel: HomeViewModel = viewModel(
                            factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                    return HomeViewModel(workoutDao) as T
                                }
                            }
                        )

                        HomeScreen(viewModel = homeViewModel)
                    }

                    AppDestinations.EXERCISES -> ExercisesScreen(
                        onExerciseSelected = { exerciseId, exerciseName ->
                            // This starts a BRAND NEW workout
                            navController.navigate(
                                AppRoutes.createActiveWorkoutRoute(exerciseId, exerciseName)
                            )
                        }
                    )

                    AppDestinations.EXERCISES_DATA -> ExercisesDataScreen()
                }
            }
        }

        // ====================================================
        // FOLDER 2: THE ACTIVE WORKOUT (Nested Graph)
        // ====================================================
        navigation(
            startDestination = AppRoutes.ACTIVE_WORKOUT,
            route = "workout_session_folder" // The name of our "Conference Room"
        ) {

            // SCREEN A: The actual workout screen
            composable(
                route = AppRoutes.ACTIVE_WORKOUT,
                arguments = listOf(
                    navArgument("exerciseId") { type = NavType.StringType },
                    navArgument("exerciseName") { type = NavType.StringType }
                )
            ) { backStackEntry ->

                // 1. Get the shared ViewModel tied to this folder
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("workout_session_folder")
                }
                val sharedViewModel: ActiveWorkoutViewModel = viewModel(
                    parentEntry,
                    factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return ActiveWorkoutViewModel(workoutDao) as T
                        }
                    }
                )

                val exerciseId = backStackEntry.arguments?.getString("exerciseId") ?: ""
                val exerciseName = backStackEntry.arguments?.getString("exerciseName") ?: ""

                ActiveWorkoutScreen(
                    exerciseId = exerciseId,
                    exerciseName = exerciseName,
                    viewModel = sharedViewModel, // Pass it to the screen
                    onNavigateToExerciseSelection = {
                        // Navigate to Screen B (inside this folder)
                        navController.navigate("add_exercise_to_workout")
                    },
                    onFinishWorkout = {
                        // Destroy the folder and go back to the main app
                        sharedViewModel.saveWorkout()
                        navController.popBackStack("main_bottom_nav_flow", inclusive = false)
                    }
                )
            }

            // SCREEN B: The "Add Another Exercise" screen
            composable("add_exercise_to_workout") { backStackEntry ->

                // 2. Grab the EXACT same ViewModel from the folder
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("workout_session_folder")
                }
                val sharedViewModel: ActiveWorkoutViewModel = viewModel(
                    parentEntry,
                    factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return ActiveWorkoutViewModel(workoutDao) as T
                        }
                    }
                )

                // 3. Reuse your ExercisesScreen, but change what the click does!
                ExercisesScreen(
                    onExerciseSelected = { newExerciseId, newExerciseName ->
                        // Add the exercise to the existing ViewModel
                        sharedViewModel.addExerciseToSession(newExerciseId, newExerciseName)

                        // Go back to the workout screen
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}