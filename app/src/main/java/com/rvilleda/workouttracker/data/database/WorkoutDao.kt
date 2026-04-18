package com.rvilleda.workouttracker.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    // 1. Save a workout to the hard drive
    @Insert
    suspend fun insertWorkout(workout: CompletedWorkoutEntity)

    // 2. Get all workouts to display on your Data screen
    // Room automatically converts the SQL table back into a Kotlin List!
    @Query("SELECT * FROM completed_workouts ORDER BY dateCompleted DESC")
    fun getAllWorkouts(): Flow<List<CompletedWorkoutEntity>>

    @Query("SELECT * FROM completed_workouts WHERE id = :workoutId LIMIT 1")
    suspend fun getWorkoutById(workoutId: String): CompletedWorkoutEntity?
}