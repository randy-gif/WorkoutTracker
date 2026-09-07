# On-device AI Coach

The Coach tab has calculated workout summaries on every supported app device. Optional local chat uses LiteRT-LM 0.8.0. It does not call a hosted inference API. Network permission is used for model downloads only; chat is kept in memory and cleared when the process ends or New chat is pressed.

## Models

| Mode | Model | Download | Eligibility heuristic |
| --- | --- | --- | --- |
| Standard | Qwen3 0.6B | 586 MiB | ARM64, Android 8+, approximately 4 GB RAM |
| Enhanced | Qwen2.5 1.5B Instruct, Q8 | 1.49 GiB | ARM64, Android 8+, approximately 8 GB RAM |

These thresholds are conservative starting points, not measured device certifications. Low-RAM devices use summaries. Available memory is checked again before inference. GPU initialization falls back to CPU; native loading/inference errors leave summaries accessible. Android may still terminate a process under memory pressure.

Model downloads are opt-in, revision-pinned, size-limited, and SHA-256 verified. They live in app-private no-backup storage, outside the APK. The Models panel supports changing the selected model and removing downloads. Downloads run while the app process is alive; interrupted downloads restart rather than resume. No model files are committed to the repository.

Sources and model licenses (Apache-2.0):
- https://huggingface.co/litert-community/Qwen3-0.6B
- https://huggingface.co/litert-community/Qwen2.5-1.5B-Instruct
- https://github.com/google-ai-edge/LiteRT-LM/tree/v0.8.0

## Workout context

Workout counts, completed sets, reps, and unit-normalized volume are calculated in Kotlin. Each question reads a fresh database snapshot. The prompt adds a bounded selection of up to three recent matching workouts and a short previous-chat excerpt. It explicitly marks details as incomplete. It does not provide complete historical retrieval or automatically change routines. Questions are limited to 500 UTF-8 bytes; context is bounded for the local model's 4096-token window. Answers can still be inaccurate and should be evaluated before release.

Native inference runs off the UI thread, streams responses, supports Stop, and releases the conversation and engine after each response. Reinitializing per response favors releasing memory over fastest follow-up latency.

## Validation before release

Run `:app:assembleDebug :app:testDebugUnitTest`. On physical ARM64 phones, test both models with known workout fixtures, airplane mode after downloading, insufficient memory/storage, interrupted downloads, Stop, rotation, model removal, GPU failure, and repeated chats. Benchmark startup, response latency, peak memory, and battery use across representative devices before adjusting eligibility thresholds. Emulator UI testing does not establish physical-device inference compatibility.

The runtime requires Kotlin 2.2 metadata support, so Kotlin, KSP, AGP, and the KSP2-compatible Room 2.7.2 were updated together. No database schema changes are introduced. The application's minSdk remains 24; local chat is guarded separately.
