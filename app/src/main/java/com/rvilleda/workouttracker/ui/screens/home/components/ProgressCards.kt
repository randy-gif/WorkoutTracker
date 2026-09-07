package com.rvilleda.workouttracker.ui.screens.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rvilleda.workouttracker.data.database.entity.CompletedWorkoutEntity
import com.rvilleda.workouttracker.model.WeightUnit
import com.rvilleda.workouttracker.ui.screens.home.*
import java.text.SimpleDateFormat
import com.rvilleda.workouttracker.ui.components.WorkoutCalendar
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneId
import java.util.Date
import java.util.Locale

private fun progressDate(timestamp: Long, pattern: String): String =
    SimpleDateFormat(pattern, Locale.getDefault()).format(Date(timestamp))

@Composable
fun WorkoutCalendarCard(
    workouts: List<CompletedWorkoutEntity>,
    now: Long,
    onClick: () -> Unit
) {
    val zone = ZoneId.systemDefault()
    val today = Instant.ofEpochMilli(now).atZone(zone).toLocalDate()
    val workoutDates = remember(workouts, now, zone) {
        workouts.filter { it.dateCompleted <= now }.map {
            Instant.ofEpochMilli(it.dateCompleted).atZone(zone).toLocalDate()
        }.toSet()
    }
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Workout calendar. Open history" },
        shape = RoundedCornerShape(16.dp)
    ) {
        WorkoutCalendar(
            workoutDates = workoutDates,
            month = YearMonth.from(today),
            today = today
        )
    }
}
@Composable
fun RecentRecordsCard(records: List<ProgressRecord>, unit: WeightUnit) {
    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Recent personal records", style = MaterialTheme.typography.titleLarge)
            if (records.isEmpty()) {
                Text("No new records in this period. Your first session establishes a baseline.")
            }
            records.forEach { record ->
                val weight = if (unit == WeightUnit.LBS) record.weightKg * 2.20462 else record.weightKg
                Column {
                    Text(record.exerciseName, fontWeight = FontWeight.Bold)
                    Text("${String.format(Locale.getDefault(), "%.1f", weight)} ${unit.name.lowercase()} × ${record.reps}")
                    val label = when {
                        record.isWeightRecord -> "Heaviest weight"
                        record.isRepRecord -> "Most reps at this weight"
                        else -> "Best weight for ${record.reps} reps"
                    }
                    Text("$label · ${progressDate(record.startTime, "MMM d, yyyy")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Text("Compared with earlier workouts; ties don't count. Up to 5 records in the selected period.",
                style = MaterialTheme.typography.bodySmall)
        }
    }
}
