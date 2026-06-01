package com.rvilleda.workouttracker.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Modifier
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
import com.rvilleda.workouttracker.ui.components.ActiveWorkoutBanner
import com.rvilleda.workouttracker.ui.screens.exercises.ExerciseViewModel
import com.rvilleda.workouttracker.ui.screens.history.HistoryScreen
import com.rvilleda.workouttracker.ui.screens.history.HistoryViewModel
import com.rvilleda.workouttracker.ui.screens.settings.SettingsScreen
import com.rvilleda.workouttracker.ui.screens.workoutdetails.WorkoutDetailsScreen
import com.rvilleda.workouttracker.ui.screens.workoutdetails.WorkoutDetailsViewModel
import com.rvilleda.workouttracker.ui.screens.settings.SettingsViewModel


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

    val settingsViewModel: SettingsViewModel = viewModel()
    val globalUnit by settingsViewModel.globalWeightUnit.collectAsState()

    NavHost(
        navController = navController,
        startDestination = "main_bottom_nav_flow",

        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },


        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {

        composable("main_bottom_nav_flow") {

            var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
            val timerText by sharedActiveWorkoutViewModel.elapsedTime.collectAsState()

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
                // 3. Wrap everything inside the scaffold in a Column
                Column(modifier = Modifier.fillMaxSize()) {

                    // 4. The Box takes up all available weight, pushing the banner to the bottom
                    Box(modifier = Modifier.weight(1f)) {
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
                                                sharedActiveWorkoutViewModel.addExerciseToSession(exercise.id, exercise.name, defaultUnit = globalUnit)
                                            }
                                            navController.navigate("active_workout_screen")
                                        } else {
                                            sharedActiveWorkoutViewModel.startNewEmptyWorkout()
                                            exercises.forEach { exercise ->
                                                sharedActiveWorkoutViewModel.addExerciseToSession(exercise.id, exercise.name, defaultUnit = globalUnit)
                                            }
                                            navController.navigate("active_workout_screen")
                                        }
                                    },
                                    onBack = { currentDestination = AppDestinations.HOME },
                                    viewModel = exerciseViewModel
                                )
                            }

                            AppDestinations.HISTORY -> {
                                val historyViewModel: HistoryViewModel = viewModel(
                                    factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                            return HistoryViewModel(workoutDao) as T
                                        }
                                    }
                                )

                                HistoryScreen(
                                    onNavigateToWorkoutDetails = { workoutId ->
                                        navController.navigate("workout_details_screen/$workoutId")
                                    },
                                    viewModel = historyViewModel
                                )
                            }

                            AppDestinations.SETTINGS -> {
                                SettingsScreen(
                                    currentUnit = globalUnit,
                                    onUnitChanged = { newUnit -> settingsViewModel.setGlobalWeightUnit(newUnit) },
                                    currentTheme = settingsViewModel.globalTheme.collectAsState().value,
                                    onThemeChanged = { newTheme -> settingsViewModel.setGlobalTheme(newTheme) },
                                    onBack = { currentDestination = AppDestinations.HOME }
                                )
                            }
                        }
                    }

                    if (isWorkoutActive) {
                        ActiveWorkoutBanner(
                            timerText = timerText,
                            onClick = { navController.navigate("active_workout_screen") }
                        )
                    }
                }
            }
        }
        composable(
            route = "active_workout_screen",
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { fullHeight -> (fullHeight * 0.8f).toInt() },
                    animationSpec = tween(durationMillis = 300)
                ) + fadeIn(animationSpec = tween(durationMillis = 300))
            },
            popExitTransition = {
                slideOutVertically(
                    targetOffsetY = { fullHeight -> (fullHeight * 0.8f).toInt() },
                    animationSpec = tween(durationMillis = 300)
                ) + fadeOut(animationSpec = tween(durationMillis = 300))
            }
        ) {

            ActiveWorkoutScreen(
                globalUnit = globalUnit,
                viewModel = sharedActiveWorkoutViewModel,
                onNavigateToExerciseSelection = {
                    navController.navigate("add_exercise_to_workout")
                },
                onFinishWorkout = { workoutName ->
                    sharedActiveWorkoutViewModel.finishAndClearWorkout(
                        workoutName = workoutName,
                        onSuccess = { navController.popBackStack("main_bottom_nav_flow", inclusive = false) }
                    )
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
                        sharedActiveWorkoutViewModel.addExerciseToSession(exercise.id, exercise.name, defaultUnit = globalUnit)
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
                globalUnit = globalUnit,
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