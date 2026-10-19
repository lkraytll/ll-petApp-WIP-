package com.example.languagelearning.presentation.login

import com.example.languagelearning.data.remote.dto.LoginResponseDto

sealed interface LoginState {
    data object Idle : LoginState
    data object Loading : LoginState
    data class Success(val response: LoginResponseDto) : LoginState
    data class Error(val message: String) : LoginState
}
