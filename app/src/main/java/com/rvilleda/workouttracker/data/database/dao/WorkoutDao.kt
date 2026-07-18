package com.rvilleda.workouttracker.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.rvilleda.workouttracker.data.database.entity.ActiveWorkoutEntity
import com.rvilleda.workouttracker.data.database.entity.CompletedWorkoutEntity
import com.rvilleda.workouttracker.data.database.entity.FullWorkout
import com.rvilleda.workouttracker.data.database.entity.WorkoutExerciseEntity
import com.rvilleda.workouttracker.data.database.entity.WorkoutSetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Insert
    suspend fun insertWorkout(workout: CompletedWorkoutEntity)

    @Insert
    suspend fun insertWorkoutExercises(exercises: List<WorkoutExerciseEntity>)

    @Insert
    suspend fun insertWorkoutSets(sets: List<WorkoutSetEntity>)

    // --- Active Workout Cache ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveActiveWorkoutCache(cache: ActiveWorkoutEntity)

    @Query("SELECT * FROM active_workout_cache WHERE id = 1 LIMIT 1")
    suspend fun getActiveWorkoutCache(): ActiveWorkoutEntity?

    @Query("DELETE FROM active_workout_cache WHERE id = 1")
    suspend fun clearActiveWorkoutCache()

    @Transaction
    suspend fun saveFullWorkout(
        workout: CompletedWorkoutEntity,
        exercises: List<WorkoutExerciseEntity>,
        sets: List<WorkoutSetEntity>
    ) {
        insertWorkout(workout)
        insertWorkoutExercises(exercises)
        insertWorkoutSets(sets)
    }

    @Query("SELECT * FROM completed_workouts ORDER BY dateCompleted DESC")
    fun getWorkoutSummaries(): Flow<List<CompletedWorkoutEntity>>

    @Transaction
    @Query("SELECT * FROM completed_workouts ORDER BY dateCompleted DESC")
    fun getAllFullWorkouts(): Flow<List<FullWorkout>>

    @Transaction
    @Query("SELECT * FROM completed_workouts WHERE id = :workoutId LIMIT 1")
    suspend fun getFullWorkoutById(workoutId: String): FullWorkout?

    @Query("""
        SELECT SUM(
            CASE 
                WHEN s.weightUnit = 'LBS' AND :targetUnitName = 'KG' THEN (s.weight / 2.20462) * s.reps
                WHEN s.weightUnit = 'KG' AND :targetUnitName = 'LBS' THEN (s.weight * 2.20462) * s.reps
                ELSE s.weight * s.reps 
            END
        ) 
        FROM workout_sets s
        INNER JOIN workout_exercises e ON s.workoutExerciseId = e.id
        INNER JOIN completed_workouts w ON e.workoutId = w.id
        -- Target the specific workout ID instead of a date
        WHERE w.id = :workoutId AND s.isCompleted = 1 
    """)
    fun getWorkoutVolumeById(workoutId: String, targetUnitName: String): Flow<Double?>

    @Query("DELETE FROM completed_workouts WHERE id = :workoutId")
    suspend fun deleteWorkout(workoutId: String)

    @Query("SELECT COUNT(id) FROM completed_workouts WHERE startTime >= :startDateMillis")
    fun getWorkoutsCountSince(startDateMillis: Long): Flow<Int>

    @Query(
        """
        SELECT SUM(
            CASE 
                -- s.weightUnit is saved as 'LBS' or 'KG' thanks to your converter!
                WHEN s.weightUnit = 'LBS' AND :targetUnitName = 'KG' THEN (s.weight / 2.20462) * s.reps
                WHEN s.weightUnit = 'KG' AND :targetUnitName = 'LBS' THEN (s.weight * 2.20462) * s.reps
                ELSE s.weight * s.reps 
            END
        ) 
        FROM workout_sets s
        INNER JOIN workout_exercises e ON s.workoutExerciseId = e.id
        INNER JOIN completed_workouts w ON e.workoutId = w.id
        WHERE w.startTime >= :startDateMillis AND s.isCompleted = 1 
    """
    )
    fun getTotalVolumeSince(startDateMillis: Long, targetUnitName: String): Flow<Double?>
}