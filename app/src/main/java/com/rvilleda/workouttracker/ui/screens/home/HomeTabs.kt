package com.rvilleda.workouttracker.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.rvilleda.workouttracker.data.database.CompletedWorkoutEntity
import com.rvilleda.workouttracker.model.Exercise
import com.rvilleda.workouttracker.model.ExerciseInSession
import com.rvilleda.workouttracker.model.allExercises
import com.rvilleda.workouttracker.ui.components.ExerciseCard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.collections.joinToString
import androidx.compose.foundation.lazy.items
import com.google.gson.reflect.TypeToken



@Composable
fun ForYouTab(
    workouts: List<CompletedWorkoutEntity>,
    onPastWorkoutClick: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Recent Workouts",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(workouts, key = { it.id }) { workoutEntity ->

                // 3. Unpack the JSON back into a Kotlin List
                val type = object : TypeToken<List<ExerciseInSession>>() {}.type
                val exercises: List<ExerciseInSession> = Gson().fromJson(workoutEntity.exercisesJson, type)

                // 4. Format the timestamp into a readable date
                val dateFormatted = SimpleDateFormat("MMM dd, yyyy - h:mm a", Locale.getDefault())
                    .format(Date(workoutEntity.dateCompleted))

                // 5. Draw a simple card for each workout
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPastWorkoutClick(workoutEntity.id) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = dateFormatted, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(text = "Total Exercises: ${exercises.size}")

                        val exerciseNames = exercises.joinToString { it.exerciseName }
                        Text(
                            text = exerciseNames,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PlaceholderTab(text: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text)
    }
}