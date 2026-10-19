package com.example.languagelearning.data.repository

import com.example.languagelearning.data.remote.ApiService
import com.example.languagelearning.data.remote.dto.CourseDto
import com.example.languagelearning.data.remote.dto.LessonDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CourseRepository @Inject constructor(
    private val api: ApiService,
) {

    suspend fun getCourses(): Result<List<CourseDto>> {
        return runCatching {
            api.getCourses()
        }
    }

    suspend fun getCourseLessons(courseId: Int): Result<List<LessonDto>> {
        return runCatching {
            api.getCourseLessons(courseId)
        }
    }
}
