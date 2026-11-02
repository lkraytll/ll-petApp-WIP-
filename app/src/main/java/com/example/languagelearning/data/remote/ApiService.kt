
package com.example.languagelearning.data.remote

import com.example.languagelearning.data.remote.dto.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {

    // Courses

    @GET("courses")
    suspend fun getCourses(): List<CourseDto>

    @GET("courses")
    suspend fun getCoursesApi(): List<CourseDto>

    @GET("courses/{courseId}/lessons")
    suspend fun getCourseLessons(
        @Path("courseId") courseId: Int
    ): List<LessonDto>


    // Lessons

    @GET("lessons/{lessonId}")
    suspend fun getLesson(
        @Path("lessonId") lessonId: Int
    ): LessonDto

    @GET("lessons/course/{courseId}")
    suspend fun getLessonsByCourse(
        @Path("courseId") courseId: Int
    ): List<LessonDto>


    // Progress

    @GET("progress/{userId}")
    suspend fun getProgress(
        @Path("userId") userId: Int
    ): List<ProgressRecordDto>

    @GET("progress/{userId}")
    suspend fun getProgressApi(
        @Path("userId") userId: Int
    ): List<ProgressDto>

    @GET("progress/{userId}/completed")
    suspend fun getCompletedLessons(
        @Path("userId") userId: Int
    ): Response<CompletedLessonsDto>

    @GET("progress/{userId}/completed")
    suspend fun getCompletedLessonsApi(
        @Path("userId") userId: Int
    ): Response<CompletedLessonsDto>

    @PUT("progress/{userId}/{lessonId}")
    suspend fun updateProgress(
        @Path("userId") userId: Int,
        @Path("lessonId") lessonId: Int,
        @Body body: UpdateProgressRequestDto
    ): Response<UpdateProgressResponseDto>

    @PUT("progress/{userId}/{lessonId}")
    suspend fun updateProgressApi(
        @Path("userId") userId: Int,
        @Path("lessonId") lessonId: Int,
        @Body body: UpdateProgressRequestDto
    ): Response<UpdateProgressResponseDto>


    // User Profile

    @GET("users/me")
    suspend fun getMe(): Response<RegisteredUser>

    @GET("users/me")
    suspend fun getMeApi(): Response<RegisteredUser>

    @PATCH("users/me")
    suspend fun updateMe(
        @Body body: UpdateProfileRequestDto
    ): Response<RegisteredUser>

    @PATCH("users/me")
    suspend fun updateMeApi(
        @Body body: UpdateProfileRequestDto
    ): Response<RegisteredUser>

    @PATCH("users/me/password")
    suspend fun changePassword(
        @Body body: ChangePasswordRequestDto
    ): Response<Map<String, String>>

    @PATCH("users/me/password")
    suspend fun changePasswordApi(
        @Body body: ChangePasswordRequestDto
    ): Response<Map<String, String>>

    @Multipart
    @PUT("users/me/avatar")
    suspend fun updateAvatar(
        @Part avatar: MultipartBody.Part
    ): Response<AvatarResponseDto>

    @Multipart
    @PUT("users/me/avatar")
    suspend fun updateAvatarApi(
        @Part avatar: MultipartBody.Part
    ): Response<AvatarResponseDto>


    // Study Time

    @POST("api/study-time/me")
    suspend fun addStudyTime(
        @Body body: AddStudyTimeRequestDto
    ): Response<Map<String, Any>>

    @POST("api/study-time/me")
    suspend fun addStudyTimeApi(
        @Body body: AddStudyTimeRequestDto
    ): Response<Map<String, Any>>

    @GET("api/study-time/me/weekly")
    suspend fun getWeeklyStudyTime(): Response<WeeklyTimeResponseDto>

    @GET("api/study-time/me/weekly")
    suspend fun getWeeklyStudyTimeApi(): Response<WeeklyTimeResponseDto>


    // TTS — оставлен без изменений

    @POST("api/tts/ingush")
    suspend fun synthesizeIngushApi(
        @Body body: TtsRequestDto
    ): Response<ResponseBody>

    @POST("api/tts/ingush")
    suspend fun synthesizeIngush(
        @Body body: TtsRequestDto
    ): Response<ResponseBody>


    // Questions

    @GET("questions/lesson/{lessonId}/multiple-choice")
    suspend fun getMultipleChoice(
        @Path("lessonId") lessonId: Int
    ): List<QuestionDto>

    @GET("questions/lesson/{lessonId}/translation-by-image")
    suspend fun getTranslationByImage(
        @Path("lessonId") lessonId: Int
    ): List<QuestionDto>

    @GET("questions/lesson/{lessonId}/image-by-text")
    suspend fun getImageByText(
        @Path("lessonId") lessonId: Int
    ): List<QuestionDto>

    @GET("questions/lesson/{lessonId}/handwritten-translation")
    suspend fun getHandwrittenTranslation(
        @Path("lessonId") lessonId: Int
    ): List<QuestionDto>

    @POST("questions/{questionId}/answer")
    suspend fun checkAnswer(
        @Path("questionId") questionId: Int,
        @Body body: CheckAnswerRequestDto
    ): Response<CheckAnswerResponseDto>

    @POST("questions/{questionId}/answer")
    suspend fun checkQuestionAnswerPath(
        @Path("questionId") questionId: Int,
        @Body body: CheckAnswerRequestDto
    ): Response<CheckAnswerResponseDto>
}
