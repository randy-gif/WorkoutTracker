package com.rvilleda.workouttracker.ui.screens.home.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

enum class TopTab(val route: String, val label: String) {
    FOR_YOU("for_you", "For You"),
    TOP_EXERCISES("top_exercises", "Top Exercises"),
    TOP_ROUTINES("top_routines", "Top Routines"),
    TUTORIALS("tutorials", "Tutorials")
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