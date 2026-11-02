
package com.example.languagelearning.data.repository

import com.example.languagelearning.data.remote.AuthApiService
import com.example.languagelearning.data.remote.ErrorUtils
import com.example.languagelearning.data.remote.dto.LoginRequestDto
import com.example.languagelearning.data.remote.dto.LoginResponseDto
import com.example.languagelearning.data.remote.dto.RegisterRequestDTO
import com.example.languagelearning.data.remote.dto.RegisterResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: AuthApiService,
) {

    suspend fun login(
        request: LoginRequestDto
    ): Result<LoginResponseDto> {
        return runCatching {
            val response = api.login(request)

            if (response.isSuccessful) {
                response.body()
                    ?: throw Exception("Login response is empty")
            } else {
                val message = ErrorUtils.parseErrorMessage(
                    response.errorBody()?.string(),
                    "Login failed. Please check your details."
                )
                throw Exception(message)
            }
        }
    }

    suspend fun register(
        request: RegisterRequestDTO
    ): Result<RegisterResponse> {
        return runCatching {
            val response = api.register(request)

            if (response.isSuccessful) {
                response.body()
                    ?: throw Exception("Registration response is empty")
            } else {
                val message = ErrorUtils.parseErrorMessage(
                    response.errorBody()?.string(),
                    "Registration failed. Please try again."
                )
                throw Exception(message)
            }
        }
    }
}
