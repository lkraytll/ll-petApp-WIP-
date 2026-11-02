package com.example.languagelearning.data.remote.dto

import org.junit.Assert.assertEquals
import org.junit.Test

class DtoModelsTest {

    @Test
    fun testCourseAndLessonDtos() {
        val course = CourseDto(1, "Ingush", "Ingush Course")
        assertEquals(1, course.id)
        assertEquals("Ingush", course.title)
        assertEquals("Ingush Course", course.description)

        val lesson = LessonDto(id = 10, courseId = 1, title = "Lesson 1", content = "Content 1")
        assertEquals(10, lesson.id)
        assertEquals(1, lesson.courseId)
        assertEquals("Lesson 1", lesson.title)
        assertEquals("Content 1", lesson.content)
    }

    @Test
    fun testQuestionDtos() {
        val option = QuestionOptionDto(1, "Option A", "image.png", true, 1)
        assertEquals(1, option.id)
        assertEquals("Option A", option.text)
        assertEquals(true, option.isCorrect)
        assertEquals("image.png", option.imageUrl)

        val question = QuestionDto(
            id = 100,
            type = "MULTIPLE_CHOICE",
            prompt = "Select correct",
            imageUrl = null,
            correctAnswer = "Option A",
            order = 1,
            pascalOptions = listOf(option),
        )
        assertEquals(100, question.id)
        assertEquals("MULTIPLE_CHOICE", question.type)
        assertEquals("Select correct", question.prompt)
        assertEquals(1, question.options.size)
    }

    @Test
    fun testProgressDtos() {
        val progress = ProgressDto(100, 1, 5, 20)
        assertEquals(100, progress.userId)
        assertEquals(1, progress.courseId)
        assertEquals(5, progress.completedLessons)
        assertEquals(20, progress.totalLessons)

        val req = UpdateProgressRequestDto(completed = true, score = 100)
        assertEquals(true, req.completed)
        assertEquals(100, req.score)
    }

    @Test
    fun testAuthAndUserDtos() {
        val loginReq = LoginRequestDto("user@example.com", "secret123")
        assertEquals("user@example.com", loginReq.email)
        assertEquals("secret123", loginReq.password)

        val user = RegisteredUser(1, "Adam", "Ingush", "adam@example.com")
        assertEquals(1, user.id)
        assertEquals("Adam", user.firstName)
        assertEquals("Ingush", user.lastName)

        val loginResp = LoginResponseDto("Success", "token123", user)
        assertEquals("Success", loginResp.message)
        assertEquals("token123", loginResp.token)
        assertEquals(user, loginResp.user)

        val regReq = RegisterRequestDTO("Adam", "Ingush", "adam@example.com", "secret123")
        assertEquals("Adam", regReq.firstName)

        val regResp = RegisterResponse("Registered", user)
        assertEquals("Registered", regResp.message)
        assertEquals(user, regResp.user)
    }

    @Test
    fun testWeeklyTimeDto() {
        val weekly = WeeklyTimeDto(rawMon = 10, rawTue = 20, rawWed = 30, rawThu = 40, rawFri = 50, rawSat = 60, rawSun = 70)
        assertEquals(10, weekly.mon)
        assertEquals(20, weekly.tue)
        assertEquals(30, weekly.wed)
        assertEquals(40, weekly.thu)
        assertEquals(50, weekly.fri)
        assertEquals(60, weekly.sat)
        assertEquals(70, weekly.sun)
        assertEquals(280, weekly.totalSeconds)
    }
}
