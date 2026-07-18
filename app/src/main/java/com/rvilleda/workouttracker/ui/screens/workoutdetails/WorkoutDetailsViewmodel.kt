package com.rvilleda.workouttracker.ui.screens.workoutdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rvilleda.workouttracker.data.database.dao.WorkoutDao
import com.rvilleda.workouttracker.model.WeightUnit
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WorkoutDetailsViewModel(private val workoutDao: WorkoutDao) : ViewModel() {

    private val currentUnit = MutableStateFlow(WeightUnit.LBS)

    private val currentWorkoutId = MutableStateFlow<String?>(null)

    fun updateUnitPreference(unit: WeightUnit) {
        currentUnit.value = unit
    }

    fun loadWorkoutId(workoutId: String) {
        currentWorkoutId.value = workoutId
    }

    // 3. Combine them!
    @OptIn(ExperimentalCoroutinesApi::class)
    val totalVolume: StateFlow<Double?> = combine(currentUnit, currentWorkoutId) { unit, id ->
        Pair(unit, id)
    }.flatMapLatest { (unit, id) ->
        if (id == null) {
            flowOf(0.0)
        } else {
            workoutDao.getWorkoutVolumeById(id, unit.name)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    fun formatVolume(volume: Double?): String {
        if (volume == null || volume == 0.0) return "0.0"

        return if (volume >= 1000) {
            String.format(java.util.Locale.US, "%.1fk", volume / 1000.0)
        } else {
            String.format(java.util.Locale.US, "%.1f", volume)
        }
    }

    fun deleteWorkout(workoutId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            workoutDao.deleteWorkout(workoutId)
            onSuccess()
        }
    }
}