package com.rvilleda.workouttracker.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rvilleda.workouttracker.data.database.dao.OneRMTrendDataPoint
import com.rvilleda.workouttracker.data.database.dao.RoutineDao
import com.rvilleda.workouttracker.data.database.entity.CompletedWorkoutEntity
import com.rvilleda.workouttracker.data.database.dao.WorkoutDao
import com.rvilleda.workouttracker.data.database.entity.FullRoutine
import com.rvilleda.workouttracker.data.database.entity.FullWorkout
import com.rvilleda.workouttracker.data.database.entity.WorkoutExerciseEntity
import com.rvilleda.workouttracker.model.WeightUnit
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf


class HomeViewModel(private val workoutDao: WorkoutDao, private val routineDao: RoutineDao) : ViewModel() {

   // Workout
    val savedWorkouts: StateFlow<List<CompletedWorkoutEntity>> = workoutDao.getWorkoutSummaries()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )


    fun deleteWorkout(workoutId: String) {
        viewModelScope.launch {
            workoutDao.deleteWorkout(workoutId)
        }
    }

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
    private val currentExerciseId = MutableStateFlow<String?>(null)

    private val thirtyDaysAgoMillis: Long
        get() {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -30)
            return calendar.timeInMillis
        }


    private val startDateMillis = MutableStateFlow(0L)

    val latestWorkout: Flow<FullWorkout?> = workoutDao.getLatestFullWorkout()

    val firstExercise: Flow<WorkoutExerciseEntity?> = workoutDao.getFirstExerciseOfLastWorkout()



    fun updateUnitPreference(unit: WeightUnit) {
        currentUnit.value = unit
    }

    fun updateExerciseTrendId(exerciseId: String) {
        currentExerciseId.value = exerciseId
    }

    fun updateSelectedTimeRange(timeInMillis: Long) {
        startDateMillis.value = timeInMillis
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    val oneRMTrend: StateFlow<List<OneRMTrendDataPoint>> = combine(
        currentUnit,
        currentExerciseId,
        startDateMillis
    ) { unit, exerciseId, startDate ->
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