package com.rvilleda.workouttracker.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rvilleda.workouttracker.data.database.WorkoutDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class HistoryViewModel(private val workoutDao: WorkoutDao) : ViewModel() {

    // 1. Grab ALL workouts from the database
    private val allWorkouts = workoutDao.getAllWorkouts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 2. Transform them into a Set of unique Dates for the Calendar
    val workoutDates: StateFlow<Set<LocalDate>> = allWorkouts.map { workouts ->
        workouts.map { entity ->
            Instant.ofEpochMilli(entity.dateCompleted)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }.toSet()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    // (Future Feature) You can eventually add logic here to fetch specific workouts
    // when a user taps a date on the calendar!
}