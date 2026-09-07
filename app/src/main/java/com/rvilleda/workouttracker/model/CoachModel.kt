package com.rvilleda.workouttracker.model

enum class CoachModel(
    val label: String, val repository: String, val filename: String,
    val bytes: Long, val sha256: String, val minimumRamGb: Int
) {
    STANDARD("Standard", "Qwen3-0.6B", "Qwen3-0.6B.litertlm", 614236160,
        "555579ff2f4fd13379abe69c1c3ab5200f7338bc92471557f1d6614a6e5ab0b4", 4),
    ENHANCED("Enhanced", "Qwen2.5-1.5B-Instruct", "Qwen2.5-1.5B-Instruct_multi-prefill-seq_q8_ekv4096.litertlm", 1597931520,
        "faa60663b333290c1496c499828b21d3e3254a788cacd8cce917ce0f761a2dc9", 8);

    private val revision get() = if (this == STANDARD) "8414150f2e9dcc82449bcc9c5abc404b399a4d06" else "19edb84c69a0212f29a6ef17ba0d6f278b6a1614"
    val downloadUrl get() = "https://huggingface.co/litert-community/$repository/resolve/$revision/$filename"
    val sizeLabel get() = if (this == STANDARD) "586 MB" else "1.49 GB"
}

/** Conservative starting thresholds, not a guarantee of inference speed or compatibility. */
data class CoachDevice(val sdk: Int, val arm64: Boolean, val ramBytes: Long, val lowRam: Boolean) {
    fun supports(model: CoachModel) = sdk >= 26 && arm64 && !lowRam &&
        ramBytes >= model.minimumRamGb * 950_000_000L
    val recommended get() = when {
        supports(CoachModel.ENHANCED) -> CoachModel.ENHANCED
        supports(CoachModel.STANDARD) -> CoachModel.STANDARD
        else -> null
    }
}
