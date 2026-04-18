package com.rvilleda.workouttracker.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [CompletedWorkoutEntity::class],
    version = 2
)
abstract class WorkoutDatabase : RoomDatabase() {

    // This tells the database about your clerk
    abstract fun workoutDao(): WorkoutDao

}