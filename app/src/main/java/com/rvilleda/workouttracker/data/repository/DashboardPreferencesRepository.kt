package com.rvilleda.workouttracker.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.rvilleda.workouttracker.model.ExerciseTrendSlot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class DashboardPreferencesRepository(private val dataStore: DataStore<Preferences>) {
    fun selectedTrendExercise(slot: ExerciseTrendSlot): Flow<Pair<String, String>> =
        dataStore.data.map { prefs ->
            val default = slot.defaultExercise
            val id = prefs[stringPreferencesKey("${slot.preferencePrefix}_id")]
            if (id == null) default.id to default.name
            else id to (prefs[stringPreferencesKey("${slot.preferencePrefix}_name")] ?: default.name)
        }.distinctUntilChanged()

    suspend fun saveTrendExercisePreference(
        slot: ExerciseTrendSlot,
        exerciseId: String,
        exerciseName: String
    ) {
        dataStore.edit { prefs ->
            // BENCH retains the original keys so existing selections survive the upgrade.
            prefs[stringPreferencesKey("${slot.preferencePrefix}_id")] = exerciseId
            prefs[stringPreferencesKey("${slot.preferencePrefix}_name")] = exerciseName
        }
    }
}