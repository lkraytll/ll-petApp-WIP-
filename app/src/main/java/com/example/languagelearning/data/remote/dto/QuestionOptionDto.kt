package com.example.languagelearning.data.remote.dto

import com.google.gson.annotations.SerializedName

data class QuestionOptionDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("text")
    val text: String? = null,
    @SerializedName("imageUrl")
    val imageUrl: String? = null,
    @SerializedName("isCorrect")
    val isCorrect: Boolean? = null,
    @SerializedName("order")
    val order: Int? = null,
)
