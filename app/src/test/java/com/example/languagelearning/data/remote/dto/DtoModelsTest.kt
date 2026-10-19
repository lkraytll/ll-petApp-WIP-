package com.example.languagelearning.data.remote.dto

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class DtoModelsTest {

    @Test
    fun testCourseDto() {
        val course = CourseDto(
            id = 1,
            title = "Ingush Language Basics",
            description = "Learn basic vocabulary and phrases"
        )

        assertEquals(1, course.id)
        assertEquals("Ingush Language Basics", course.title)
        assertEquals("Learn basic vocabulary and phrases", course.description)
    }

    @Test
    fun testLessonDto() {
        val lesson = LessonDto(
            id = 10,
            courseId = 1,
            title = "Greetings",
            content = "Basic greetings content"
        )

        assertEquals(10, lesson.id)
        assertEquals(1, lesson.courseId)
        assertEquals("Greetings", lesson.title)
        assertEquals("Basic greetings content", lesson.content)
    }

    @Test
    fun testProgressDtoAndRequest() {
        val progress = ProgressDto(
            userId = 100,
            courseId = 1,
            completedLessons = 5,
            totalLessons = 20
        )

        assertEquals(100, progress.userId)
        assertEquals(1, progress.courseId)
        assertEquals(5, progress.completedLessons)
        assertEquals(20, progress.totalLessons)

        val req = UpdateProgressRequestDto(isCompleted = true, score = 100)
        assertEquals(true, req.isCompleted)
        assertEquals(100, req.score)
    }

    @Test
    fun testAuthAndUserDtos() {
        val loginReq = LoginRequestDto("user@example.com", "secret123")
        assertEquals("user@example.com", loginReq.email)
        assertEquals("secret123", loginReq.password)

        val regUser = RegisteredUser(1, "Adam", "Ingush", "adam@example.com")
        assertEquals(1, regUser.id)
        assertEquals("Adam", regUser.firstName)
        assertEquals("Ingush", regUser.lastName)
        assertEquals("adam@example.com", regUser.email)

        val loginResp = LoginResponseDto("Success", "jwt_token_123", regUser)
        assertEquals("Success", loginResp.message)
        assertEquals("jwt_token_123", loginResp.token)
        assertNotNull(loginResp.user)

        val regReq = RegisterRequestDTO("Adam", "Ingush", "adam@example.com", "pass12345")
        assertEquals("Adam", regReq.firstName)
        assertEquals("Ingush", regReq.lastName)

        val regResp = RegisterResponse("Registered", regUser)
        assertEquals("Registered", regResp.message)

        val updateReq = UpdateProfileRequestDto("NewName", "NewLast", "new@example.com")
        assertEquals("NewName", updateReq.firstName)
        assertEquals("NewLast", updateReq.lastName)
        assertEquals("new@example.com", updateReq.email)

        val passReq = ChangePasswordRequestDto("oldPass123", "newPass123")
        assertEquals("oldPass123", passReq.currentPassword)
        assertEquals("newPass123", passReq.newPassword)
    }
}
