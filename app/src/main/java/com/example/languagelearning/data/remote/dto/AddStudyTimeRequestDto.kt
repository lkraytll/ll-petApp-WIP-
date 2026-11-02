package com.example.languagelearning.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AddStudyTimeRequestDto(
    @SerializedName("seconds")
    val seconds: Int,
)
