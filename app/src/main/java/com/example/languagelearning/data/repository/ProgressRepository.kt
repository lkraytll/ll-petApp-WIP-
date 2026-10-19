package com.example.languagelearning.data.repository

import com.example.languagelearning.data.remote.ApiService
import com.example.languagelearning.data.remote.dto.ProgressDto
import com.example.languagelearning.data.remote.dto.UpdateProgressRequestDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProgressRepository @Inject constructor(
    private val api: ApiService,
) {

    suspend fun getProgress(userId: Int): Result<List<ProgressDto>> {
        return runCatching {
            api.getProgress(userId)
        }
    }

    suspend fun updateProgress(
        userId: Int,
        lessonId: Int,
        isCompleted: Boolean,
        score: Int? = null,
    ): Result<ProgressDto> {
        return runCatching {
            val response = api.updateProgress(
                userId = userId,
                lessonId = lessonId,
                body = UpdateProgressRequestDto(isCompleted, score),
            )
            if (response.isSuccessful && (response.body() != null)) {
                response.body()!!
            } else {
                val rawError = response.errorBody()?.string()
                val parsedMessage = com.example.languagelearning.data.remote.ErrorUtils.parseErrorMessage(
                    rawError,
                    "Failed to save progress."
                )
                throw Exception(parsedMessage)
            }
        }
    }
}
