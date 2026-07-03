package com.rvilleda.workouttracker.ui.screens.exercises

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rvilleda.workouttracker.data.database.ExerciseDao
import com.rvilleda.workouttracker.model.Equipment
import com.rvilleda.workouttracker.model.Exercise
import com.rvilleda.workouttracker.model.MovementType
import com.rvilleda.workouttracker.model.MuscleGroup
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ExerciseViewModel(private val exerciseDao: ExerciseDao) : ViewModel() {

    private val _selectedExercises = MutableStateFlow<Set<Exercise>>(emptySet())
    private val _selectionMode = MutableStateFlow(false)
    val selectedExercises: StateFlow<Set<Exercise>> = _selectedExercises.asStateFlow()
    val selectionMode: StateFlow<Boolean> = _selectionMode.asStateFlow()

    val exercises: StateFlow<List<Exercise>> = exerciseDao.getAllExercises()
        .map { entities ->
            entities.map { entity ->
                Exercise(
                    id = entity.id,
                    name = entity.name,
                    muscleGroup = runCatching { MuscleGroup.valueOf(entity.muscleGroup) }.getOrDefault(MuscleGroup.CHEST),
                    equipment = runCatching { Equipment.valueOf(entity.equipment) }.getOrDefault(Equipment.BARBELL),
                    movementType = runCatching { MovementType.valueOf(entity.movementType) }.getOrDefault(MovementType.COMPOUND)
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

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
