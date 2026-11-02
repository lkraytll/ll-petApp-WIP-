package com.example.languagelearning.data.repository

import com.example.languagelearning.data.remote.ApiService
import com.example.languagelearning.data.remote.ErrorUtils
import com.example.languagelearning.data.remote.dto.AddStudyTimeRequestDto
import com.example.languagelearning.data.remote.dto.ProgressRecordDto
import com.example.languagelearning.data.remote.dto.UpdateProgressRequestDto
import com.example.languagelearning.data.remote.dto.UpdateProgressResponseDto
import com.example.languagelearning.data.remote.dto.WeeklyTimeDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProgressRepository @Inject constructor(
    private val api: ApiService,
) {

    suspend fun getProgress(userId: Int): Result<List<ProgressRecordDto>> {
        return runCatching {
            api.getProgress(userId)
        }
    }

    suspend fun updateProgress(
        userId: Int,
        lessonId: Int,
        isCompleted: Boolean,
        score: Int? = null,
    ): Result<UpdateProgressResponseDto> {
        return runCatching {
            val body = UpdateProgressRequestDto(
                completed = isCompleted,
                score = score
            )

            val response = api.updateProgress(userId, lessonId, body)

            if (response.isSuccessful && response.body() != null) {
                response.body()!!
            } else {
                val message = ErrorUtils.parseErrorMessage(
                    response.errorBody()?.string(),
                    "Failed to save progress."
                )
                throw Exception(message)
            }
        }
    }

    suspend fun getCompletedLessons(userId: Int): Result<Int> {
        return runCatching {
            val response = api.getCompletedLessons(userId)

            if (response.isSuccessful && response.body() != null) {
                response.body()!!.completedLessons
            } else {
                val message = ErrorUtils.parseErrorMessage(
                    response.errorBody()?.string(),
                    "Failed to get completed lessons count"
                )
                throw Exception(message)
            }
        }
    }

    suspend fun addStudyTime(seconds: Int): Result<Boolean> {
        return runCatching {
            require(seconds in 1..300) {
                "Study time must be between 1 and 300 seconds"
            }

            val response = api.addStudyTime(
                AddStudyTimeRequestDto(seconds)
            )

            if (response.isSuccessful) {
                true
            } else {
                val message = ErrorUtils.parseErrorMessage(
                    response.errorBody()?.string(),
                    "Failed to save study time"
                )
                throw Exception(message)
            }
        }
    }

    suspend fun getWeeklyStudyTime(): Result<WeeklyTimeDto> {
        return runCatching {
            val response = api.getWeeklyStudyTime()

            if (response.isSuccessful && response.body() != null) {
                response.body()!!.weeklyTime
            } else {
                val message = ErrorUtils.parseErrorMessage(
                    response.errorBody()?.string(),
                    "Failed to load weekly study time"
                )
                throw Exception(message)
            }
        }
    }
}