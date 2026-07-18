package com.rvilleda.workouttracker.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rvilleda.workouttracker.model.Equipment
import com.rvilleda.workouttracker.model.FatigueTier
import com.rvilleda.workouttracker.model.Mechanics
import com.rvilleda.workouttracker.model.MovementPattern
import com.rvilleda.workouttracker.model.ResistanceCurve
import com.rvilleda.workouttracker.model.TargetMuscle
import java.util.UUID

@Entity(tableName = "custom_exercises")
data class CustomExerciseEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val isUserCreated: Boolean = true,
    val primaryMuscle: TargetMuscle,
    val secondaryMuscles: List<TargetMuscle> = emptyList(),
    val equipment: Equipment,
    val gifUrl: String? = null,
    val instructions: List<String> = emptyList(),
    val movementPattern: MovementPattern,
    val fatigueTier: FatigueTier,
    val mechanics: Mechanics,
    val resistanceCurve: ResistanceCurve
)