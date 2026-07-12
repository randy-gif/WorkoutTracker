package com.rvilleda.workouttracker.ui

import CreateCustomExerciseScreen
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rvilleda.workouttracker.data.database.dao.WorkoutDao
import com.rvilleda.workouttracker.ui.navigation.AppDestinations
import com.rvilleda.workouttracker.ui.screens.activeworkout.ActiveWorkoutScreen
import com.rvilleda.workouttracker.ui.screens.activeworkout.ActiveWorkoutViewModel
import com.rvilleda.workouttracker.ui.screens.exercises.ExercisesScreen
import com.rvilleda.workouttracker.ui.screens.home.HomeScreen
import com.rvilleda.workouttracker.ui.screens.home.HomeViewModel
import com.rvilleda.workouttracker.data.database.dao.ExerciseDao
import com.rvilleda.workouttracker.data.database.dao.RoutineDao
import com.rvilleda.workouttracker.ui.components.ActiveWorkoutBanner
import com.rvilleda.workouttracker.ui.screens.createroutine.CreateRoutineScreen
import com.rvilleda.workouttracker.ui.screens.createroutine.CreateRoutineViewModel
import com.rvilleda.workouttracker.ui.screens.exercises.CreateCustomExerciseViewModel
import com.rvilleda.workouttracker.ui.screens.exercises.ExerciseViewModel
import com.rvilleda.workouttracker.ui.screens.history.HistoryScreen
import com.rvilleda.workouttracker.ui.screens.history.HistoryViewModel
import com.rvilleda.workouttracker.ui.screens.settings.SettingsScreen
import com.rvilleda.workouttracker.ui.screens.workoutdetails.WorkoutDetailsScreen
import com.rvilleda.workouttracker.ui.screens.workoutdetails.WorkoutDetailsViewModel
import com.rvilleda.workouttracker.ui.screens.settings.SettingsViewModel

@Composable
fun WorkoutTrackerApp(workoutDao: WorkoutDao, exerciseDao: ExerciseDao, routineDao: RoutineDao) {

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
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(1f)) {
                        when (currentDestination) {
                            AppDestinations.HOME -> {
                                val homeViewModel: HomeViewModel = viewModel(
                                    factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                            // Pass BOTH DAOs now
                                            return HomeViewModel(workoutDao, routineDao) as T
                                        }
                                    }
                                )

                                HomeScreen(
                                    viewModel = homeViewModel,
                                    onCreateRoutineClick = { navController.navigate("create_routine_screen")},
                                    onPastWorkoutClick = { workoutId ->
                                        navController.navigate("workout_details_screen/$workoutId")
                                    }
                                )
                            }

                            AppDestinations.EXERCISES -> {
                                val exerciseViewModel : ExerciseViewModel =  viewModel(
                                    factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                            return ExerciseViewModel(exerciseDao) as T
                                        }
                                    }
                                )

                                LaunchedEffect(Unit) {
                                    exerciseViewModel.clearSelection()
                                }

                                ExercisesScreen(
                                    onConfirmSelection = { exercises ->
                                        if (!isWorkoutActive) {
                                            sharedActiveWorkoutViewModel.startNewEmptyWorkout()
                                        }
                                        exercises.forEach { exercise ->
                                            sharedActiveWorkoutViewModel.addExerciseToSession(
                                                baseExerciseId = exercise.id,
                                                exerciseName = exercise.name,
                                                defaultUnit = globalUnit
                                            )
                                        }
                                        navController.navigate("active_workout_screen") {
                                            popUpTo(AppDestinations.EXERCISES.name) { inclusive = true }
                                        }
                                    },
                                    onCreateCustomExercise = { navController.navigate("create_custom_exercise") },
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
                    // Start 100% off-screen at the very bottom
                    initialOffsetY = { fullHeight -> fullHeight },
                    // LinearOutSlowIn gives it that premium "starts fast, settles gently" Apple-like feel
                    animationSpec = tween(durationMillis = 400, easing = LinearOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(durationMillis = 400))
            },
            popExitTransition = {
                slideOutVertically(
                    // Slide 100% off-screen back to the bottom
                    targetOffsetY = { fullHeight -> fullHeight },
                    // FastOutLinearIn makes it accelerate as it drops away
                    animationSpec = tween(durationMillis = 350, easing = FastOutLinearInEasing)
                ) + fadeOut(animationSpec = tween(durationMillis = 350))
            },
            // NEW: What happens to THIS screen when the user clicks "Add Exercise"?
            exitTransition = {
                // Keeps the workout screen perfectly still in the background while the selector slides over it
                fadeOut(animationSpec = tween(durationMillis = 300))
            },
            // NEW: What happens when they come BACK from the "Add Exercise" screen?
            popEnterTransition = {
                fadeIn(animationSpec = tween(durationMillis = 300))
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
                    sharedActiveWorkoutViewModel.discardWorkout(
                        onSuccess = { navController.popBackStack("main_bottom_nav_flow", inclusive = false) }
                    )
                },
                onBack = {
                    navController.popBackStack("main_bottom_nav_flow", inclusive = false)
                }
            )
        }
        composable(route = "create_routine_screen") {
            val viewModel: CreateRoutineViewModel = viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return CreateRoutineViewModel(routineDao) as T
                    }
                }
            )
            CreateRoutineScreen(
                globalUnit = globalUnit,
                onNavigateToExerciseSelection = { navController.navigate("add_exercise_to_routine") },
                onSaveRoutine = { routineName ->
                    viewModel.saveRoutine(routineName) {
                        navController.popBackStack()
                    }
                },
                onBack = { navController.popBackStack() },
                viewModel
            )


        }
        composable("add_exercise_to_routine") {

            val exerciseViewModel : ExerciseViewModel =  viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return ExerciseViewModel(exerciseDao) as T
                    }
                }
            )
            val parentEntry = remember(it) {
                navController.getBackStackEntry("create_routine_screen")
            }

            val sharedRoutineViewModel: CreateRoutineViewModel = viewModel(
                viewModelStoreOwner = parentEntry, // <-- THIS IS THE MAGIC KEY
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return CreateRoutineViewModel(routineDao) as T
                    }
                }
            )
            LaunchedEffect(Unit) {
                exerciseViewModel.clearSelection()
            }
            ExercisesScreen(
                onConfirmSelection = { exercises ->
                    exercises.forEach { exercise ->  sharedRoutineViewModel.addExerciseToSession(exercise.id, exercise.name, globalUnit) }
                    navController.popBackStack()
                },
                onCreateCustomExercise = { navController.navigate("create_custom_exercise") },
                onBack = { navController.popBackStack() },
                viewModel = exerciseViewModel
            )
        }
        composable("create_custom_exercise") {
            class CreateCustomExerciseViewModelFactory(
                private val exerciseDao: ExerciseDao
            ) : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(CreateCustomExerciseViewModel::class.java)) {
                        @Suppress("UNCHECKED_CAST")
                        return CreateCustomExerciseViewModel(exerciseDao) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
            val viewModel: CreateCustomExerciseViewModel = viewModel(
                factory = CreateCustomExerciseViewModelFactory(exerciseDao)
            )
            CreateCustomExerciseScreen(
                onNavigateBack = { navController.popBackStack() },
                viewModel = viewModel
            )
        }

        composable("add_exercise_to_workout") {

            val exerciseViewModel : ExerciseViewModel =  viewModel(
                factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return ExerciseViewModel(exerciseDao) as T
                    }
                }
            )
            LaunchedEffect(Unit) {
                exerciseViewModel.clearSelection()
            }
            ExercisesScreen(
                onConfirmSelection = { exercises ->
                    exercises.forEach { exercise ->  sharedActiveWorkoutViewModel.addExerciseToSession(exercise.id, exercise.name, globalUnit) }
                    navController.popBackStack()
                },
                onCreateCustomExercise = { navController.navigate("create_custom_exercise") },
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