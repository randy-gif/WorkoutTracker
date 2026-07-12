package com.rvilleda.workouttracker.ui.screens.home.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun RoutinesTab(
    onCreateRoutineClick: () -> Unit,
    // Future parameters you will need:
    // routines: List<RoutineEntity>,
    // onStartRoutine: (routineId: String) -> Unit,
    // onEditRoutine: (routineId: String) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 88.dp), // Bottom padding ensures the FAB doesn't hide the last item
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // --- SECTION: MY ROUTINES ---
            item {
                Text(
                    text = "My Routines",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // TODO: Replace these hardcoded items with a real list -> items(routines) { routine -> ... }
            item {
                RoutineCard(
                    title = "Push Day",
                    subtitle = "Chest, Shoulders, Triceps",
                    onStartClick = { /* TODO */ },
                    onEditClick = { /* TODO */ }
                )
            }
            item {
                RoutineCard(
                    title = "Pull Day",
                    subtitle = "Back, Biceps, Rear Delts",
                    onStartClick = { /* TODO */ },
                    onEditClick = { /* TODO */ }
                )
            }
            item {
                RoutineCard(
                    title = "Leg Day",
                    subtitle = "Quads, Hamstrings, Glutes, Calves",
                    onStartClick = { /* TODO */ },
                    onEditClick = { /* TODO */ }
                )
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }

            // --- SECTION: BUILT-IN TEMPLATES (Future Feature) ---
            item {
                Text(
                    text = "Example Templates",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Start with a proven program",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                )
            }

            item {
                RoutineCard(
                    title = "Full Body Beginner",
                    subtitle = "3 Days / Week • Focus on compound movements",
                    onStartClick = { /* TODO */ },
                    onEditClick = { /* TODO */ },
                    isTemplate = true
                )
            }
        }

        // The Create Routine Button
        ExtendedFloatingActionButton(
            onClick = onCreateRoutineClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            icon = { Icon(Icons.Filled.Add, contentDescription = "Create Routine") },
            text = { Text("New Routine") },
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun RoutineCard(
    title: String,
    subtitle: String,
    onStartClick: () -> Unit,
    onEditClick: () -> Unit,
    isTemplate: Boolean = false
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isTemplate) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isTemplate) 0.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.offset(x = 8.dp, y = (-8).dp) // Adjusts alignment so it sits perfectly in the corner
                    ) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Options")
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        if (isTemplate) {
                            DropdownMenuItem(
                                text = { Text("Duplicate to My Routines") },
                                onClick = {
                                    menuExpanded = false
                                    /* TODO */
                                }
                            )
                        } else {
                            DropdownMenuItem(
                                text = { Text("Edit Routine") },
                                onClick = {
                                    onEditClick()
                                    menuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Share Routine") },
                                onClick = {
                                    menuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                                onClick = {
                                    menuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onStartClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.PlayArrow, contentDescription = "Start Workout")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Start Routine")
            }
        }
    }
}