package com.rvilleda.workouttracker.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rvilleda.workouttracker.data.database.dao.ExerciseDao
import com.rvilleda.workouttracker.data.database.dao.RoutineDao
import com.rvilleda.workouttracker.data.database.dao.WorkoutDao
import com.rvilleda.workouttracker.data.database.entity.CompletedWorkoutEntity
import com.rvilleda.workouttracker.data.database.entity.WorkoutExerciseEntity
import com.rvilleda.workouttracker.data.database.entity.WorkoutSetEntity
import com.rvilleda.workouttracker.data.database.entity.CustomExerciseEntity
import com.rvilleda.workouttracker.data.database.entity.RoutineEntity
import com.rvilleda.workouttracker.data.database.entity.RoutineExerciseEntity
import com.rvilleda.workouttracker.data.database.entity.RoutineSetEntity
import com.rvilleda.workouttracker.data.database.entity.ActiveWorkoutEntity



@Database(
    entities = [
        CompletedWorkoutEntity::class,
        WorkoutExerciseEntity::class,
        WorkoutSetEntity::class,
        CustomExerciseEntity::class,
        RoutineEntity::class,
        RoutineExerciseEntity::class,
        RoutineSetEntity::class,
        ActiveWorkoutEntity::class
    ],
    version = 9,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class WorkoutDatabase : RoomDatabase() {

    abstract fun workoutDao(): WorkoutDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun routineDao(): RoutineDao

    companion object {
        @Volatile
        private var INSTANCE: WorkoutDatabase? = null

        // Removed the CoroutineScope parameter since we no longer need background prepopulation
        fun getDatabase(context: Context): WorkoutDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WorkoutDatabase::class.java,
                    "workout_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}