package com.example.languagelearning.data.remote.dto

import com.google.gson.annotations.SerializedName

data class QuestionDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("type")
    val type: String, // "IMAGE_BY_TEXT", "TRANSLATION_BY_IMAGE", "MULTIPLE_CHOICE", "HANDWRITTEN_TRANSLATION"
    @SerializedName("prompt")
    val prompt: String,
    @SerializedName("imageUrl")
    val imageUrl: String? = null,
    @SerializedName("correctAnswer")
    val correctAnswer: String? = null,
    @SerializedName("order")
    val order: Int? = null,
    @SerializedName("QuestionOptions")
    private val pascalOptions: List<QuestionOptionDto>? = null,
    @SerializedName("options")
    private val rawOptions: List<QuestionOptionDto>? = null,
    @SerializedName("question_options")
    private val snakeOptions: List<QuestionOptionDto>? = null,
) {
    val options: List<QuestionOptionDto>
        get() = pascalOptions ?: rawOptions ?: snakeOptions ?: emptyList()
}
