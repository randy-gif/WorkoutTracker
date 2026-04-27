package com.rvilleda.workouttracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rvilleda.workouttracker.ui.WorkoutTrackerApp
import com.rvilleda.workouttracker.ui.theme.WorkoutTrackerTheme
import androidx.room.Room
import com.rvilleda.workouttracker.data.database.WorkoutDatabase
import com.rvilleda.workouttracker.model.AppTheme
import com.rvilleda.workouttracker.ui.screens.settings.SettingsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(
            applicationContext,
            WorkoutDatabase::class.java,
            "workout-database"
        )
            .fallbackToDestructiveMigration()
            .build()

        enableEdgeToEdge()

        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()
            val themeState by settingsViewModel.globalTheme.collectAsState()

            // 2. Figure out if we should be using Dark Mode based on the setting
            val useDarkTheme = when (themeState) {
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
                AppTheme.SYSTEM -> isSystemInDarkTheme() // Compose's built-in system checker
            }
            WorkoutTrackerTheme(darkTheme = useDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WorkoutTrackerApp(workoutDao = db.workoutDao())
                }
            }
        }
    }
}