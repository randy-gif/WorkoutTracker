package com.rvilleda.workouttracker.model

import kotlin.math.roundToInt

// 1. Calculate volume for a SINGLE exercise
fun ExerciseInSession.calculateVolume(targetUnit: WeightUnit = WeightUnit.LBS): Float {
    var totalVolume = 0f

    for (set in sets) {
        // Only count sets that were actually finished!
        if (set.isCompleted) {
            val weight = set.weight.toFloatOrNull() ?: 0f
            val reps = set.reps.toFloatOrNull() ?: 0f

            // Normalize the weight to match the target unit
            val normalizedWeight = when {
                set.weightUnit == WeightUnit.LBS && targetUnit == WeightUnit.KG -> weight / 2.20462f
                set.weightUnit == WeightUnit.KG && targetUnit == WeightUnit.LBS -> weight * 2.20462f
                else -> weight // Units already match
            }

            totalVolume += (normalizedWeight * reps)
        }
    }

    return totalVolume
}

// 2. Calculate volume for an ENTIRE workout session
fun List<ExerciseInSession>.calculateTotalWorkoutVolume(targetUnit: WeightUnit = WeightUnit.LBS): Float {
    val rawVolume = this.sumOf { exercise ->
        exercise.calculateVolume(targetUnit).toDouble()
    }.toFloat()

    // Round to 1 decimal place for clean UI display (e.g., 10500.5)
    return (rawVolume * 10f).roundToInt() / 10f
}