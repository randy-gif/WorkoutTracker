package com.rvilleda.workouttracker.ui.screens.home.components

import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

enum class HomeTopTabs(val route: String, val label: String) {
    DASHBOARD("dashboard", "Dashboard"),
    ROUTINES("routines", "Routines"),
    PROGRESS("progress", "Progress"),
    AI_COACH("ai_coach", "AI Coach")
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
        HomeTopTabs.entries.forEachIndexed { index, tab ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) },
                text = { Text(text = tab.label) }
            )
        }
    }
}