package com.rvilleda.workouttracker.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rvilleda.workouttracker.data.database.WorkoutDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
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
    // 3. Keep track of the currently selected date (Default to today)
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate = _selectedDate.asStateFlow()

    // 4. Combine the selected date with the full list to get just the ones we want to see
    val dailyWorkouts = combine(allWorkouts, _selectedDate) { workouts, date ->
        workouts.filter { entity ->
            val entityDate = Instant.ofEpochMilli(entity.dateCompleted)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()

            entityDate == date // Only keep workouts that match the selected date!
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 5. Function for the UI to call when a user taps a day
    fun updateSelectedDate(date: LocalDate) {
        _selectedDate.value = date
    }
}