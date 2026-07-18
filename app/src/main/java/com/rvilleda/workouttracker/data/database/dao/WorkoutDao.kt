package com.rvilleda.workouttracker.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.rvilleda.workouttracker.data.database.entity.ActiveWorkoutEntity
import com.rvilleda.workouttracker.data.database.entity.workout.CompletedWorkoutEntity
import com.rvilleda.workouttracker.data.database.entity.workout.FullWorkout
import com.rvilleda.workouttracker.data.database.entity.workout.WorkoutExerciseEntity
import com.rvilleda.workouttracker.data.database.entity.workout.WorkoutSetEntity
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

    @Query("DELETE FROM completed_workouts WHERE id = :workoutId")
    suspend fun deleteWorkout(workoutId: String)

    @Query("SELECT COUNT(id) FROM completed_workouts WHERE startTime >= :startDateMillis")
    fun getWorkoutsCountSince(startDateMillis: Long): Flow<Int>

    @Query("""
        SELECT SUM(CAST(s.weight AS REAL) * CAST(s.reps AS INTEGER)) 
        FROM workout_sets s
        INNER JOIN workout_exercises e ON s.workoutExerciseId = e.id
        INNER JOIN completed_workouts w ON e.workoutId = w.id
        WHERE w.startTime >= :startDateMillis
    """)
    fun getTotalVolumeSince(startDateMillis: Long): Flow<Double?>
}