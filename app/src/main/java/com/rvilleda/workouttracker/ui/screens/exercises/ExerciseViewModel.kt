package com.rvilleda.workouttracker.ui.screens.exercises

import androidx.lifecycle.ViewModel
import com.rvilleda.workouttracker.model.Exercise
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ExerciseViewModel : ViewModel() {

    private val _selectedExercises = MutableStateFlow<Set<Exercise>>(emptySet())
    val selectedExercises: StateFlow<Set<Exercise>> = _selectedExercises.asStateFlow()

    // 2. Accept the whole object as a parameter
    fun toggleSelection(exercise: Exercise) {
        val currentSet = _selectedExercises.value

        // Kotlin automatically compares the data inside the object to see if it exists!
        if (currentSet.contains(exercise)) {
            _selectedExercises.value = currentSet - exercise
        } else {
            _selectedExercises.value = currentSet + exercise
        }
    }

    fun clearSelection() {
        _selectedExercises.value = emptySet()
    }
}