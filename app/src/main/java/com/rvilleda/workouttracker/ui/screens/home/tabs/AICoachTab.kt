package com.rvilleda.workouttracker.ui.screens.home.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rvilleda.workouttracker.data.repository.CoachFacts
import com.rvilleda.workouttracker.model.CoachModel
import com.rvilleda.workouttracker.model.WeightUnit
import com.rvilleda.workouttracker.ui.screens.home.CoachViewModel

@Composable
fun AICoach(viewModel: CoachViewModel, unit: WeightUnit) {
    val state by viewModel.state.collectAsState()
    val workouts by viewModel.workouts.collectAsState()
    var question by rememberSaveable { mutableStateOf("") }
    var showSetup by rememberSaveable { mutableStateOf(false) }
    val summary = remember(workouts, unit) { CoachFacts.summary(workouts, unit) }
    val listState = rememberLazyListState()
    LaunchedEffect(state.messages.size, state.busy) {
        if (state.messages.isNotEmpty()) listState.animateScrollToItem((listState.layoutInfo.totalItemsCount - 1).coerceAtLeast(0))
    }
    Column(Modifier.fillMaxSize().imePadding().padding(horizontal = 16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("AI Coach", style = MaterialTheme.typography.headlineSmall)
            TextButton(onClick = { showSetup = !showSetup }) { Text("Models") }
            TextButton(onClick = viewModel::clear, enabled = !state.busy) { Text("New chat") }
        }
        LazyColumn(state = listState, modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(vertical = 12.dp)) {
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Workout summary · Calculated from your log", style = MaterialTheme.typography.titleSmall)
                        Text(summary)
                    }
                }
            }
            if (viewModel.device.recommended == null) {
                item { Text("This phone doesn't meet the local coach's current requirements. Your workout summary works without an AI model.") }
            } else if (showSetup || state.selected !in state.installed) {
                item {
                    Text("Private, on-device answers", style = MaterialTheme.typography.titleMedium)
                    Text("Download a model once, then chat offline. No questions or workout data are sent to an AI service. Downloads use internet data; Wi-Fi is recommended.")
                }
                itemsIndexed(CoachModel.entries) { _, model ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("${model.label} · ${model.sizeLabel}" +
                                if (viewModel.device.recommended == model) " · Recommended" else "",
                                style = MaterialTheme.typography.titleMedium)
                            Text(if (model == CoachModel.STANDARD) "Smaller download and lower memory use." else "Larger model for more capable phones. Uses more memory and may take longer.")
                            Text(if (viewModel.device.supports(model)) "Performance depends on available memory and device drivers."
                                else "Requires a compatible 64-bit phone with approximately ${model.minimumRamGb} GB RAM.",
                                style = MaterialTheme.typography.bodySmall)
                            TextButton(onClick = { viewModel.select(model) },
                                enabled = !state.busy && viewModel.device.supports(model) && state.selected != model
                            ) { Text(if (state.selected == model) "Selected" else "Select") }
                        }
                    }
                }
                item {
                    if (state.selected in state.installed) {
                        OutlinedButton(onClick = viewModel::remove, enabled = !state.busy) { Text("Remove ${state.selected.label} download") }
                    } else {
                        Button(onClick = viewModel::download, enabled = !state.busy) { Text("Download ${state.selected.label} · ${state.selected.sizeLabel}") }
                    }
                    if (state.downloading) {
                        LinearProgressIndicator(progress = { state.progress }, modifier = Modifier.fillMaxWidth())
                        Text("Downloading ${(state.progress * 100).toInt()}% · Keep the app open")
                    }
                }
            }
            if (state.messages.isEmpty() && state.selected in state.installed) {
                item {
                    Text("Ask about your consistency, recent sessions, or exercise progress. Answers use a limited selection of your workout log and can make mistakes.")
                    TextButton(onClick = { question = "How consistent have I been over the past 30 days?" }) { Text("Review my consistency") }
                    TextButton(onClick = { question = "How has my bench press been going recently?" }) { Text("Review my bench press") }
                }
            }
            itemsIndexed(state.messages) { _, message ->
                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
                    containerColor = if (message.user) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(Modifier.padding(12.dp)) {
                        Text(if (message.user) "You" else "Coach", style = MaterialTheme.typography.labelMedium)
                        SelectionContainer { Text(message.text.ifBlank { "Loading model and reading your workout log…" }) }
                    }
                }
            }
            state.error?.let { error -> item { Text(error, color = MaterialTheme.colorScheme.error) } }
        }
        if (state.busy) {
            TextButton(onClick = viewModel::stop) { Text(if (state.downloading) "Cancel download" else "Stop answer") }
        }
        if (viewModel.device.recommended != null) {
            Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = question,
                    onValueChange = { if (it.toByteArray(Charsets.UTF_8).size <= 500) question = it },
                    modifier = Modifier.weight(1f), placeholder = { Text("Ask about your workouts") },
                    maxLines = 3, enabled = !state.busy)
                Button(onClick = { if (viewModel.send(question, unit)) question = "" },
                    enabled = !state.busy && question.isNotBlank() && state.selected in state.installed
                ) { Text("Send") }
            }
        }
    }
}
