package com.example.languagelearning.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AvatarResponseDto(
    @SerializedName("avatarUrl")
    val avatarUrl: String? = null,
    @SerializedName("avatar")
    val avatar: String? = null,
    @SerializedName("url")
    val url: String? = null,
    @SerializedName("message")
    val message: String? = null,
) {
    val resolvedAvatarUrl: String?
        get() = avatarUrl ?: avatar ?: url
}
