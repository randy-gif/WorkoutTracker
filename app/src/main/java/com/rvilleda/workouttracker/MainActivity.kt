package com.rvilleda.workouttracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.rvilleda.workouttracker.ui.WorkoutTrackerApp
import com.rvilleda.workouttracker.ui.theme.WorkoutTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WorkoutTrackerTheme {
                WorkoutTrackerApp()
            }
        }
    }
}