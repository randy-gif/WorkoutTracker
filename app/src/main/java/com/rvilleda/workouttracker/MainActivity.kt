package com.rvilleda.workouttracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.rvilleda.workouttracker.ui.WorkoutTrackerApp
import com.rvilleda.workouttracker.ui.theme.WorkoutTrackerTheme
import androidx.room.Room
import com.rvilleda.workouttracker.data.database.WorkoutDatabase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(
            applicationContext,
            WorkoutDatabase::class.java,
            "workout-database"
        ).build()

        enableEdgeToEdge()

        setContent {
            WorkoutTrackerTheme {
                WorkoutTrackerApp(workoutDao = db.workoutDao())
            }
        }
    }
}