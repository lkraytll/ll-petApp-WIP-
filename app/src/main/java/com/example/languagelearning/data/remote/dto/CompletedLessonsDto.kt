package com.example.languagelearning.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CompletedLessonsDto(
    @SerializedName("completedLessons")
    val completedLessons: Int = 0,
    @SerializedName("completedLessonsCount")
    val completedLessonsCount: Int = 0,
    @SerializedName("count")
    val count: Int = 0,
) {
    val totalCount: Int
        get() = if (completedLessons > 0) completedLessons else if (completedLessonsCount > 0) completedLessonsCount else count
}
