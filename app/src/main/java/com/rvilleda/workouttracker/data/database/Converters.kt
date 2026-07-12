package com.rvilleda.workouttracker.data.database

import androidx.room.TypeConverter
import com.rvilleda.workouttracker.model.TargetMuscle

class Converters {

    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        // Joins the list with a special separator so commas in instructions don't break it
        return value?.joinToString(separator = "|||") ?: ""
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        if (value.isBlank()) return emptyList()
        return value.split("|||")
    }

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
                null // Ignores any strings that don't match your enums
            }
        }
    }

}