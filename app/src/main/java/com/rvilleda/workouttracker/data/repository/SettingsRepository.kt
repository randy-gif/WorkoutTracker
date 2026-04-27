package com.rvilleda.workouttracker.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.rvilleda.workouttracker.model.WeightUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// This creates the actual file on the device named "user_settings"
val Context.dataStore by preferencesDataStore(name = "user_settings")

class SettingsRepository(private val context: Context) {

    companion object {
        // The "Key" we use to find the preference in the database
        val GLOBAL_WEIGHT_UNIT = stringPreferencesKey("global_weight_unit")
    }

    // 1. READ: A Flow that constantly streams the current setting
    val weightUnitFlow: Flow<WeightUnit> = context.dataStore.data.map { preferences ->
        // Grab the string, default to LBS if it doesn't exist yet
        val unitString = preferences[GLOBAL_WEIGHT_UNIT] ?: WeightUnit.LBS.name
        WeightUnit.valueOf(unitString)
    }

    // 2. WRITE: A function to save the new setting
    suspend fun updateGlobalWeightUnit(unit: WeightUnit) {
        context.dataStore.edit { preferences ->
            preferences[GLOBAL_WEIGHT_UNIT] = unit.name
        }
    }
}