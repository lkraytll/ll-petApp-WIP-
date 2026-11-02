package com.example.languagelearning.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CheckAnswerRequestDto(
    @SerializedName("optionId")
    val optionId: Int? = null,
    @SerializedName("answer")
    val answer: String? = null,
)
