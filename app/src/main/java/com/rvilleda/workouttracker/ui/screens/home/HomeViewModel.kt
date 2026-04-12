package com.rvilleda.workouttracker.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rvilleda.workouttracker.data.database.CompletedWorkoutEntity
import com.rvilleda.workouttracker.data.database.WorkoutDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(private val workoutDao: WorkoutDao) : ViewModel() {

    val savedWorkouts: StateFlow<List<CompletedWorkoutEntity>> =
        workoutDao.getAllWorkouts()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
}