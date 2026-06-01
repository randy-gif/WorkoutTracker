package com.rvilleda.workouttracker.ui.screens.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import com.rvilleda.workouttracker.ui.screens.history.components.WorkoutCalendar
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rvilleda.workouttracker.ui.screens.history.components.WorkoutHistoryCard
import java.time.format.DateTimeFormatter

@Composable
fun HistoryScreen(
    onNavigateToWorkoutDetails: (String) -> Unit,
    viewModel: HistoryViewModel
) {
    // Collect our states from the ViewModel
    val workoutDates by viewModel.workoutDates.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val dailyWorkouts by viewModel.dailyWorkouts.collectAsState()

    // Format the date to look nice (e.g., "May 8, 2026")
    val dateFormatter = remember { DateTimeFormatter.ofPattern("MMMM d, yyyy") }

    Scaffold { padding ->
        Column(modifier = Modifier.padding(padding)) {

            WorkoutCalendar(
                workoutDates = workoutDates,
                onDateSelected = { date ->
                    // Tell the ViewModel to update the selected date!
                    viewModel.updateSelectedDate(date)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Section Header
            Text(
                text = "Workouts on ${selectedDate.format(dateFormatter)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // The List (or Empty State)
            if (dailyWorkouts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Rest Day! No workouts found.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(dailyWorkouts, key = { it.id }) { workout ->
                        // Build a simple card for each workout
                        WorkoutHistoryCard(
                            workoutName = workout.name,
                            durationMs = workout.durationMs,
                            onClick = {
                                onNavigateToWorkoutDetails(workout.id)
                            }
                        )
                    }
                }
            }
        }
    }
}