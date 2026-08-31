package com.rvilleda.workouttracker.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rvilleda.workouttracker.data.database.dao.OneRMTrendDataPoint
import com.rvilleda.workouttracker.data.database.dao.RoutineDao
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

    fun updateExerciseTrend(exerciseId: String, exerciseName: String) {
        viewModelScope.launch {
            preferencesRepository.saveTrendExercisePreference(exerciseId, exerciseName)
        }
    }


    fun updateUnitPreference(unit: WeightUnit) {
        currentUnit.value = unit
    }

    fun updateSelectedTimeRange(timeInMillis: Long) {
        startDateMillis.value = timeInMillis
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    val oneRMTrend: StateFlow<List<OneRMTrendDataPoint>> = combine(
        currentUnit,
        trendExercisePreference,
        startDateMillis
    ) { unit, preferencePair, startDate ->
        val (exerciseId, _) = preferencePair
        Triple(unit, exerciseId, startDate)
    }.flatMapLatest { (unit, exerciseId, startDate) ->
        if (exerciseId == null) {
            flowOf(emptyList())
        } else {
            workoutDao.getExercise1RMTrend(
                exerciseId = exerciseId,
                startDateMillis = startDate,
                endDateMillis = System.currentTimeMillis(),
                targetUnitName = unit.name
            )
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


    fun formatVolume(volume: Double?): String {
        if (volume == null || volume == 0.0) return "0.0"

        return if (volume >= 1000) {
            String.format(java.util.Locale.US, "%.1fk", volume / 1000.0)
        } else {
            String.format(java.util.Locale.US, "%.1f", volume)
        }
    }

}