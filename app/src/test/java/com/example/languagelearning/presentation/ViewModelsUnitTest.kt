package com.example.languagelearning.presentation

import com.example.languagelearning.data.remote.dto.CourseDto
import com.example.languagelearning.data.remote.dto.ProgressDto
import com.example.languagelearning.data.remote.dto.RegisteredUser
import com.example.languagelearning.presentation.login.LoginState
import com.example.languagelearning.presentation.mainMenu.MainMenuState
import com.example.languagelearning.presentation.progress.ProgressState
import com.example.languagelearning.presentation.quizz.QuizState
import com.example.languagelearning.presentation.registration.RegistrationState
import com.example.languagelearning.presentation.settings.ProfileEditState
import org.junit.Assert.assertEquals
import org.junit.Test

class ViewModelsUnitTest {

    @Test
    fun testLoginStateSealedInterface() {
        val idleState: LoginState = LoginState.Idle
        val loadingState: LoginState = LoginState.Loading
        val errorState: LoginState = LoginState.Error("Invalid credentials")

        assertEquals(LoginState.Idle, idleState)
        assertEquals(LoginState.Loading, loadingState)
        assertEquals("Invalid credentials", (errorState as LoginState.Error).message)
    }

    @Test
    fun testRegistrationStateSealedInterface() {
        val idle: RegistrationState = RegistrationState.Idle
        val loading: RegistrationState = RegistrationState.Loading
        val user = RegisteredUser(1, "FirstName", "LastName", "user@test.com")
        val success: RegistrationState = RegistrationState.Success(user)
        val error: RegistrationState = RegistrationState.Error("Email taken")

        assertEquals(RegistrationState.Idle, idle)
        assertEquals(RegistrationState.Loading, loading)
        assertEquals(user, (success as RegistrationState.Success).user)
        assertEquals("Email taken", (error as RegistrationState.Error).message)
    }

    @Test
    fun testProfileEditStateSealedInterface() {
        val user = RegisteredUser(2, "Test", "User", "test@user.com")
        val profileUpdated: ProfileEditState = ProfileEditState.ProfileUpdated(user, "Updated")
        val passChanged: ProfileEditState = ProfileEditState.PasswordChanged("Password updated")

        assertEquals(user, (profileUpdated as ProfileEditState.ProfileUpdated).user)
        assertEquals("Updated", profileUpdated.message)
        assertEquals("Password updated", (passChanged as ProfileEditState.PasswordChanged).message)
    }

    @Test
    fun testMainMenuStateSealedInterface() {
        val loading: MainMenuState = MainMenuState.Loading
        val course = CourseDto(1, "Ingush 101", "Basics")
        val success: MainMenuState = MainMenuState.Success(listOf(course))
        val error: MainMenuState = MainMenuState.Error("Server unavailable")

        assertEquals(MainMenuState.Loading, loading)
        assertEquals(1, (success as MainMenuState.Success).courses.size)
        assertEquals("Server unavailable", (error as MainMenuState.Error).message)
    }

    @Test
    fun testProgressStateSealedInterface() {
        val loading: ProgressState = ProgressState.Loading
        val progress = ProgressDto(1, 1, 5, 20)
        val success: ProgressState = ProgressState.Success(listOf(progress))
        val error: ProgressState = ProgressState.Error("Error")

        assertEquals(ProgressState.Loading, loading)
        assertEquals(1, (success as ProgressState.Success).progressList.size)
        assertEquals("Error", (error as ProgressState.Error).message)
    }

    @Test
    fun testQuizStateSealedInterface() {
        val idle: QuizState = QuizState.Idle
        val submitting: QuizState = QuizState.Submitting
        val completed: QuizState = QuizState.Completed("Lesson finished")
        val error: QuizState = QuizState.Error("Failed to save progress")

        assertEquals(QuizState.Idle, idle)
        assertEquals(QuizState.Submitting, submitting)
        assertEquals("Lesson finished", (completed as QuizState.Completed).message)
        assertEquals("Failed to save progress", (error as QuizState.Error).message)
    }
}
