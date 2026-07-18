package com.rvilleda.workouttracker.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rvilleda.workouttracker.model.ExerciseInSession
import com.rvilleda.workouttracker.model.TargetMuscle

class Converters {

    private val gson = Gson()

    // --- String List Converters ---
    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return value?.joinToString(separator = "|||") ?: ""
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        if (value.isBlank()) return emptyList()
        return value.split("|||")
    }

    // --- Muscle Group Converters ---
    @TypeConverter
    fun fromMuscleGroupList(value: List<TargetMuscle>?): String {
        return value?.joinToString(separator = ",") { it.name } ?: ""
    }

    @TypeConverter
    fun toMuscleGroupList(value: String): List<TargetMuscle> {
        if (value.isBlank()) return emptyList()
        return value.split(",").mapNotNull {
            try {
                TargetMuscle.valueOf(it)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }

    // --- Active Workout Cache Converters ---
    @TypeConverter
    fun fromExerciseList(exercises: List<ExerciseInSession>?): String {
        return gson.toJson(exercises)
    }

    @TypeConverter
    fun toExerciseList(json: String?): List<ExerciseInSession> {
        if (json.isNullOrBlank()) return emptyList()
        val type = object : TypeToken<List<ExerciseInSession>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }
}