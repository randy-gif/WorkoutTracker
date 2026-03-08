package com.rvilleda.workouttracker.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rvilleda.workouttracker.model.Exercise
import com.rvilleda.workouttracker.model.allExercises
import com.rvilleda.workouttracker.ui.components.ExerciseCard



@Composable
fun ForYouTab() {

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(allExercises.size) { index ->
            ExerciseCard(
                exercise = allExercises[index],
                onClick = { /* Handle exercise card click */ }
            )
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