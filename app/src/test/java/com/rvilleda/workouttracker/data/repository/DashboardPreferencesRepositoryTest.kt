package com.rvilleda.workouttracker.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.rvilleda.workouttracker.model.ExerciseTrendSlot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.junit.Assert.assertEquals
import org.junit.Test

class DashboardPreferencesRepositoryTest {
    // Test preference mapping independently of Android DataStore's file operations.
    private val store = object : DataStore<Preferences> {
        override val data = MutableStateFlow(emptyPreferences())
        private val mutex = Mutex()

        override suspend fun updateData(transform: suspend (Preferences) -> Preferences): Preferences =
            mutex.withLock {
                transform(data.value).also { data.value = it }
            }
    }
    private val repository = DashboardPreferencesRepository(store)
    @Test fun defaultsMatchTheFourRequestedLifts() = runBlocking {
        assertEquals(
            listOf("Barbell Bench Press", "Barbell Back Squat", "Barbell Deadlift", "Overhead Press (OHP)"),
            ExerciseTrendSlot.entries.map { repository.selectedTrendExercise(it).first().second }
        )
    }

    @Test fun existingBenchPreferenceIsPreserved() = runBlocking {
        store.edit {
            it[stringPreferencesKey("trend_exercise_id")] = "custom-bench"
            it[stringPreferencesKey("trend_exercise_name")] = "My bench variation"
        }
        assertEquals("custom-bench" to "My bench variation", repository.selectedTrendExercise(ExerciseTrendSlot.BENCH).first())
        assertEquals("Barbell Back Squat", repository.selectedTrendExercise(ExerciseTrendSlot.SQUAT).first().second)
    }

    @Test fun changingEachSlotPreservesOtherSelections() = runBlocking {
        ExerciseTrendSlot.entries.forEach { slot ->
            repository.saveTrendExercisePreference(slot, "custom-${slot.name}", "Custom ${slot.name}")
        }
        repository.saveTrendExercisePreference(ExerciseTrendSlot.SQUAT, "front-squat", "Front squat")
        // A new repository reads the saved selections rather than in-memory UI state.
        val restored = DashboardPreferencesRepository(store)
        ExerciseTrendSlot.entries.forEach { slot ->
            val expected = if (slot == ExerciseTrendSlot.SQUAT) "front-squat" to "Front squat"
                else "custom-${slot.name}" to "Custom ${slot.name}"
            assertEquals(expected, restored.selectedTrendExercise(slot).first())
        }
    }
}
