package com.rvilleda.workouttracker.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rvilleda.workouttracker.model.AppTheme
import com.rvilleda.workouttracker.model.WeightUnit // Adjust this import based on your actual data model!

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentUnit: WeightUnit,
    onUnitChanged: (WeightUnit) -> Unit,
    currentTheme: AppTheme,
    onThemeChanged: (AppTheme) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section Header
            Text(
                text = "Preferences",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            // The Unit Toggle Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Global Weight Unit",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "This sets the default unit for new workouts.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // A clean, selectable group for the two options
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectableGroup(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // LBS Option
                        SettingsOptionChip(
                            text = "LBS",
                            isSelected = currentUnit == WeightUnit.LBS,
                            onClick = { onUnitChanged(WeightUnit.LBS) },
                            modifier = Modifier.weight(1f)
                        )

                        // KG Option
                        SettingsOptionChip(
                            text = "KG",
                            isSelected = currentUnit == WeightUnit.KG,
                            onClick = { onUnitChanged(WeightUnit.KG) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "App Theme",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth().selectableGroup(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Reusing your chip composable!
                        SettingsOptionChip(
                            text = AppTheme.SYSTEM.displayName,
                            isSelected = currentTheme == AppTheme.SYSTEM,
                            onClick = { onThemeChanged(AppTheme.SYSTEM) },
                            modifier = Modifier.weight(1f)
                        )
                        SettingsOptionChip(
                            text = AppTheme.LIGHT.displayName,
                            isSelected = currentTheme == AppTheme.LIGHT,
                            onClick = { onThemeChanged(AppTheme.LIGHT) },
                            modifier = Modifier.weight(1f)
                        )
                        SettingsOptionChip(
                            text = AppTheme.DARK.displayName,
                            isSelected = currentTheme == AppTheme.DARK,
                            onClick = { onThemeChanged(AppTheme.DARK) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

// A custom reusable chip design for the selection options
@Composable
private fun SettingsOptionChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }

    val textColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Surface(
        modifier = modifier.selectable(
            selected = isSelected,
            onClick = onClick,
            role = Role.RadioButton
        ),
        shape = MaterialTheme.shapes.medium,
        color = backgroundColor,
        tonalElevation = if (isSelected) 0.dp else 1.dp
    ) {
        Box(
            modifier = Modifier.padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = textColor,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}