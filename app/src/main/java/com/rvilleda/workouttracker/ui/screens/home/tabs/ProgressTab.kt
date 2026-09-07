package com.rvilleda.workouttracker.ui.screens.home.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.produceState
import kotlinx.coroutines.delay
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rvilleda.workouttracker.model.ExerciseTrendSlot
import androidx.compose.foundation.lazy.items
import com.rvilleda.workouttracker.model.WeightUnit
import com.rvilleda.workouttracker.ui.screens.home.HomeViewModel
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.entryModelOf
import androidx.compose.ui.graphics.Color
import com.patrykandpatrick.vico.compose.chart.line.lineSpec
import com.patrykandpatrick.vico.compose.component.shape.shader.verticalGradient
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import android.graphics.Typeface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.patrykandpatrick.vico.compose.component.lineComponent
import com.patrykandpatrick.vico.compose.component.shapeComponent
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.compose.dimensions.dimensionsOf
import com.patrykandpatrick.vico.core.component.marker.MarkerComponent
import com.patrykandpatrick.vico.core.component.shape.DashedShape
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.marker.Marker
import com.patrykandpatrick.vico.core.marker.MarkerLabelFormatter
import com.patrykandpatrick.vico.core.chart.values.ChartValues
import com.patrykandpatrick.vico.compose.axis.axisLabelComponent
import com.patrykandpatrick.vico.core.axis.AxisItemPlacer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.rvilleda.workouttracker.ui.screens.home.TimeRange
import com.rvilleda.workouttracker.ui.screens.home.components.RecentRecordsCard
import com.rvilleda.workouttracker.ui.screens.home.components.WorkoutCalendarCard
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Date

@Composable
fun ProgressTab(
    globalUnit: WeightUnit,
    onChangeExerciseTrend: (ExerciseTrendSlot) -> Unit,
    onNavigateToHistory: () -> Unit,
    viewModel: HomeViewModel
) {
    var selectedTimeRange by remember { mutableStateOf(TimeRange.MONTH) }
    var isTimeRangeDropdownExpanded by remember { mutableStateOf(false) }


    val workoutsCount by viewModel.workoutsCount.collectAsState(0)
    val totalVolume by viewModel.totalVolume.collectAsState(0.0)

    // 1. Collect the newly created flows from the ViewModel
    val savedWorkouts by viewModel.savedWorkouts.collectAsState()
    val recentRecords by viewModel.recentRecords.collectAsState()
    val context = LocalContext.current.applicationContext
    val now by produceState(initialValue = System.currentTimeMillis()) {
        while (true) {
            value = System.currentTimeMillis()
            delay(30_000)
        }
    }
    LaunchedEffect(viewModel, context) { viewModel.initializeProgress(context) }

    LaunchedEffect(globalUnit) {
        viewModel.updateUnitPreference(globalUnit)
    }

    LaunchedEffect(selectedTimeRange) {
        val calendar = Calendar.getInstance()

        val startTimestamp = when (selectedTimeRange) {
            TimeRange.DAY -> {
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                calendar.timeInMillis
            }
            TimeRange.WEEK -> {
                calendar.add(Calendar.DAY_OF_YEAR, -7)
                calendar.timeInMillis
            }
            TimeRange.MONTH -> {
                calendar.add(Calendar.MONTH, -1)
                calendar.timeInMillis
            }
            TimeRange.YEAR -> {
                calendar.add(Calendar.YEAR, -1)
                calendar.timeInMillis
            }
            TimeRange.FIVE_YEARS -> {
                calendar.add(Calendar.YEAR, -5)
                calendar.timeInMillis
            }
            TimeRange.ALL_TIME -> {
                0L
            }
        }

        // 2. Pass BOTH the selected range and timestamp to the ViewModel
        viewModel.updateSelectedTimeRange(selectedTimeRange, startTimestamp)
    }

    val formattedVolume = viewModel.formatVolume(totalVolume)

    // 3. Dynamic Date Formatter based on Time Range
    val dateFormatter = remember(selectedTimeRange) {
        when (selectedTimeRange) {
            TimeRange.FIVE_YEARS, TimeRange.ALL_TIME -> SimpleDateFormat("MMM yyyy", Locale.getDefault())
            else -> SimpleDateFormat("MMM dd", Locale.getDefault())
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Overview",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Box {
                    TextButton(onClick = { isTimeRangeDropdownExpanded = true }) {
                        Text(text = selectedTimeRange.progressLabel())
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select Time Range"
                        )
                    }

                    DropdownMenu(
                        expanded = isTimeRangeDropdownExpanded,
                        onDismissRequest = { isTimeRangeDropdownExpanded = false }
                    ) {
                        TimeRange.values().forEach { range ->
                            DropdownMenuItem(
                                text = { Text(range.progressLabel()) },
                                onClick = {
                                    selectedTimeRange = range
                                    isTimeRangeDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    title = "Workouts",
                    value = workoutsCount.toString(),
                    subtitle = selectedTimeRange.progressLabel(),
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    title = "Volume",
                    value = formattedVolume,
                    subtitle = "$globalUnit Lifted",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            WorkoutCalendarCard(
                workouts = savedWorkouts,
                now = now,
                onClick = onNavigateToHistory
            )
        }
        item { RecentRecordsCard(recentRecords, globalUnit) }

        item {
            Text(
                text = "Exercise Trends",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
        items(ExerciseTrendSlot.entries, key = { it.name }) { slot ->
            val trendExercisePreference by viewModel.trendExercisePreferences.getValue(slot).collectAsState()
            val oneRepMaxTrend by viewModel.oneRMTrends.getValue(slot).collectAsState()
            val trendMarker = rememberMarker(unitLabel = globalUnit.name)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = trendExercisePreference.second,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Estimated 1-Rep Max",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        TextButton(onClick = { onChangeExerciseTrend(slot) }) {
                            Text("Change")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val trendOneRepMax = remember(oneRepMaxTrend) {
                        if (oneRepMaxTrend.isEmpty()) null else {
                            val entries = oneRepMaxTrend.mapIndexed { index, dataPoint ->
                                FloatEntry(
                                    x = index.toFloat(),
                                    y = dataPoint.estimatedMax.toFloat()
                                )
                            }
                            entryModelOf(entries)
                        }
                    }

                    val primaryColor = MaterialTheme.colorScheme.primary

                    if (trendOneRepMax == null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Not enough data for this time range.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        Chart(
                            chart = lineChart(
                                lines = listOf(
                                    lineSpec(
                                        lineColor = primaryColor,
                                        lineThickness = 3.dp,
                                        lineBackgroundShader = verticalGradient(
                                            colors = arrayOf(primaryColor.copy(alpha = 0.4f), Color.Transparent)
                                        ),
                                        point = shapeComponent(
                                            shape = Shapes.pillShape,
                                            color = primaryColor,
                                        ),
                                        pointSize = 8.dp
                                    )
                                ),
                                axisValuesOverrider = AxisValuesOverrider.adaptiveYValues(yFraction = 1.05f, round = true)
                            ),
                            model = trendOneRepMax,
                            marker = trendMarker,
                            startAxis = rememberStartAxis(
                                label = axisLabelComponent(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                valueFormatter = { value, _ -> "${value.toInt()} ${globalUnit.name}" }
                            ),
                            bottomAxis = rememberBottomAxis(
                                guideline = null,
                                label = axisLabelComponent(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                // 6. Shared date formatter for 1RM chart too
                                valueFormatter = { value, _ ->
                                    val index = value.toInt()
                                    val dataPoint = oneRepMaxTrend.getOrNull(index)

                                    if (dataPoint != null) {
                                        dateFormatter.format(Date(dataPoint.startTime))
                                    } else {
                                        ""
                                    }
                                }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun rememberMarker(unitLabel: String = ""): Marker {
    val labelBackground = shapeComponent(
        shape = Shapes.pillShape,
        color = MaterialTheme.colorScheme.primary
    )

    val label = textComponent(
        background = labelBackground,
        color = MaterialTheme.colorScheme.onPrimary,
        padding = dimensionsOf(8.dp, 4.dp),
        typeface = Typeface.DEFAULT_BOLD
    )

    val indicator = shapeComponent(
        shape = Shapes.pillShape,
        color = MaterialTheme.colorScheme.primary
    )

    val guideline = lineComponent(
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
        thickness = 2.dp,
        shape = DashedShape(Shapes.pillShape, dashLengthDp = 8f, gapLengthDp = 4f)
    )

    return remember(label, indicator, guideline, unitLabel) {
        object : MarkerComponent(label, indicator, guideline) {
            init {
                indicatorSizeDp = 12f

                labelFormatter = object : MarkerLabelFormatter {
                    override fun getLabel(
                        markedEntries: List<Marker.EntryModel>,
                        chartValues: ChartValues
                    ): CharSequence {
                        val yValue = markedEntries.firstOrNull()?.entry?.y ?: 0f
                        return if (unitLabel.isNotBlank()) {
                            "${yValue.toInt()} $unitLabel"
                        } else {
                            yValue.toInt().toString()
                        }
                    }
                }
            }
        }
    }
}

private fun TimeRange.progressLabel(): String = when (this) {
    TimeRange.DAY -> "Past day"
    TimeRange.WEEK -> "Past week"
    TimeRange.MONTH -> "Past month"
    TimeRange.YEAR -> "Past year"
    TimeRange.FIVE_YEARS -> "Past 5 years"
    TimeRange.ALL_TIME -> "All time"
}

