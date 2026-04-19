package com.rvilleda.workouttracker.ui.screens.workoutdetails

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rvilleda.workouttracker.data.database.CompletedWorkoutEntity
import com.rvilleda.workouttracker.data.database.WorkoutDao
import com.rvilleda.workouttracker.model.ExerciseInSession
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailsScreen(
    workoutId: String,
    workoutDao: WorkoutDao,
    onBack: () -> Unit,
    onWorkoutAgain: (List<ExerciseInSession>) -> Unit,
    onDeleteWorkout: () -> Unit
) {


    val coroutineScope = rememberCoroutineScope()
    var workout by remember { mutableStateOf<CompletedWorkoutEntity?>(null) }
    var exercises by remember { mutableStateOf<List<ExerciseInSession>>(emptyList()) }

    // 1. Fetch the exact workout when the screen opens
    LaunchedEffect(workoutId) {
        val fetchedWorkout = workoutDao.getWorkoutById(workoutId)
        if (fetchedWorkout != null) {
            workout = fetchedWorkout
            val type = object : TypeToken<List<ExerciseInSession>>() {}.type
            exercises = Gson().fromJson(fetchedWorkout.exercisesJson, type)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Workout Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        bottomBar = {
            Row(Modifier.fillMaxWidth()) {
                OutlinedButton (
                    onClick = { onWorkoutAgain(exercises) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                ) {
                    Text("Workout Again")
                }

                OutlinedButton(
                    onClick = { onDeleteWorkout() },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                ) {
                    Text("Delete")
                }
            }
        }
    ) { padding ->
        if (workout == null) {
            CircularProgressIndicator(modifier = Modifier.padding(padding))
            return@Scaffold
        }

        val dateFormatted = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            .format(Date(workout!!.dateCompleted))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(text = "Completed on $dateFormatted", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Draw every exercise and its sets!
            items(exercises, key = { it.id }) { exercise ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = exercise.exerciseName, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))

                        exercise.sets.forEachIndexed { index, set ->
                            Text(text = "Set ${index + 1}: ${set.weight} lbs x ${set.reps} reps")
                        }
                    }
                }
            }
        }
    }
}