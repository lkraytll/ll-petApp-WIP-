package com.example.languagelearning.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ProgressDto(
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("courseId")
    val courseId: Int,
    @SerializedName("completedLessons")
    val completedLessons: Int,
    @SerializedName("totalLessons")
    val totalLessons: Int,
)
