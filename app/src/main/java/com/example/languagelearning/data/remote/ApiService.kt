package com.example.languagelearning.data.remote

import com.example.languagelearning.data.remote.dto.CourseDto
import com.example.languagelearning.data.remote.dto.LessonDto
import com.example.languagelearning.data.remote.dto.ProgressDto
import com.example.languagelearning.data.remote.dto.UpdateProgressRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    // Courses
    @GET("courses")
    suspend fun getCourses(): List<CourseDto>

    @GET("courses/{courseId}/lessons")
    suspend fun getCourseLessons(
        @Path("courseId") courseId: Int,
    ): List<LessonDto>

    // Lessons
    @GET("lessons/{lessonId}")
    suspend fun getLesson(
        @Path("lessonId") lessonId: Int,
    ): LessonDto

    @GET("lessons/course/{courseId}")
    suspend fun getLessonsByCourse(
        @Path("courseId") courseId: Int,
    ): List<LessonDto>

    // Progress
    @GET("progress/{userId}")
    suspend fun getProgress(
        @Path("userId") userId: Int,
    ): List<ProgressDto>

    @PUT("progress/{userId}/{lessonId}")
    suspend fun updateProgress(
        @Path("userId") userId: Int,
        @Path("lessonId") lessonId: Int,
        @Body body: UpdateProgressRequestDto,
    ): Response<ProgressDto>

    // User Profile (UserController)
    @GET("users/me")
    suspend fun getMe(): Response<com.example.languagelearning.data.remote.dto.RegisteredUser>

    @retrofit2.http.PATCH("users/me")
    suspend fun updateMe(
        @Body body: com.example.languagelearning.data.remote.dto.UpdateProfileRequestDto,
    ): Response<com.example.languagelearning.data.remote.dto.RegisteredUser>

    @retrofit2.http.PATCH("users/me/password")
    suspend fun changePassword(
        @Body body: com.example.languagelearning.data.remote.dto.ChangePasswordRequestDto,
    ): Response<Map<String, String>>
}
