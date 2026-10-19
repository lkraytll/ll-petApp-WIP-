package com.example.languagelearning.data.remote

import com.example.languagelearning.data.remote.dto.LoginRequestDto
import com.example.languagelearning.data.remote.dto.LoginResponseDto
import com.example.languagelearning.data.remote.dto.RegisterRequestDTO
import com.example.languagelearning.data.remote.dto.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequestDto,
    ): Response<LoginResponseDto>

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequestDTO,
    ): Response<RegisterResponse>
}
