package com.example.languagelearning.data.repository

import com.example.languagelearning.data.remote.ApiService
import com.example.languagelearning.data.remote.ErrorUtils
import com.example.languagelearning.data.remote.dto.AvatarResponseDto
import com.example.languagelearning.data.remote.dto.ChangePasswordRequestDto
import com.example.languagelearning.data.remote.dto.RegisteredUser
import com.example.languagelearning.data.remote.dto.UpdateProfileRequestDto
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val api: ApiService,
) {

    suspend fun getMe(): Result<RegisteredUser> {
        return runCatching {
            val response = api.getMe()

            if (response.isSuccessful) {
                response.body()
                    ?: throw Exception("Profile response is empty")
            } else {
                val message = ErrorUtils.parseErrorMessage(
                    response.errorBody()?.string(),
                    "Failed to get profile"
                )
                throw Exception(message)
            }
        }
    }

    suspend fun updateMe(
        firstName: String?,
        lastName: String?,
        email: String?,
    ): Result<RegisteredUser> {
        return runCatching {
            val body = UpdateProfileRequestDto(
                firstName = firstName,
                lastName = lastName,
                email = email
            )

            val response = api.updateMe(body)

            if (response.isSuccessful) {
                response.body()
                    ?: throw Exception("Updated profile response is empty")
            } else {
                val message = ErrorUtils.parseErrorMessage(
                    response.errorBody()?.string(),
                    "Failed to update profile"
                )
                throw Exception(message)
            }
        }
    }

    suspend fun changePassword(
        currentPassword: String,
        newPassword: String,
    ): Result<String> {
        return runCatching {
            val body = ChangePasswordRequestDto(
                currentPassword = currentPassword,
                newPassword = newPassword
            )

            val response = api.changePassword(body)

            if (response.isSuccessful) {
                response.body()?.get("message")
                    ?: "Password updated successfully"
            } else {
                val message = ErrorUtils.parseErrorMessage(
                    response.errorBody()?.string(),
                    "Failed to change password"
                )
                throw Exception(message)
            }
        }
    }

    suspend fun updateAvatar(file: File): Result<AvatarResponseDto> {
        return runCatching {
            val requestFile = file.asRequestBody(
                "image/*".toMediaTypeOrNull()
            )

            val body = MultipartBody.Part.createFormData(
                "avatar",
                file.name,
                requestFile
            )

            val response = api.updateAvatar(body)

            if (response.isSuccessful) {
                response.body()
                    ?: throw Exception("Avatar response is empty")
            } else {
                val message = ErrorUtils.parseErrorMessage(
                    response.errorBody()?.string(),
                    "Failed to update avatar"
                )
                throw Exception(message)
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    suspend fun uploadAvatar(
        userId: Int,
        file: File
    ): Result<RegisteredUser> {
        return Result.failure(
            UnsupportedOperationException(
                "Use updateAvatar(file) instead."
            )
        )
    }
}
