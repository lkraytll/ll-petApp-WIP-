package com.example.languagelearning.data.local

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudyTimeTracker @Inject constructor(
    private val tokenManager: TokenManager
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
        val elapsedSeconds = (elapsedMs / 1000).toInt()
        isTracking = false
        startTimeMs = 0L

        if (elapsedSeconds > 0) {
            coroutineScope.launch {
                tokenManager.addStudyTimeSeconds(elapsedSeconds)
            }
        }
    }
}
