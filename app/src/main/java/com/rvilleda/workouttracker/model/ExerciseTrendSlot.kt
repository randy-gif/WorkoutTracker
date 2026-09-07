package com.rvilleda.workouttracker.model

enum class ExerciseTrendSlot(val preferencePrefix: String, val defaultExerciseId: String) {
    BENCH("trend_exercise", "ex_chest_001"),
    SQUAT("trend_squat_exercise", "ex_legs_001"),
    DEADLIFT("trend_deadlift_exercise", "ex_back_001"),
    OVERHEAD_PRESS("trend_overhead_press_exercise", "ex_shld_001");

    val defaultExercise: Exercise
        get() = allDefaultExercises.first { it.id == defaultExerciseId }
}
