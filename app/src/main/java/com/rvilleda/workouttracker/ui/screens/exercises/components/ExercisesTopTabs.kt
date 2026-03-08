package com.rvilleda.workouttracker.ui.screens.exercises.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

enum class TopTab(val route: String, val label: String) {
    CHEST("chest", "Chest"),
    BACK("back", "Back"),
    LEGS("legs", "Legs"),
    SHOULDERS("shoulders", "Shoulders"),
    ARMS("arms", "Arms"),
    CORE("core", "Core"),
    CARDIO("cardio", "Cardio")
}

@Composable
fun TopTabs(navController: NavHostController, currentRoute: String?) {
    val selectedIndex = TopTab.entries.indexOfFirst { it.route == currentRoute }.coerceAtLeast(0)

    ScrollableTabRow(
        selectedTabIndex = selectedIndex,
        modifier = Modifier.fillMaxWidth(),
        edgePadding = 6.dp
    ) {
        TopTab.entries.forEachIndexed { index, tab ->
            Tab(
                selected = index == selectedIndex,
                onClick = {
                    if (currentRoute != tab.route) {
                        navController.navigate(tab.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                text = { Text(tab.label) }
            )
        }
    }
}