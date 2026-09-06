package com.rvilleda.workouttracker.ui.screens.home.components

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import com.rvilleda.workouttracker.data.database.dao.ProgressSetSummary
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.util.Calendar

// A separate, uniquely named store; the existing dashboard store stays intact.
private val Context.progressGoalStore by preferencesDataStore(name = "progress_weekly_goal")

class ProgressGoalRepository(context: Context) {
    private val store = context.applicationContext.progressGoalStore
    private val goalKey = intPreferencesKey("workouts_per_week")
    val weeklyGoal = store.data.catch {
        if (it is IOException) emit(emptyPreferences()) else throw it
    }.map { (it[goalKey] ?: 3).coerceIn(1, 14) }

    suspend fun saveWeeklyGoal(goal: Int) {
        store.edit { it[goalKey] = goal.coerceIn(1, 14) }
    }
}

data class ProgressRecord(
    val exerciseName: String,
    val startTime: Long,
    val weightKg: Double,
    val reps: Int,
    val isWeightRecord: Boolean,
    val isRepRecord: Boolean = false
)

// Compare a workout with EARLIER workouts, not other sets in the same workout.
// The first observation establishes a baseline; ties are not new records.
fun buildProgressRecords(rows: List<ProgressSetSummary>): List<ProgressRecord> {
    val bestWeight = mutableMapOf<String, Double>()
    val bestAtReps = mutableMapOf<Pair<String, Int>, Double>()
    val bestRepsAtWeight = mutableMapOf<Pair<String, Long>, Int>()
    val records = mutableListOf<ProgressRecord>()
    rows.groupBy { it.workoutId }.values.sortedBy { it.first().startTime }.forEach { session ->
        session.groupBy { it.exerciseId }.forEach { (id, sets) ->
            val sessionBest = sets.maxOf { it.weightKg }
            val previousBest = bestWeight[id]
            if (previousBest != null && sessionBest > previousBest + 0.01) {
                val top = sets.filter { it.weightKg == sessionBest }.maxByOrNull { it.reps }!!
                records += ProgressRecord(top.exerciseName, top.startTime, top.weightKg, top.reps, true)
            } else {
                // Show at most one rep-specific weight record per exercise/workout.
                val improved = sets.filter {
                    val previous = bestAtReps[id to it.reps]
                    previous != null && it.weightKg > previous + 0.01
                }.maxByOrNull { it.weightKg }
                val repImprovement = sets.filter {
                    val previous = bestRepsAtWeight[id to Math.round(it.weightKg * 100)]
                    previous != null && it.reps > previous
                }.maxByOrNull { it.reps }
                if (repImprovement != null) {
                    records += ProgressRecord(repImprovement.exerciseName, repImprovement.startTime,
                        repImprovement.weightKg, repImprovement.reps, false, isRepRecord = true)
                } else if (improved != null) records += ProgressRecord(
                    improved.exerciseName, improved.startTime, improved.weightKg, improved.reps, false
                )
            }
            bestWeight[id] = maxOf(previousBest ?: sessionBest, sessionBest)
            sets.forEach {
                val key = id to it.reps
                bestAtReps[key] = maxOf(bestAtReps[key] ?: it.weightKg, it.weightKg)
                val weightKey = id to Math.round(it.weightKg * 100)
                bestRepsAtWeight[weightKey] = maxOf(bestRepsAtWeight[weightKey] ?: it.reps, it.reps)
            }
        }
    }
    return records.sortedByDescending { it.startTime }
}

fun progressDay(timestamp: Long): Long = Calendar.getInstance().apply {
    timeInMillis = timestamp
    set(Calendar.HOUR_OF_DAY, 0)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}.timeInMillis

fun progressAdd(timestamp: Long, field: Int, amount: Int): Long = Calendar.getInstance().apply {
    timeInMillis = timestamp
    add(field, amount)
}.timeInMillis

fun progressWeek(timestamp: Long): Long = Calendar.getInstance().apply {
    timeInMillis = progressDay(timestamp)
    add(Calendar.DAY_OF_YEAR, -((get(Calendar.DAY_OF_WEEK) + 5) % 7))
}.timeInMillis

fun progressStreak(timestamps: List<Long>, goal: Int, now: Long): Int {
    val counts = timestamps.filter { it <= now }.groupingBy { progressWeek(it) }.eachCount()
    var week = progressWeek(now)
    // Give the current week time to finish before breaking an existing streak.
    if ((counts[week] ?: 0) < goal) week = progressAdd(week, Calendar.WEEK_OF_YEAR, -1)
    var streak = 0
    while ((counts[week] ?: 0) >= goal) {
        streak++
        week = progressAdd(week, Calendar.WEEK_OF_YEAR, -1)
    }
    return streak
}
