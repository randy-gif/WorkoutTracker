package com.rvilleda.workouttracker.ui.screens.createroutine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rvilleda.workouttracker.data.database.dao.RoutineDao
import com.rvilleda.workouttracker.data.database.routine.RoutineExerciseEntity
import com.rvilleda.workouttracker.data.database.routine.RoutineSetEntity
import com.rvilleda.workouttracker.data.database.routine.RoutineEntity
import com.rvilleda.workouttracker.model.ExerciseInSession
import com.rvilleda.workouttracker.model.ExerciseSet
import com.rvilleda.workouttracker.model.WeightUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.roundToInt


class CreateRoutineViewModel( private val routineDao: RoutineDao) : ViewModel() {

    private val _routineExercises = MutableStateFlow<List<ExerciseInSession>>(emptyList())
    val routineExercises: StateFlow<List<ExerciseInSession>> = _routineExercises.asStateFlow()

    fun discardRoutine() {
        _routineExercises.value = emptyList()
    }

    fun addExerciseToSession(
        baseExerciseId: String,
        exerciseName: String,
        defaultUnit: WeightUnit,
    ) {
        _routineExercises.update { currentExercises ->
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
        val updatedExercises = _routineExercises.value.map { exercise ->
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
        _routineExercises.value = updatedExercises
    }

    fun updateSetWeight(workoutExerciseId: String, setId: String, weight: String) {
        _routineExercises.update { currentExercises ->
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
        _routineExercises.update { currentExercises ->
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

    fun removeSet(workoutExerciseId: String, setId: String) {
        _routineExercises.update { currentExercises ->
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
    }

    fun toggleExerciseUnit(exerciseId: String) {
        _routineExercises.update { currentExercises ->
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

    fun removeExercise(exerciseId: String) {
        _routineExercises.value = _routineExercises.value.filterNot { it.id == exerciseId }
    }

    fun moveExerciseByKey(fromId: String, toId: String) {
        _routineExercises.update { currentList ->
            val mutableList = currentList.toMutableList()

            val fromIndex = mutableList.indexOfFirst { it.id == fromId }
            val toIndex = mutableList.indexOfFirst { it.id == toId }

            if (fromIndex != -1 && toIndex != -1) {
                val item = mutableList.removeAt(fromIndex)
                mutableList.add(toIndex, item)
            }
            mutableList
        }
    }

    fun toggleAutoRest(exerciseId: String) {
        _routineExercises.value = _routineExercises.value.map { exercise ->
            if (exercise.id == exerciseId) {
                exercise.copy(autoRestEnabled = !exercise.autoRestEnabled)
            } else exercise
        }
    }

    fun updateRestTime(exerciseId: String, seconds: Int) {
        _routineExercises.value = _routineExercises.value.map { exercise ->
            if (exercise.id == exerciseId) {
                exercise.copy(restTimeSeconds = seconds)
            } else exercise
        }
    }

    fun saveRoutine(routineName: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val routineId = UUID.randomUUID().toString()

            val routineEntity = RoutineEntity(
                id = routineId,
                name = routineName
            )

            val exerciseEntities = mutableListOf<RoutineExerciseEntity>()
            val setEntities = mutableListOf<RoutineSetEntity>()

            _routineExercises.value.forEachIndexed { exerciseIndex, activeExercise ->
                val routineExerciseId = UUID.randomUUID().toString()

                exerciseEntities.add(
                    RoutineExerciseEntity(
                        id = routineExerciseId,
                        routineId = routineId,
                        baseExerciseId = activeExercise.baseExerciseId,
                        exerciseName = activeExercise.exerciseName,
                        orderInRoutine = exerciseIndex,
                        restTimeSeconds = activeExercise.restTimeSeconds,
                        autoRestEnabled = activeExercise.autoRestEnabled
                    )
                )

                activeExercise.sets.forEachIndexed { setIndex, set ->
                    setEntities.add(
                        RoutineSetEntity(
                            id = UUID.randomUUID().toString(),
                            routineExerciseId = routineExerciseId,
                            setNumber = setIndex + 1,
                            targetWeight = set.weight.toFloatOrNull() ?: 0f,
                            targetReps = set.reps.toIntOrNull() ?: 0
                        )
                    )
                }
            }

            routineDao.saveFullRoutine(routineEntity, exerciseEntities, setEntities)

            _routineExercises.value = emptyList()
            onSuccess()
        }
    }
}