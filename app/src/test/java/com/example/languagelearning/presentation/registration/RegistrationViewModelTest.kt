package com.example.languagelearning.presentation.registration

import android.content.ContextWrapper
import com.example.languagelearning.data.local.GoalInfo
import com.example.languagelearning.data.local.TokenManager
import com.example.languagelearning.data.local.UserInfo
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
import com.example.languagelearning.data.repository.AuthRepository
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class RegistrationViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val fakeUser = RegisteredUser(100, "Adam", "Ingush", "adam@example.com")

    private val fakeApiService = object : ApiService {
        override suspend fun getCourses(): List<CourseDto> = emptyList()
        override suspend fun getCoursesApi(): List<CourseDto> = emptyList()
        override suspend fun getCourseLessons(courseId: Int): List<LessonDto> = emptyList()
        override suspend fun getLesson(lessonId: Int): LessonDto = LessonDto(1, 1, "Title", "Content")
        override suspend fun getLessonsByCourse(courseId: Int): List<LessonDto> = emptyList()
        override suspend fun getProgress(userId: Int): List<ProgressRecordDto> = emptyList()
        override suspend fun getProgressApi(userId: Int): List<ProgressDto> = emptyList()
        override suspend fun getCompletedLessons(userId: Int): Response<CompletedLessonsDto> = Response.success(CompletedLessonsDto())
        override suspend fun getCompletedLessonsApi(userId: Int): Response<CompletedLessonsDto> = Response.success(CompletedLessonsDto())
        override suspend fun updateProgress(userId: Int, lessonId: Int, body: UpdateProgressRequestDto): Response<UpdateProgressResponseDto> = Response.success(UpdateProgressResponseDto("Success", ProgressRecordDto(1, userId, lessonId, true, 100), 1, 100, 100))
        override suspend fun updateProgressApi(userId: Int, lessonId: Int, body: UpdateProgressRequestDto): Response<UpdateProgressResponseDto> = Response.success(UpdateProgressResponseDto("Success", ProgressRecordDto(1, userId, lessonId, true, 100), 1, 100, 100))
        override suspend fun getMe(): Response<RegisteredUser> = Response.success(fakeUser)
        override suspend fun getMeApi(): Response<RegisteredUser> = Response.success(fakeUser)
        override suspend fun updateMe(body: UpdateProfileRequestDto): Response<RegisteredUser> = Response.success(fakeUser)
        override suspend fun updateMeApi(body: UpdateProfileRequestDto): Response<RegisteredUser> = Response.success(fakeUser)
        override suspend fun changePassword(body: ChangePasswordRequestDto): Response<Map<String, String>> = Response.success(mapOf("ok" to "true"))
        override suspend fun changePasswordApi(body: ChangePasswordRequestDto): Response<Map<String, String>> = Response.success(mapOf("ok" to "true"))
        override suspend fun updateAvatar(avatar: MultipartBody.Part): Response<AvatarResponseDto> = Response.success(AvatarResponseDto())
        override suspend fun updateAvatarApi(avatar: MultipartBody.Part): Response<AvatarResponseDto> = Response.success(AvatarResponseDto())
        override suspend fun addStudyTime(body: AddStudyTimeRequestDto): Response<Map<String, Any>> = Response.success(mapOf("ok" to true))
        override suspend fun addStudyTimeApi(body: AddStudyTimeRequestDto): Response<Map<String, Any>> = Response.success(mapOf("ok" to true))
        override suspend fun getWeeklyStudyTime(): Response<WeeklyTimeResponseDto> = Response.success(WeeklyTimeResponseDto(WeeklyTimeDto()))
        override suspend fun getWeeklyStudyTimeApi(): Response<WeeklyTimeResponseDto> = Response.success(WeeklyTimeResponseDto(WeeklyTimeDto()))
        override suspend fun synthesizeIngush(body: TtsRequestDto): Response<ResponseBody> = Response.success("audio".toResponseBody(null))
        override suspend fun synthesizeIngushApi(body: TtsRequestDto): Response<ResponseBody> = Response.success("audio".toResponseBody(null))
        override suspend fun getMultipleChoice(lessonId: Int): List<QuestionDto> = emptyList()
        override suspend fun getTranslationByImage(lessonId: Int): List<QuestionDto> = emptyList()
        override suspend fun getImageByText(lessonId: Int): List<QuestionDto> = emptyList()
        override suspend fun getHandwrittenTranslation(lessonId: Int): List<QuestionDto> = emptyList()
        override suspend fun checkAnswer(questionId: Int, body: CheckAnswerRequestDto): Response<CheckAnswerResponseDto> = Response.success(CheckAnswerResponseDto(true, "Correct"))
        override suspend fun checkQuestionAnswerPath(questionId: Int, body: CheckAnswerRequestDto): Response<CheckAnswerResponseDto> = Response.success(CheckAnswerResponseDto(true, "Correct"))
    }

    private val fakeAuthApiService = object : AuthApiService {
        override suspend fun login(request: LoginRequestDto): Response<LoginResponseDto> {
            return Response.success(LoginResponseDto("OK", "token_123", fakeUser))
        }

        override suspend fun register(request: RegisterRequestDTO): Response<RegisterResponse> {
            return if (request.email == "error@example.com") {
                Response.error(409, "{\"message\": \"Email already registered\"}".toResponseBody(null))
            } else {
                Response.success(RegisterResponse("User registered", fakeUser))
            }
        }
    }

    private fun createFakeTokenManager(): TokenManager {
        val dummyContext = object : ContextWrapper(null) {}
        return object : TokenManager(dummyContext) {
            override val tokenFlow: Flow<String?> = flowOf("fake_token")
            override val userInfoFlow: Flow<UserInfo?> = flowOf(UserInfo(100, "Adam Ingush", "adam@example.com"))
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
    fun testValidateAndSetPersonalData() {
        val authRepo = AuthRepository(fakeAuthApiService)
        val userRepo = UserRepository(fakeApiService)
        val vm = RegistrationViewModel(authRepo, userRepo, createFakeTokenManager())

        val err1 = vm.validateAndSetPersonalData("", "Ingush", "adam@example.com")
        assertEquals("Enter your first name", err1)

        val err2 = vm.validateAndSetPersonalData("Adam", "", "adam@example.com")
        assertEquals("Enter your last name", err2)

        val err3 = vm.validateAndSetPersonalData("Adam", "Ingush", "invalid-email")
        assertEquals("Enter a valid email address", err3)

        val valid = vm.validateAndSetPersonalData("Adam", "Ingush", "adam@example.com")
        assertNull(valid)
    }

    @Test
    fun testRegisterPasswordValidationAndSuccess() {
        val authRepo = AuthRepository(fakeAuthApiService)
        val userRepo = UserRepository(fakeApiService)
        val vm = RegistrationViewModel(authRepo, userRepo, createFakeTokenManager())

        vm.validateAndSetPersonalData("Adam", "Ingush", "adam@example.com")

        vm.register("", "pass123")
        assertTrue(vm.state.value is RegistrationState.Error)
        assertEquals("Enter a password", (vm.state.value as RegistrationState.Error).message)

        vm.register("pass123", "differentPass")
        assertTrue(vm.state.value is RegistrationState.Error)
        assertEquals("Passwords don't match", (vm.state.value as RegistrationState.Error).message)

        vm.register("pass123", "pass123")
        assertTrue(vm.state.value is RegistrationState.Success)
        assertEquals(fakeUser, (vm.state.value as RegistrationState.Success).user)

        vm.resetState()
        assertEquals(RegistrationState.Idle, vm.state.value)
    }

    @Test
    fun testRegisterFailureState() {
        val authRepo = AuthRepository(fakeAuthApiService)
        val userRepo = UserRepository(fakeApiService)
        val vm = RegistrationViewModel(authRepo, userRepo, createFakeTokenManager())

        vm.validateAndSetPersonalData("Adam", "Ingush", "error@example.com")
        vm.register("pass123", "pass123")

        assertTrue(vm.state.value is RegistrationState.Error)
        assertEquals("Email already registered", (vm.state.value as RegistrationState.Error).message)
    }
}
