package com.example.languagelearning.data.repository

import com.example.languagelearning.data.remote.ApiService
import com.example.languagelearning.data.remote.AuthApiService
import com.example.languagelearning.data.remote.dto.CourseDto
import com.example.languagelearning.data.remote.dto.LessonDto
import com.example.languagelearning.data.remote.dto.LoginRequestDto
import com.example.languagelearning.data.remote.dto.LoginResponseDto
import com.example.languagelearning.data.remote.dto.ProgressDto
import com.example.languagelearning.data.remote.dto.RegisterRequestDTO
import com.example.languagelearning.data.remote.dto.RegisterResponse
import com.example.languagelearning.data.remote.dto.RegisteredUser
import com.example.languagelearning.data.remote.dto.UpdateProgressRequestDto
import com.example.languagelearning.data.remote.dto.UpdateProfileRequestDto
import com.example.languagelearning.data.remote.dto.ChangePasswordRequestDto
import com.example.languagelearning.data.remote.dto.DailyProgressDto
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class RepositoriesTest {

    private val fakeUser = RegisteredUser(1, "Adam", "Ingush", "adam@example.com")

    private val fakeApiService = object : ApiService {
        override suspend fun getCourses(): List<CourseDto> {
            return listOf(CourseDto(1, "Ingush Course", "Description"))
        }

        override suspend fun getCourseLessons(courseId: Int): List<LessonDto> {
            return listOf(LessonDto(10, courseId, "Lesson 1", "Content"))
        }

        override suspend fun getLesson(lessonId: Int): LessonDto {
            return LessonDto(lessonId, 1, "Lesson Title", "Content")
        }

        override suspend fun getLessonsByCourse(courseId: Int): List<LessonDto> {
            return listOf(LessonDto(10, courseId, "Lesson 1", "Content"))
        }

        override suspend fun getProgress(userId: Int): List<ProgressDto> {
            return listOf(ProgressDto(userId, 1, 5, 20))
        }

        override suspend fun getDailyProgress(userId: Int): Response<DailyProgressDto> {
            return Response.success(DailyProgressDto(day = "Today", timeSeconds = 900, timeMinutes = 15))
        }

        override suspend fun updateProgress(
            userId: Int,
            lessonId: Int,
            body: UpdateProgressRequestDto
        ): Response<ProgressDto> {
            return if (userId > 0) {
                Response.success(ProgressDto(userId, 1, 6, 20))
            } else {
                Response.error(400, "{\"message\":\"Invalid user\"}".toResponseBody(null))
            }
        }

        override suspend fun getMe(): Response<RegisteredUser> {
            return Response.success(fakeUser)
        }

        override suspend fun updateMe(body: UpdateProfileRequestDto): Response<RegisteredUser> {
            return if (body.firstName == "Error") {
                Response.error(400, "{\"message\":\"First name invalid\"}".toResponseBody(null))
            } else {
                Response.success(fakeUser)
            }
        }

        override suspend fun changePassword(body: ChangePasswordRequestDto): Response<Map<String, String>> {
            return if (body.currentPassword == "wrong") {
                Response.error(400, "{\"message\":\"Current password incorrect\"}".toResponseBody(null))
            } else {
                Response.success(mapOf("message" to "Password updated successfully"))
            }
        }
    }

    private val fakeErrorApiService = object : ApiService {
        override suspend fun getCourses(): List<CourseDto> = throw Exception("Network error")
        override suspend fun getCourseLessons(courseId: Int): List<LessonDto> = throw Exception("Network error")
        override suspend fun getLesson(lessonId: Int): LessonDto = throw Exception("Network error")
        override suspend fun getLessonsByCourse(courseId: Int): List<LessonDto> = throw Exception("Network error")
        override suspend fun getProgress(userId: Int): List<ProgressDto> = throw Exception("Network error")
        override suspend fun getDailyProgress(userId: Int): Response<DailyProgressDto> = Response.error(500, "{\"message\":\"Daily progress error\"}".toResponseBody(null))
        override suspend fun updateProgress(userId: Int, lessonId: Int, body: UpdateProgressRequestDto): Response<ProgressDto> = Response.error(500, "{\"message\":\"Server error\"}".toResponseBody(null))
        override suspend fun getMe(): Response<RegisteredUser> = Response.error(404, "{\"message\":\"User not found\"}".toResponseBody(null))
        override suspend fun updateMe(body: UpdateProfileRequestDto): Response<RegisteredUser> = Response.error(409, "{\"message\":\"Email in use\"}".toResponseBody(null))
        override suspend fun changePassword(body: ChangePasswordRequestDto): Response<Map<String, String>> = Response.error(400, "{\"message\":\"Invalid\"}".toResponseBody(null))
    }

    private val fakeAuthApiService = object : AuthApiService {
        override suspend fun login(request: LoginRequestDto): Response<LoginResponseDto> {
            return if (request.email == "error@example.com") {
                Response.error(401, "{\"message\":\"Invalid credentials\"}".toResponseBody(null))
            } else {
                Response.success(LoginResponseDto("Login success", "jwt_token_123", fakeUser))
            }
        }

        override suspend fun register(request: RegisterRequestDTO): Response<RegisterResponse> {
            return if (request.email == "error@example.com") {
                Response.error(409, "{\"message\":\"Email already in use\"}".toResponseBody(null))
            } else {
                Response.success(RegisterResponse("Register success", fakeUser))
            }
        }
    }

    @Test
    fun testCourseRepository() = runBlocking {
        val repo = CourseRepository(fakeApiService)
        val coursesResult = repo.getCourses()
        assertTrue(coursesResult.isSuccess)
        assertEquals(1, coursesResult.getOrNull()?.size)

        val lessonsResult = repo.getCourseLessons(1)
        assertTrue(lessonsResult.isSuccess)
        assertEquals(1, lessonsResult.getOrNull()?.size)
    }

    @Test
    fun testProgressRepositorySuccessAndError() = runBlocking {
        val repo = ProgressRepository(fakeApiService)
        val progressResult = repo.getProgress(100)
        assertTrue(progressResult.isSuccess)

        val dailyResult = repo.getDailyProgress(100)
        assertTrue(dailyResult.isSuccess)
        assertEquals(900, dailyResult.getOrNull()?.timeSeconds)

        val updateResult = repo.updateProgress(100, 10, true, 100)
        assertTrue(updateResult.isSuccess)

        val errorResult = repo.updateProgress(-1, 10, true, 100)
        assertTrue(errorResult.isFailure)
        assertEquals("Invalid user", errorResult.exceptionOrNull()?.message)

        val errRepo = ProgressRepository(fakeErrorApiService)
        val dailyErr = errRepo.getDailyProgress(100)
        assertTrue(dailyErr.isFailure)
        assertEquals("Daily progress error", dailyErr.exceptionOrNull()?.message)
    }

    @Test
    fun testUserRepositorySuccessAndErrors() = runBlocking {
        val repo = UserRepository(fakeApiService)
        val meResult = repo.getMe()
        assertTrue(meResult.isSuccess)
        assertEquals(fakeUser, meResult.getOrNull())

        val updateResult = repo.updateMe("Adam", "Ingush", "adam@example.com")
        assertTrue(updateResult.isSuccess)

        val passResult = repo.changePassword("oldPass123", "newPass123")
        assertTrue(passResult.isSuccess)

        val updateError = repo.updateMe("Error", "Ingush", "adam@example.com")
        assertTrue(updateError.isFailure)
        assertEquals("First name invalid", updateError.exceptionOrNull()?.message)

        val passError = repo.changePassword("wrong", "newPass123")
        assertTrue(passError.isFailure)
        assertEquals("Current password incorrect", passError.exceptionOrNull()?.message)

        val errorRepo = UserRepository(fakeErrorApiService)
        val getMeErr = errorRepo.getMe()
        assertTrue(getMeErr.isFailure)
        assertEquals("User not found", getMeErr.exceptionOrNull()?.message)
    }

    @Test
    fun testAuthRepositorySuccessAndErrors() = runBlocking {
        val repo = AuthRepository(fakeAuthApiService)
        val loginResult = repo.login(LoginRequestDto("adam@example.com", "pass123"))
        assertTrue(loginResult.isSuccess)
        assertNotNull(loginResult.getOrNull()?.token)

        val loginErr = repo.login(LoginRequestDto("error@example.com", "pass123"))
        assertTrue(loginErr.isFailure)
        assertEquals("Invalid credentials", loginErr.exceptionOrNull()?.message)

        val regResult = repo.register(RegisterRequestDTO("Adam", "Ingush", "adam@example.com", "pass123"))
        assertTrue(regResult.isSuccess)

        val regErr = repo.register(RegisterRequestDTO("Adam", "Ingush", "error@example.com", "pass123"))
        assertTrue(regErr.isFailure)
        assertEquals("Email already in use", regErr.exceptionOrNull()?.message)
    }
}
