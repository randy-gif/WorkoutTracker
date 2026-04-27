package com.rvilleda.workouttracker.ui.screens.activeworkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
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
import kotlin.math.roundToInt


class ActiveWorkoutViewModel(private val workoutDao: WorkoutDao) : ViewModel() {

    private val _activeExercises = MutableStateFlow<List<ExerciseInSession>>(emptyList())
    val activeExercises: StateFlow<List<ExerciseInSession>> = _activeExercises.asStateFlow()

    private val _isWorkoutActive = MutableStateFlow(false)
    val isWorkoutActive: StateFlow<Boolean> = _isWorkoutActive.asStateFlow()
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
    fun startNewWorkout(firstExerciseId: String, firstExerciseName: String, firstExerciseUnit: WeightUnit) {
        if (_isWorkoutActive.value) return
        _activeExercises.value = emptyList()
        startTime = System.currentTimeMillis()
        _isWorkoutActive.value = true
        addExerciseToSession(firstExerciseId, firstExerciseName, firstExerciseUnit)
    }

    fun startNewEmptyWorkout() {
        if (_isWorkoutActive.value) return
        _activeExercises.value = emptyList()
        startTime = System.currentTimeMillis()
        _isWorkoutActive.value = true
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

    fun addExerciseToSession(
        baseExerciseId: String,
        exerciseName: String,
        defaultUnit: WeightUnit,
    ) {
        _activeExercises.update { currentExercises ->
            currentExercises + ExerciseInSession(
                baseExerciseId = baseExerciseId,
                exerciseName = exerciseName,
                sets = listOf(
                    ExerciseSet(
                        weightUnit = defaultUnit
                    )
                )
            )
        }
    }

    fun addSetToExercise(exerciseId: String, defaultUnit: WeightUnit) {

        val updatedExercises = _activeExercises.value.map { exercise ->
            if (exercise.id == exerciseId) {
                val newSet = ExerciseSet(
                    id = UUID.randomUUID().toString(),
                    weight = "",
                    reps = "",
                    weightUnit = defaultUnit
                )
                exercise.copy(sets = exercise.sets + newSet)
            } else exercise
        }
        _activeExercises.value = updatedExercises
    }

    fun updateSet(workoutExerciseId: String, setId: String, weight: String = "", reps: String = "", isCompleted: Boolean = false, weightUnit: WeightUnit = WeightUnit.LBS) {
        _activeExercises.update { currentExercises ->
            currentExercises.map { exercise ->
                if (exercise.id == workoutExerciseId) {
                    val updatedSets = exercise.sets.map { set ->
                        if (set.id == setId) set.copy(weight = weight, reps = reps, isCompleted = isCompleted, weightUnit = weightUnit ) else set
                    }
                    exercise.copy(sets = updatedSets)
                } else {
                    exercise
                }
            }
        }
    }

    fun updateSetWeight(workoutExerciseId: String, setId: String, weight: String) {
        _activeExercises.update { currentExercises ->
            currentExercises.map { exercise ->
                if (exercise.id == workoutExerciseId) {
                    val updatedSets = exercise.sets.map { set ->
                        if (set.id == setId) set.copy(weight = weight) else set
                    }
                    exercise.copy(sets = updatedSets)
                } else {
                    exercise
                }
            }
        }
    }

    fun updateSetReps(workoutExerciseId: String, setId: String, reps: String) {
        _activeExercises.update { currentExercises ->
            currentExercises.map { exercise ->
                if (exercise.id == workoutExerciseId) {
                    val updatedSets = exercise.sets.map { set ->
                        if (set.id == setId) set.copy(reps = reps) else set
                    }
                    exercise.copy(sets = updatedSets)
                } else {
                    exercise
                }
            }
        }
    }

    fun updateSetUnit(workoutExerciseId: String, setId: String, unit: WeightUnit) {
        _activeExercises.update { currentExercises ->
            currentExercises.map { exercise ->
                if (exercise.id == workoutExerciseId) {
                    val updatedSets = exercise.sets.map { set ->
                        // 1. Check if it's the right set AND if the unit is actually changing
                        if (set.id == setId && set.weightUnit != unit) {
                            val currentWeight = set.weight.toFloatOrNull() ?: 0f

                            val convertedWeight = when (unit) {
                                WeightUnit.LBS -> currentWeight * 2.20462f
                                WeightUnit.KG -> currentWeight / 2.20462f
                            }

                            // Round and convert back to String, dropping the ".0" if it's a whole number
                            val roundedWeight = (convertedWeight * 10f).roundToInt() / 10f
                            val weightString = if (roundedWeight % 1f == 0f) {
                                roundedWeight.toInt().toString()
                            } else {
                                roundedWeight.toString()
                            }

                            set.copy(weightUnit = unit, weight = weightString)
                        } else {
                            set
                        }
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

    // UPDATE EXERCISE

    fun toggleExerciseUnit(exerciseId: String) {
        _activeExercises.update { currentExercises ->
            currentExercises.map { exercise ->
                if (exercise.id == exerciseId) {
                    val updatedSets = exercise.sets.map { set ->
                        val newUnit = if (set.weightUnit == WeightUnit.LBS) WeightUnit.KG else WeightUnit.LBS

                        val weightString = if (set.weight.isBlank()) {
                            ""
                        } else {
                            val currentWeight = set.weight.toFloatOrNull() ?: 0f

                            val convertedWeight = when (set.weightUnit) {
                                WeightUnit.LBS -> currentWeight / 2.20462f
                                WeightUnit.KG -> currentWeight * 2.20462f
                            }

                            val roundedWeight = (convertedWeight * 10f).roundToInt() / 10f
                            if (roundedWeight % 1f == 0f) {
                                roundedWeight.toInt().toString()
                            } else {
                                roundedWeight.toString()
                            }
                        }

                        set.copy(weightUnit = newUnit, weight = weightString)
                    }
                    exercise.copy(sets = updatedSets)
                } else {
                    exercise
                }
            }
        }
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
                        if (newCompletionStatus && exercise.autoRestEnabled) {
                            startRestTimer(exercise.restTimeSeconds)
                        }

                        set.copy(isCompleted = newCompletionStatus)
                    } else set
                }
                exercise.copy(sets = updatedSets)
            } else exercise
        }
        _activeExercises.value = updatedExercises
    }

    fun toggleAutoRest(exerciseId: String) {
        _activeExercises.value = _activeExercises.value.map { exercise ->
            if (exercise.id == exerciseId) {
                exercise.copy(autoRestEnabled = !exercise.autoRestEnabled)
            } else exercise
        }
    }

    fun updateRestTime(exerciseId: String, seconds: Int) {
        _activeExercises.value = _activeExercises.value.map { exercise ->
            if (exercise.id == exerciseId) {
                exercise.copy(restTimeSeconds = seconds)
            } else exercise
        }
    }

    private fun startRestTimer(seconds: Int) {
        // Cancel any existing timer so they don't overlap!
        restTimerJob?.cancel()

        // Set the default rest time (e.g., 90 seconds)
        _restTimeRemaining.value = seconds

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