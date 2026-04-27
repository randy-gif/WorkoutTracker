package com.rvilleda.workouttracker.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.rvilleda.workouttracker.model.AppTheme
import com.rvilleda.workouttracker.model.WeightUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// This creates the actual file on the device named "user_settings"
val Context.dataStore by preferencesDataStore(name = "user_settings")

class SettingsRepository(private val context: Context) {

    companion object {
        val GLOBAL_WEIGHT_UNIT = stringPreferencesKey("global_weight_unit")
        val GLOBAL_THEME = stringPreferencesKey("global_theme")
    }

    val weightUnitFlow: Flow<WeightUnit> = context.dataStore.data.map { preferences ->
        // Grab the string, default to LBS if it doesn't exist yet
        val unitString = preferences[GLOBAL_WEIGHT_UNIT] ?: WeightUnit.LBS.name
        WeightUnit.valueOf(unitString)
    }

    suspend fun updateGlobalWeightUnit(unit: WeightUnit) {
        context.dataStore.edit { preferences ->
            preferences[GLOBAL_WEIGHT_UNIT] = unit.name
        }
    }

    val themeFlow: Flow<AppTheme> = context.dataStore.data.map { preferences ->
        val themeString = preferences[GLOBAL_THEME] ?: AppTheme.SYSTEM.name
        AppTheme.valueOf(themeString)
    }

    suspend fun updateGlobalTheme(theme: AppTheme) {
        context.dataStore.edit { preferences ->
            preferences[GLOBAL_THEME] = theme.name
        }
    }
}