package com.rvilleda.workouttracker.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rvilleda.workouttracker.data.database.entity.CompletedWorkoutEntity
import com.rvilleda.workouttracker.model.WeightUnit
import com.rvilleda.workouttracker.ui.screens.home.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private fun progressDate(timestamp: Long, pattern: String): String =
    SimpleDateFormat(pattern, Locale.getDefault()).format(Date(timestamp))

@Composable
fun WorkoutCalendarCard(
    workouts: List<CompletedWorkoutEntity>,
    weeklyGoal: Int,
    onGoalChange: (Int) -> Unit,
    now: Long,
    longRange: Boolean,
    error: String?
) {
    var displayedDate by rememberSaveable { mutableStateOf(now) }
    var selectedDay by rememberSaveable { mutableStateOf(progressDay(now)) }
    var showGoalMenu by remember { mutableStateOf(false) }
    val today = progressDay(now)
    // Recomputed when the clock refreshes so timezone/day changes are reflected.
    val byDay = remember(workouts, now) {
        workouts.filter { it.startTime <= now }.groupBy { progressDay(it.startTime) }
    }
    val thisWeek = progressWeek(now)
    val weeklyCount = workouts.count { it.startTime in thisWeek..now }
    val streak = remember(workouts, weeklyGoal, now) {
        progressStreak(workouts.map { it.startTime }, weeklyGoal, now)
    }
    val monthStart = Calendar.getInstance().apply {
        timeInMillis = progressDay(displayedDate)
        set(Calendar.DAY_OF_MONTH, 1)
    }.timeInMillis
    val yearStart = Calendar.getInstance().apply {
        timeInMillis = monthStart
        set(Calendar.MONTH, Calendar.JANUARY)
    }.timeInMillis
    val periodStart = if (longRange) yearStart else monthStart
    val field = if (longRange) Calendar.YEAR else Calendar.MONTH
    val periodEnd = progressAdd(periodStart, field, 1)
    val gridStart = progressWeek(periodStart)

    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Workout calendar", style = MaterialTheme.typography.titleLarge)
            Text("Calendar and weekly goal are independent of the overview filter.",
                style = MaterialTheme.typography.bodySmall)
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = { displayedDate = progressAdd(periodStart, field, -1) }) { Text("‹") }
                Text(progressDate(periodStart, if (longRange) "yyyy" else "MMMM yyyy"),
                    Modifier.weight(1f), fontWeight = FontWeight.Bold)
                TextButton(
                    onClick = { displayedDate = periodEnd },
                    enabled = periodEnd <= now
                ) { Text("›") }
            }
            if (longRange) {
                Text("Each square is one day. Scroll to explore; tap for workouts.",
                    style = MaterialTheme.typography.bodySmall)
                Row(Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    var weekStart = gridStart
                    while (weekStart < periodEnd) {
                        val thisWeekStart = weekStart
                        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            repeat(7) { offset ->
                                val day = progressAdd(thisWeekStart, Calendar.DAY_OF_YEAR, offset)
                                CalendarDay(day, byDay[day]?.size ?: 0, day == selectedDay,
                                    day >= periodStart && day < periodEnd && day <= today,
                                    Modifier.size(24.dp), compact = true) { selectedDay = day }
                            }
                        }
                        weekStart = progressAdd(weekStart, Calendar.WEEK_OF_YEAR, 1)
                    }
                }
            } else {
                Row(Modifier.fillMaxWidth()) {
                    repeat(7) { offset ->
                        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            Text(progressDate(progressAdd(gridStart, Calendar.DAY_OF_YEAR, offset), "EE"),
                                style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
                repeat(6) { week ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        repeat(7) { weekday ->
                            val day = progressAdd(gridStart, Calendar.DAY_OF_YEAR, week * 7 + weekday)
                            if (day < monthStart || day >= periodEnd) {
                                Spacer(Modifier.weight(1f).height(48.dp))
                            } else {
                                CalendarDay(day, byDay[day]?.size ?: 0, day == selectedDay,
                                    day <= today, Modifier.weight(1f).height(48.dp), compact = false) {
                                    selectedDay = day
                                }
                            }
                        }
                    }
                }
            }
            Text("Highlighted = workout • Weeks start Monday", style = MaterialTheme.typography.bodySmall)
            Text(progressDate(selectedDay, "EEE, MMM d, yyyy"), fontWeight = FontWeight.Bold)
            val selectedWorkouts = byDay[selectedDay].orEmpty().sortedBy { it.startTime }
            if (selectedWorkouts.isEmpty()) Text("No workouts recorded.", style = MaterialTheme.typography.bodyMedium)
            selectedWorkouts.forEach {
                Text("${progressDate(it.startTime, "h:mm a")} · ${it.name}")
            }
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("$weeklyCount of $weeklyGoal workouts this week", Modifier.weight(1f))
                Box {
                    TextButton(onClick = { showGoalMenu = true }) { Text("Goal") }
                    DropdownMenu(expanded = showGoalMenu, onDismissRequest = { showGoalMenu = false }) {
                        (1..14).forEach { goal ->
                            DropdownMenuItem(text = { Text("$goal per week") }, onClick = {
                                onGoalChange(goal)
                                showGoalMenu = false
                            })
                        }
                    }
                }
            }
            // Scalar overload works with the Material 3 generation used by this project.
            LinearProgressIndicator(
                progress = (weeklyCount.toFloat() / weeklyGoal).coerceIn(0f, 1f),
                modifier = Modifier.fillMaxWidth()
            )
            Text("$streak consecutive ${if (streak == 1) "week" else "weeks"} meeting your goal",
                style = MaterialTheme.typography.bodyMedium)
            Text("Streak uses your current goal. Rest days are welcome.",
                style = MaterialTheme.typography.bodySmall)
            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        }
    }
}

@Composable
private fun CalendarDay(
    day: Long, count: Int, selected: Boolean, enabled: Boolean,
    modifier: Modifier, compact: Boolean, onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val background = when {
        !enabled -> colors.surfaceVariant.copy(alpha = 0.25f)
        selected -> colors.secondaryContainer
        count > 0 -> colors.primary
        else -> colors.surfaceVariant
    }
    val foreground = when {
        selected -> colors.onSecondaryContainer
        count > 0 -> colors.onPrimary
        else -> colors.onSurfaceVariant
    }
    Box(modifier.background(background, RoundedCornerShape(6.dp))
        .clickable(enabled = enabled, onClick = onClick)
        .semantics { contentDescription = "${progressDate(day, "MMMM d, yyyy")}, $count workouts" },
        contentAlignment = Alignment.Center) {
        if (!compact) Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(progressDate(day, "d"), color = foreground.copy(alpha = if (enabled) 1f else 0.4f))
            if (count > 0) Text("•", color = foreground, style = MaterialTheme.typography.labelSmall)
        }
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
