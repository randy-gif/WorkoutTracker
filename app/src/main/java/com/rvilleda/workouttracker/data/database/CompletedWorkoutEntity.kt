package com.rvilleda.workouttracker.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "completed_workouts")
data class CompletedWorkoutEntity(
    @PrimaryKey val id: String, // Room uses this to ensure no duplicates
    val dateCompleted: Long,

    // We will convert your List<ActiveExercise> into a JSON string to save it easily
    val exercisesJson: String
)