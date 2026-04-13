package com.rvilleda.workouttracker.ui.screens.activeworkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.rvilleda.workouttracker.model.Exercise
import com.rvilleda.workouttracker.model.ExerciseInSession
import com.rvilleda.workouttracker.model.ExerciseSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import com.rvilleda.workouttracker.data.database.WorkoutDao
import com.rvilleda.workouttracker.data.database.CompletedWorkoutEntity
import kotlinx.coroutines.delay


class ActiveWorkoutViewModel(private val workoutDao: WorkoutDao) : ViewModel() {

    private val _activeExercises = MutableStateFlow<List<ExerciseInSession>>(emptyList())
    val activeExercises: StateFlow<List<ExerciseInSession>> = _activeExercises.asStateFlow()

    private val _isWorkoutActive = MutableStateFlow(false)
    val isWorkoutActive: StateFlow<Boolean> = _isWorkoutActive.asStateFlow()

    private val startTime = System.currentTimeMillis()
    private val _elapsedTime = MutableStateFlow("00:00")
    val elapsedTime: StateFlow<String> = _elapsedTime.asStateFlow()

    init {
        startTimer()
    }

    private fun startTimer() {
        viewModelScope.launch {
            while (true) {
                // Calculate how much time has passed
                val currentMs = System.currentTimeMillis()
                val diff = currentMs - startTime

                val seconds = (diff / 1000) % 60
                val minutes = (diff / (1000 * 60)) % 60
                val hours = (diff / (1000 * 60 * 60))

                // Format it nicely like a digital clock
                _elapsedTime.value = if (hours > 0) {
                    String.format("%d:%02d:%02d", hours, minutes, seconds)
                } else {
                    String.format("%02d:%02d", minutes, seconds)
                }

                // Wait exactly 1 second, then loop again
                delay(1000L)
            }
        }
    }
    fun startNewWorkout(firstExerciseId: String, firstExerciseName: String) {
        if (_isWorkoutActive.value) return
        _activeExercises.value = emptyList()
        _isWorkoutActive.value = true
        addExerciseToSession(firstExerciseId, firstExerciseName)
    }
    fun finishAndClearWorkout() {
        saveWorkout() // Save it to the database
        _isWorkoutActive.value = false // Hide the bottom tab
        _activeExercises.value = emptyList() // Wipe the memory
    }
    fun addExerciseToSession(baseExerciseId: String, exerciseName: String) {
        _activeExercises.update { currentExercises ->
            currentExercises + ExerciseInSession(
                baseExerciseId = baseExerciseId,
                exerciseName = exerciseName
            )
        }
    }

    fun addSetToExercise(workoutExerciseId: String) {
        _activeExercises.update { currentExercises ->
            currentExercises.map { exercise ->
                if (exercise.id == workoutExerciseId) {
                    exercise.copy(sets = exercise.sets + ExerciseSet())
                } else {
                    exercise
                }
            }
        }
    }

    fun updateSet(workoutExerciseId: String, setId: String, weight: String, reps: String) {
        _activeExercises.update { currentExercises ->
            currentExercises.map { exercise ->
                if (exercise.id == workoutExerciseId) {
                    val updatedSets = exercise.sets.map { set ->
                        if (set.id == setId) set.copy(weight = weight, reps = reps) else set
                    }
                    exercise.copy(sets = updatedSets)
                } else {
                    exercise
                }
            }
        }
    }

    fun removeSet(workoutExerciseId: String, setId: String) {
        _activeExercises.update { currentExercises ->
            currentExercises.map { exercise ->
                if (exercise.id == workoutExerciseId) {
                    exercise.copy(sets = exercise.sets.filterNot { it.id == setId })
                } else {
                    exercise
                }
            }
        }
    }

    fun saveWorkout() {
        viewModelScope.launch {

            val jsonString = Gson().toJson(_activeExercises.value)

            val newEntity = CompletedWorkoutEntity(
                id = UUID.randomUUID().toString(),
                dateCompleted = System.currentTimeMillis(),
                durationMs = System.currentTimeMillis() - startTime,
                exercisesJson = jsonString
            )

            workoutDao.insertWorkout(newEntity)
        }
    }

}