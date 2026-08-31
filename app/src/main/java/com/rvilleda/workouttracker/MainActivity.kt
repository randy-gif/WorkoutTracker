package com.rvilleda.workouttracker

import android.content.Context
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
import androidx.datastore.core.DataStore // <-- Add this
import androidx.datastore.preferences.core.Preferences // <-- Add this
import androidx.datastore.preferences.preferencesDataStore // <-- Add this
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rvilleda.workouttracker.ui.WorkoutTrackerApp
import com.rvilleda.workouttracker.ui.theme.WorkoutTrackerTheme
import com.rvilleda.workouttracker.data.database.WorkoutDatabase
import com.rvilleda.workouttracker.model.AppTheme
import com.rvilleda.workouttracker.ui.screens.settings.SettingsViewModel
import com.rvilleda.workouttracker.data.repository.DashboardPreferencesRepository

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "dashboard_prefs")
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 2. FIX: Use the Singleton we created!
        val db = WorkoutDatabase.getDatabase(applicationContext)

        // Grab both DAOs
        val workoutDao = db.workoutDao()
        val exerciseDao = db.exerciseDao()
        val routineDao = db.routineDao()

        val dashboardPreferencesRepository = DashboardPreferencesRepository(applicationContext.dataStore)

        enableEdgeToEdge()

        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()
            val themeState by settingsViewModel.globalTheme.collectAsState()

            val useDarkTheme = when (themeState) {
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
                AppTheme.SYSTEM -> isSystemInDarkTheme()
            }

            WorkoutTrackerTheme(darkTheme = useDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 3. FIX: Pass both DAOs into the App
                    WorkoutTrackerApp(
                        workoutDao = workoutDao,
                        exerciseDao = exerciseDao,
                        routineDao = routineDao,
                        dashboardPreferencesRepository = dashboardPreferencesRepository
                    )
                }
            }
        }
    }
}