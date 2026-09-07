package com.rvilleda.workouttracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.Locale

/** A non-scrolling month grid. Omit callbacks for a read-only calendar preview. */
@Composable
fun WorkoutCalendar(
    workoutDates: Set<LocalDate>,
    month: YearMonth,
    modifier: Modifier = Modifier,
    selectedDate: LocalDate? = null,
    today: LocalDate = LocalDate.now(),
    onMonthChange: ((YearMonth) -> Unit)? = null,
    onDateSelected: ((LocalDate) -> Unit)? = null
) {
    val locale = Locale.getDefault()
    val colors = MaterialTheme.colorScheme
    Column(modifier.fillMaxWidth().padding(16.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (onMonthChange != null) {
                IconButton(onClick = { onMonthChange(month.minusMonths(1)) }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Previous month")
                }
            }
            Text(
                text = "${month.month.getDisplayName(TextStyle.FULL, locale)} ${month.year}",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            if (onMonthChange != null) {
                IconButton(onClick = { onMonthChange(month.plusMonths(1)) }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Next month")
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth()) {
            DayOfWeek.values().forEach { day ->
                Text(
                    day.getDisplayName(TextStyle.SHORT, locale),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.onSurfaceVariant
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        val offset = month.atDay(1).dayOfWeek.value - 1
        val weekCount = (offset + month.lengthOfMonth() + 6) / 7
        repeat(weekCount) { week ->
            Row(Modifier.fillMaxWidth()) {
                repeat(7) { weekday ->
                    val day = week * 7 + weekday - offset + 1
                    if (day !in 1..month.lengthOfMonth()) {
                        Spacer(Modifier.weight(1f).aspectRatio(1f))
                    } else {
                        val date = month.atDay(day)
                        val hasWorkout = date in workoutDates
                        val isSelected = date == selectedDate
                        val background = when {
                            hasWorkout -> colors.primary
                            date == today -> colors.surfaceVariant
                            else -> Color.Transparent
                        }
                        Box(
                            modifier = Modifier.weight(1f).aspectRatio(1f).padding(4.dp)
                                .clip(CircleShape)
                                .background(background)
                                .then(if (isSelected) Modifier.border(2.dp, colors.secondary, CircleShape) else Modifier)
                                .then(if (onDateSelected != null) Modifier.clickable { onDateSelected(date) } else Modifier)
                                .semantics {
                                    selected = isSelected
                                    contentDescription = date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)) +
                                        if (hasWorkout) ", workout recorded" else ", no workouts"
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                day.toString(),
                                color = if (hasWorkout) colors.onPrimary else colors.onSurface,
                                fontWeight = if (date == today || hasWorkout) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
}
