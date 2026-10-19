package com.example.languagelearning.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RegisterRequestDTO(
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("lastName")
    val lastName: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
)
