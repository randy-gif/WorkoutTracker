package com.rvilleda.workouttracker.model
import java.util.UUID
import com.rvilleda.workouttracker.model.ExerciseInSession


data class ExerciseSession(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "My Workout", // Optional: Let users name their sessions
    val exercises: List<ExerciseInSession> = emptyList(),
    val dateTimestamp: Long = System.currentTimeMillis()
)
