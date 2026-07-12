package com.rvilleda.workouttracker.data.database.routine

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "routines")
data class RoutineEntity(
    @PrimaryKey val id: String,
    val name: String
)

@Entity(
    tableName = "routine_exercises",
    foreignKeys = [
        ForeignKey(
            entity = RoutineEntity::class,
            parentColumns = ["id"],
            childColumns = ["routineId"],
            onDelete = ForeignKey.CASCADE // If you delete the routine, this deletes automatically!
        )
    ],
    // Indexing the foreign key makes database queries much faster
    indices = [androidx.room.Index("routineId")]
)
data class RoutineExerciseEntity(
    @PrimaryKey val id: String,
    val routineId: String,
    val baseExerciseId: String,
    val exerciseName: String,
    val orderInRoutine: Int,
    val restTimeSeconds: Int,
    val autoRestEnabled: Boolean
)

@Entity(
    tableName = "routine_sets",
    foreignKeys = [
        ForeignKey(
            entity = RoutineExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["routineExerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [androidx.room.Index("routineExerciseId")]
)
data class RoutineSetEntity(
    @PrimaryKey val id: String,
    val routineExerciseId: String,
    val setNumber: Int,
    val targetWeight: Float,
    val targetReps: Int
)

// --- RELATIONS (How Room groups them together for you) ---

data class RoutineExerciseWithSets(
    @Embedded val exercise: RoutineExerciseEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "routineExerciseId"
    )
    val sets: List<RoutineSetEntity>
)

data class FullRoutine(
    @Embedded val routine: RoutineEntity,
    @Relation(
        entity = RoutineExerciseEntity::class,
        parentColumn = "id",
        entityColumn = "routineId"
    )
    val exercises: List<RoutineExerciseWithSets>
)