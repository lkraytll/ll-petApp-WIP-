package com.example.languagelearning.presentation.progress

import android.content.ContextWrapper
import com.example.languagelearning.data.local.GoalInfo
import com.example.languagelearning.data.local.TokenManager
import com.example.languagelearning.data.local.UserInfo
import com.example.languagelearning.data.local.WeeklyStudyTime
import com.example.languagelearning.data.remote.ApiService
import com.example.languagelearning.data.remote.dto.ChangePasswordRequestDto
import com.example.languagelearning.data.remote.dto.CourseDto
import com.example.languagelearning.data.remote.dto.DailyProgressDto
import com.example.languagelearning.data.remote.dto.LessonDto
import com.example.languagelearning.data.remote.dto.ProgressDto
import com.example.languagelearning.data.remote.dto.RegisteredUser
import com.example.languagelearning.data.remote.dto.UpdateProfileRequestDto
import com.example.languagelearning.data.remote.dto.UpdateProgressRequestDto
import com.example.languagelearning.data.remote.dto.WeeklyTimeDto
import com.example.languagelearning.data.repository.ProgressRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class ProgressViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val fakeApiService = object : ApiService {
        override suspend fun getCourses(): List<CourseDto> = emptyList()
        override suspend fun getCourseLessons(courseId: Int): List<LessonDto> = emptyList()
        override suspend fun getLesson(lessonId: Int): LessonDto = LessonDto(1, 1, "Title", "Content")
        override suspend fun getLessonsByCourse(courseId: Int): List<LessonDto> = emptyList()

        override suspend fun getProgress(userId: Int): List<ProgressDto> {
            return if (userId > 0) {
                listOf(ProgressDto(userId, 1, 5, 20))
            } else {
                throw Exception("Failed to load progress")
            }
        }

        override suspend fun getDailyProgress(userId: Int): Response<DailyProgressDto> {
            val weekly = WeeklyTimeDto(100, 200, 300, 400, 500, 600, 700)
            return Response.success(DailyProgressDto("Thu", 900, 15, 5, 20, weekly))
        }

        override suspend fun updateProgress(userId: Int, lessonId: Int, body: UpdateProgressRequestDto): Response<ProgressDto> {
            return Response.success(ProgressDto(userId, 1, 6, 20))
        }

        override suspend fun getMe(): Response<RegisteredUser> = Response.error(404, "{\"message\":\"Error\"}".toResponseBody(null))
        override suspend fun updateMe(body: UpdateProfileRequestDto): Response<RegisteredUser> = Response.error(400, "{\"message\":\"Error\"}".toResponseBody(null))
        override suspend fun changePassword(body: ChangePasswordRequestDto): Response<Map<String, String>> = Response.error(400, "{\"message\":\"Error\"}".toResponseBody(null))
    }

    private fun createFakeTokenManager(): TokenManager {
        val dummyContext = object : ContextWrapper(null) {}
        return object : TokenManager(dummyContext) {
            override val tokenFlow: Flow<String?> = flowOf("fake_token")
            override val userInfoFlow: Flow<UserInfo?> = flowOf(UserInfo(10, "Adam Ingush", "adam@example.com"))
            override val goalInfoFlow: Flow<GoalInfo> = flowOf(GoalInfo(6, 1, 6))
            override val weeklyStudyTimeFlow: Flow<WeeklyStudyTime> = flowOf(WeeklyStudyTime(100, 200, 300, 400, 500, 600, 700))
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
        val vm = ProgressViewModel(progressRepo, createFakeTokenManager())

        vm.loadUserProgress()
        assertTrue(vm.state.value is ProgressState.Success)
        assertEquals(1, (vm.state.value as ProgressState.Success).progressList.size)
        assertNotNull(vm.dailyProgress.value)
        assertEquals("Thu", vm.dailyProgress.value?.day)
        assertEquals(400, vm.weeklyStudyTime.value.thu)
    }
}
