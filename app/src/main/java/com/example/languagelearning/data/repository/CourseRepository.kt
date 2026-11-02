package com.example.languagelearning.data.repository

import com.example.languagelearning.data.remote.ApiService
import com.example.languagelearning.data.remote.ErrorUtils
import com.example.languagelearning.data.remote.dto.CheckAnswerRequestDto
import com.example.languagelearning.data.remote.dto.CheckAnswerResponseDto
import com.example.languagelearning.data.remote.dto.CourseDto
import com.example.languagelearning.data.remote.dto.LessonDto
import com.example.languagelearning.data.remote.dto.QuestionDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CourseRepository @Inject constructor(
    private val api: ApiService,
) {

    suspend fun getCourses(): Result<List<CourseDto>> {
        return runCatching {
            try {
                val list = api.getCoursesApi()
                if (list.isNotEmpty()) list else api.getCourses()
            } catch (_: Exception) {
                api.getCourses()
            }
        }
    }

    suspend fun getCourseLessons(courseId: Int): Result<List<LessonDto>> {
        return runCatching {
            api.getCourseLessons(courseId)
        }
    }

    suspend fun getLesson(lessonId: Int): Result<LessonDto> {
        return runCatching {
            api.getLesson(lessonId)
        }
    }

    suspend fun getLessonQuestions(lessonId: Int): Result<List<QuestionDto>> {
        return runCatching {
            val questions = mutableListOf<QuestionDto>()

            questions.addAll(api.getMultipleChoice(lessonId))
            questions.addAll(api.getTranslationByImage(lessonId))
            questions.addAll(api.getImageByText(lessonId))
            questions.addAll(api.getHandwrittenTranslation(lessonId))

            questions.sortedBy { it.order ?: Int.MAX_VALUE }
        }
    }

    suspend fun getMultipleChoice(lessonId: Int): Result<List<QuestionDto>> {
        return runCatching {
            api.getMultipleChoice(lessonId)
        }
    }

    suspend fun getTranslationByImage(lessonId: Int): Result<List<QuestionDto>> {
        return runCatching {
            api.getTranslationByImage(lessonId)
        }
    }

    suspend fun getImageByText(lessonId: Int): Result<List<QuestionDto>> {
        return runCatching {
            api.getImageByText(lessonId)
        }
    }

    suspend fun getHandwrittenTranslation(lessonId: Int): Result<List<QuestionDto>> {
        return runCatching {
            api.getHandwrittenTranslation(lessonId)
        }
    }

    suspend fun checkAnswer(
        questionId: Int,
        optionId: Int? = null,
        answer: String? = null,
    ): Result<CheckAnswerResponseDto> {
        return runCatching {
            val response = api.checkAnswer(
                questionId = questionId,
                body = CheckAnswerRequestDto(optionId = optionId, answer = answer)
            )
            if (response.isSuccessful && response.body() != null) {
                response.body()!!
            } else {
                val altResponse = api.checkQuestionAnswerPath(
                    questionId = questionId,
                    body = CheckAnswerRequestDto(optionId = optionId, answer = answer)
                )
                if (altResponse.isSuccessful && altResponse.body() != null) {
                    altResponse.body()!!
                } else {
                    val rawError = altResponse.errorBody()?.string() ?: response.errorBody()?.string()
                    val parsed = ErrorUtils.parseErrorMessage(rawError, "Failed to check answer")
                    throw Exception(parsed)
                }
            }
        }
    }
}
