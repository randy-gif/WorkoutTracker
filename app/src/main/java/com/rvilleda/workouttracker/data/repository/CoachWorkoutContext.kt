package com.rvilleda.workouttracker.data.repository

import com.rvilleda.workouttracker.data.database.entity.FullWorkout
import com.rvilleda.workouttracker.model.WeightUnit
import java.time.Instant
import java.time.ZoneId
import java.util.Locale

/** Bounded, factual context for the coach; excludes unfinished sets and database identifiers. */
object CoachWorkoutContext {
    fun build(
        workouts: List<FullWorkout>,
        unit: WeightUnit,
        now: Long = System.currentTimeMillis(),
        zone: ZoneId = ZoneId.systemDefault()
    ): String = buildString {
        val completed = workouts.filter { it.workout.dateCompleted <= now }
            .sortedByDescending { it.workout.dateCompleted }
        val recent = completed.take(30)
        appendLine("Today: ${Instant.ofEpochMilli(now).atZone(zone).toLocalDate()}. Time zone: $zone.")
        appendLine("Preferred weight unit: ${unit.name}. Total recorded workouts: ${completed.size}.")
        appendLine("Details cover the latest ${recent.size} workouts only. Older sessions may be omitted.")
        appendLine("Only completed sets are included. Missing RPE means unknown, not zero.")
        if (recent.isEmpty()) appendLine("No completed workouts recorded. Do not infer training history.")
        recent.forEach { full ->
            val date = Instant.ofEpochMilli(full.workout.startTime).atZone(zone).toLocalDate()
            val minutes = (full.workout.dateCompleted - full.workout.startTime).coerceAtLeast(0) / 60_000
            appendLine("Workout: $date, ${full.workout.name.take(100)}, duration $minutes minutes.")
            full.exercises.sortedBy { it.exercise.orderInWorkout }.take(15).forEach { exercise ->
                appendLine("Exercise: ${exercise.exercise.exerciseName.take(100)}")
                exercise.sets.filter { it.isCompleted }.sortedBy { it.setNumber }.take(12).forEach { set ->
                    val weight = when {
                        set.weightUnit == unit -> set.weight.toDouble()
                        unit == WeightUnit.KG -> set.weight / 2.20462
                        else -> set.weight * 2.20462
                    }
                    appendLine("  ${set.reps} reps x ${String.format(Locale.US, "%.2f", weight)} ${unit.name}; RPE ${set.rpe ?: "unknown"}")
                }
            }
        }
        appendLine("Limits: at most 15 exercises per workout and 12 completed sets per exercise shown.")
    }
}
