package com.rvilleda.workouttracker.ui.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rvilleda.workouttracker.data.repository.SettingsRepository
import com.rvilleda.workouttracker.model.WeightUnit
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// We use AndroidViewModel here so we can easily pass the Application Context to DataStore
class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SettingsRepository(application)

    // Converts the DataStore Flow into a StateFlow that Compose can easily read
    val globalWeightUnit: StateFlow<WeightUnit> = repository.weightUnitFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = WeightUnit.LBS // Default starting value
        )

    fun setGlobalWeightUnit(unit: WeightUnit) {
        viewModelScope.launch {
            repository.updateGlobalWeightUnit(unit)
        }
    }
}