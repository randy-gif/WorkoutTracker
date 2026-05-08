package com.rvilleda.workouttracker.ui.screens.workoutdetails

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.font.FontWeight
import com.rvilleda.workouttracker.model.WeightUnit
import com.rvilleda.workouttracker.model.calculateTotalWorkoutVolume
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailsScreen(
    workoutId: String,
    workoutDao: WorkoutDao,
    globalUnit: WeightUnit,
    onBack: () -> Unit,
    onWorkoutAgain: (List<ExerciseInSession>) -> Unit,
    onDeleteWorkout: () -> Unit
) {


    var showDeleteDialog by remember { mutableStateOf(false) }
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

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(text = "Delete Workout?")
            },
            text = {
                Text(text = "Are you sure you want to delete this workout? This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteWorkout()
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
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
                    onClick = { showDeleteDialog = true },
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

        val dateFormatted = SimpleDateFormat("MMM dd, yyyy", LocalLocale.current.platformLocale)
            .format(Date(workout!!.dateCompleted))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Calculate the data BEFORE the item block
            val totalMinutes = workout!!.durationMs / (1000 * 60)
            val hours = totalMinutes / 60
            val minutes = totalMinutes % 60
            val durationText = if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"

            val totalSets = exercises.sumOf { it.sets.size }
            val totalVolume = exercises.calculateTotalWorkoutVolume(globalUnit)

            // NEW: Format the completion date
            val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy • h:mm a")
            val dateString = Instant.ofEpochMilli(workout!!.dateCompleted)
                .atZone(ZoneId.systemDefault())
                .format(formatter)

            // 2. The Overview Card & Date
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp, top = 16.dp)
                ) {
                    // Display the Date right above the card
                    Text(
                        text = dateString,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row (
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // Duration Stat
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Duration",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = durationText,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            // Volume Stat
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Volume",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = "${totalVolume.toInt()} ${globalUnit.name}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            // Sets Stat
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Sets",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = "$totalSets",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }

            // 3. Draw every exercise and its sets
            items(exercises, key = { it.id }) { exercise ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = exercise.exerciseName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        exercise.sets.forEachIndexed { index, set ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Set ${index + 1}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${set.weight} ${set.weightUnit.name} × ${set.reps}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}