package com.rvilleda.workouttracker.ui.screens.history

import com.rvilleda.workouttracker.ui.screens.history.components.WorkoutCalendar
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier

@Composable
fun HistoryScreen(viewModel: HistoryViewModel) {
    val workoutDates by viewModel.workoutDates.collectAsState()

    Scaffold { padding ->
        Column(modifier = Modifier.padding(padding)) {

            WorkoutCalendar(
                workoutDates = workoutDates,
                onDateSelected = { selectedDate ->
                    // Tell the ViewModel a date was clicked!
                    println("Selected: $selectedDate")
                }
            )

            // UI to show workouts for the selected date will go here later
        }
    }
}