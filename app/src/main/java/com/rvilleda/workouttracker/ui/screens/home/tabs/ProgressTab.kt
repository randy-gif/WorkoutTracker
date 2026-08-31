package com.rvilleda.workouttracker.ui.screens.home.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.Date

enum class TimeRange(val displayName: String) {
    DAY("Day"),
    WEEK("Week"),
    MONTH("Month"),
    YEAR("Year"),
    FIVE_YEARS("5 Years"),
    ALL_TIME("All Time")
}

@Composable
fun ProgressTab(
    globalUnit: WeightUnit,
    onChangeExerciseTrend: () -> Unit,
    viewModel: HomeViewModel) {

    // 2. State for the selected time range and dropdown visibility
    var selectedTimeRange by remember { mutableStateOf(TimeRange.MONTH) }

    var isTimeRangeDropdownExpanded by remember { mutableStateOf(false) }
    val trendExercisePreference by viewModel.trendExercisePreference.collectAsState()

    // Note: You will need to update these ViewModel variables to react to the selected time range
    val workoutsCount by viewModel.workoutsCount.collectAsState(0)
    val totalVolume by viewModel.totalVolume.collectAsState(0.0)
    val oneRepMaxTrend by viewModel.oneRMTrend.collectAsState(emptyList())

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
                0L // 0 represents the Unix Epoch (beginning of time), grabbing everything
            }
        }

        viewModel.updateSelectedTimeRange(startTimestamp)
    }

    val formattedVolume = viewModel.formatVolume(totalVolume)

    val consistencyMarker = rememberMarker()
    val trendMarker = rememberMarker(unitLabel = globalUnit.name)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            // 4. Header Row with Title and Dropdown Menu
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
                        Text(text = selectedTimeRange.displayName)
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
                                text = { Text(range.displayName) },
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
                    // Make subtitle dynamic based on range
                    subtitle = "This ${selectedTimeRange.displayName}",
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
            Text(
                text = "Consistency",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Workouts Over Time",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // TODO: Replace with dynamic data from ViewModel based on selectedTimeRange
                    val consistencyMockData = entryModelOf(2, 4, 3, 5)

                    Chart(
                        chart = columnChart(
                            columns = listOf(
                                lineComponent(
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            ),
                            axisValuesOverrider = AxisValuesOverrider.fixed(minY = 0f, maxY = consistencyMockData.maxY)
                        ),
                        model = consistencyMockData,
                        marker = consistencyMarker,
                        startAxis = rememberStartAxis(
                            label = axisLabelComponent(color = MaterialTheme.colorScheme.onSurfaceVariant),
                            itemPlacer = AxisItemPlacer.Vertical.default(maxItemCount = consistencyMockData.maxY.toInt() + 1),
                            valueFormatter = { value, _ -> value.toInt().toString() }
                        ),
                        bottomAxis = rememberBottomAxis(
                            label = axisLabelComponent(color = MaterialTheme.colorScheme.onSurfaceVariant),
                            // Optional TODO: Adjust X-axis labels based on time range (e.g., Days vs Weeks vs Months)
                            valueFormatter = { value, _ -> "Wk ${value.toInt() + 1}" }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            }
        }

        item {
            Text(
                text = "Exercise Trends",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 12.dp)
            )
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
                        Column {
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
                        TextButton(onClick = onChangeExerciseTrend) {
                            Text("Change")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val trendOneRepMax = remember(oneRepMaxTrend) {
                        val entries = oneRepMaxTrend.mapIndexed { index, dataPoint ->
                            FloatEntry(
                                x = index.toFloat(),
                                y = dataPoint.estimatedMax.toFloat()
                            )
                        }

                        if (entries.isEmpty()) {
                            entryModelOf(List(1){FloatEntry(0f, 0f)})
                        } else {
                            entryModelOf(entries)
                        }
                    }

                    val dateFormatter = remember {
                        SimpleDateFormat("MMM dd", Locale.getDefault())
                    }

                    val primaryColor = MaterialTheme.colorScheme.primary

                    // Conditionally render the chart or an empty state message
                    if (oneRepMaxTrend.isEmpty()) {
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