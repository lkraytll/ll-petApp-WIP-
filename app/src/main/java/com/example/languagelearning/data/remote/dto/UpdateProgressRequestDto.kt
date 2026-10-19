package com.example.languagelearning.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UpdateProgressRequestDto(
    @SerializedName("isCompleted")
    val isCompleted: Boolean,
    @SerializedName("score")
    val score: Int? = null,
)
