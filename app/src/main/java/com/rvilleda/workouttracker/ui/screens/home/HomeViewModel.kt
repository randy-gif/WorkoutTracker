package com.rvilleda.workouttracker.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rvilleda.workouttracker.data.database.entity.workout.CompletedWorkoutEntity
import com.rvilleda.workouttracker.data.database.dao.WorkoutDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(private val workoutDao: WorkoutDao) : ViewModel() {

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

}