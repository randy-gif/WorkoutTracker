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

data class OneRMTrendDataPoint(
    val startTime: Long,
    val estimatedMax: Double
)
data class WorkoutCountTrendDataPoint(
    val startTime: Long,
    val workoutCount: Int
)
@Dao
interface WorkoutDao {
    @Query(
        """
        SELECT s.*
        FROM workout_sets s
        WHERE s.workoutExerciseId = (
            SELECT e.id
            FROM workout_exercises e
            INNER JOIN completed_workouts w ON e.workoutId = w.id
            WHERE e.baseExerciseId = :baseExerciseId
              AND w.dateCompleted <= :beforeTime
              AND EXISTS (
                  SELECT 1 FROM workout_sets completedSet
                  WHERE completedSet.workoutExerciseId = e.id
                    AND completedSet.isCompleted = 1
              )
            ORDER BY w.dateCompleted DESC, w.id DESC, e.orderInWorkout DESC, e.id DESC
            LIMIT 1
        )
          AND s.isCompleted = 1
        ORDER BY s.setNumber ASC
        """
    )
    suspend fun getLastCompletedSetsForExercise(
        baseExerciseId: String,
        beforeTime: Long
    ): List<WorkoutSetEntity>

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

    @Query("SELECT * FROM completed_workouts WHERE dateCompleted = (SELECT MAX(dateCompleted) FROM completed_workouts) LIMIT 1")
    fun getLatestFullWorkout(): Flow<FullWorkout?>
    // 1. DAILY GROUPING (For Day, Week, Month)
    @Query(
        """
        SELECT MIN(startTime) AS startTime, COUNT(id) AS workoutCount
        FROM completed_workouts
        WHERE startTime BETWEEN :startDateMillis AND :endDateMillis
        GROUP BY (startTime / 86400000)
        ORDER BY startTime ASC
    """
    )
    fun getWorkoutCountTrendDaily(startDateMillis: Long, endDateMillis: Long): Flow<List<WorkoutCountTrendDataPoint>>

    // 2. WEEKLY GROUPING (For Year view)
    @Query(
        """
        SELECT MIN(startTime) AS startTime, COUNT(id) AS workoutCount
        FROM completed_workouts
        WHERE startTime BETWEEN :startDateMillis AND :endDateMillis
        GROUP BY strftime('%Y-%W', startTime / 1000, 'unixepoch')
        ORDER BY startTime ASC
    """
    )
    fun getWorkoutCountTrendWeekly(startDateMillis: Long, endDateMillis: Long): Flow<List<WorkoutCountTrendDataPoint>>

    // 3. MONTHLY GROUPING (For 5 Years / All Time view)
    @Query(
        """
        SELECT MIN(startTime) AS startTime, COUNT(id) AS workoutCount
        FROM completed_workouts
        WHERE startTime BETWEEN :startDateMillis AND :endDateMillis
        GROUP BY strftime('%Y-%m', startTime / 1000, 'unixepoch')
        ORDER BY startTime ASC
    """
    )
    fun getWorkoutCountTrendMonthly(startDateMillis: Long, endDateMillis: Long): Flow<List<WorkoutCountTrendDataPoint>>

    // 1. DAILY (For Day, Week, Month view - every workout session shown)
    @Query(
        """
        SELECT w.startTime, 
               MAX(
                   (CASE 
                        WHEN s.weightUnit = 'LBS' AND :targetUnitName = 'KG' THEN (s.weight / 2.20462)
                        WHEN s.weightUnit = 'KG' AND :targetUnitName = 'LBS' THEN (s.weight * 2.20462)
                        ELSE s.weight 
                    END) * (1.0 + (s.reps / 30.0))
               ) AS estimatedMax
        FROM workout_sets s
        INNER JOIN workout_exercises e ON s.workoutExerciseId = e.id
        INNER JOIN completed_workouts w ON e.workoutId = w.id
        WHERE e.baseExerciseId = :exerciseId
            AND w.startTime BETWEEN :startDateMillis AND :endDateMillis
            AND s.isCompleted = 1
        GROUP BY w.id 
        ORDER BY w.startTime ASC
    """
    )
    fun getExercise1RMTrendDaily(
        exerciseId: String,
        startDateMillis: Long,
        endDateMillis: Long,
        targetUnitName: String
    ): Flow<List<OneRMTrendDataPoint>>

    // 2. WEEKLY (For Year view - peak 1RM per week)
    @Query(
        """
        WITH SessionMaxes AS (
            SELECT w.startTime, 
                   MAX(
                       (CASE 
                            WHEN s.weightUnit = 'LBS' AND :targetUnitName = 'KG' THEN (s.weight / 2.20462)
                            WHEN s.weightUnit = 'KG' AND :targetUnitName = 'LBS' THEN (s.weight * 2.20462)
                            ELSE s.weight 
                        END) * (1.0 + (s.reps / 30.0))
                   ) AS estimatedMax
            FROM workout_sets s
            INNER JOIN workout_exercises e ON s.workoutExerciseId = e.id
            INNER JOIN completed_workouts w ON e.workoutId = w.id
            WHERE e.baseExerciseId = :exerciseId
                AND w.startTime BETWEEN :startDateMillis AND :endDateMillis
                AND s.isCompleted = 1
            GROUP BY w.id
        )
        SELECT MIN(startTime) AS startTime, MAX(estimatedMax) AS estimatedMax
        FROM SessionMaxes
        GROUP BY strftime('%Y-%W', startTime / 1000, 'unixepoch')
        ORDER BY startTime ASC
    """
    )
    fun getExercise1RMTrendWeekly(
        exerciseId: String,
        startDateMillis: Long,
        endDateMillis: Long,
        targetUnitName: String
    ): Flow<List<OneRMTrendDataPoint>>

    // 3. MONTHLY (For 5 Years / All Time view - peak 1RM per month)
    @Query(
        """
        WITH SessionMaxes AS (
            SELECT w.startTime, 
                   MAX(
                       (CASE 
                            WHEN s.weightUnit = 'LBS' AND :targetUnitName = 'KG' THEN (s.weight / 2.20462)
                            WHEN s.weightUnit = 'KG' AND :targetUnitName = 'LBS' THEN (s.weight * 2.20462)
                            ELSE s.weight 
                        END) * (1.0 + (s.reps / 30.0))
                   ) AS estimatedMax
            FROM workout_sets s
            INNER JOIN workout_exercises e ON s.workoutExerciseId = e.id
            INNER JOIN completed_workouts w ON e.workoutId = w.id
            WHERE e.baseExerciseId = :exerciseId
                AND w.startTime BETWEEN :startDateMillis AND :endDateMillis
                AND s.isCompleted = 1
            GROUP BY w.id
        )
        SELECT MIN(startTime) AS startTime, MAX(estimatedMax) AS estimatedMax
        FROM SessionMaxes
        GROUP BY strftime('%Y-%m', startTime / 1000, 'unixepoch')
        ORDER BY startTime ASC
    """
    )
    fun getExercise1RMTrendMonthly(
        exerciseId: String,
        startDateMillis: Long,
        endDateMillis: Long,
        targetUnitName: String
    ): Flow<List<OneRMTrendDataPoint>>

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