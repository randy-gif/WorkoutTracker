package com.rvilleda.workouttracker.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rvilleda.workouttracker.R
import com.rvilleda.workouttracker.ui.navigation.TopTab
import com.rvilleda.workouttracker.ui.screens.home.components.TopTabs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topBarState)

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: TopTab.FOR_YOU.route


    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .padding(top = 8.dp),
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Image(
                            painter = painterResource(R.drawable.workout_tracker_title),
                            contentDescription = "Title"
                        )
                    },
                    navigationIcon = {
                        Image(
                            painter = painterResource(R.drawable.workout_tracker_logo),
                            contentDescription = "Logo"
                        )
                    },
                    actions = {
                        IconButton(
                            onClick = { /* doSomething() */ },
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_account),
                                contentDescription = "Account",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
                TopTabs(navController = navController, currentRoute = currentRoute)
            }
        }, content = { padding ->
            NavHost(
                navController = navController,
                startDestination = TopTab.FOR_YOU.route,
                modifier = Modifier.padding(padding)
            ) {
                composable(TopTab.FOR_YOU.route) {
                    ForYouTab()
                }
                composable(TopTab.TOP_EXERCISES.route) {
                    PlaceholderTab("Top Exercises Content")
                }
                composable(TopTab.TOP_ROUTINES.route) {
                    PlaceholderTab("Top Routines Content")
                }
                composable(TopTab.TUTORIALS.route) {
                    PlaceholderTab("Tutorials Content")
                }
            }
        }
    )
}