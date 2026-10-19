package com.example.languagelearning.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LessonDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("courseId")
    val courseId: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("content")
    val content: String?,
)
