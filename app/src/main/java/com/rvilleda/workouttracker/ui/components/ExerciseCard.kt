package com.rvilleda.workouttracker.ui.components


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rvilleda.workouttracker.model.Exercise


@Composable
fun ExerciseCard(
    exercise: Exercise,
    onClick: () -> Unit,
    isSelected: Boolean = false, // 1. ADD THIS PARAMETER WITH A DEFAULT VALUE
    modifier: Modifier = Modifier
) {
    // 2. Define the background and content colors based on the selection state
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant // A clean, explicit base color
    }

    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    // 3. Define the icon and its tint based on the selection state
    val icon = if (isSelected) {
        Icons.Default.CheckCircle
    } else {
        Icons.Default.AddCircle
    }

    val iconTint = if (isSelected) {
        MaterialTheme.colorScheme.primary // Use the thematic primary color for the checkmark
    } else {
        MaterialTheme.colorScheme.outline // The original subtle outline for the arrow
    }

    ElevatedCard(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        // 4. Apply the dynamic container color
        colors = CardDefaults.elevatedCardColors(containerColor = backgroundColor),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Side: Name and Muscle Group
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    // 5. Apply the dynamic content color for contrast
                    color = contentColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Note: The muscle group chip is a self-contained element.
                // We preserve its colors to maintain your original design.
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = exercise.muscleGroup.toString(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            // Right Side: Dynamic Icon and Tint
            Icon(
                imageVector = icon, // 6. Use the dynamic icon
                contentDescription = if (isSelected) "Selected" else "Details",
                modifier = Modifier.padding(start = 8.dp),
                tint = iconTint // 7. Use the dynamic tint
            )
        }
    }
}