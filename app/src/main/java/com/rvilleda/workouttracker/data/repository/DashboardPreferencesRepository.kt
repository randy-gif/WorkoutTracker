package com.rvilleda.workouttracker.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DashboardPreferencesRepository(private val dataStore: DataStore<Preferences>) {

    private companion object {
        val TREND_EXERCISE_ID = stringPreferencesKey("trend_exercise_id")
        val TREND_EXERCISE_NAME = stringPreferencesKey("trend_exercise_name")
    }

    // Expose a Pair holding the ID and Name.
    // If no preference exists, it returns null for the ID and "Bench Press" for the name.
    val selectedTrendExercise: Flow<Pair<String?, String>> = dataStore.data.map { prefs ->
        val id = prefs[TREND_EXERCISE_ID]
        val name = prefs[TREND_EXERCISE_NAME] ?: "Bench Press"
        Pair(id, name)
    }

    suspend fun saveTrendExercisePreference(exerciseId: String, exerciseName: String) {
        dataStore.edit { prefs ->
            prefs[TREND_EXERCISE_ID] = exerciseId
            prefs[TREND_EXERCISE_NAME] = exerciseName
        }
    }
}