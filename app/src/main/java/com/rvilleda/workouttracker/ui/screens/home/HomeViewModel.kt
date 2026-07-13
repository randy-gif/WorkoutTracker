package com.rvilleda.workouttracker.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rvilleda.workouttracker.data.database.dao.RoutineDao
import com.rvilleda.workouttracker.data.database.entity.workout.CompletedWorkoutEntity
import com.rvilleda.workouttracker.data.database.dao.WorkoutDao
import com.rvilleda.workouttracker.data.database.routine.FullRoutine
import com.rvilleda.workouttracker.data.database.routine.RoutineEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(private val workoutDao: WorkoutDao, private val routineDao: RoutineDao) : ViewModel() {

   // Workout
    val savedWorkouts: StateFlow<List<CompletedWorkoutEntity>> = workoutDao.getWorkoutSummaries()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )


    fun deleteWorkout(workoutId: String) {
        viewModelScope.launch {
            workoutDao.deleteWorkout(workoutId)
        }
    }

    // Routine


    val savedRoutines: StateFlow<List<FullRoutine>> = routineDao.getAllFullRoutines()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


    fun deleteRoutine(routineId: String) {
        viewModelScope.launch {
            routineDao.deleteRoutine(routineId)
        }
    }


}