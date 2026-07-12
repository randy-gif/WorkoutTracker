package com.rvilleda.workouttracker.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.rvilleda.workouttracker.data.database.routine.FullRoutine
import com.rvilleda.workouttracker.data.database.routine.RoutineEntity
import com.rvilleda.workouttracker.data.database.routine.RoutineExerciseEntity
import com.rvilleda.workouttracker.data.database.routine.RoutineSetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: RoutineEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutineExercises(exercises: List<RoutineExerciseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutineSets(sets: List<RoutineSetEntity>)

    // 1. Save an entire routine at once
    @Transaction
    suspend fun saveFullRoutine(
        routine: RoutineEntity,
        exercises: List<RoutineExerciseEntity>,
        sets: List<RoutineSetEntity>
    ) {
        insertRoutine(routine)
        insertRoutineExercises(exercises)
        insertRoutineSets(sets)
    }

    // 2. Get just the titles/IDs for a list screen (like a routines tab)
    @Query("SELECT * FROM routines ORDER BY name ASC")
    fun getAllRoutines(): Flow<List<RoutineEntity>>

    // 3. Get everything (Routine + Exercises + Sets) by ID to start a workout or edit it
    @Transaction
    @Query("SELECT * FROM routines WHERE id = :routineId LIMIT 1")
    suspend fun getFullRoutineById(routineId: String): FullRoutine?

    // 4. Delete the routine (Cascade will handle deleting the exercises and sets)
    @Query("DELETE FROM routines WHERE id = :routineId")
    suspend fun deleteRoutine(routineId: String)
}