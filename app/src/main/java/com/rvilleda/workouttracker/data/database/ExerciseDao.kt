package com.rvilleda.workouttracker.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {

    // 1. Get all exercises to display in your "Select Exercise" screen
    @Query("SELECT * FROM custom_exercises ORDER BY name ASC")
    fun getAllExercises(): Flow<List<CustomExerciseEntity>>

    // 2. Get exercises filtered by a specific muscle group
    @Query("SELECT * FROM custom_exercises WHERE muscleGroup = :muscle")
    fun getExercisesByMuscle(muscle: String): Flow<List<CustomExerciseEntity>>

    // 3. Save a user's new custom exercise
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomExercise(exercise: CustomExerciseEntity)

    // 4. Batch insert for your first-launch pre-population
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllExercises(exercises: List<CustomExerciseEntity>)

    // 5. Delete a custom exercise (You might want to prevent deleting default ones later!)
    @Query("DELETE FROM custom_exercises WHERE id = :exerciseId")
    suspend fun deleteExercise(exerciseId: String)
}