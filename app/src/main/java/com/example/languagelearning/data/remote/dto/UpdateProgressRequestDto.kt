package com.example.languagelearning.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UpdateProgressRequestDto(
    @SerializedName("completed")
    val completed: Boolean,

    @SerializedName("score")
    val score: Int? = null,
)