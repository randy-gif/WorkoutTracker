package com.rvilleda.workouttracker.model

import java.util.UUID

enum class MuscleGroup(val displayName: String) {
    CHEST("Chest"),
    BACK("Back"),
    ARMS("Arms"),
    LEGS("Legs"),
    SHOULDERS("Shoulders"),
    CORE("Core"),
    CARDIO("Cardio"),
    FULL_BODY("Full Body"),

}

enum class TargetMuscle(val displayName: String, val group: MuscleGroup) {
    UPPER_CHEST("Upper Chest", MuscleGroup.CHEST),
    MID_CHEST("Mid Chest", MuscleGroup.CHEST),
    LOWER_CHEST("Lower Chest", MuscleGroup.CHEST),

    // Back
    LATS("Lats", MuscleGroup.BACK),
    TRAPS("Traps", MuscleGroup.BACK),
    RHOMBOIDS("Rhomboids", MuscleGroup.BACK),
    LOWER_BACK("Lower Back", MuscleGroup.BACK),

    // Arms
    BICEPS("Biceps", MuscleGroup.ARMS),
    TRICEPS("Triceps", MuscleGroup.ARMS),
    FOREARMS("Forearms", MuscleGroup.ARMS),

    // Shoulders
    FRONT_DELT("Front Delt", MuscleGroup.SHOULDERS),
    SIDE_DELT("Side Delt", MuscleGroup.SHOULDERS),
    REAR_DELT("Rear Delt", MuscleGroup.SHOULDERS),

    // Legs
    QUADS("Quads", MuscleGroup.LEGS),
    HAMSTRINGS("Hamstrings", MuscleGroup.LEGS),
    GLUTES("Glutes", MuscleGroup.LEGS),
    CALVES("Calves", MuscleGroup.LEGS),
    HIP_FLEXORS("Hip Flexors", MuscleGroup.LEGS),

    // Core & Other
    ABS("Abs", MuscleGroup.CORE),
    OBLIQUES("Obliques", MuscleGroup.CORE),
    NECK("Neck", MuscleGroup.FULL_BODY),
    UNKNOWN("Unknown", MuscleGroup.FULL_BODY)
}

enum class MovementPattern {

    // --- PUSHING ---
    HORIZONTAL_PUSH,        // Bench Press, Push-up, Close Grip Bench
    VERTICAL_PUSH,          // Overhead Press, Shoulder Press

    // --- PULLING ---
    HORIZONTAL_PULL,        // Rows
    VERTICAL_PULL,          // Pull-ups, Pulldowns

    // --- CHEST / SHOULDER FLYING MOVEMENTS ---
    HORIZONTAL_ADDUCTION,   // Cable Fly, Pec Deck
    VERTICAL_ADDUCTION,     // Pullover variations

    // --- SHOULDER MOVEMENTS ---
    SHOULDER_ABDUCTION,     // Lateral Raise
    SHOULDER_FLEXION,       // Front Raise
    SHOULDER_EXTERNAL_ROTATION,
    SHOULDER_INTERNAL_ROTATION,

    // --- SCAPULA / UPPER BACK ---
    SCAPULAR_RETRACTION,    // Face Pulls, Rear Delt Fly
    SCAPULAR_ELEVATION,     // Shrugs
    SCAPULAR_DEPRESSION,    // Straight Arm Pulldown emphasis

    // --- ELBOW ---
    ELBOW_FLEXION,          // Bicep Curl
    ELBOW_EXTENSION,        // Tricep Pushdown

    // --- LOWER BODY ---
    SQUAT,                  // Squat, Leg Press
    HINGE,                  // Deadlift, RDL
    HIP_EXTENSION,          // Hip Thrust, Glute Bridge
    HIP_FLEXION,            // Hanging Leg Raise
    LUNGE,                  // Split Squat, Walking Lunge

    KNEE_EXTENSION,         // Leg Extension
    KNEE_FLEXION,           // Leg Curl

    CALF_RAISE,             // Standing/Seated Calf Raise

    // --- CORE ---
    TRUNK_FLEXION,          // Crunches
    TRUNK_EXTENSION,        // Back Extensions
    TRUNK_ROTATION,         // Russian Twist
    ANTI_EXTENSION,         // Plank, Ab Wheel
    ANTI_ROTATION,          // Pallof Press
    CORE,

    // --- CARRY / ATHLETIC ---
    CARRY,                  // Farmer Carry
    ROTATION,               // Cable Rotations
    LOCOMOTION,             // Walking, Sled Push

    // --- EXPLOSIVE ---
    PLYOMETRIC,             // Box Jump, Broad Jump

    // --- FOREARMS / NECK ---
    WRIST_FLEXION,
    WRIST_EXTENSION,
    NECK_FLEXION,
    NECK_EXTENSION,

    // --- FALLBACK ---
    ISOLATION
}

enum class FatigueTier {
    LOW,        // Little CNS impact (Bicep curls, Lateral raises)
    MEDIUM,     // Moderate impact (Cable rows, Leg extensions)
    HIGH,       // Heavy breathing, CNS load (Bench press, Pull-ups)
    EXTREME     // Massively taxing (Heavy Barbell Squats, Deadlifts)
}

enum class Mechanics {
    BILATERAL,  // Uses both limbs simultaneously (Barbell Bench)
    UNILATERAL  // Uses one limb independently (Dumbbell Row, Pistol Squat)
}

enum class ResistanceCurve {
    STRETCH,    // Hardest when muscle is stretched (Dumbbell Fly, RDL)
    MID,        // Hardest in the middle of the rep (Barbell Curl)
    CONTRACTED, // Hardest when muscle is squeezed (Hip Thrust, Cable Fly)
    CONSTANT    // Even tension throughout (Machines, Cables)
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
    val isUserCreated: Boolean = false,
    val primaryMuscle: TargetMuscle,
    val secondaryMuscles: List<TargetMuscle> = emptyList(),
    val equipment: Equipment,
    val gifUrl: String? = null,
    val instructions: List<String> = emptyList(),
    val movementPattern: MovementPattern,
    val fatigueTier: FatigueTier,
    val mechanics: Mechanics,
    val resistanceCurve: ResistanceCurve
)

val allDefaultExercises = listOf(
    // --- CHEST ---
    Exercise(
        id = "ex_chest_001",
        name = "Barbell Bench Press",
        primaryMuscle = TargetMuscle.MID_CHEST,
        secondaryMuscles = listOf(TargetMuscle.FRONT_DELT, TargetMuscle.TRICEPS),
        equipment = Equipment.BARBELL,
        movementPattern = MovementPattern.HORIZONTAL_PUSH,
        fatigueTier = FatigueTier.HIGH,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.MID
    ),

    Exercise(
        id = "ex_chest_002",
        name = "Incline Barbell Press",
        primaryMuscle = TargetMuscle.UPPER_CHEST,
        secondaryMuscles = listOf(TargetMuscle.FRONT_DELT, TargetMuscle.TRICEPS),
        equipment = Equipment.BARBELL,
        movementPattern = MovementPattern.HORIZONTAL_PUSH,
        fatigueTier = FatigueTier.HIGH,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.MID
    ),

    Exercise(
        id = "ex_chest_003",
        name = "Dumbbell Bench Press",
        primaryMuscle = TargetMuscle.MID_CHEST,
        secondaryMuscles = listOf(TargetMuscle.FRONT_DELT, TargetMuscle.TRICEPS),
        equipment = Equipment.DUMBBELL,
        movementPattern = MovementPattern.HORIZONTAL_PUSH,
        fatigueTier = FatigueTier.HIGH,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.MID
    ),

    Exercise(
        id = "ex_chest_004",
        name = "Incline Dumbbell Press",
        primaryMuscle = TargetMuscle.UPPER_CHEST,
        secondaryMuscles = listOf(TargetMuscle.FRONT_DELT, TargetMuscle.TRICEPS),
        equipment = Equipment.DUMBBELL,
        movementPattern = MovementPattern.HORIZONTAL_PUSH,
        fatigueTier = FatigueTier.HIGH,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.MID
    ),

    Exercise(
        id = "ex_chest_005",
        name = "Decline Barbell Press",
        primaryMuscle = TargetMuscle.LOWER_CHEST,
        secondaryMuscles = listOf(TargetMuscle.TRICEPS, TargetMuscle.FRONT_DELT),
        equipment = Equipment.BARBELL,
        movementPattern = MovementPattern.HORIZONTAL_PUSH,
        fatigueTier = FatigueTier.HIGH,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.MID
    ),

    Exercise(
        id = "ex_chest_006",
        name = "Cable Crossovers",
        primaryMuscle = TargetMuscle.MID_CHEST,
        secondaryMuscles = listOf(TargetMuscle.FRONT_DELT),
        equipment = Equipment.CABLE,
        movementPattern = MovementPattern.HORIZONTAL_PUSH,
        fatigueTier = FatigueTier.LOW,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.CONSTANT
    ),

    Exercise(
        id = "ex_chest_007",
        name = "Machine Chest Press",
        primaryMuscle = TargetMuscle.MID_CHEST,
        secondaryMuscles = listOf(TargetMuscle.FRONT_DELT, TargetMuscle.TRICEPS),
        equipment = Equipment.MACHINE,
        movementPattern = MovementPattern.HORIZONTAL_PUSH,
        fatigueTier = FatigueTier.MEDIUM,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.CONSTANT
    ),

    Exercise(
        id = "ex_chest_008",
        name = "Pec Deck Fly",
        primaryMuscle = TargetMuscle.MID_CHEST,
        secondaryMuscles = listOf(TargetMuscle.FRONT_DELT),
        equipment = Equipment.MACHINE,
        movementPattern = MovementPattern.HORIZONTAL_PUSH,
        fatigueTier = FatigueTier.LOW,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.CONTRACTED
    ),

    Exercise(
        id = "ex_chest_009",
        name = "Push-ups",
        primaryMuscle = TargetMuscle.MID_CHEST,
        secondaryMuscles = listOf(TargetMuscle.FRONT_DELT, TargetMuscle.TRICEPS, TargetMuscle.ABS),
        equipment = Equipment.BODYWEIGHT,
        movementPattern = MovementPattern.HORIZONTAL_PUSH,
        fatigueTier = FatigueTier.MEDIUM,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.MID
    ),

    Exercise(
        id = "ex_chest_010",
        name = "Dips (Chest Focus)",
        primaryMuscle = TargetMuscle.LOWER_CHEST,
        secondaryMuscles = listOf(TargetMuscle.TRICEPS, TargetMuscle.FRONT_DELT),
        equipment = Equipment.BODYWEIGHT,
        movementPattern = MovementPattern.HORIZONTAL_PUSH,
        fatigueTier = FatigueTier.HIGH,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.STRETCH
    ),

    // --- BACK ---
    Exercise(
        id = "ex_back_001",
        name = "Barbell Deadlift",
        primaryMuscle = TargetMuscle.LOWER_BACK,
        secondaryMuscles = listOf(
            TargetMuscle.GLUTES,
            TargetMuscle.HAMSTRINGS,
            TargetMuscle.TRAPS,
            TargetMuscle.ABS
        ),
        equipment = Equipment.BARBELL,
        movementPattern = MovementPattern.HINGE,
        fatigueTier = FatigueTier.EXTREME,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.STRETCH
    ),

    Exercise(
        id = "ex_back_002",
        name = "Pull-ups",
        primaryMuscle = TargetMuscle.LATS,
        secondaryMuscles = listOf(
            TargetMuscle.BICEPS,
            TargetMuscle.RHOMBOIDS
        ),
        equipment = Equipment.BODYWEIGHT,
        movementPattern = MovementPattern.VERTICAL_PULL,
        fatigueTier = FatigueTier.HIGH,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.STRETCH
    ),

    Exercise(
        id = "ex_back_003",
        name = "Lat Pulldown",
        primaryMuscle = TargetMuscle.LATS,
        secondaryMuscles = listOf(
            TargetMuscle.BICEPS,
            TargetMuscle.RHOMBOIDS
        ),
        equipment = Equipment.CABLE,
        movementPattern = MovementPattern.VERTICAL_PULL,
        fatigueTier = FatigueTier.MEDIUM,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.CONSTANT
    ),

    Exercise(
        id = "ex_back_004",
        name = "Barbell Bent-Over Row",
        primaryMuscle = TargetMuscle.RHOMBOIDS,
        secondaryMuscles = listOf(
            TargetMuscle.LATS,
            TargetMuscle.BICEPS,
            TargetMuscle.TRAPS
        ),
        equipment = Equipment.BARBELL,
        movementPattern = MovementPattern.HORIZONTAL_PULL,
        fatigueTier = FatigueTier.HIGH,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.MID
    ),

    Exercise(
        id = "ex_back_005",
        name = "Dumbbell Single-Arm Row",
        primaryMuscle = TargetMuscle.LATS,
        secondaryMuscles = listOf(
            TargetMuscle.RHOMBOIDS,
            TargetMuscle.BICEPS,
            TargetMuscle.ABS
        ),
        equipment = Equipment.DUMBBELL,
        movementPattern = MovementPattern.HORIZONTAL_PULL,
        fatigueTier = FatigueTier.MEDIUM,
        mechanics = Mechanics.UNILATERAL,
        resistanceCurve = ResistanceCurve.MID
    ),

    Exercise(
        id = "ex_back_006",
        name = "Seated Cable Row",
        primaryMuscle = TargetMuscle.RHOMBOIDS,
        secondaryMuscles = listOf(
            TargetMuscle.LATS,
            TargetMuscle.BICEPS
        ),
        equipment = Equipment.CABLE,
        movementPattern = MovementPattern.HORIZONTAL_PULL,
        fatigueTier = FatigueTier.MEDIUM,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.CONSTANT
    ),

    Exercise(
        id = "ex_back_007",
        name = "T-Bar Row",
        primaryMuscle = TargetMuscle.RHOMBOIDS,
        secondaryMuscles = listOf(
            TargetMuscle.LATS,
            TargetMuscle.BICEPS,
            TargetMuscle.TRAPS
        ),
        equipment = Equipment.BARBELL,
        movementPattern = MovementPattern.HORIZONTAL_PULL,
        fatigueTier = FatigueTier.HIGH,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.MID
    ),

    Exercise(
        id = "ex_back_008",
        name = "Straight-Arm Pulldown",
        primaryMuscle = TargetMuscle.LATS,
        secondaryMuscles = listOf(
            TargetMuscle.TRAPS
        ),
        equipment = Equipment.CABLE,
        movementPattern = MovementPattern.ISOLATION,
        fatigueTier = FatigueTier.LOW,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.CONSTANT
    ),

    Exercise(
        id = "ex_back_009",
        name = "Machine Row",
        primaryMuscle = TargetMuscle.RHOMBOIDS,
        secondaryMuscles = listOf(
            TargetMuscle.LATS,
            TargetMuscle.BICEPS
        ),
        equipment = Equipment.MACHINE,
        movementPattern = MovementPattern.HORIZONTAL_PULL,
        fatigueTier = FatigueTier.MEDIUM,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.CONSTANT
    ),

    Exercise(
        id = "ex_back_010",
        name = "Back Extensions",
        primaryMuscle = TargetMuscle.LOWER_BACK,
        secondaryMuscles = listOf(
            TargetMuscle.GLUTES,
            TargetMuscle.HAMSTRINGS
        ),
        equipment = Equipment.BODYWEIGHT,
        movementPattern = MovementPattern.HINGE,
        fatigueTier = FatigueTier.LOW,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.CONTRACTED
    ),

    // --- LEGS & GLUTES ---
    Exercise(
        id = "ex_legs_001",
        name = "Barbell Back Squat",
        primaryMuscle = TargetMuscle.QUADS,
        secondaryMuscles = listOf(
            TargetMuscle.GLUTES,
            TargetMuscle.HAMSTRINGS,
            TargetMuscle.ABS
        ),
        equipment = Equipment.BARBELL,
        movementPattern = MovementPattern.SQUAT,
        fatigueTier = FatigueTier.EXTREME,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.MID
    ),

    Exercise(
        id = "ex_legs_002",
        name = "Barbell Front Squat",
        primaryMuscle = TargetMuscle.QUADS,
        secondaryMuscles = listOf(
            TargetMuscle.GLUTES,
            TargetMuscle.ABS
        ),
        equipment = Equipment.BARBELL,
        movementPattern = MovementPattern.SQUAT,
        fatigueTier = FatigueTier.EXTREME,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.MID
    ),

    Exercise(
        id = "ex_legs_003",
        name = "Romanian Deadlift (RDL)",
        primaryMuscle = TargetMuscle.HAMSTRINGS,
        secondaryMuscles = listOf(
            TargetMuscle.GLUTES,
            TargetMuscle.LOWER_BACK
        ),
        equipment = Equipment.BARBELL,
        movementPattern = MovementPattern.HINGE,
        fatigueTier = FatigueTier.HIGH,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.STRETCH
    ),

    Exercise(
        id = "ex_legs_004",
        name = "Leg Press",
        primaryMuscle = TargetMuscle.QUADS,
        secondaryMuscles = listOf(
            TargetMuscle.GLUTES,
            TargetMuscle.HAMSTRINGS
        ),
        equipment = Equipment.MACHINE,
        movementPattern = MovementPattern.SQUAT,
        fatigueTier = FatigueTier.HIGH,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.CONSTANT
    ),

    Exercise(
        id = "ex_legs_005",
        name = "Bulgarian Split Squat",
        primaryMuscle = TargetMuscle.QUADS,
        secondaryMuscles = listOf(
            TargetMuscle.GLUTES,
            TargetMuscle.HAMSTRINGS,
            TargetMuscle.ABS
        ),
        equipment = Equipment.DUMBBELL,
        movementPattern = MovementPattern.LUNGE,
        fatigueTier = FatigueTier.HIGH,
        mechanics = Mechanics.UNILATERAL,
        resistanceCurve = ResistanceCurve.MID
    ),

    Exercise(
        id = "ex_legs_006",
        name = "Leg Extensions",
        primaryMuscle = TargetMuscle.QUADS,
        secondaryMuscles = emptyList(),
        equipment = Equipment.MACHINE,
        movementPattern = MovementPattern.ISOLATION,
        fatigueTier = FatigueTier.LOW,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.CONTRACTED
    ),

    Exercise(
        id = "ex_legs_007",
        name = "Seated Leg Curl",
        primaryMuscle = TargetMuscle.HAMSTRINGS,
        secondaryMuscles = emptyList(),
        equipment = Equipment.MACHINE,
        movementPattern = MovementPattern.ISOLATION,
        fatigueTier = FatigueTier.LOW,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.CONTRACTED
    ),

    Exercise(
        id = "ex_legs_008",
        name = "Lying Leg Curl",
        primaryMuscle = TargetMuscle.HAMSTRINGS,
        secondaryMuscles = emptyList(),
        equipment = Equipment.MACHINE,
        movementPattern = MovementPattern.ISOLATION,
        fatigueTier = FatigueTier.LOW,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.STRETCH
    ),

    Exercise(
        id = "ex_legs_009",
        name = "Standing Calf Raises",
        primaryMuscle = TargetMuscle.CALVES,
        secondaryMuscles = emptyList(),
        equipment = Equipment.MACHINE,
        movementPattern = MovementPattern.ISOLATION,
        fatigueTier = FatigueTier.LOW,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.STRETCH
    ),

    Exercise(
        id = "ex_legs_010",
        name = "Glute Bridge",
        primaryMuscle = TargetMuscle.GLUTES,
        secondaryMuscles = listOf(
            TargetMuscle.HAMSTRINGS
        ),
        equipment = Equipment.BODYWEIGHT,
        movementPattern = MovementPattern.HIP_EXTENSION,
        fatigueTier = FatigueTier.MEDIUM,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.CONTRACTED
    ),

    // --- SHOULDERS ---
    Exercise(
        id = "ex_shld_001",
        name = "Overhead Press (OHP)",
        primaryMuscle = TargetMuscle.FRONT_DELT,
        secondaryMuscles = listOf(
            TargetMuscle.TRICEPS,
            TargetMuscle.ABS
        ),
        equipment = Equipment.BARBELL,
        movementPattern = MovementPattern.VERTICAL_PUSH,
        fatigueTier = FatigueTier.HIGH,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.MID
    ),

    Exercise(
        id = "ex_shld_002",
        name = "Dumbbell Shoulder Press",
        primaryMuscle = TargetMuscle.FRONT_DELT,
        secondaryMuscles = listOf(
            TargetMuscle.TRICEPS,
            TargetMuscle.ABS
        ),
        equipment = Equipment.DUMBBELL,
        movementPattern = MovementPattern.VERTICAL_PUSH,
        fatigueTier = FatigueTier.HIGH,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.MID
    ),

    Exercise(
        id = "ex_shld_003",
        name = "Arnold Press",
        primaryMuscle = TargetMuscle.FRONT_DELT,
        secondaryMuscles = listOf(
            TargetMuscle.SIDE_DELT,
            TargetMuscle.TRICEPS
        ),
        equipment = Equipment.DUMBBELL,
        movementPattern = MovementPattern.VERTICAL_PUSH,
        fatigueTier = FatigueTier.HIGH,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.MID
    ),

    Exercise(
        id = "ex_shld_004",
        name = "Dumbbell Lateral Raise",
        primaryMuscle = TargetMuscle.SIDE_DELT,
        secondaryMuscles = emptyList(),
        equipment = Equipment.DUMBBELL,
        movementPattern = MovementPattern.ISOLATION,
        fatigueTier = FatigueTier.LOW,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.MID
    ),

    Exercise(
        id = "ex_shld_005",
        name = "Cable Lateral Raise",
        primaryMuscle = TargetMuscle.SIDE_DELT,
        secondaryMuscles = emptyList(),
        equipment = Equipment.CABLE,
        movementPattern = MovementPattern.ISOLATION,
        fatigueTier = FatigueTier.LOW,
        mechanics = Mechanics.UNILATERAL,
        resistanceCurve = ResistanceCurve.CONSTANT
    ),

    Exercise(
        id = "ex_shld_006",
        name = "Dumbbell Front Raise",
        primaryMuscle = TargetMuscle.FRONT_DELT,
        secondaryMuscles = listOf(
            TargetMuscle.UPPER_CHEST
        ),
        equipment = Equipment.DUMBBELL,
        movementPattern = MovementPattern.ISOLATION,
        fatigueTier = FatigueTier.LOW,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.MID
    ),

    Exercise(
        id = "ex_shld_007",
        name = "Reverse Pec Deck",
        primaryMuscle = TargetMuscle.REAR_DELT,
        secondaryMuscles = listOf(
            TargetMuscle.TRAPS,
            TargetMuscle.RHOMBOIDS
        ),
        equipment = Equipment.MACHINE,
        movementPattern = MovementPattern.HORIZONTAL_ADDUCTION,
        fatigueTier = FatigueTier.LOW,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.CONTRACTED
    ),

    Exercise(
        id = "ex_shld_008",
        name = "Face Pulls",
        primaryMuscle = TargetMuscle.REAR_DELT,
        secondaryMuscles = listOf(
            TargetMuscle.TRAPS,
            TargetMuscle.RHOMBOIDS
        ),
        equipment = Equipment.CABLE,
        movementPattern = MovementPattern.SCAPULAR_RETRACTION,
        fatigueTier = FatigueTier.LOW,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.CONSTANT
    ),

    Exercise(
        id = "ex_shld_009",
        name = "Smith Machine Press",
        primaryMuscle = TargetMuscle.FRONT_DELT,
        secondaryMuscles = listOf(
            TargetMuscle.TRICEPS
        ),
        equipment = Equipment.SMITH_MACHINE,
        movementPattern = MovementPattern.VERTICAL_PUSH,
        fatigueTier = FatigueTier.MEDIUM,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.CONSTANT
    ),

    Exercise(
        id = "ex_shld_010",
        name = "Barbell Shrugs",
        primaryMuscle = TargetMuscle.TRAPS,
        secondaryMuscles = emptyList(),
        equipment = Equipment.BARBELL,
        movementPattern = MovementPattern.SCAPULAR_RETRACTION,
        fatigueTier = FatigueTier.MEDIUM,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.CONTRACTED
    ),

    // --- ARMS (BICEPS & TRICEPS) ---
    Exercise(
        id = "ex_arms_001",
        name = "Barbell Bicep Curl",
        primaryMuscle = TargetMuscle.BICEPS,
        secondaryMuscles = listOf(
            TargetMuscle.FOREARMS
        ),
        equipment = Equipment.BARBELL,
        movementPattern = MovementPattern.ISOLATION,
        fatigueTier = FatigueTier.LOW,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.MID
    ),

    Exercise(
        id = "ex_arms_002",
        name = "Dumbbell Alternate Curl",
        primaryMuscle = TargetMuscle.BICEPS,
        secondaryMuscles = listOf(
            TargetMuscle.FOREARMS
        ),
        equipment = Equipment.DUMBBELL,
        movementPattern = MovementPattern.ISOLATION,
        fatigueTier = FatigueTier.LOW,
        mechanics = Mechanics.UNILATERAL,
        resistanceCurve = ResistanceCurve.MID
    ),

    Exercise(
        id = "ex_arms_003",
        name = "Hammer Curls",
        primaryMuscle = TargetMuscle.BICEPS,
        secondaryMuscles = listOf(
            TargetMuscle.FOREARMS
        ),
        equipment = Equipment.DUMBBELL,
        movementPattern = MovementPattern.ISOLATION,
        fatigueTier = FatigueTier.LOW,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.MID
    ),

    Exercise(
        id = "ex_arms_004",
        name = "Cable Rope Curls",
        primaryMuscle = TargetMuscle.BICEPS,
        secondaryMuscles = listOf(
            TargetMuscle.FOREARMS
        ),
        equipment = Equipment.CABLE,
        movementPattern = MovementPattern.ISOLATION,
        fatigueTier = FatigueTier.LOW,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.CONSTANT
    ),

    Exercise(
        id = "ex_arms_005",
        name = "Preacher Curl",
        primaryMuscle = TargetMuscle.BICEPS,
        secondaryMuscles = listOf(
            TargetMuscle.FOREARMS
        ),
        equipment = Equipment.MACHINE,
        movementPattern = MovementPattern.ISOLATION,
        fatigueTier = FatigueTier.LOW,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.STRETCH
    ),

    Exercise(
        id = "ex_arms_006",
        name = "Tricep Rope Pushdown",
        primaryMuscle = TargetMuscle.TRICEPS,
        secondaryMuscles = emptyList(),
        equipment = Equipment.CABLE,
        movementPattern = MovementPattern.ISOLATION,
        fatigueTier = FatigueTier.LOW,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.CONTRACTED
    ),

    Exercise(
        id = "ex_arms_007",
        name = "Skullcrushers (EZ Bar)",
        primaryMuscle = TargetMuscle.TRICEPS,
        secondaryMuscles = emptyList(),
        equipment = Equipment.BARBELL,
        movementPattern = MovementPattern.ISOLATION,
        fatigueTier = FatigueTier.MEDIUM,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.STRETCH
    ),

    Exercise(
        id = "ex_arms_008",
        name = "Overhead Tricep Extension",
        primaryMuscle = TargetMuscle.TRICEPS,
        secondaryMuscles = emptyList(),
        equipment = Equipment.DUMBBELL,
        movementPattern = MovementPattern.ISOLATION,
        fatigueTier = FatigueTier.LOW,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.STRETCH
    ),

    Exercise(
        id = "ex_arms_009",
        name = "Close-Grip Bench Press",
        primaryMuscle = TargetMuscle.TRICEPS,
        secondaryMuscles = listOf(
            TargetMuscle.MID_CHEST,
            TargetMuscle.FRONT_DELT
        ),
        equipment = Equipment.BARBELL,
        movementPattern = MovementPattern.HORIZONTAL_PUSH,
        fatigueTier = FatigueTier.HIGH,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.MID
    ),

    Exercise(
        id = "ex_arms_010",
        name = "Tricep Kickbacks",
        primaryMuscle = TargetMuscle.TRICEPS,
        secondaryMuscles = emptyList(),
        equipment = Equipment.DUMBBELL,
        movementPattern = MovementPattern.ISOLATION,
        fatigueTier = FatigueTier.LOW,
        mechanics = Mechanics.UNILATERAL,
        resistanceCurve = ResistanceCurve.CONTRACTED
    ),

    // --- CORE ---
    Exercise(
        id = "ex_core_001",
        name = "Cable Crunches",
        primaryMuscle = TargetMuscle.ABS,
        secondaryMuscles = emptyList(),
        equipment = Equipment.CABLE,
        movementPattern = MovementPattern.CORE,
        fatigueTier = FatigueTier.LOW,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.CONSTANT
    ),

    Exercise(
        id = "ex_core_002",
        name = "Hanging Leg Raises",
        primaryMuscle = TargetMuscle.ABS,
        secondaryMuscles = listOf(
            TargetMuscle.HIP_FLEXORS
        ),
        equipment = Equipment.BODYWEIGHT,
        movementPattern = MovementPattern.CORE,
        fatigueTier = FatigueTier.MEDIUM,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.STRETCH
    ),

    Exercise(
        id = "ex_core_003",
        name = "Ab Wheel Rollout",
        primaryMuscle = TargetMuscle.ABS,
        secondaryMuscles = listOf(
            TargetMuscle.LATS,
            TargetMuscle.FRONT_DELT
        ),
        equipment = Equipment.BODYWEIGHT,
        movementPattern = MovementPattern.CORE,
        fatigueTier = FatigueTier.HIGH,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.STRETCH
    ),

    Exercise(
        id = "ex_core_004",
        name = "Decline Crunches",
        primaryMuscle = TargetMuscle.ABS,
        secondaryMuscles = emptyList(),
        equipment = Equipment.BODYWEIGHT,
        movementPattern = MovementPattern.CORE,
        fatigueTier = FatigueTier.LOW,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.MID
    ),

    Exercise(
        id = "ex_core_005",
        name = "Russian Twists",
        primaryMuscle = TargetMuscle.OBLIQUES,
        secondaryMuscles = listOf(
            TargetMuscle.ABS
        ),
        equipment = Equipment.DUMBBELL,
        movementPattern = MovementPattern.CORE,
        fatigueTier = FatigueTier.LOW,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.MID
    ),

    Exercise(
        id = "ex_core_006",
        name = "Plank",
        primaryMuscle = TargetMuscle.ABS,
        secondaryMuscles = listOf(
            TargetMuscle.FRONT_DELT
        ),
        equipment = Equipment.BODYWEIGHT,
        movementPattern = MovementPattern.CORE,
        fatigueTier = FatigueTier.MEDIUM,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.CONSTANT
    ),

    Exercise(
        id = "ex_core_007",
        name = "Machine Crunches",
        primaryMuscle = TargetMuscle.ABS,
        secondaryMuscles = emptyList(),
        equipment = Equipment.MACHINE,
        movementPattern = MovementPattern.CORE,
        fatigueTier = FatigueTier.LOW,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.CONSTANT
    ),

    Exercise(
        id = "ex_core_008",
        name = "Bicycle Crunches",
        primaryMuscle = TargetMuscle.ABS,
        secondaryMuscles = listOf(
            TargetMuscle.OBLIQUES
        ),
        equipment = Equipment.BODYWEIGHT,
        movementPattern = MovementPattern.CORE,
        fatigueTier = FatigueTier.LOW,
        mechanics = Mechanics.UNILATERAL,
        resistanceCurve = ResistanceCurve.MID
    ),

    Exercise(
        id = "ex_core_009",
        name = "Dragon Flags",
        primaryMuscle = TargetMuscle.ABS,
        secondaryMuscles = listOf(
            TargetMuscle.HIP_FLEXORS
        ),
        equipment = Equipment.BODYWEIGHT,
        movementPattern = MovementPattern.CORE,
        fatigueTier = FatigueTier.HIGH,
        mechanics = Mechanics.BILATERAL,
        resistanceCurve = ResistanceCurve.STRETCH
    ),

    Exercise(
        id = "ex_core_010",
        name = "Woodchoppers",
        primaryMuscle = TargetMuscle.OBLIQUES,
        secondaryMuscles = listOf(
            TargetMuscle.ABS,
            TargetMuscle.FRONT_DELT
        ),
        equipment = Equipment.CABLE,
        movementPattern = MovementPattern.CORE,
        fatigueTier = FatigueTier.MEDIUM,
        mechanics = Mechanics.UNILATERAL,
        resistanceCurve = ResistanceCurve.CONSTANT
    )
)