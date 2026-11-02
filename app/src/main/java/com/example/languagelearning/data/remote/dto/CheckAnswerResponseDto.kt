package com.example.languagelearning.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CheckAnswerResponseDto(
    @SerializedName("correct")
    val correct: Boolean,
    @SerializedName("message")
    val message: String,
)
