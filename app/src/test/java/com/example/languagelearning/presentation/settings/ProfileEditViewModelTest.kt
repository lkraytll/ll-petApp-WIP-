package com.example.languagelearning.presentation.settings

import android.content.ContextWrapper
import com.example.languagelearning.data.local.GoalInfo
import com.example.languagelearning.data.local.TokenManager
import com.example.languagelearning.data.local.UserInfo
import com.example.languagelearning.data.remote.ApiService
import com.example.languagelearning.data.remote.dto.ChangePasswordRequestDto
import com.example.languagelearning.data.remote.dto.CourseDto
import com.example.languagelearning.data.remote.dto.LessonDto
import com.example.languagelearning.data.remote.dto.ProgressDto
import com.example.languagelearning.data.remote.dto.RegisteredUser
import com.example.languagelearning.data.remote.dto.UpdateProfileRequestDto
import com.example.languagelearning.data.remote.dto.UpdateProgressRequestDto
import com.example.languagelearning.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileEditViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val fakeUser = RegisteredUser(1, "Adam", "Ingush", "adam@example.com")

    private val fakeApiService = object : ApiService {
        override suspend fun getCourses(): List<CourseDto> = emptyList()
        override suspend fun getCourseLessons(courseId: Int): List<LessonDto> = emptyList()
        override suspend fun getLesson(lessonId: Int): LessonDto = LessonDto(1, 1, "Title", "Content")
        override suspend fun getLessonsByCourse(courseId: Int): List<LessonDto> = emptyList()
        override suspend fun getProgress(userId: Int): List<ProgressDto> = emptyList()
        override suspend fun updateProgress(userId: Int, lessonId: Int, body: UpdateProgressRequestDto): Response<ProgressDto> = Response.success(ProgressDto(userId, 1, 1, 1))

        override suspend fun getMe(): Response<RegisteredUser> {
            return Response.success(fakeUser)
        }

        override suspend fun updateMe(body: UpdateProfileRequestDto): Response<RegisteredUser> {
            return Response.success(RegisteredUser(1, body.firstName ?: "Adam", body.lastName ?: "Ingush", body.email ?: "adam@example.com"))
        }

        override suspend fun changePassword(body: ChangePasswordRequestDto): Response<Map<String, String>> {
            return Response.success(mapOf("message" to "Password updated successfully"))
        }
    }

    private fun createFakeTokenManager(): TokenManager {
        val dummyContext = object : ContextWrapper(null) {}
        return object : TokenManager(dummyContext) {
            override val tokenFlow: Flow<String?> = flowOf("fake_token")
            override val userInfoFlow: Flow<UserInfo?> = flowOf(UserInfo(1, "Adam Ingush", "adam@example.com"))
            override val goalInfoFlow: Flow<GoalInfo> = flowOf(GoalInfo(6, 1, 6))

            override suspend fun saveSession(token: String, userId: Int, userName: String, email: String) {}
            override suspend fun saveToken(token: String) {}
            override suspend fun clearToken() {}
            override suspend fun saveGoal(targetDays: Int) {}
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
    fun testLoadUserProfileAndState() {
        val userRepo = UserRepository(fakeApiService)
        val vm = ProfileEditViewModel(userRepo, createFakeTokenManager())

        vm.loadUserProfile()
        assertNotNull(vm.userProfile.value)
        assertEquals("Adam", vm.userProfile.value?.firstName)

        vm.resetState()
        assertEquals(ProfileEditState.Idle, vm.state.value)
    }

    @Test
    fun testUpdateProfileValidationAndSuccess() {
        val userRepo = UserRepository(fakeApiService)
        val vm = ProfileEditViewModel(userRepo, createFakeTokenManager())

        vm.updateProfile("", "Ingush", "adam@example.com")
        assertTrue(vm.state.value is ProfileEditState.Error)
        assertEquals("First name cannot be empty", (vm.state.value as ProfileEditState.Error).message)

        vm.updateProfile("AdamUpdated", "IngushUpdated", "adam@example.com")
        assertTrue(vm.state.value is ProfileEditState.ProfileUpdated)
        assertEquals("AdamUpdated", vm.userProfile.value?.firstName)
    }

    @Test
    fun testChangePasswordValidationAndSuccess() {
        val userRepo = UserRepository(fakeApiService)
        val vm = ProfileEditViewModel(userRepo, createFakeTokenManager())

        vm.changePassword("", "newPassword123")
        assertTrue(vm.state.value is ProfileEditState.Error)
        assertEquals("Current and new passwords are required", (vm.state.value as ProfileEditState.Error).message)

        vm.changePassword("oldPass123", "short")
        assertTrue(vm.state.value is ProfileEditState.Error)
        assertEquals("New password must be at least 8 characters", (vm.state.value as ProfileEditState.Error).message)

        vm.changePassword("oldPass123", "validNewPassword123")
        assertTrue(vm.state.value is ProfileEditState.PasswordChanged)
        assertEquals("Password updated successfully", (vm.state.value as ProfileEditState.PasswordChanged).message)
    }
}
