package com.rvilleda.workouttracker.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rvilleda.workouttracker.data.database.WorkoutDao
import com.rvilleda.workouttracker.ui.navigation.AppDestinations
import com.rvilleda.workouttracker.ui.screens.activeworkout.ActiveWorkoutScreen
import com.rvilleda.workouttracker.ui.screens.activeworkout.ActiveWorkoutViewModel
import com.rvilleda.workouttracker.ui.screens.data.ExercisesDataScreen
import com.rvilleda.workouttracker.ui.screens.exercises.ExercisesScreen
import com.rvilleda.workouttracker.ui.screens.home.HomeScreen
import com.rvilleda.workouttracker.ui.screens.home.HomeViewModel
import com.rvilleda.workouttracker.R
import com.rvilleda.workouttracker.model.Exercise
import com.rvilleda.workouttracker.ui.screens.exercises.ExerciseViewModel
import com.rvilleda.workouttracker.ui.screens.workoutdetails.WorkoutDetailsScreen
import com.rvilleda.workouttracker.ui.screens.workoutdetails.WorkoutDetailsViewModel

@Composable
fun WorkoutTrackerApp(workoutDao: WorkoutDao) {

    val navController = rememberNavController()

    val sharedActiveWorkoutViewModel: ActiveWorkoutViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ActiveWorkoutViewModel(workoutDao) as T
            }
        }
    )
    val isWorkoutActive by sharedActiveWorkoutViewModel.isWorkoutActive.collectAsState()

    NavHost(
        navController = navController,
        startDestination = "main_bottom_nav_flow"
    ) {

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
                    if (isWorkoutActive) {
                        item(
                            icon = { Icon(painterResource(R.drawable.ic_timer), contentDescription = "Active") },
                            label = { Text("Active") },
                            selected = false,
                            onClick = {
                                navController.navigate("active_workout_screen")
                            }
                        )
                    }
                }
            ) {
                when (currentDestination) {
                    AppDestinations.HOME -> {
                        val homeViewModel: HomeViewModel = viewModel(
                            factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                    return HomeViewModel(workoutDao) as T
                                }
                            }
                        )

                        HomeScreen(
                            viewModel = homeViewModel,
                            onPastWorkoutClick = { workoutId ->
                                navController.navigate("workout_details_screen/$workoutId")
                            }
                        )
                    }

                    AppDestinations.EXERCISES -> {
                        val exerciseViewModel : ExerciseViewModel =  viewModel()

                        LaunchedEffect(Unit) {
                            exerciseViewModel.clearSelection()
                        }

                        ExercisesScreen(
                            onAddToWorkout = { exercises ->
                                if (isWorkoutActive) {
                                    exercises.forEach { exercise ->
                                        sharedActiveWorkoutViewModel.addExerciseToSession(exercise.id, exercise.name)
                                    }
                                    navController.navigate("active_workout_screen")
                                } else {
                                    sharedActiveWorkoutViewModel.startNewEmptyWorkout()
                                    exercises.forEach { exercise ->
                                        sharedActiveWorkoutViewModel.addExerciseToSession(exercise.id, exercise.name)
                                    }
                                    navController.navigate("active_workout_screen")
                                }
                            },
                            onBack = { currentDestination = AppDestinations.HOME },
                            viewModel = exerciseViewModel
                        )
                    }

                    AppDestinations.EXERCISES_DATA -> ExercisesDataScreen()
                }
            }
        }

        composable("active_workout_screen") {

            ActiveWorkoutScreen(
                viewModel = sharedActiveWorkoutViewModel,
                onNavigateToExerciseSelection = {
                    navController.navigate("add_exercise_to_workout")
                },
                onFinishWorkout = {
                    sharedActiveWorkoutViewModel.finishAndClearWorkout({navController.popBackStack("main_bottom_nav_flow", inclusive = false)})
                },
                onDiscardWorkout = {
                    navController.popBackStack("main_bottom_nav_flow", inclusive = false)
                    sharedActiveWorkoutViewModel.discardWorkout()
                },
                onBack = {
                    navController.popBackStack("main_bottom_nav_flow", inclusive = false)
                }
            )
        }

        composable("add_exercise_to_workout") {

            val exerciseViewModel : ExerciseViewModel =  viewModel()
            LaunchedEffect(Unit) {
                exerciseViewModel.clearSelection()
            }
            ExercisesScreen(
                onAddToWorkout = { exercises ->
                    exercises.forEach { exercise ->
                        sharedActiveWorkoutViewModel.addExerciseToSession(exercise.id, exercise.name)
                    }
                    navController.popBackStack()
                },
                onBack = { navController.popBackStack() },
                viewModel = exerciseViewModel
            )
        }

        composable("workout_details_screen/{workoutId}") { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getString("workoutId") ?: return@composable

            val detailsViewModel: WorkoutDetailsViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return WorkoutDetailsViewModel(workoutDao) as T
                    }
                }
            )

            WorkoutDetailsScreen(
                workoutId = workoutId,
                workoutDao = workoutDao,
                onBack = { navController.popBackStack() },
                onWorkoutAgain = { pastExercises ->
                    sharedActiveWorkoutViewModel.startWorkoutAgain(pastExercises)
                    navController.navigate("active_workout_screen")
                },
                onDeleteWorkout = {
                    detailsViewModel.deleteWorkout(
                        workoutId = workoutId,
                        onSuccess = { navController.popBackStack() }
                    )
                }
            )
        }
    }
}