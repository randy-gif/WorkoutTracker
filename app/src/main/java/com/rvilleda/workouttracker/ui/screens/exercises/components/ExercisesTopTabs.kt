package com.rvilleda.workouttracker.ui.screens.exercises.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

enum class ExercisesTabs(val route: String, val label: String) {
    CHEST("chest", "Chest"),
    BACK("back", "Back"),
    LEGS("legs", "Legs"),
    SHOULDERS("shoulders", "Shoulders"),
    ARMS("arms", "Arms"),
    CORE("core", "Core"),
}


@Composable
fun TabRowHeader(
    currentTab: ExercisesTabs,
    onTabSelected: (ExercisesTabs) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = currentTab.ordinal,
        edgePadding = 8.dp
    ) {
        // We loop through all the options in your Enum to build the tabs
        ExercisesTabs.entries.forEach { tab ->
            Tab(
                selected = currentTab == tab,
                onClick = { onTabSelected(tab) },
                text = { Text(text = tab.name) }
            )
        }
    }
}