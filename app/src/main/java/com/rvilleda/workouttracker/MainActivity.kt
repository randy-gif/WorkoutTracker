package com.rvilleda.workouttracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rvilleda.workouttracker.ui.theme.WorkoutTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WorkoutTrackerTheme {
                WorkoutTrackerApp()
            }
        }
    }
}


@Preview
@Composable
fun WorkoutTrackerApp() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }


    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            painter = painterResource(it.icon),
                            contentDescription = it.label
                        )
                    },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {
        when (currentDestination) {
            AppDestinations.HOME -> HomeScreen()
            AppDestinations.EXERCISES -> ExercisesScreen()
            AppDestinations.EXERCISES_DATA -> ExercisesDataScreen()
        }
    }
}

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

@Composable
fun ForYouTab() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(20) {
            ExerciseCard()
        }
    }
}

@Composable
fun PlaceholderTab(text: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text)
    }
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

@Composable
fun ExercisesDataScreen() {
    Text("Exercises Data Screen")
}

@Composable
fun ExercisesScreen() {
    Text("Exercises Screen")
}

@Composable
fun ExerciseCard() {
    Text("Exercise Card", modifier = Modifier.padding(16.dp))
}

enum class TopTab(val route: String, val label: String) {
    FOR_YOU("for_you", "For You"),
    TOP_EXERCISES("top_exercises", "Top Exercises"),
    TOP_ROUTINES("top_routines", "Top Routines"),
    TUTORIALS("tutorials", "Tutorials")
}

enum class AppDestinations(
    val label: String,
    val icon: Int,
) {
    HOME("Home", R.drawable.ic_home),
    EXERCISES("Exercises", R.drawable.ic_exercises),
    EXERCISES_DATA("Chart", R.drawable.ic_exercises_data),
}
