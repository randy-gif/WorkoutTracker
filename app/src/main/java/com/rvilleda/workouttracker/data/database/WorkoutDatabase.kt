package com.rvilleda.workouttracker.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rvilleda.workouttracker.model.allExercises
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
// Make sure to import your allExercises list here!
// import com.rvilleda.workouttracker.model.allExercises

@Database(
    entities = [
        CompletedWorkoutEntity::class,
        CustomExerciseEntity::class // 1. Added the new table here!
    ],
    version = 4, // 2. Bumped version to 4 because the schema changed
    exportSchema = false
)
abstract class WorkoutDatabase : RoomDatabase() {

    // These tell the database about your clerks (DAOs)
    abstract fun workoutDao(): WorkoutDao
    abstract fun exerciseDao(): ExerciseDao // 3. Added the new DAO here!

    // The Companion Object acts as our "Builder"
    companion object {
        @Volatile
        private var INSTANCE: WorkoutDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): WorkoutDatabase {
            // If the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WorkoutDatabase::class.java,
                    "workout_database"
                )
                    // 4. The Prepopulation Callback
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)

                            // We launch a coroutine to do this in the background
                            INSTANCE?.let { database ->
                                scope.launch(Dispatchers.IO) {
                                    val dao = database.exerciseDao()

                                    // Map your Kotlin list to Room Entities
                                    val defaultEntities = allExercises.map { exercise ->
                                        CustomExerciseEntity(
                                            id = exercise.id,
                                            name = exercise.name,
                                            muscleGroup = exercise.muscleGroup.name,
                                            equipment = exercise.equipment.name,
                                            movementType = exercise.movementType.name
                                        )
                                    }

                                    // Insert them all!
                                    dao.insertAllExercises(defaultEntities)
                                }
                            }
                        }
                    })
                    // Since you are in development, this prevents crashes when you change tables
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}