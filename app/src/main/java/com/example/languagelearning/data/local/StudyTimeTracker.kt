package com.example.languagelearning.data.local

import com.example.languagelearning.data.repository.ProgressRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudyTimeTracker @Inject constructor(
    private val tokenManager: TokenManager,
    private val progressRepository: ProgressRepository,
) {
    private var startTimeMs: Long = 0L
    private var isTracking = false



    fun startSession() {
        startTimeMs = System.currentTimeMillis()
        isTracking = true
    }

    fun stopAndSaveSession(coroutineScope: CoroutineScope) {
        if (!isTracking || startTimeMs <= 0L) return
        val elapsedMs = System.currentTimeMillis() - startTimeMs
        val elapsedSeconds = if (elapsedMs >= 500L) maxOf(1, (elapsedMs / 1000).toInt()) else 0
        isTracking = false
        startTimeMs = 0L

        if (elapsedSeconds > 0) {
            coroutineScope.launch {
                tokenManager.addStudyTimeSeconds(elapsedSeconds)
                progressRepository.addStudyTime(elapsedSeconds)
            }
        }
    }
}
