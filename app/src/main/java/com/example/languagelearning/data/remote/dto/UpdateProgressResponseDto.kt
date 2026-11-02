
package com.example.languagelearning.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UpdateProgressResponseDto(
    @SerializedName("message")
    val message: String,

    @SerializedName("progress")
    val progress: ProgressRecordDto,

    @SerializedName("userLevel")
    val userLevel: Int,

    @SerializedName("totalXp")
    val totalXp: Int,

    @SerializedName("xpGained")
    val xpGained: Int
)

data class ProgressRecordDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("userId")
    val userId: Int,

    @SerializedName("lessonId")
    val lessonId: Int,

    @SerializedName("completed")
    val completed: Boolean,

    @SerializedName("score")
    val score: Int?
)
