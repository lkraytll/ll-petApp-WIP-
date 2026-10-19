package com.example.languagelearning.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("user")
    val user: RegisteredUser,
)

data class RegisteredUser(
    @SerializedName("id")
    val id: Int,
    @SerializedName("firstname")
    val firstName: String,
    @SerializedName("lastname")
    val lastName: String?,
    @SerializedName("email")
    val email: String,
)
