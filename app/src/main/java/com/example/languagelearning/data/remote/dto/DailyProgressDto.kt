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
    @SerializedName("mon") private val rawMon: Int? = null,
    @SerializedName("Mon") private val pascalMon: Int? = null,
    @SerializedName("Monday") private val fullMon: Int? = null,

    @SerializedName("tue") private val rawTue: Int? = null,
    @SerializedName("Tue") private val pascalTue: Int? = null,
    @SerializedName("Tuesday") private val fullTue: Int? = null,

    @SerializedName("wed") private val rawWed: Int? = null,
    @SerializedName("Wed") private val pascalWed: Int? = null,
    @SerializedName("Wednesday") private val fullWed: Int? = null,

    @SerializedName("thu") private val rawThu: Int? = null,
    @SerializedName("Thu") private val pascalThu: Int? = null,
    @SerializedName("Thursday") private val fullThu: Int? = null,

    @SerializedName("fri") private val rawFri: Int? = null,
    @SerializedName("Fri") private val pascalFri: Int? = null,
    @SerializedName("Friday") private val fullFri: Int? = null,

    @SerializedName("sat") private val rawSat: Int? = null,
    @SerializedName("Sat") private val pascalSat: Int? = null,
    @SerializedName("Saturday") private val fullSat: Int? = null,

    @SerializedName("sun") private val rawSun: Int? = null,
    @SerializedName("Sun") private val pascalSun: Int? = null,
    @SerializedName("Sunday") private val fullSun: Int? = null,
) {
    val mon: Int get() = rawMon ?: pascalMon ?: fullMon ?: 0
    val tue: Int get() = rawTue ?: pascalTue ?: fullTue ?: 0
    val wed: Int get() = rawWed ?: pascalWed ?: fullWed ?: 0
    val thu: Int get() = rawThu ?: pascalThu ?: fullThu ?: 0
    val fri: Int get() = rawFri ?: pascalFri ?: fullFri ?: 0
    val sat: Int get() = rawSat ?: pascalSat ?: fullSat ?: 0
    val sun: Int get() = rawSun ?: pascalSun ?: fullSun ?: 0

    val totalSeconds: Int get() = mon + tue + wed + thu + fri + sat + sun
}

data class WeeklyTimeResponseDto(
    @SerializedName("weeklyTime")
    val weeklyTime: WeeklyTimeDto
)