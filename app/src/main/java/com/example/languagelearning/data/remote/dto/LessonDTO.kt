package com.example.languagelearning.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LessonDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("courseId")
    val courseId: Int? = null,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("content")
    val content: String? = null,
    @SerializedName("order")
    val order: Int? = null,
    @SerializedName("questions")
    private val lowercaseQuestions: List<QuestionDto>? = null,
    @SerializedName("Questions")
    private val pascalQuestions: List<QuestionDto>? = null,
) {
    val questions: List<QuestionDto>?
        get() = lowercaseQuestions ?: pascalQuestions
}
