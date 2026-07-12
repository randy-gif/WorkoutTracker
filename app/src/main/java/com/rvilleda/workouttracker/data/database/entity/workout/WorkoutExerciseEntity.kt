package com.rvilleda.workouttracker.data.database.entity.workout

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "workout_exercises",
    foreignKeys = [
        ForeignKey(
            entity = CompletedWorkoutEntity::class,
            parentColumns = ["id"],
            childColumns = ["workoutId"],
            onDelete = ForeignKey.Companion.CASCADE
        )
    ],
    indices = [Index("workoutId")]
)
data class WorkoutExerciseEntity(
    @PrimaryKey val id: String,
    val workoutId: String,
    val baseExerciseId: String,
    val exerciseName: String,
    val orderInWorkout: Int
)