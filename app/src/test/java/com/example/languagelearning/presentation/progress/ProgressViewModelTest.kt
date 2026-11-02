package com.example.languagelearning.presentation.progress

import android.content.ContextWrapper
import com.example.languagelearning.data.local.GoalInfo
import com.example.languagelearning.data.local.TokenManager
import com.example.languagelearning.data.local.UserInfo
import com.example.languagelearning.data.local.WeeklyStudyTime
import com.example.languagelearning.data.remote.ApiService
import com.example.languagelearning.data.remote.dto.AddStudyTimeRequestDto
import com.example.languagelearning.data.remote.dto.AvatarResponseDto
import com.example.languagelearning.data.remote.dto.ChangePasswordRequestDto
import com.example.languagelearning.data.remote.dto.CheckAnswerRequestDto
import com.example.languagelearning.data.remote.dto.CheckAnswerResponseDto
import com.example.languagelearning.data.remote.dto.CompletedLessonsDto
import com.example.languagelearning.data.remote.dto.CourseDto
import com.example.languagelearning.data.remote.dto.DailyProgressDto
import com.example.languagelearning.data.remote.dto.LessonDto
import com.example.languagelearning.data.remote.dto.ProgressDto
import com.example.languagelearning.data.remote.dto.ProgressRecordDto
import com.example.languagelearning.data.remote.dto.QuestionDto
import com.example.languagelearning.data.remote.dto.RegisteredUser
import com.example.languagelearning.data.remote.dto.TtsRequestDto
import com.example.languagelearning.data.remote.dto.UpdateProfileRequestDto
import com.example.languagelearning.data.remote.dto.UpdateProgressRequestDto
import com.example.languagelearning.data.remote.dto.UpdateProgressResponseDto
import com.example.languagelearning.data.remote.dto.WeeklyTimeDto
import com.example.languagelearning.data.remote.dto.WeeklyTimeResponseDto
import com.example.languagelearning.data.repository.ProgressRepository
import com.example.languagelearning.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class ProgressViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val fakeUser = RegisteredUser(10, "Adam", "Ingush", "adam@example.com")

    private val fakeApiService = object : ApiService {
        override suspend fun getCourses(): List<CourseDto> = emptyList()
        override suspend fun getCoursesApi(): List<CourseDto> = emptyList()
        override suspend fun getCourseLessons(courseId: Int): List<LessonDto> = emptyList()
        override suspend fun getLesson(lessonId: Int): LessonDto = LessonDto(1, 1, "Title", "Content")
        override suspend fun getLessonsByCourse(courseId: Int): List<LessonDto> = emptyList()

        override suspend fun getProgress(userId: Int): List<ProgressRecordDto> {
            return if (userId > 0) {
                listOf(ProgressRecordDto(1, userId, 1, true, 100))
            } else {
                throw Exception("Failed to load progress")
            }
        }

        override suspend fun getProgressApi(userId: Int): List<ProgressDto> = listOf(ProgressDto(userId, 1, 5, 20))

        override suspend fun getCompletedLessons(userId: Int): Response<CompletedLessonsDto> = Response.success(CompletedLessonsDto(5))
        override suspend fun getCompletedLessonsApi(userId: Int): Response<CompletedLessonsDto> = Response.success(CompletedLessonsDto(5))

        override suspend fun updateProgress(userId: Int, lessonId: Int, body: UpdateProgressRequestDto): Response<UpdateProgressResponseDto> {
            return Response.success(UpdateProgressResponseDto("Success", ProgressRecordDto(1, userId, lessonId, true, 100), 1, 100, 100))
        }

        override suspend fun updateProgressApi(userId: Int, lessonId: Int, body: UpdateProgressRequestDto): Response<UpdateProgressResponseDto> {
            return Response.success(UpdateProgressResponseDto("Success", ProgressRecordDto(1, userId, lessonId, true, 100), 1, 100, 100))
        }

        override suspend fun getMe(): Response<RegisteredUser> = Response.success(fakeUser)
        override suspend fun getMeApi(): Response<RegisteredUser> = Response.success(fakeUser)

        override suspend fun updateMe(body: UpdateProfileRequestDto): Response<RegisteredUser> = Response.error(400, "{\"message\":\"Error\"}".toResponseBody(null))
        override suspend fun updateMeApi(body: UpdateProfileRequestDto): Response<RegisteredUser> = Response.error(400, "{\"message\":\"Error\"}".toResponseBody(null))

        override suspend fun changePassword(body: ChangePasswordRequestDto): Response<Map<String, String>> = Response.error(400, "{\"message\":\"Error\"}".toResponseBody(null))
        override suspend fun changePasswordApi(body: ChangePasswordRequestDto): Response<Map<String, String>> = Response.error(400, "{\"message\":\"Error\"}".toResponseBody(null))

        override suspend fun updateAvatar(avatar: MultipartBody.Part): Response<AvatarResponseDto> = Response.success(AvatarResponseDto())
        override suspend fun updateAvatarApi(avatar: MultipartBody.Part): Response<AvatarResponseDto> = Response.success(AvatarResponseDto())

        override suspend fun addStudyTime(body: AddStudyTimeRequestDto): Response<Map<String, Any>> = Response.success(mapOf("ok" to true))
        override suspend fun addStudyTimeApi(body: AddStudyTimeRequestDto): Response<Map<String, Any>> = Response.success(mapOf("ok" to true))

        override suspend fun getWeeklyStudyTime(): Response<WeeklyTimeResponseDto> = Response.success(WeeklyTimeResponseDto(WeeklyTimeDto(rawMon = 100, rawTue = 200, rawWed = 300, rawThu = 400, rawFri = 500, rawSat = 600, rawSun = 700)))
        override suspend fun getWeeklyStudyTimeApi(): Response<WeeklyTimeResponseDto> = Response.success(WeeklyTimeResponseDto(WeeklyTimeDto(rawMon = 100, rawTue = 200, rawWed = 300, rawThu = 400, rawFri = 500, rawSat = 600, rawSun = 700)))

        override suspend fun synthesizeIngush(body: TtsRequestDto): Response<ResponseBody> = Response.success("audio".toResponseBody(null))
        override suspend fun synthesizeIngushApi(body: TtsRequestDto): Response<ResponseBody> = Response.success("audio".toResponseBody(null))

        override suspend fun getMultipleChoice(lessonId: Int): List<QuestionDto> = emptyList()
        override suspend fun getTranslationByImage(lessonId: Int): List<QuestionDto> = emptyList()
        override suspend fun getImageByText(lessonId: Int): List<QuestionDto> = emptyList()
        override suspend fun getHandwrittenTranslation(lessonId: Int): List<QuestionDto> = emptyList()

        override suspend fun checkAnswer(questionId: Int, body: CheckAnswerRequestDto): Response<CheckAnswerResponseDto> = Response.success(CheckAnswerResponseDto(true, "Correct"))
        override suspend fun checkQuestionAnswerPath(questionId: Int, body: CheckAnswerRequestDto): Response<CheckAnswerResponseDto> = Response.success(CheckAnswerResponseDto(true, "Correct"))
    }

    private fun createFakeTokenManager(): TokenManager {
        val dummyContext = object : ContextWrapper(null) {}
        return object : TokenManager(dummyContext) {
            override val tokenFlow: Flow<String?> = flowOf("fake_token")
            override val userInfoFlow: Flow<UserInfo?> = flowOf(UserInfo(10, "Adam Ingush", "adam@example.com"))
            override val goalInfoFlow: Flow<GoalInfo> = flowOf(GoalInfo(6, 1, 6))
            override val weeklyStudyTimeFlow: Flow<WeeklyStudyTime> = flowOf(WeeklyStudyTime(100, 200, 300, 400, 500, 600, 700))

            override suspend fun checkAndResetWeeklyProgress() {}
        }
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testLoadUserProgressSuccess() {
        val progressRepo = ProgressRepository(fakeApiService)
        val userRepo = UserRepository(fakeApiService)
        val vm = ProgressViewModel(progressRepo, userRepo, createFakeTokenManager())

        vm.loadUserProgress()
        assertTrue(vm.state.value is ProgressState.Success)
        assertEquals(1, (vm.state.value as ProgressState.Success).progressList.size)
        assertEquals(400, vm.weeklyStudyTime.value.thu)
    }
}
