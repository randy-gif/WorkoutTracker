package com.rvilleda.workouttracker.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rvilleda.workouttracker.model.ExerciseInSession


@Entity(tableName = "active_workout_cache")
data class ActiveWorkoutEntity(
    @PrimaryKey val id: Int = 1,
    val routineId: String?,
    val startTime: Long,
    val exercises: List<ExerciseInSession>
)