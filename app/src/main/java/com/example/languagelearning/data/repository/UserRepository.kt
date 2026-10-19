package com.example.languagelearning.data.repository

import com.example.languagelearning.data.remote.ApiService
import com.example.languagelearning.data.remote.ErrorUtils
import com.example.languagelearning.data.remote.dto.ChangePasswordRequestDto
import com.example.languagelearning.data.remote.dto.RegisteredUser
import com.example.languagelearning.data.remote.dto.UpdateProfileRequestDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val api: ApiService,
) {

    suspend fun getMe(): Result<RegisteredUser> {
        return runCatching {
            val response = api.getMe()
            if (response.isSuccessful && response.body() != null) {
                response.body()!!
            } else {
                val rawError = response.errorBody()?.string()
                val parsed = ErrorUtils.parseErrorMessage(rawError, "Failed to get profile")
                throw Exception(parsed)
            }
        }
    }

    suspend fun updateMe(
        firstName: String?,
        lastName: String?,
        email: String?,
    ): Result<RegisteredUser> {
        return runCatching {
            val response = api.updateMe(
                UpdateProfileRequestDto(
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                ),
            )
            if (response.isSuccessful && response.body() != null) {
                response.body()!!
            } else {
                val rawError = response.errorBody()?.string()
                val parsed = ErrorUtils.parseErrorMessage(rawError, "Failed to update profile")
                throw Exception(parsed)
            }
        }
    }

    suspend fun changePassword(
        currentPassword: String,
        newPassword: String,
    ): Result<String> {
        return runCatching {
            val response = api.changePassword(
                ChangePasswordRequestDto(
                    currentPassword = currentPassword,
                    newPassword = newPassword,
                ),
            )
            if (response.isSuccessful) {
                response.body()?.get("message") ?: "Password updated successfully"
            } else {
                val rawError = response.errorBody()?.string()
                val parsed = ErrorUtils.parseErrorMessage(rawError, "Failed to change password")
                throw Exception(parsed)
            }
        }
    }
}
