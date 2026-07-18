package com.rvilleda.workouttracker.ui.screens.exercises

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rvilleda.workouttracker.data.database.entity.CustomExerciseEntity
import com.rvilleda.workouttracker.data.database.dao.ExerciseDao
import com.rvilleda.workouttracker.model.Equipment
import com.rvilleda.workouttracker.model.FatigueTier
import com.rvilleda.workouttracker.model.Mechanics
import com.rvilleda.workouttracker.model.MovementPattern
import com.rvilleda.workouttracker.model.TargetMuscle
import com.rvilleda.workouttracker.model.ResistanceCurve
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.String

class CreateCustomExerciseViewModel(
    private val exerciseDao: ExerciseDao
) : ViewModel() {

    // 1. Hold the form state here instead of the Composable
    val exerciseName = MutableStateFlow("")
    val selectedMuscle = MutableStateFlow<TargetMuscle?>(null)
    val selectedEquipment = MutableStateFlow<Equipment?>(null)
    val notes = MutableStateFlow("")

    // 2. Automatically compute if the Save button should be enabled
    val isSaveEnabled = combine(exerciseName, selectedMuscle, selectedEquipment) { name, muscle, equip ->
        name.isNotBlank() && muscle != null && equip != null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // 3. Update functions called by the UI
    fun updateName(name: String) { exerciseName.value = name }
    fun updateMuscle(muscle: TargetMuscle) { selectedMuscle.value = muscle }
    fun updateEquipment(equip: Equipment) { selectedEquipment.value = equip }
    fun updateNotes(newNotes: String) { notes.value = newNotes }

    // 4. Save to the database
    fun saveExercise(onSuccess: () -> Unit) {
        val name = exerciseName.value
        val muscle = selectedMuscle.value
        val equip = selectedEquipment.value

        if (name.isNotBlank() && muscle != null && equip != null) {
            viewModelScope.launch {
                val newExercise = CustomExerciseEntity(
                    name = name,
                    isUserCreated = true,
                    primaryMuscle = muscle,
                    secondaryMuscles =  emptyList(),
                    equipment = equip,
                    instructions = emptyList(),
                    movementPattern = MovementPattern.ISOLATION,
                    fatigueTier = FatigueTier.LOW,
                    mechanics = Mechanics.BILATERAL,
                    resistanceCurve = ResistanceCurve.CONSTANT,
                )
                exerciseDao.insertCustomExercise(newExercise)
                onSuccess() // Navigate back after saving!
            }
        }
    }
}