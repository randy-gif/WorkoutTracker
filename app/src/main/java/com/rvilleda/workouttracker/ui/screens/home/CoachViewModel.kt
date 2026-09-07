package com.rvilleda.workouttracker.ui.screens.home

import android.app.ActivityManager
import android.app.Application
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rvilleda.workouttracker.data.database.dao.WorkoutDao
import com.rvilleda.workouttracker.data.repository.*
import com.rvilleda.workouttracker.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File

data class CoachChatMessage(val user: Boolean, val text: String)
data class CoachState(
    val selected: CoachModel,
    val installed: Set<CoachModel> = emptySet(),
    val messages: List<CoachChatMessage> = emptyList(),
    val busy: Boolean = false,
    val downloading: Boolean = false,
    val progress: Float = 0f,
    val error: String? = null
)

class CoachViewModel(application: Application, private val dao: WorkoutDao) : AndroidViewModel(application) {
    private val manager = application.getSystemService(ActivityManager::class.java)
    private fun memory() = ActivityManager.MemoryInfo().also(manager::getMemoryInfo)
    val device = CoachDevice(Build.VERSION.SDK_INT, "arm64-v8a" in Build.SUPPORTED_ABIS, memory().totalMem, manager.isLowRamDevice)
    private val preferences = application.getSharedPreferences("coach", 0)
    private val store = CoachModelStore(application)
    private val initialModel = CoachModel.entries.firstOrNull { it.name == preferences.getString("model", null) }
        ?.takeIf(device::supports) ?: device.recommended ?: CoachModel.STANDARD
    private val mutableState = MutableStateFlow(CoachState(initialModel, CoachModel.entries.filter(store::installed).toSet()))
    val state = mutableState.asStateFlow()
    val workouts = dao.getAllFullWorkouts().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    private var operation: Job? = null

    fun select(model: CoachModel) {
        if (state.value.busy || !device.supports(model)) return
        preferences.edit().putString("model", model.name).apply()
        mutableState.update { it.copy(selected = model, error = null) }
    }

    fun download() {
        if (state.value.busy || !device.supports(state.value.selected)) return
        val model = state.value.selected
        mutableState.update { it.copy(busy = true, downloading = true, progress = 0f, error = null) }
        operation = viewModelScope.launch {
            try {
                store.download(model) { amount -> mutableState.update { it.copy(progress = amount) } }
                mutableState.update { it.copy(installed = it.installed + model) }
            } catch (cancel: CancellationException) { throw cancel }
            catch (error: Exception) { mutableState.update { it.copy(error = error.message ?: "Download failed. Try again.") } }
            finally { mutableState.update { it.copy(busy = false, downloading = false) } }
        }
    }

    fun remove() {
        if (state.value.busy) return
        val model = state.value.selected
        try {
            store.remove(model)
            mutableState.update { it.copy(installed = it.installed - model, error = null) }
        } catch (error: Exception) { mutableState.update { it.copy(error = error.message) } }
    }

    fun clear() { if (!state.value.busy) mutableState.update { it.copy(messages = emptyList(), error = null) } }
    fun stop() { operation?.cancel() }

    fun send(question: String, unit: WeightUnit): Boolean {
        val text = question.trim()
        val model = state.value.selected
        if (text.isEmpty() || text.toByteArray(Charsets.UTF_8).size > 500 || state.value.busy || model !in state.value.installed || !device.supports(model)) return false
        if (memory().availMem < (if (model == CoachModel.STANDARD) 1_500_000_000L else 3_000_000_000L)) {
            mutableState.update { it.copy(error = "Not enough free memory. Close other apps or try Standard. Your workout summary is still available.") }
            return false
        }
        val previous = state.value.messages.takeLast(2).joinToString("\n") { "${if (it.user) "User" else "Coach"}: ${it.text}" }
        mutableState.update { it.copy(busy = true, error = null, messages = (it.messages + CoachChatMessage(true, text) + CoachChatMessage(false, "")).takeLast(40)) }
        operation = viewModelScope.launch {
            try {
                val snapshot = dao.getAllFullWorkouts().first()
                val prompt = withContext(Dispatchers.Default) { CoachFacts.prompt(snapshot, unit, text, previous) }
                val cache = File(getApplication<Application>().cacheDir, "coach").apply { mkdirs() }
                withTimeout(180_000) {
                    LocalCoachEngine().answer(store.file(model), cache, prompt) { response ->
                        mutableState.update { it.copy(messages = it.messages.dropLast(1) + CoachChatMessage(false, response)) }
                    }
                }
                if (state.value.messages.last().text.isBlank()) mutableState.update { it.copy(error = "No answer was generated. Try a shorter question.") }
            } catch (timeout: TimeoutCancellationException) {
                mutableState.update { it.copy(error = "The answer took too long. Try Standard or a shorter question.") }
            } catch (cancel: CancellationException) { throw cancel }
            catch (error: Exception) {
                mutableState.update { it.copy(error = "The local model couldn't answer on this device. Try Standard or download the model again. Workout summaries remain available.") }
            } catch (error: LinkageError) {
                mutableState.update { it.copy(error = "Local AI isn't supported on this device. You can still use workout summaries.") }
            } finally {
                mutableState.update { it.copy(busy = false, messages = it.messages.filter { message -> message.text.isNotBlank() }) }
            }
        }
        return true
    }
}
