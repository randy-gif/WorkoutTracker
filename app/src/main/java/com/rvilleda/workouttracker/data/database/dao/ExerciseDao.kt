package com.rvilleda.workouttracker.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rvilleda.workouttracker.data.database.entity.exercise.CustomExerciseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {

    @Query("SELECT * FROM custom_exercises")
    fun getCustomExercises(): Flow<List<CustomExerciseEntity>>

    @Query("SELECT * FROM custom_exercises WHERE name = :name")
    fun getCustomExerciseByName(name: String): Flow<CustomExerciseEntity?>

    @Query("SELECT * FROM custom_exercises WHERE id = :id")
    fun getCustomExerciseById(id: String): Flow<CustomExerciseEntity?>

    @Query("SELECT * FROM custom_exercises WHERE equipment = :equipment")
    fun getExercisesByEquipment(equipment: String): Flow<List<CustomExerciseEntity>>

    @Query("SELECT * FROM custom_exercises WHERE fatigueTier = :fatigueTier")
    fun getExercisesByFatigueTier(fatigueTier: String): Flow<List<CustomExerciseEntity>>

    @Query("SELECT * FROM custom_exercises WHERE mechanics = :mechanics")
    fun getExercisesByMechanics(mechanics: String): Flow<List<CustomExerciseEntity>>

    @Query("SELECT * FROM custom_exercises WHERE resistanceCurve = :resistanceCurve")
    fun getExercisesByResistanceCurve(resistanceCurve: String): Flow<List<CustomExerciseEntity>>

    @Query("SELECT * FROM custom_exercises WHERE movementPattern = :movementPattern")
    fun getExercisesByMovementPattern(movementPattern: String): Flow<List<CustomExerciseEntity>>

    @Query("SELECT * FROM custom_exercises WHERE primaryMuscle = :muscle")
    fun getExercisesByPrimaryMuscle(muscle: String): Flow<List<CustomExerciseEntity>>

    @Query("SELECT * FROM custom_exercises WHERE secondaryMuscles LIKE '%' || :muscle || '%'")
    fun getExercisesBySecondaryMuscle(muscle: String): Flow<List<CustomExerciseEntity>>


    // 3. Save a user's new custom exercise
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertCustomExercise(exercise: CustomExerciseEntity)

    // 4. Batch insert for your first-launch pre-population
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAllExercises(exercises: List<CustomExerciseEntity>)

    // 5. Delete a custom exercise (You might want to prevent deleting default ones later!)
    @Query("DELETE FROM custom_exercises WHERE id = :exerciseId")
    suspend fun deleteExercise(exerciseId: String)
}