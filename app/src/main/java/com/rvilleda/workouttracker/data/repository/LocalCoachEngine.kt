package com.rvilleda.workouttracker.data.repository

import com.google.ai.edge.litertlm.Backend
import com.google.ai.edge.litertlm.ConversationConfig
import com.google.ai.edge.litertlm.Engine
import com.google.ai.edge.litertlm.EngineConfig
import com.google.ai.edge.litertlm.Message
import com.google.ai.edge.litertlm.SamplerConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.File

class LocalCoachEngine {
    suspend fun answer(model: File, cache: File, prompt: String, onText: (String) -> Unit) = withContext(Dispatchers.IO) {
        // Try acceleration first, then CPU when the GPU driver cannot initialize.
        val engine = try {
            initialize(model, cache, Backend.GPU)
        } catch (_: Exception) {
            currentCoroutineContext().ensureActive()
            initialize(model, cache, Backend.CPU)
        }
        engine.use {
            currentCoroutineContext().ensureActive()
            it.createConversation(ConversationConfig(samplerConfig = SamplerConfig(20, 0.9, 0.2))).use { conversation ->
                val output = StringBuilder()
                try {
                    conversation.sendMessageAsync(Message.of(prompt)).collect { chunk ->
                        output.append(chunk.toString())
                        onText(visibleAnswer(output.toString()))
                        if (output.length > 6000) conversation.cancelProcess()
                    }
                } finally {
                    conversation.cancelProcess()
                }
            }
        }
    }

    private fun initialize(model: File, cache: File, backend: Backend): Engine {
        val engine = Engine(EngineConfig(model.path, backend, maxNumTokens = 4096, cacheDir = cache.path))
        try { engine.initialize(); return engine }
        catch (error: Throwable) { engine.close(); throw error }
    }

    companion object {
        fun visibleAnswer(raw: String): String {
            val withoutThought = raw.replace(Regex("<think>.*?</think>", RegexOption.DOT_MATCHES_ALL), "")
            return withoutThought.substringBefore("<think>").trim()
        }
    }
}
