package com.rvilleda.workouttracker.ui.screens.workoutdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rvilleda.workouttracker.data.database.WorkoutDao
import kotlinx.coroutines.launch

class WorkoutDetailsViewModel(private val workoutDao: WorkoutDao) : ViewModel() {

    fun deleteWorkout(workoutId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            workoutDao.deleteWorkout(workoutId)
            onSuccess()
        }
    }
}