package com.example.languagelearning.data.remote.dto

import com.google.gson.annotations.SerializedName

data class UpdateProfileRequestDto(
    @SerializedName("firstName")
    val firstName: String? = null,
    @SerializedName("lastName")
    val lastName: String? = null,
    @SerializedName("email")
    val email: String? = null,
)
