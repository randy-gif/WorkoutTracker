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
import com.rvilleda.workouttracker.model.WeightUnit
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job


class ActiveWorkoutViewModel(private val workoutDao: WorkoutDao) : ViewModel() {

    private val _activeExercises = MutableStateFlow<List<ExerciseInSession>>(emptyList())
    val activeExercises: StateFlow<List<ExerciseInSession>> = _activeExercises.asStateFlow()

    private val _isWorkoutActive = MutableStateFlow(false)
    val isWorkoutActive: StateFlow<Boolean> = _isWorkoutActive.asStateFlow()

    private val _preferredUnit = MutableStateFlow(WeightUnit.LBS)
    val preferredUnit: StateFlow<WeightUnit> = _preferredUnit.asStateFlow()

    private var startTime = System.currentTimeMillis()
    private val _elapsedTime = MutableStateFlow("00:00")
    val elapsedTime: StateFlow<String> = _elapsedTime.asStateFlow()

    private var restTimerJob: Job? = null
    private val _restTimeRemaining = MutableStateFlow(0)
    val restTimeRemaining: StateFlow<Int> = _restTimeRemaining.asStateFlow()

    init {
        startTimer()
    }

    private fun startTimer() {
        viewModelScope.launch {
            while (true) {
                if (_isWorkoutActive.value) {
                    val currentMs = System.currentTimeMillis()
                    val diff = currentMs - startTime

                    val seconds = (diff / 1000) % 60
                    val minutes = (diff / (1000 * 60)) % 60
                    val hours = (diff / (1000 * 60 * 60))

                    _elapsedTime.value = if (hours > 0) {
                        String.format("%d:%02d:%02d", hours, minutes, seconds)
                    } else {
                        String.format("%02d:%02d", minutes, seconds)
                    }
                } else {
                    _elapsedTime.value = "00:00"
                }
                delay(1000L)
            }
        }
    }
    fun startNewWorkout(firstExerciseId: String, firstExerciseName: String) {
        if (_isWorkoutActive.value) return
        _activeExercises.value = emptyList()
        startTime = System.currentTimeMillis()
        _isWorkoutActive.value = true
        addExerciseToSession(firstExerciseId, firstExerciseName)
    }

    fun startWorkoutAgain(pastExercises: List<ExerciseInSession>) {
        // Guard Clause: Don't overwrite if they are already working out!
        if (_isWorkoutActive.value) return

        val freshExercises = pastExercises.map { oldExercise ->
            oldExercise.copy(
                id = UUID.randomUUID().toString(), // Fresh ID
                sets = oldExercise.sets.map { oldSet ->
                    oldSet.copy(id = UUID.randomUUID().toString()) // Fresh ID
                }
            )
        }

        _activeExercises.value = freshExercises
        _isWorkoutActive.value = true
        startTime = System.currentTimeMillis()
    }
    fun finishAndClearWorkout(onSuccess: () -> Unit) {
        saveWorkout()
        _isWorkoutActive.value = false
        _activeExercises.value = emptyList()
        restTimerJob?.cancel()
        onSuccess()

    }

    fun discardWorkout() {
        _isWorkoutActive.value = false
        _activeExercises.value = emptyList()
        restTimerJob?.cancel()
    }

    fun addExerciseToSession(baseExerciseId: String, exerciseName: String) {
        _activeExercises.update { currentExercises ->
            currentExercises + ExerciseInSession(
                baseExerciseId = baseExerciseId,
                exerciseName = exerciseName
            )
        }
    }

    fun addSetToExercise(exerciseId: String) {
        val currentUnit = _preferredUnit.value

        val updatedExercises = _activeExercises.value.map { exercise ->
            if (exercise.id == exerciseId) {
                val newSet = ExerciseSet(
                    id = UUID.randomUUID().toString(),
                    weight = "",
                    reps = "",
                    weightUnit = currentUnit
                )
                exercise.copy(sets = exercise.sets + newSet)
            } else exercise
        }
        _activeExercises.value = updatedExercises
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

    fun toggleUnit() {
        _preferredUnit.value = if (_preferredUnit.value == WeightUnit.LBS) WeightUnit.KG else WeightUnit.LBS
    }

    // 2. DELETE ENTIRE EXERCISE
    fun removeExerciseFromSession(exerciseId: String) {
        _activeExercises.value = _activeExercises.value.filterNot { it.id == exerciseId }
    }

    // 3. REARRANGE EXERCISES (Move Up / Down)
    fun moveExerciseUp(exerciseId: String) {
        val currentList = _activeExercises.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == exerciseId }
        if (index > 0) {
            val item = currentList.removeAt(index)
            currentList.add(index - 1, item)
            _activeExercises.value = currentList
        }
    }

    fun moveExerciseDown(exerciseId: String) {
        val currentList = _activeExercises.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == exerciseId }
        if (index >= 0 && index < currentList.size - 1) {
            val item = currentList.removeAt(index)
            currentList.add(index + 1, item)
            _activeExercises.value = currentList
        }
    }

    // 4. COMPLETE SET & TRIGGER TIMER
    fun toggleSetCompletion(exerciseId: String, setId: String) {
        val updatedExercises = _activeExercises.value.map { exercise ->
            if (exercise.id == exerciseId) {
                val updatedSets = exercise.sets.map { set ->
                    if (set.id == setId) {
                        val newCompletionStatus = !set.isCompleted

                        // IF IT WAS JUST COMPLETED, START THE REST TIMER!
                        if (newCompletionStatus) {
                            startRestTimer()
                        }

                        set.copy(isCompleted = newCompletionStatus)
                    } else set
                }
                exercise.copy(sets = updatedSets)
            } else exercise
        }
        _activeExercises.value = updatedExercises
    }

    private fun startRestTimer() {
        // Cancel any existing timer so they don't overlap!
        restTimerJob?.cancel()

        // Set the default rest time (e.g., 90 seconds)
        _restTimeRemaining.value = 90

        restTimerJob = viewModelScope.launch {
            while (_restTimeRemaining.value > 0) {
                delay(1000L) // Wait exactly 1 second
                _restTimeRemaining.value -= 1 // Tick down
            }
        }
    }

    fun skipRestTimer() {
        restTimerJob?.cancel()
        _restTimeRemaining.value = 0
    }

    fun addRestTime(seconds: Int) {
        _restTimeRemaining.value += seconds
    }

    fun subtractRestTime(seconds: Int) {
        val newTime = _restTimeRemaining.value - seconds
        _restTimeRemaining.value = if (newTime > 0) newTime else 0
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