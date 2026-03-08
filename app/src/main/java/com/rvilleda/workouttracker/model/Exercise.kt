package com.rvilleda.workouttracker.model

data class Exercise(
    val name: String,
    val muscleGroup: String,
    val sets: Int,
    val reps: Int,
    val lastWeight: String = "0 lbs"
)
