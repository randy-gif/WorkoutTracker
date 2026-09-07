package com.rvilleda.workouttracker.data.repository

import android.content.Context
import com.rvilleda.workouttracker.model.CoachModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest

class CoachModelStore(context: Context) {
    private val directory = File(context.noBackupFilesDir, "coach-models").apply { mkdirs() }
    fun file(model: CoachModel) = File(directory, model.filename)
    fun installed(model: CoachModel) = file(model).length() == model.bytes
    fun remove(model: CoachModel) {
        if (file(model).exists() && !file(model).delete()) throw IOException("Couldn't remove model.")
    }

    suspend fun download(model: CoachModel, progress: (Float) -> Unit) = withContext(Dispatchers.IO) {
        val partial = File(directory, "${model.filename}.part")
        // A process killed during a download cannot run finally; reclaim its incomplete file.
        if (partial.exists() && !partial.delete()) throw IOException("Couldn't clear an interrupted download.")
        if (directory.usableSpace < model.bytes + 300_000_000L) {
            throw IOException("Free at least ${model.sizeLabel} plus 300 MB before downloading.")
        }
        val connection = URL(model.downloadUrl).openConnection() as HttpURLConnection
        connection.connectTimeout = 20_000
        connection.readTimeout = 20_000
        try {
            if (connection.responseCode != 200) throw IOException("Download unavailable. Please try again later.")
            val digest = MessageDigest.getInstance("SHA-256")
            var total = 0L
            connection.inputStream.use { input ->
                partial.outputStream().use { output ->
                    val buffer = ByteArray(64 * 1024)
                    var lastPercent = -1
                    while (true) {
                        currentCoroutineContext().ensureActive()
                        val count = input.read(buffer)
                        if (count < 0) break
                        total += count
                        if (total > model.bytes) throw IOException("Unexpected model download size.")
                        digest.update(buffer, 0, count)
                        output.write(buffer, 0, count)
                        val percent = (total * 100 / model.bytes).toInt()
                        if (percent != lastPercent) { progress(total.toFloat() / model.bytes); lastPercent = percent }
                    }
                }
            }
            val hash = digest.digest().joinToString("") { "%02x".format(it.toInt() and 255) }
            if (total != model.bytes || hash != model.sha256) throw IOException("Model verification failed. Please download again.")
            currentCoroutineContext().ensureActive()
            if (!partial.renameTo(file(model))) throw IOException("Couldn't save the model.")
        } finally {
            connection.disconnect()
            partial.delete()
        }
    }
}
