package com.rvilleda.workouttracker.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [CompletedWorkoutEntity::class],
    version = 1 // If you add new tables later, you change this to 2
)
abstract class WorkoutDatabase : RoomDatabase() {

    // This tells the database about your clerk
    abstract fun workoutDao(): WorkoutDao

}