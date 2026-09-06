package com.rvilleda.workouttracker.ui.screens.activeworkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rvilleda.workouttracker.data.database.dao.RoutineDao
import com.rvilleda.workouttracker.model.ExerciseInSession
import com.rvilleda.workouttracker.model.ExerciseSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import com.rvilleda.workouttracker.data.database.dao.WorkoutDao
import com.rvilleda.workouttracker.data.database.entity.ActiveWorkoutEntity
import com.rvilleda.workouttracker.data.database.entity.CompletedWorkoutEntity
import com.rvilleda.workouttracker.data.database.entity.WorkoutExerciseEntity
import com.rvilleda.workouttracker.data.database.entity.WorkoutSetEntity
import com.rvilleda.workouttracker.model.WeightUnit
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import kotlin.math.roundToInt


// Shared by the placeholder UI and completion so the saved value matches the hint.
fun WorkoutSetEntity.weightTextIn(unit: WeightUnit): String {
    val converted = when {
        weightUnit == unit -> weight
        unit == WeightUnit.KG -> weight / 2.20462f
        else -> weight * 2.20462f
    }
    val rounded = (converted * 10f).roundToInt() / 10f
    return if (rounded % 1f == 0f) rounded.toInt().toString() else rounded.toString()
}

class ActiveWorkoutViewModel(private val workoutDao: WorkoutDao, private val routineDao: RoutineDao) : ViewModel() {

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

    private var currentRoutineId: String? = null

    // Suggestions are separate from entered values and are never saved as performed sets.
    private val _setHistory = MutableStateFlow<Map<String, WorkoutSetEntity>>(emptyMap())
    val setHistory: StateFlow<Map<String, WorkoutSetEntity>> = _setHistory.asStateFlow()
    private val historyJobs = mutableMapOf<String, Job>()

    private fun loadActiveSetHistory() {
        historyJobs.values.forEach { it.cancel() }
        historyJobs.clear()
        _setHistory.value = emptyMap()
        _activeExercises.value.forEach { exercise ->
            exercise.sets.forEachIndexed { index, set ->
                loadSetHistory(exercise.id, exercise.baseExerciseId, set, index + 1)
            }
        }
    }


    init {
        startTimer()
        checkForActiveWorkoutCache()
    }

    private fun checkForActiveWorkoutCache() {
        viewModelScope.launch {
            val cache = workoutDao.getActiveWorkoutCache()
            if (cache != null) {
                currentRoutineId = cache.routineId
                startTime = cache.startTime
                _activeExercises.value = cache.exercises

                _isWorkoutActive.value = true
                loadActiveSetHistory()
            }
        }
    }

    private fun autoSaveCache() {
        if (!_isWorkoutActive.value) return

        viewModelScope.launch {
            val cacheEntity = ActiveWorkoutEntity(
                id = 1,
                routineId = currentRoutineId,
                startTime = startTime,
                exercises = _activeExercises.value
            )
            workoutDao.saveActiveWorkoutCache(cacheEntity)
        }
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
        loadActiveSetHistory()
    }

    fun startWorkoutFromRoutine(routineId: String) {
        viewModelScope.launch {

            val fullRoutine = routineDao.getFullRoutineById(routineId)

            if (fullRoutine != null) {
                currentRoutineId = routineId
                _activeExercises.value = fullRoutine.exercises.map { routineExercise ->
                    ExerciseInSession(
                        exerciseName = routineExercise.exercise.exerciseName,
                        baseExerciseId = routineExercise.exercise.baseExerciseId,
                        sets = routineExercise.sets.map { routineSet ->
                            ExerciseSet(
                                weight = routineSet.targetWeight.toString(),
                                reps = routineSet.targetReps.toString(),
                            )
                        }
                    )
                }
                startTime = System.currentTimeMillis()
                _isWorkoutActive.value = true
                loadActiveSetHistory()
                autoSaveCache()
            }
        }
    }
    fun finishAndClearWorkout(workoutName: String, onSuccess: () -> Unit) {
        saveWorkout(workoutName)
        onSuccess()

        _isWorkoutActive.value = false
        restTimerJob?.cancel()
        _restTimeRemaining.value = 0

        viewModelScope.launch {
            delay(400L)
            _activeExercises.value = emptyList()
            _elapsedTime.value = "00:00"
            startTime = 0L
        }
    }

    fun discardWorkout(onSuccess: () -> Unit) {

        _isWorkoutActive.value = false
        restTimerJob?.cancel()
        _restTimeRemaining.value = 0
        _activeExercises.value = emptyList()
        _elapsedTime.value = "00:00"

        onSuccess()
        viewModelScope.launch {
            workoutDao.clearActiveWorkoutCache()
        }
    }

    fun addExerciseToSession(
        baseExerciseId: String,
        exerciseName: String,
        defaultUnit: WeightUnit,
    ) {
        val firstSet = ExerciseSet(weightUnit = defaultUnit)
        val exercise = ExerciseInSession(
            baseExerciseId = baseExerciseId,
            exerciseName = exerciseName,
            sets = listOf(firstSet)
        )
        _activeExercises.update { it + exercise }
        autoSaveCache()
        loadSetHistory(exercise.id, baseExerciseId, firstSet, 1)
    }

    fun addSetToExercise(exerciseId: String, defaultUnit: WeightUnit) {
        val exercise = _activeExercises.value.firstOrNull { it.id == exerciseId } ?: return
        val newSet = ExerciseSet(
            id = UUID.randomUUID().toString(),
            weight = "",
            reps = "",
            weightUnit = exercise.sets.lastOrNull()?.weightUnit ?: defaultUnit,
            isCompleted = false
        )
        val setNumber = exercise.sets.size + 1
        _activeExercises.update { exercises ->
            exercises.map {
                if (it.id == exerciseId) it.copy(sets = it.sets + newSet) else it
            }
        }
        autoSaveCache()
        loadSetHistory(exerciseId, exercise.baseExerciseId, newSet, setNumber)
    }

    private fun loadSetHistory(
        exerciseId: String,
        baseExerciseId: String,
        originalSet: ExerciseSet,
        setNumber: Int
    ) {
        val workoutStart = startTime
        historyJobs[originalSet.id] = viewModelScope.launch {
            val previousSets = try {
                workoutDao.getLastCompletedSetsForExercise(baseExerciseId, workoutStart)
            } catch (cancelled: kotlinx.coroutines.CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                android.util.Log.w("ActiveWorkoutViewModel", "Could not load previous sets", error)
                return@launch
            }
            if (!_isWorkoutActive.value || startTime != workoutStart) return@launch
            val exists = _activeExercises.value.any { exercise ->
                exercise.id == exerciseId && exercise.sets.any { it.id == originalSet.id }
            }
            if (!exists) return@launch

            val previousSet = previousSets.firstOrNull { it.setNumber == setNumber }
                ?: previousSets.lastOrNull()?.takeIf { setNumber > it.setNumber }
                ?: return@launch
            _setHistory.update { it + (originalSet.id to previousSet) }
        }
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
        autoSaveCache()
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
        autoSaveCache()
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
        autoSaveCache()
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

                            set.copy(weightUnit = unit, weight = if (set.weight.isBlank()) "" else weightString)
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
        autoSaveCache()
    }


    fun removeSet(workoutExerciseId: String, setId: String) {
        _activeExercises.update { currentExercises ->
            currentExercises.map { exercise ->
                if (exercise.id == workoutExerciseId) {
                    exercise.copy(sets = exercise.sets.filterNot { it.id == setId })
                } else {
                    exercise
                }
            }.filterNot { exercise ->
                exercise.sets.isEmpty()
            }
        }
        autoSaveCache()
    }

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
        autoSaveCache()
    }

    fun removeExerciseFromSession(exerciseId: String) {
        _activeExercises.value = _activeExercises.value.filterNot { it.id == exerciseId }
        autoSaveCache()
    }

    fun moveExerciseByKey(fromId: String, toId: String) {
        _activeExercises.update { currentList ->
            val mutableList = currentList.toMutableList()

            // Find the exact positions of the dragged item and its target
            val fromIndex = mutableList.indexOfFirst { it.id == fromId }
            val toIndex = mutableList.indexOfFirst { it.id == toId }

            if (fromIndex != -1 && toIndex != -1) {
                // Swap them
                val item = mutableList.removeAt(fromIndex)
                mutableList.add(toIndex, item)
            }
            mutableList
        }
        autoSaveCache()
    }

    // 4. COMPLETE SET & TRIGGER TIMER
    fun toggleSetCompletion(exerciseId: String, setId: String) {
        val workoutStart = startTime
        viewModelScope.launch {
            // A quick tap after Add Set still uses the history being loaded.
            historyJobs[setId]?.join()
            if (!_isWorkoutActive.value || startTime != workoutStart) return@launch
            val exercise = _activeExercises.value.firstOrNull { it.id == exerciseId }
                ?: return@launch
            val set = exercise.sets.firstOrNull { it.id == setId } ?: return@launch
            val completing = !set.isCompleted
            val previous = _setHistory.value[setId]
            val updatedSet = set.copy(
                weight = if (completing && set.weight.isBlank()) {
                    previous?.weightTextIn(set.weightUnit) ?: set.weight
                } else set.weight,
                reps = if (completing && set.reps.isBlank()) {
                    previous?.reps?.toString() ?: set.reps
                } else set.reps,
                isCompleted = completing
            )
            _activeExercises.update { exercises ->
                exercises.map { current ->
                    if (current.id == exerciseId) {
                        current.copy(sets = current.sets.map {
                            if (it.id == setId) updatedSet else it
                        })
                    } else current
                }
            }
            if (completing && exercise.autoRestEnabled) {
                startRestTimer(exercise.restTimeSeconds)
            }
            autoSaveCache()
        }
    }

    fun toggleAutoRest(exerciseId: String) {
        _activeExercises.value = _activeExercises.value.map { exercise ->
            if (exercise.id == exerciseId) {
                exercise.copy(autoRestEnabled = !exercise.autoRestEnabled)
            } else exercise
        }
        autoSaveCache()
    }

    fun updateRestTime(exerciseId: String, seconds: Int) {
        _activeExercises.value = _activeExercises.value.map { exercise ->
            if (exercise.id == exerciseId) {
                exercise.copy(restTimeSeconds = seconds)
            } else exercise
        }
        autoSaveCache()
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

    fun saveWorkout(workoutName: String) {
        viewModelScope.launch {
            val workoutId = UUID.randomUUID().toString()

            val workoutEntity = CompletedWorkoutEntity(
                id = workoutId,
                name = workoutName,
                startTime = startTime,
                dateCompleted = System.currentTimeMillis(),
            )

            val exerciseEntities = mutableListOf<WorkoutExerciseEntity>()
            val setEntities = mutableListOf<WorkoutSetEntity>()

            _activeExercises.value.forEachIndexed { exerciseIndex, activeExercise ->
                val workoutExerciseId = UUID.randomUUID().toString()
                exerciseEntities.add(
                    WorkoutExerciseEntity(
                        id = workoutExerciseId,
                        workoutId = workoutId,
                        baseExerciseId = activeExercise.baseExerciseId,
                        exerciseName = activeExercise.exerciseName,
                        orderInWorkout = exerciseIndex
                    )
                )

                activeExercise.sets.forEachIndexed { setIndex, set ->
                    if (set.isCompleted) {
                        setEntities.add(
                            WorkoutSetEntity(
                                id = UUID.randomUUID().toString(),
                                workoutExerciseId = workoutExerciseId,
                                setNumber = setIndex + 1,
                                weight = set.weight.toFloatOrNull() ?: 0f,
                                weightUnit = set.weightUnit,
                                reps = set.reps.toIntOrNull() ?: 0,
                                rpe = set.rpe,
                                isCompleted = true
                            )
                        )
                    }
                }
            }

            workoutDao.saveFullWorkout(workoutEntity, exerciseEntities, setEntities)
            workoutDao.clearActiveWorkoutCache()
        }
    }

}
