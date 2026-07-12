package com.rvilleda.workouttracker.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
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
}