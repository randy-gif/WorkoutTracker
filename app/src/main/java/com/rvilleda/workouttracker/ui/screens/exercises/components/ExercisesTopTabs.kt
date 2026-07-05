package com.rvilleda.workouttracker.ui.screens.exercises.components

import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

enum class ExercisesTopTabs(val route: String, val label: String) {
    CHEST("chest", "Chest"),
    BACK("back", "Back"),
    LEGS("legs", "Legs"),
    SHOULDERS("shoulders", "Shoulders"),
    ARMS("arms", "Arms"),
    CORE("core", "Core"),
}


@Composable
fun TabRowHeader(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        edgePadding = 8.dp
    ) {
        ExercisesTopTabs.entries.forEachIndexed { index, tab ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) },
                text = { Text(text = tab.name) }
            )
        }
    }
}