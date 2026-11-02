package com.example.languagelearning.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("user")
    val user: RegisteredUser,
)

data class RegisteredUser(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("firstName")
    val firstName: String = "User",

    @SerializedName("lastName")
    val lastName: String? = null,

    @SerializedName("email")
    val email: String = "",

    @SerializedName("level")
    val level: Int = 1,

    @SerializedName("xp")
    val xp: Int = 0,

    @SerializedName("avatarUrl")
    val avatarUrl: String? = null,

    @SerializedName("firstname")
    private val lowerFirstName: String? = null,

    @SerializedName("name")
    private val nameProp: String? = null,

    @SerializedName("lastname")
    private val lowerLastName: String? = null,
) {
    val resolvedFirstName: String
        get() {
            val fn = if (firstName != "User") firstName else (lowerFirstName ?: nameProp ?: "User")
            return if (fn.isNotBlank()) fn else "User"
        }

    val resolvedLastName: String?
        get() = lastName ?: lowerLastName

    val fullName: String
        get() {
            val fn = resolvedFirstName
            val ln = resolvedLastName?.trim() ?: ""
            return if (ln.isNotBlank()) "$fn $ln" else fn
        }
}
