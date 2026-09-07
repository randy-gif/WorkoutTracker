package com.rvilleda.workouttracker.data.repository

import com.rvilleda.workouttracker.data.database.entity.*
import com.rvilleda.workouttracker.model.WeightUnit
import java.time.ZoneId
import org.junit.Assert.*
import org.junit.Test

class CoachWorkoutContextTest {
    @Test fun includesOnlyCompletedSetsAndConvertsUnits() {
        val workout = FullWorkout(
            CompletedWorkoutEntity("private-id", "Push", 0, 60_000),
            listOf(ExerciseWithSets(
                WorkoutExerciseEntity("exercise-id", "private-id", "base-id", "Bench press", 0),
                listOf(
                    WorkoutSetEntity("set-id", "exercise-id", 1, 100f, WeightUnit.LBS, 5, null, true),
                    WorkoutSetEntity("unfinished", "exercise-id", 2, 999f, WeightUnit.LBS, 9, 10f, false)
                )
            ))
        )
        val context = CoachWorkoutContext.build(listOf(workout), WeightUnit.KG, 120_000, ZoneId.of("UTC"))
        assertTrue(context.contains("5 reps x 45.36 KG"))
        assertTrue(context.contains("RPE unknown"))
        assertFalse(context.contains("999"))
        assertFalse(context.contains("private-id"))
    }

    @Test fun emptyHistoryIsExplicit() {
        assertTrue(CoachWorkoutContext.build(emptyList(), WeightUnit.LBS).contains("No completed workouts recorded"))
    }
}
