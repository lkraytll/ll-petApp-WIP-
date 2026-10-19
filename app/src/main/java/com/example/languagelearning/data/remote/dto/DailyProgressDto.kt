package com.example.languagelearning.data.remote.dto

import com.google.gson.annotations.SerializedName

data class DailyProgressDto(
    @SerializedName("day")
    val day: String? = "Today",
    @SerializedName("timeSeconds")
    val timeSeconds: Int = 0,
    @SerializedName("timeMinutes")
    val timeMinutes: Int = 0,
    @SerializedName("completedLessons")
    val completedLessons: Int = 0,
    @SerializedName("totalLessons")
    val totalLessons: Int = 20,
    @SerializedName("weeklyTime")
    val weeklyTime: WeeklyTimeDto? = null,
)

data class WeeklyTimeDto(
    @SerializedName("mon") val mon: Int = 0,
    @SerializedName("tue") val tue: Int = 0,
    @SerializedName("wed") val wed: Int = 0,
    @SerializedName("thu") val thu: Int = 0,
    @SerializedName("fri") val fri: Int = 0,
    @SerializedName("sat") val sat: Int = 0,
    @SerializedName("sun") val sun: Int = 0,
)
