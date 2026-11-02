package com.example.languagelearning.data.repository

import com.example.languagelearning.data.remote.ApiService
import com.example.languagelearning.data.remote.AuthApiService
import com.example.languagelearning.data.remote.dto.AddStudyTimeRequestDto
import com.example.languagelearning.data.remote.dto.AvatarResponseDto
import com.example.languagelearning.data.remote.dto.ChangePasswordRequestDto
import com.example.languagelearning.data.remote.dto.CheckAnswerRequestDto
import com.example.languagelearning.data.remote.dto.CheckAnswerResponseDto
import com.example.languagelearning.data.remote.dto.CompletedLessonsDto
import com.example.languagelearning.data.remote.dto.CourseDto
import com.example.languagelearning.data.remote.dto.LessonDto
import com.example.languagelearning.data.remote.dto.LoginRequestDto
import com.example.languagelearning.data.remote.dto.LoginResponseDto
import com.example.languagelearning.data.remote.dto.ProgressDto
import com.example.languagelearning.data.remote.dto.ProgressRecordDto
import com.example.languagelearning.data.remote.dto.QuestionDto
import com.example.languagelearning.data.remote.dto.RegisterRequestDTO
import com.example.languagelearning.data.remote.dto.RegisterResponse
import com.example.languagelearning.data.remote.dto.RegisteredUser
import com.example.languagelearning.data.remote.dto.TtsRequestDto
import com.example.languagelearning.data.remote.dto.UpdateProfileRequestDto
import com.example.languagelearning.data.remote.dto.UpdateProgressRequestDto
import com.example.languagelearning.data.remote.dto.UpdateProgressResponseDto
import com.example.languagelearning.data.remote.dto.WeeklyTimeDto
import com.example.languagelearning.data.remote.dto.WeeklyTimeResponseDto
import kotlinx.coroutines.runBlocking
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class RepositoriesTest {

    private val fakeUser = RegisteredUser(1, "Adam", "Ingush", "adam@example.com")

    private val fakeApiService = object : ApiService {
        override suspend fun getCourses(): List<CourseDto> = listOf(CourseDto(1, "Ingush Course", "Description"))
        override suspend fun getCoursesApi(): List<CourseDto> = listOf(CourseDto(1, "Ingush Course", "Description"))
        override suspend fun getCourseLessons(courseId: Int): List<LessonDto> = listOf(LessonDto(10, courseId, "Lesson 1", "Content"))
        override suspend fun getLesson(lessonId: Int): LessonDto = LessonDto(lessonId, 1, "Lesson Title", "Content")
        override suspend fun getLessonsByCourse(courseId: Int): List<LessonDto> = listOf(LessonDto(10, courseId, "Lesson 1", "Content"))

        override suspend fun getProgress(userId: Int): List<ProgressRecordDto> = listOf(ProgressRecordDto(1, userId, 1, true, 100))
        override suspend fun getProgressApi(userId: Int): List<ProgressDto> = listOf(ProgressDto(userId, 1, 5, 20))

        override suspend fun getCompletedLessons(userId: Int): Response<CompletedLessonsDto> = Response.success(CompletedLessonsDto(completedLessons = 5))
        override suspend fun getCompletedLessonsApi(userId: Int): Response<CompletedLessonsDto> = Response.success(CompletedLessonsDto(completedLessons = 5))

        override suspend fun updateProgress(userId: Int, lessonId: Int, body: UpdateProgressRequestDto): Response<UpdateProgressResponseDto> {
            return if (userId > 0) {
                Response.success(UpdateProgressResponseDto("Success", ProgressRecordDto(1, userId, lessonId, true, 100), 1, 100, 100))
            } else {
                Response.error(400, "{\"message\":\"Invalid user\"}".toResponseBody(null))
            }
        }

        override suspend fun updateProgressApi(userId: Int, lessonId: Int, body: UpdateProgressRequestDto): Response<UpdateProgressResponseDto> {
            return updateProgress(userId, lessonId, body)
        }

        override suspend fun getMe(): Response<RegisteredUser> = Response.success(fakeUser)
        override suspend fun getMeApi(): Response<RegisteredUser> = Response.success(fakeUser)

        override suspend fun updateMe(body: UpdateProfileRequestDto): Response<RegisteredUser> {
            return if (body.firstName == "Error") {
                Response.error(400, "{\"message\":\"First name invalid\"}".toResponseBody(null))
            } else {
                Response.success(fakeUser)
            }
        }

        override suspend fun updateMeApi(body: UpdateProfileRequestDto): Response<RegisteredUser> = updateMe(body)

        override suspend fun changePassword(body: ChangePasswordRequestDto): Response<Map<String, String>> {
            return if (body.currentPassword == "wrong") {
                Response.error(400, "{\"message\":\"Current password incorrect\"}".toResponseBody(null))
            } else {
                Response.success(mapOf("message" to "Password updated successfully"))
            }
        }

        override suspend fun changePasswordApi(body: ChangePasswordRequestDto): Response<Map<String, String>> = changePassword(body)

        override suspend fun updateAvatar(avatar: MultipartBody.Part): Response<AvatarResponseDto> = Response.success(AvatarResponseDto("/uploads/avatars/me.png"))
        override suspend fun updateAvatarApi(avatar: MultipartBody.Part): Response<AvatarResponseDto> = Response.success(AvatarResponseDto("/uploads/avatars/me.png"))

        override suspend fun addStudyTime(body: AddStudyTimeRequestDto): Response<Map<String, Any>> = Response.success(mapOf("success" to true))
        override suspend fun addStudyTimeApi(body: AddStudyTimeRequestDto): Response<Map<String, Any>> = Response.success(mapOf("success" to true))

        override suspend fun getWeeklyStudyTime(): Response<WeeklyTimeResponseDto> {
            return Response.success(WeeklyTimeResponseDto(WeeklyTimeDto(rawMon = 100, rawTue = 200, rawWed = 300, rawThu = 400, rawFri = 500, rawSat = 600, rawSun = 700)))
        }

        override suspend fun getWeeklyStudyTimeApi(): Response<WeeklyTimeResponseDto> = getWeeklyStudyTime()

        override suspend fun synthesizeIngush(body: TtsRequestDto): Response<ResponseBody> = Response.success("dummy audio".toResponseBody(null))
        override suspend fun synthesizeIngushApi(body: TtsRequestDto): Response<ResponseBody> = Response.success("dummy audio".toResponseBody(null))

        override suspend fun getMultipleChoice(lessonId: Int): List<QuestionDto> = listOf(QuestionDto(1, "MULTIPLE_CHOICE", "Select correct word"))
        override suspend fun getTranslationByImage(lessonId: Int): List<QuestionDto> = listOf(QuestionDto(2, "TRANSLATION_BY_IMAGE", "Meaning prompt"))
        override suspend fun getImageByText(lessonId: Int): List<QuestionDto> = listOf(QuestionDto(3, "IMAGE_BY_TEXT", "Image prompt"))
        override suspend fun getHandwrittenTranslation(lessonId: Int): List<QuestionDto> = listOf(QuestionDto(4, "HANDWRITTEN_TRANSLATION", "Translate prompt"))

        override suspend fun checkAnswer(questionId: Int, body: CheckAnswerRequestDto): Response<CheckAnswerResponseDto> {
            return if (questionId > 0) {
                Response.success(CheckAnswerResponseDto(correct = true, message = "Correct answer"))
            } else {
                Response.error(400, "{\"message\":\"Invalid question ID\"}".toResponseBody(null))
            }
        }

        override suspend fun checkQuestionAnswerPath(questionId: Int, body: CheckAnswerRequestDto): Response<CheckAnswerResponseDto> = checkAnswer(questionId, body)
    }

    private val fakeErrorApiService = object : ApiService {
        override suspend fun getCourses(): List<CourseDto> = throw Exception("Network error")
        override suspend fun getCoursesApi(): List<CourseDto> = throw Exception("Network error")
        override suspend fun getCourseLessons(courseId: Int): List<LessonDto> = throw Exception("Network error")
        override suspend fun getLesson(lessonId: Int): LessonDto = throw Exception("Network error")
        override suspend fun getLessonsByCourse(courseId: Int): List<LessonDto> = throw Exception("Network error")
        override suspend fun getProgress(userId: Int): List<ProgressRecordDto> = throw Exception("Network error")
        override suspend fun getProgressApi(userId: Int): List<ProgressDto> = throw Exception("Network error")
        override suspend fun getCompletedLessons(userId: Int): Response<CompletedLessonsDto> = Response.error(500, "{\"message\":\"Error\"}".toResponseBody(null))
        override suspend fun getCompletedLessonsApi(userId: Int): Response<CompletedLessonsDto> = Response.error(500, "{\"message\":\"Error\"}".toResponseBody(null))
        override suspend fun updateProgress(userId: Int, lessonId: Int, body: UpdateProgressRequestDto): Response<UpdateProgressResponseDto> = Response.error(500, "{\"message\":\"Server error\"}".toResponseBody(null))
        override suspend fun updateProgressApi(userId: Int, lessonId: Int, body: UpdateProgressRequestDto): Response<UpdateProgressResponseDto> = Response.error(500, "{\"message\":\"Server error\"}".toResponseBody(null))
        override suspend fun getMe(): Response<RegisteredUser> = Response.error(404, "{\"message\":\"User not found\"}".toResponseBody(null))
        override suspend fun getMeApi(): Response<RegisteredUser> = Response.error(404, "{\"message\":\"User not found\"}".toResponseBody(null))
        override suspend fun updateMe(body: UpdateProfileRequestDto): Response<RegisteredUser> = Response.error(409, "{\"message\":\"Email in use\"}".toResponseBody(null))
        override suspend fun updateMeApi(body: UpdateProfileRequestDto): Response<RegisteredUser> = Response.error(409, "{\"message\":\"Email in use\"}".toResponseBody(null))
        override suspend fun changePassword(body: ChangePasswordRequestDto): Response<Map<String, String>> = Response.error(400, "{\"message\":\"Invalid\"}".toResponseBody(null))
        override suspend fun changePasswordApi(body: ChangePasswordRequestDto): Response<Map<String, String>> = Response.error(400, "{\"message\":\"Invalid\"}".toResponseBody(null))
        override suspend fun updateAvatar(avatar: MultipartBody.Part): Response<AvatarResponseDto> = Response.error(400, "{\"message\":\"Upload failed\"}".toResponseBody(null))
        override suspend fun updateAvatarApi(avatar: MultipartBody.Part): Response<AvatarResponseDto> = Response.error(400, "{\"message\":\"Upload failed\"}".toResponseBody(null))
        override suspend fun addStudyTime(body: AddStudyTimeRequestDto): Response<Map<String, Any>> = Response.error(400, "{\"message\":\"Error\"}".toResponseBody(null))
        override suspend fun addStudyTimeApi(body: AddStudyTimeRequestDto): Response<Map<String, Any>> = Response.error(400, "{\"message\":\"Error\"}".toResponseBody(null))
        override suspend fun getWeeklyStudyTime(): Response<WeeklyTimeResponseDto> = Response.error(500, "{\"message\":\"Error loading weekly\"}".toResponseBody(null))
        override suspend fun getWeeklyStudyTimeApi(): Response<WeeklyTimeResponseDto> = Response.error(500, "{\"message\":\"Error loading weekly\"}".toResponseBody(null))
        override suspend fun synthesizeIngush(body: TtsRequestDto): Response<ResponseBody> = Response.error(500, "{\"message\":\"TTS error\"}".toResponseBody(null))
        override suspend fun synthesizeIngushApi(body: TtsRequestDto): Response<ResponseBody> = Response.error(500, "{\"message\":\"TTS error\"}".toResponseBody(null))
        override suspend fun getMultipleChoice(lessonId: Int): List<QuestionDto> = throw Exception("Error")
        override suspend fun getTranslationByImage(lessonId: Int): List<QuestionDto> = throw Exception("Error")
        override suspend fun getImageByText(lessonId: Int): List<QuestionDto> = throw Exception("Error")
        override suspend fun getHandwrittenTranslation(lessonId: Int): List<QuestionDto> = throw Exception("Error")
        override suspend fun checkAnswer(questionId: Int, body: CheckAnswerRequestDto): Response<CheckAnswerResponseDto> = Response.error(404, "{\"message\":\"Question not found\"}".toResponseBody(null))
        override suspend fun checkQuestionAnswerPath(questionId: Int, body: CheckAnswerRequestDto): Response<CheckAnswerResponseDto> = Response.error(404, "{\"message\":\"Question not found\"}".toResponseBody(null))
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

        val lessonResult = repo.getLesson(1)
        assertTrue(lessonResult.isSuccess)
        assertEquals("Lesson Title", lessonResult.getOrNull()?.title)

        val checkResult = repo.checkAnswer(1, optionId = 2)
        assertTrue(checkResult.isSuccess)
        assertTrue(checkResult.getOrNull()?.correct == true)

        val checkErr = repo.checkAnswer(-1, optionId = 2)
        assertTrue(checkErr.isFailure)
        assertEquals("Invalid question ID", checkErr.exceptionOrNull()?.message)
    }

    @Test
    fun testProgressRepositorySuccessAndError() = runBlocking {
        val repo = ProgressRepository(fakeApiService)
        val progressResult = repo.getProgress(100)
        assertTrue(progressResult.isSuccess)

        val completedResult = repo.getCompletedLessons(100)
        assertTrue(completedResult.isSuccess)
        assertEquals(5, completedResult.getOrNull())

        val addTimeResult = repo.addStudyTime(300)
        assertTrue(addTimeResult.isSuccess)

        val weeklyResult = repo.getWeeklyStudyTime()
        assertTrue(weeklyResult.isSuccess)
        assertEquals(400, weeklyResult.getOrNull()?.thu)

        val updateResult = repo.updateProgress(100, 10, true, 100)
        assertTrue(updateResult.isSuccess)

        val errorResult = repo.updateProgress(-1, 10, true, 100)
        assertTrue(errorResult.isFailure)
        assertEquals("Invalid user", errorResult.exceptionOrNull()?.message)
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
