package com.rvilleda.workouttracker.data.repository

import com.rvilleda.workouttracker.data.database.entity.*
import com.rvilleda.workouttracker.model.*
import org.junit.Assert.*
import org.junit.Test

class CoachPolicyTest {
    @Test fun recommendationScalesWithMemory() {
        assertNull(CoachDevice(35, true, 3_000_000_000, false).recommended)
        assertEquals(CoachModel.STANDARD, CoachDevice(35, true, 4_000_000_000, false).recommended)
        assertEquals(CoachModel.ENHANCED, CoachDevice(35, true, 8_000_000_000, false).recommended)
        assertNull(CoachDevice(35, false, 8_000_000_000, false).recommended)
        assertNull(CoachDevice(24, true, 8_000_000_000, false).recommended)
        assertNull(CoachDevice(35, true, 8_000_000_000, true).recommended)
    }

    @Test fun summaryExcludesFutureAndUnfinishedSetsAndConvertsUnits() {
        val past = FullWorkout(CompletedWorkoutEntity("one", "Workout", 0, 60_000), listOf(
            ExerciseWithSets(WorkoutExerciseEntity("e", "one", "base", "Squat", 0), listOf(
                WorkoutSetEntity("s", "e", 1, 100f, WeightUnit.LBS, 5, null, true),
                WorkoutSetEntity("u", "e", 2, 1000f, WeightUnit.LBS, 10, null, false)
            ))
        ))
        val future = past.copy(workout = past.workout.copy(dateCompleted = 999_999))
        val result = CoachFacts.summary(listOf(past, future), WeightUnit.KG, 120_000)
        assertTrue(result.contains("1 recorded workouts"))
        assertTrue(result.contains("1 completed sets, 5 reps"))
        assertTrue(result.contains("227 KG"))
    }

    @Test fun promptRemainsBoundedAndContainsCurrentQuestion() {
        val prompt = CoachFacts.prompt(emptyList(), WeightUnit.KG, "How is my squat?", "old chat".repeat(2000))
        assertTrue(prompt.toByteArray().size < 3000)
        assertTrue(prompt.contains("How is my squat?"))
        assertTrue(prompt.contains("0 recorded workouts"))
    }

    @Test fun hiddenThinkingIsNotShownEvenDuringStreaming() {
        assertEquals("", LocalCoachEngine.visibleAnswer("<think>unfinished"))
        assertEquals("Try this", LocalCoachEngine.visibleAnswer("<think>private</think>Try this"))
    }
}
