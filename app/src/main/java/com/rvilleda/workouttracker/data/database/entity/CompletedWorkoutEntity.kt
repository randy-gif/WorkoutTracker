package com.rvilleda.workouttracker.data.database.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.rvilleda.workouttracker.model.WeightUnit


@Entity(tableName = "completed_workouts")
data class CompletedWorkoutEntity(
    @PrimaryKey val id: String,
    val name: String,
    val startTime: Long,
    val dateCompleted: Long,
)

@Entity(
    tableName = "workout_exercises",
    foreignKeys = [
        ForeignKey(
            entity = CompletedWorkoutEntity::class,
            parentColumns = ["id"],
            childColumns = ["workoutId"],
            onDelete = ForeignKey.Companion.CASCADE
        )
    ],
    indices = [Index("workoutId")]
)
data class WorkoutExerciseEntity(
    @PrimaryKey val id: String,
    val workoutId: String,
    val baseExerciseId: String,
    val exerciseName: String,
    val orderInWorkout: Int
)

@Entity(
    tableName = "workout_sets",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["workoutExerciseId"],
            onDelete = ForeignKey.Companion.CASCADE
        )
    ],
    indices = [Index("workoutExerciseId")]
)
data class WorkoutSetEntity(
    @PrimaryKey val id: String,
    val workoutExerciseId: String,
    val setNumber: Int,
    val weight: Float,
    val weightUnit: WeightUnit,
    val reps: Int,
    val rpe: Float?,
    val isCompleted: Boolean
)

data class ExerciseWithSets(
    @Embedded val exercise: WorkoutExerciseEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "workoutExerciseId"
    )
    val sets: List<WorkoutSetEntity>
)

data class FullWorkout(
    @Embedded val workout: CompletedWorkoutEntity,

    @Relation(
        entity = WorkoutExerciseEntity::class,
        parentColumn = "id",
        entityColumn = "workoutId"
    )
    val exercises: List<ExerciseWithSets>
)