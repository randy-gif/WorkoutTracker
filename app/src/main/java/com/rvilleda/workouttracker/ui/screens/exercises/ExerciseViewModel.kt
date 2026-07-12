package com.rvilleda.workouttracker.ui.screens.exercises

import androidx.lifecycle.ViewModel
import com.rvilleda.workouttracker.data.database.dao.ExerciseDao
import com.rvilleda.workouttracker.data.repository.ExerciseRepository
import com.rvilleda.workouttracker.model.Exercise
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.Flow

class ExerciseViewModel(private val exerciseDao: ExerciseDao) : ViewModel() {

    private val _selectedExercises = MutableStateFlow<Set<Exercise>>(emptySet())
    private val _selectionMode = MutableStateFlow(false)
    val selectedExercises: StateFlow<Set<Exercise>> = _selectedExercises.asStateFlow()
    val selectionMode: StateFlow<Boolean> = _selectionMode.asStateFlow()

    val exercises: Flow<List<Exercise>> = ExerciseRepository(exerciseDao).getAllExercises()

    // 2. Accept the whole object as a parameter
    fun toggleSelection(exercise: Exercise) {
        val currentSet = _selectedExercises.value

        // Kotlin automatically compares the data inside the object to see if it exists!
        if (currentSet.contains(exercise)) {
            _selectedExercises.value = currentSet - exercise
        } else {
            _selectedExercises.value = currentSet + exercise
        }
        if (_selectedExercises.value.isEmpty()) {
            _selectionMode.value = false
        }
    }

    fun clearSelection() {
        _selectedExercises.value = emptySet()
        _selectionMode.value = false
    }

    fun toggleSelectionMode() {
        _selectionMode.value = !_selectionMode.value
        if (!_selectionMode.value) {
            clearSelection()
        }
    }
}
