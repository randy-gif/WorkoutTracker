package com.rvilleda.workouttracker.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "custom_exercises")
data class CustomExerciseEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val muscleGroup: String,
    val equipment: String,
    val movementType: String
)