package com.rvilleda.workouttracker.model

data class Exercise(
    val name: String,
    val muscleGroup: String,
    val sets: Int,
    val reps: Int,
    val lastWeight: String = "0 lbs"
)

val allExercises = listOf(
    // --- CHEST (15) ---
    Exercise("Bench Press (Barbell)", "Chest", 3, 10),
    Exercise("Incline Bench Press (Barbell)", "Chest", 3, 10),
    Exercise("Decline Bench Press (Barbell)", "Chest", 3, 10),
    Exercise("Dumbbell Chest Press", "Chest", 3, 12),
    Exercise("Incline Dumbbell Press", "Chest", 3, 12),
    Exercise("Decline Dumbbell Press", "Chest", 3, 12),
    Exercise("Chest Fly (Dumbbell)", "Chest", 3, 15),
    Exercise("Cable Crossover", "Chest", 3, 15),
    Exercise("Pec Deck Machine", "Chest", 3, 12),
    Exercise("Pushups", "Chest", 3, 20),
    Exercise("Diamond Pushups", "Chest", 3, 15),
    Exercise("Dips (Chest focus)", "Chest", 3, 10),
    Exercise("Landmine Press", "Chest", 3, 12),
    Exercise("Floor Press", "Chest", 3, 10),
    Exercise("Svend Press", "Chest", 3, 15),

    // --- BACK (15) ---
    Exercise("Deadlift (Conventional)", "Back", 3, 5),
    Exercise("Pull Ups", "Back", 3, 10),
    Exercise("Chin Ups", "Back", 3, 10),
    Exercise("Lat Pulldown (Wide Grip)", "Back", 3, 12),
    Exercise("Bent Over Row (Barbell)", "Back", 3, 10),
    Exercise("One-Arm Dumbbell Row", "Back", 3, 12),
    Exercise("Seated Cable Row", "Back", 3, 12),
    Exercise("T-Bar Row", "Back", 3, 10),
    Exercise("Face Pulls", "Back", 3, 15),
    Exercise("Back Extensions", "Back", 3, 15),
    Exercise("Good Mornings", "Back", 3, 10),
    Exercise("Reverse Fly (Dumbbell)", "Back", 3, 15),
    Exercise("Lat Pulldown (Underhand)", "Back", 3, 12),
    Exercise("Straight Arm Pulldown", "Back", 3, 15),
    Exercise("Rack Pulls", "Back", 3, 5),

    // --- LEGS (20) ---
    Exercise("Back Squat (Barbell)", "Legs", 3, 8),
    Exercise("Front Squat (Barbell)", "Legs", 3, 8),
    Exercise("Leg Press", "Legs", 3, 12),
    Exercise("Walking Lunges", "Legs", 3, 20),
    Exercise("Bulgarian Split Squat", "Legs", 3, 10),
    Exercise("Goblet Squat", "Legs", 3, 12),
    Exercise("Romanian Deadlift", "Legs", 3, 10),
    Exercise("Leg Extensions", "Legs", 3, 15),
    Exercise("Leg Curls (Lying)", "Legs", 3, 15),
    Exercise("Leg Curls (Seated)", "Legs", 3, 15),
    Exercise("Hack Squat Machine", "Legs", 3, 10),
    Exercise("Calf Raises (Standing)", "Legs", 3, 20),
    Exercise("Calf Raises (Seated)", "Legs", 3, 20),
    Exercise("Hip Thrusts", "Legs", 3, 10),
    Exercise("Glute Bridges", "Legs", 3, 15),
    Exercise("Sumo Squat", "Legs", 3, 12),
    Exercise("Box Squat", "Legs", 3, 8),
    Exercise("Step Ups", "Legs", 3, 12),
    Exercise("Pistol Squat", "Legs", 3, 5),
    Exercise("Glute Ham Raise", "Legs", 3, 10),

    // --- SHOULDERS (15) ---
    Exercise("Overhead Press (Barbell)", "Shoulders", 3, 8),
    Exercise("Dumbbell Shoulder Press", "Shoulders", 3, 10),
    Exercise("Arnold Press", "Shoulders", 3, 10),
    Exercise("Lateral Raises (Dumbbell)", "Shoulders", 3, 15),
    Exercise("Front Raises (Dumbbell)", "Shoulders", 3, 15),
    Exercise("Upright Row (Barbell)", "Shoulders", 3, 12),
    Exercise("Military Press (Seated)", "Shoulders", 3, 8),
    Exercise("Rear Delt Fly (Machine)", "Shoulders", 3, 15),
    Exercise("Push Press", "Shoulders", 3, 5),
    Exercise("Handstand Pushups", "Shoulders", 3, 8),
    Exercise("Face Pulls (Cable)", "Shoulders", 3, 15),
    Exercise("Clean and Press", "Shoulders", 3, 5),
    Exercise("Bradford Press", "Shoulders", 3, 12),
    Exercise("Single-Arm Landmine Press", "Shoulders", 3, 12),
    Exercise("Battle Ropes", "Shoulders", 3, 30), // 30 sec

    // --- ARMS (20) ---
    Exercise("Bicep Curl (Barbell)", "Arms", 3, 12),
    Exercise("Dumbbell Curl", "Arms", 3, 12),
    Exercise("Hammer Curl", "Arms", 3, 12),
    Exercise("Preacher Curl", "Arms", 3, 10),
    Exercise("Concentration Curl", "Arms", 3, 12),
    Exercise("21s (Bicep Curls)", "Arms", 3, 21),
    Exercise("Tricep Pushdown (Cable)", "Arms", 3, 15),
    Exercise("Skull Crushers", "Arms", 3, 12),
    Exercise("Overhead Tricep Extension", "Arms", 3, 12),
    Exercise("Tricep Dips", "Arms", 3, 12),
    Exercise("Close Grip Bench Press", "Arms", 3, 8),
    Exercise("Rope Tricep Extension", "Arms", 3, 15),
    Exercise("Incline Dumbbell Curl", "Arms", 3, 12),
    Exercise("Spider Curls", "Arms", 3, 12),
    Exercise("Reverse Curl (EZ Bar)", "Arms", 3, 12),
    Exercise("Cable Bicep Curl", "Arms", 3, 15),
    Exercise("Tricep Kickbacks", "Arms", 3, 15),
    Exercise("Bench Dips", "Arms", 3, 15),
    Exercise("Forearm Wrist Curls", "Arms", 3, 20),
    Exercise("Zottman Curls", "Arms", 3, 12),

    // --- CORE & CARDIO (15) ---
    Exercise("Plank", "Core", 3, 60), // 60 sec
    Exercise("Crunches", "Core", 3, 20),
    Exercise("Leg Raises", "Core", 3, 15),
    Exercise("Russian Twists", "Core", 3, 30),
    Exercise("Hanging Leg Raise", "Core", 3, 10),
    Exercise("Bicycle Crunches", "Core", 3, 20),
    Exercise("Ab Wheel Rollout", "Core", 3, 10),
    Exercise("Mountain Climbers", "Core", 3, 30),
    Exercise("Burpees", "Cardio", 3, 15),
    Exercise("Box Jumps", "Cardio", 3, 10),
    Exercise("Kettlebell Swing", "Cardio", 3, 15),
    Exercise("Jump Rope", "Cardio", 3, 60), // 60 sec
    Exercise("Medicine Ball Slam", "Cardio", 3, 12),
    Exercise("Bear Crawl", "Core", 3, 30), // 30 sec
    Exercise("Dead Bug", "Core", 3, 12)
)