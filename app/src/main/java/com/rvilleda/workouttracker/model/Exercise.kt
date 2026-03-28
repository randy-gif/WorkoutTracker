package com.rvilleda.workouttracker.model

import java.util.UUID
enum class MuscleGroup(val displayName: String) {
    CHEST("Chest"),
    BACK("Back"),
    LEGS("Legs"),
    SHOULDERS("Shoulders"),
    ARMS("Arms"),
    CORE("Core")
}

enum class Equipment(val displayName: String) {
    BARBELL("Barbell"),
    DUMBBELL("Dumbbell"),
    MACHINE("Machine"),
    CABLE("Cable"),
    BODYWEIGHT("Bodyweight"),
    SMITH_MACHINE("Smith Machine")
}

enum class MovementType(val displayName: String) {
    COMPOUND("Compound"), // Multi-joint (e.g., Squat, Bench Press)
    ISOLATION("Isolation") // Single-joint (e.g., Bicep Curl)
}
data class Exercise(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val muscleGroup: MuscleGroup,
    val equipment: Equipment,
    val movementType: MovementType
)

val allExercises = listOf(
    // --- CHEST ---
    Exercise("ex_chest_001", "Barbell Bench Press", MuscleGroup.CHEST, Equipment.BARBELL, MovementType.COMPOUND),
    Exercise("ex_chest_002", "Incline Barbell Press", MuscleGroup.CHEST, Equipment.BARBELL, MovementType.COMPOUND),
    Exercise("ex_chest_003", "Dumbbell Bench Press", MuscleGroup.CHEST, Equipment.DUMBBELL, MovementType.COMPOUND),
    Exercise("ex_chest_004", "Incline Dumbbell Press", MuscleGroup.CHEST, Equipment.DUMBBELL, MovementType.COMPOUND),
    Exercise("ex_chest_005", "Decline Barbell Press", MuscleGroup.CHEST, Equipment.BARBELL, MovementType.COMPOUND),
    Exercise("ex_chest_006", "Cable Crossovers", MuscleGroup.CHEST, Equipment.CABLE, MovementType.ISOLATION),
    Exercise("ex_chest_007", "Machine Chest Press", MuscleGroup.CHEST, Equipment.MACHINE, MovementType.COMPOUND),
    Exercise("ex_chest_008", "Pec Deck Fly", MuscleGroup.CHEST, Equipment.MACHINE, MovementType.ISOLATION),
    Exercise("ex_chest_009", "Push-ups", MuscleGroup.CHEST, Equipment.BODYWEIGHT, MovementType.COMPOUND),
    Exercise("ex_chest_010", "Dips (Chest Focus)", MuscleGroup.CHEST, Equipment.BODYWEIGHT, MovementType.COMPOUND),

    // --- BACK ---
    Exercise("ex_back_001", "Barbell Deadlift", MuscleGroup.BACK, Equipment.BARBELL, MovementType.COMPOUND),
    Exercise("ex_back_002", "Pull-ups", MuscleGroup.BACK, Equipment.BODYWEIGHT, MovementType.COMPOUND),
    Exercise("ex_back_003", "Lat Pulldown", MuscleGroup.BACK, Equipment.CABLE, MovementType.COMPOUND),
    Exercise("ex_back_004", "Barbell Bent-Over Row", MuscleGroup.BACK, Equipment.BARBELL, MovementType.COMPOUND),
    Exercise("ex_back_005", "Dumbbell Single-Arm Row", MuscleGroup.BACK, Equipment.DUMBBELL, MovementType.COMPOUND),
    Exercise("ex_back_006", "Seated Cable Row", MuscleGroup.BACK, Equipment.CABLE, MovementType.COMPOUND),
    Exercise("ex_back_007", "T-Bar Row", MuscleGroup.BACK, Equipment.BARBELL, MovementType.COMPOUND),
    Exercise("ex_back_008", "Straight-Arm Pulldown", MuscleGroup.BACK, Equipment.CABLE, MovementType.ISOLATION),
    Exercise("ex_back_009", "Machine Row", MuscleGroup.BACK, Equipment.MACHINE, MovementType.COMPOUND),
    Exercise("ex_back_010", "Back Extensions", MuscleGroup.BACK, Equipment.BODYWEIGHT, MovementType.ISOLATION),

    // --- LEGS & GLUTES ---
    Exercise("ex_legs_001", "Barbell Back Squat", MuscleGroup.LEGS, Equipment.BARBELL, MovementType.COMPOUND),
    Exercise("ex_legs_002", "Barbell Front Squat", MuscleGroup.LEGS, Equipment.BARBELL, MovementType.COMPOUND),
    Exercise("ex_legs_003", "Romanian Deadlift (RDL)", MuscleGroup.LEGS, Equipment.BARBELL, MovementType.COMPOUND),
    Exercise("ex_legs_004", "Leg Press", MuscleGroup.LEGS, Equipment.MACHINE, MovementType.COMPOUND),
    Exercise("ex_legs_005", "Bulgarian Split Squat", MuscleGroup.LEGS, Equipment.DUMBBELL, MovementType.COMPOUND),
    Exercise("ex_legs_006", "Leg Extensions", MuscleGroup.LEGS, Equipment.MACHINE, MovementType.ISOLATION),
    Exercise("ex_legs_007", "Seated Leg Curl", MuscleGroup.LEGS, Equipment.MACHINE, MovementType.ISOLATION),
    Exercise("ex_legs_008", "Lying Leg Curl", MuscleGroup.LEGS, Equipment.MACHINE, MovementType.ISOLATION),
    Exercise("ex_legs_009", "Standing Calf Raises", MuscleGroup.LEGS, Equipment.MACHINE, MovementType.ISOLATION),
    Exercise("ex_legs_010", "Glute Bridge", MuscleGroup.LEGS, Equipment.BODYWEIGHT, MovementType.ISOLATION),

    // --- SHOULDERS ---
    Exercise("ex_shld_001", "Overhead Press (OHP)", MuscleGroup.SHOULDERS, Equipment.BARBELL, MovementType.COMPOUND),
    Exercise("ex_shld_002", "Dumbbell Shoulder Press", MuscleGroup.SHOULDERS, Equipment.DUMBBELL, MovementType.COMPOUND),
    Exercise("ex_shld_003", "Arnold Press", MuscleGroup.SHOULDERS, Equipment.DUMBBELL, MovementType.COMPOUND),
    Exercise("ex_shld_004", "Dumbbell Lateral Raise", MuscleGroup.SHOULDERS, Equipment.DUMBBELL, MovementType.ISOLATION),
    Exercise("ex_shld_005", "Cable Lateral Raise", MuscleGroup.SHOULDERS, Equipment.CABLE, MovementType.ISOLATION),
    Exercise("ex_shld_006", "Dumbbell Front Raise", MuscleGroup.SHOULDERS, Equipment.DUMBBELL, MovementType.ISOLATION),
    Exercise("ex_shld_007", "Reverse Pec Deck", MuscleGroup.SHOULDERS, Equipment.MACHINE, MovementType.ISOLATION),
    Exercise("ex_shld_008", "Face Pulls", MuscleGroup.SHOULDERS, Equipment.CABLE, MovementType.ISOLATION),
    Exercise("ex_shld_009", "Smith Machine Press", MuscleGroup.SHOULDERS, Equipment.SMITH_MACHINE, MovementType.COMPOUND),
    Exercise("ex_shld_010", "Barbell Shrugs", MuscleGroup.SHOULDERS, Equipment.BARBELL, MovementType.ISOLATION),

    // --- ARMS (BICEPS & TRICEPS) ---
    Exercise("ex_arms_001", "Barbell Bicep Curl", MuscleGroup.ARMS, Equipment.BARBELL, MovementType.ISOLATION),
    Exercise("ex_arms_002", "Dumbbell Alternate Curl", MuscleGroup.ARMS, Equipment.DUMBBELL, MovementType.ISOLATION),
    Exercise("ex_arms_003", "Hammer Curls", MuscleGroup.ARMS, Equipment.DUMBBELL, MovementType.ISOLATION),
    Exercise("ex_arms_004", "Cable Rope Curls", MuscleGroup.ARMS, Equipment.CABLE, MovementType.ISOLATION),
    Exercise("ex_arms_005", "Preacher Curl", MuscleGroup.ARMS, Equipment.MACHINE, MovementType.ISOLATION),
    Exercise("ex_arms_006", "Tricep Rope Pushdown", MuscleGroup.ARMS, Equipment.CABLE, MovementType.ISOLATION),
    Exercise("ex_arms_007", "Skullcrushers (EZ Bar)", MuscleGroup.ARMS, Equipment.BARBELL, MovementType.ISOLATION),
    Exercise("ex_arms_008", "Overhead Tricep Extension", MuscleGroup.ARMS, Equipment.DUMBBELL, MovementType.ISOLATION),
    Exercise("ex_arms_009", "Close-Grip Bench Press", MuscleGroup.ARMS, Equipment.BARBELL, MovementType.COMPOUND),
    Exercise("ex_arms_010", "Tricep Kickbacks", MuscleGroup.ARMS, Equipment.DUMBBELL, MovementType.ISOLATION),

    // --- CORE ---
    Exercise("ex_core_001", "Cable Crunches", MuscleGroup.CORE, Equipment.CABLE, MovementType.ISOLATION),
    Exercise("ex_core_002", "Hanging Leg Raises", MuscleGroup.CORE, Equipment.BODYWEIGHT, MovementType.ISOLATION),
    Exercise("ex_core_003", "Ab Wheel Rollout", MuscleGroup.CORE, Equipment.BODYWEIGHT, MovementType.COMPOUND),
    Exercise("ex_core_004", "Decline Crunches", MuscleGroup.CORE, Equipment.BODYWEIGHT, MovementType.ISOLATION),
    Exercise("ex_core_005", "Russian Twists", MuscleGroup.CORE, Equipment.DUMBBELL, MovementType.ISOLATION),
    Exercise("ex_core_006", "Plank", MuscleGroup.CORE, Equipment.BODYWEIGHT, MovementType.ISOLATION),
    Exercise("ex_core_007", "Machine Crunches", MuscleGroup.CORE, Equipment.MACHINE, MovementType.ISOLATION),
    Exercise("ex_core_008", "Bicycle Crunches", MuscleGroup.CORE, Equipment.BODYWEIGHT, MovementType.ISOLATION),
    Exercise("ex_core_009", "Dragon Flags", MuscleGroup.CORE, Equipment.BODYWEIGHT, MovementType.COMPOUND),
    Exercise("ex_core_010", "Woodchoppers", MuscleGroup.CORE, Equipment.CABLE, MovementType.COMPOUND)
)