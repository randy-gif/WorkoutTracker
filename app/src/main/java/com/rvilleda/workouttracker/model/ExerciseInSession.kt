package com.rvilleda.workouttracker.model
import java.util.UUID
import com.rvilleda.workouttracker.model.ExerciseSet


data class ExerciseInSession(
    val id: String = UUID.randomUUID().toString(),
    val baseExerciseId: String, // Links back to your master database of exercises
    val exerciseName: String,
    val sets: List<ExerciseSet> = emptyList(),
    val autoRestEnabled: Boolean = true,
    val restTimeSeconds: Int = 90
)
