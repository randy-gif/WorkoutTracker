package com.rvilleda.workouttracker.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "completed_workouts")
data class CompletedWorkoutEntity(
    @PrimaryKey val id: String,
    val dateCompleted: Long,
    val durationMs: Long,
    val exercisesJson: String
)