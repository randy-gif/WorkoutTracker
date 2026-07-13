package com.rvilleda.workouttracker.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import com.rvilleda.workouttracker.R
import com.rvilleda.workouttracker.ui.screens.home.components.TabRowHeader
import com.rvilleda.workouttracker.ui.screens.home.components.HomeTopTabs
import com.rvilleda.workouttracker.ui.screens.home.tabs.AICoach
import com.rvilleda.workouttracker.ui.screens.home.tabs.DashboardTab
import com.rvilleda.workouttracker.ui.screens.home.tabs.ProgressTab
import com.rvilleda.workouttracker.ui.screens.home.tabs.RoutinesTab
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onStartRoutineClick: (String) -> Unit,
    onEditRoutineClick: (String) -> Unit,
    onCreateRoutineClick: () -> Unit
) {

    val workouts by viewModel.savedWorkouts.collectAsState()


    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topBarState)

    val pagerState = rememberPagerState(pageCount = { HomeTopTabs.entries.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
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
                TabRowHeader(
                    selectedTabIndex = pagerState.currentPage,
                    onTabSelected = { index ->
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                )
            }
        }, content = { padding ->
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) { page ->
                when(HomeTopTabs.entries[page]) {
                    HomeTopTabs.DASHBOARD -> DashboardTab()
                    HomeTopTabs.ROUTINES -> RoutinesTab(onCreateRoutineClick, onStartRoutineClick, onEditRoutineClick, viewModel)
                    HomeTopTabs.PROGRESS -> ProgressTab()
                    HomeTopTabs.AI_COACH -> AICoach()
                }
            }

        }
    )
}