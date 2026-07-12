package com.rvilleda.workouttracker.data.repository

import com.rvilleda.workouttracker.data.database.dao.ExerciseDao
import com.rvilleda.workouttracker.model.Exercise
import com.rvilleda.workouttracker.model.allDefaultExercises
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf

class ExerciseRepository(private val exerciseDao: ExerciseDao) {

    private val defaultExercisesFlow = flowOf(allDefaultExercises)


    fun getAllExercises(): Flow<List<Exercise>> {

        return combine(
            defaultExercisesFlow,
            exerciseDao.getCustomExercises(),
        ) { defaults, customsDb ->

            // Map Custom DB entities to the UI Domain Model
            val customExercises = customsDb.map { entity ->
                Exercise(
                    id = entity.id,
                    name = entity.name,
                    isUserCreated = true,
                    primaryMuscle = entity.primaryMuscle,
                    secondaryMuscles = entity.secondaryMuscles,
                    equipment = entity.equipment,
                    gifUrl = entity.gifUrl,
                    instructions = entity.instructions,
                    movementPattern = entity.movementPattern,
                    fatigueTier = entity.fatigueTier,
                    mechanics = entity.mechanics,
                    resistanceCurve = entity.resistanceCurve,
                )
            }

            (defaults + customExercises).sortedBy { it.name }
        }
    }
}

