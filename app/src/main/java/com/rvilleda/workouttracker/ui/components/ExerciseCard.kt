package com.rvilleda.workouttracker.ui.components


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rvilleda.workouttracker.model.Exercise


@OptIn(ExperimentalFoundationApi::class) // Required for combinedClickable
@Composable
fun ExerciseCard(
    exercise: Exercise,
    isSelectionModeActive: Boolean, // 1. Tells the card if we are in "Selection Mode"
    isSelected: Boolean,            // 2. Tells the card if THIS specific item is selected
    onItemClick: () -> Unit,        // 3. Action for normal tap
    onItemLongClick: () -> Unit,    // 4. Action for press-and-hold
    modifier: Modifier = Modifier
) {
    // 5. Dynamic Colors: Only highlight if Selection Mode is ON and this item is SELECTED
    val isHighlighted = isSelectionModeActive && isSelected

    val backgroundColor = if (isHighlighted) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val contentColor = if (isHighlighted) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    // 6. Dynamic Icon: Changes based on the current mode
    val icon = when {
        isHighlighted -> Icons.Default.CheckCircle // Selected state
        isSelectionModeActive -> Icons.Default.AddCircle // Unselected state while selecting
        else -> Icons.AutoMirrored.Filled.KeyboardArrowRight // Default viewing state
    }

    val iconTint = if (isHighlighted) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline
    }

    // We use a standard ElevatedCard (no onClick parameter) and apply the clicks to the modifier
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(CardDefaults.elevatedShape) // Ensures the ripple effect stays inside the rounded corners
            .combinedClickable(
                onClick = onItemClick,
                onLongClick = onItemLongClick
            ),
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
                    color = contentColor
                )
                Spacer(modifier = Modifier.height(4.dp))

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

            // Right Side: Dynamic Icon
            Icon(
                imageVector = icon,
                contentDescription = when {
                    isHighlighted -> "Selected"
                    isSelectionModeActive -> "Select"
                    else -> "View Details"
                },
                modifier = Modifier.padding(start = 8.dp),
                tint = iconTint
            )
        }
    }
}

@Composable
fun CreateExerciseCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        // Locks in the base surface color to match unselected exercises
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Side: Call to Action Text
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Create New Exercise",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Replacing the muscle group chip with a subtle helper text
                Text(
                    text = "Add a custom movement to your library",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Right Side: Add Icon
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Create Custom Exercise",
                modifier = Modifier.padding(start = 8.dp),
                // Using the primary color makes this stand out as an action button
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}