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


class ActiveWorkoutViewModel(private val workoutDao: WorkoutDao) : ViewModel() {

    // Now holding a list of Exercises instead of just Sets
    private val _activeExercises = MutableStateFlow<List<ExerciseInSession>>(emptyList())
    val activeExercises: StateFlow<List<ExerciseInSession>> = _activeExercises.asStateFlow()

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
                exercisesJson = jsonString
            )

            workoutDao.insertWorkout(newEntity)
        }
    }

}