package com.rvilleda.workouttracker.model
import java.util.UUID
enum class WeightUnit(val displayName: String) {
    LBS("Lbs"),
    KG("Kg")
}
data class ExerciseSet(
    val id: String = UUID.randomUUID().toString(),
    val weight: String = "",
    val reps: String = "",
    val isCompleted: Boolean = false,
    val weightUnit: WeightUnit = WeightUnit.LBS
)
