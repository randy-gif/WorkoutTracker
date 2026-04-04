package com.rvilleda.workouttracker.model
import java.util.UUID

data class ExerciseSet(
    val id: String = UUID.randomUUID().toString(),
    val weight: String = "",
    val reps: String = ""
)
