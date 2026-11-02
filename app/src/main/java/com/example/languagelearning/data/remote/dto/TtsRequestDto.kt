package com.example.languagelearning.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TtsRequestDto(
    @SerializedName("text")
    val text: String,
    @SerializedName("speaker")
    val speaker: String = "inh_0",
    @SerializedName("ssml")
    val ssml: Boolean = false,
)
