package com.rvilleda.workouttracker.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rvilleda.workouttracker.data.database.dao.OneRMTrendDataPoint
import com.rvilleda.workouttracker.data.database.dao.RoutineDao
import com.rvilleda.workouttracker.data.database.dao.WorkoutCountTrendDataPoint
import com.rvilleda.workouttracker.data.database.entity.CompletedWorkoutEntity
import com.rvilleda.workouttracker.data.database.dao.WorkoutDao
import com.rvilleda.workouttracker.data.database.entity.FullRoutine
import com.rvilleda.workouttracker.data.repository.DashboardPreferencesRepository
import com.rvilleda.workouttracker.model.WeightUnit
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import com.rvilleda.workouttracker.model.allDefaultExercises

enum class TimeRange(val displayName: String) {
    DAY("Day"),
    WEEK("Week"),
    MONTH("Month"),
    YEAR("Year"),
    FIVE_YEARS("5 Years"),
    ALL_TIME("All Time")
}

class HomeViewModel(
    private val workoutDao: WorkoutDao,
    private val routineDao: RoutineDao,
    private val preferencesRepository: DashboardPreferencesRepository
) : ViewModel() {

   // Workout
    val savedWorkouts: StateFlow<List<CompletedWorkoutEntity>> = workoutDao.getWorkoutSummaries()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // --- Routine Tab ---


    val savedRoutines: StateFlow<List<FullRoutine>> = routineDao.getAllFullRoutines()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


    fun deleteRoutine(routineId: String) {
        viewModelScope.launch {
            routineDao.deleteRoutine(routineId)
        }
    }


    // --- Progress Tab ---

    private data class TrendConfig(
        val unit: WeightUnit,
        val exerciseId: String?,
        val startDateMillis: Long,
        val timeRange: TimeRange
    )
    val selectedTimeRange = MutableStateFlow(TimeRange.MONTH)
    private val currentUnit = MutableStateFlow(WeightUnit.LBS)
    private val startDateMillis = MutableStateFlow(0L)

    val trendExercisePreference = preferencesRepository.selectedTrendExercise
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Pair(null, "Bench Press")
        )

    init {
        viewModelScope.launch {
            val currentPref = preferencesRepository.selectedTrendExercise.first()
            val currentId = currentPref.first

            if (currentId == null) {
                val benchPress = allDefaultExercises.find {
                    it.name.contains("Bench Press", ignoreCase = true)
                } ?: allDefaultExercises.firstOrNull()

                if (benchPress != null) {
                    preferencesRepository.saveTrendExercisePreference(
                        exerciseId = benchPress.id,
                        exerciseName = benchPress.name
                    )
                }
            }
        }
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    val oneRMTrend: StateFlow<List<OneRMTrendDataPoint>> = combine(
        currentUnit,
        trendExercisePreference,
        startDateMillis,
        selectedTimeRange
    ) { unit, preferencePair, startDate, timeRange ->

        val (exerciseId, _) = preferencePair
        // Return our custom data class instead of Quad
        TrendConfig(unit, exerciseId, startDate, timeRange)

    }.flatMapLatest { config ->

        if (config.exerciseId == null) {
            flowOf(emptyList())
        } else {
            val endDate = System.currentTimeMillis()
            when (config.timeRange) {
                TimeRange.DAY, TimeRange.WEEK, TimeRange.MONTH ->
                    workoutDao.getExercise1RMTrendDaily(config.exerciseId, config.startDateMillis, endDate, config.unit.name)
                TimeRange.YEAR ->
                    workoutDao.getExercise1RMTrendWeekly(config.exerciseId, config.startDateMillis, endDate, config.unit.name)
                TimeRange.FIVE_YEARS, TimeRange.ALL_TIME ->
                    workoutDao.getExercise1RMTrendMonthly(config.exerciseId, config.startDateMillis, endDate, config.unit.name)
            }
        }

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    val workoutCountTrend: StateFlow<List<WorkoutCountTrendDataPoint>> = combine(
        selectedTimeRange, // e.g., TimeRange enum (DAY, WEEK, MONTH, YEAR, FIVE_YEARS, ALL)
        startDateMillis
    ) { timeRange, startDate ->
        Pair(timeRange, startDate)
    }.flatMapLatest { (timeRange, startDate) ->
        val endDate = System.currentTimeMillis()
        when (timeRange) {
            TimeRange.DAY, TimeRange.WEEK, TimeRange.MONTH ->
                workoutDao.getWorkoutCountTrendDaily(startDate, endDate)
            TimeRange.YEAR ->
                workoutDao.getWorkoutCountTrendWeekly(startDate, endDate)
            TimeRange.FIVE_YEARS, TimeRange.ALL_TIME ->
                workoutDao.getWorkoutCountTrendMonthly(startDate, endDate)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    @OptIn(ExperimentalCoroutinesApi::class)
    val workoutsCount: StateFlow<Int> = startDateMillis
        .flatMapLatest { startDate ->
            workoutDao.getWorkoutsCountSince(startDate)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val totalVolume: StateFlow<Double?> = combine(
        currentUnit,
        startDateMillis
    ) { unit, startDate ->
        Pair(unit, startDate)
    }.flatMapLatest { (unit, startDate) ->
        workoutDao.getTotalVolumeSince(startDate, unit.name)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    fun updateExerciseTrend(exerciseId: String, exerciseName: String) {
        viewModelScope.launch {
            preferencesRepository.saveTrendExercisePreference(exerciseId, exerciseName)
        }
    }


    fun updateUnitPreference(unit: WeightUnit) {
        currentUnit.value = unit
    }

    fun updateSelectedTimeRange(timeRange: TimeRange, timeInMillis: Long) {
        selectedTimeRange.value = timeRange
        startDateMillis.value = timeInMillis
    }
    fun formatVolume(volume: Double?): String {
        if (volume == null || volume == 0.0) return "0.0"

        return if (volume >= 1000) {
            String.format(java.util.Locale.US, "%.1fk", volume / 1000.0)
        } else {
            String.format(java.util.Locale.US, "%.1f", volume)
        }
    }

}