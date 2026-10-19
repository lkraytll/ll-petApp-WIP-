package com.example.languagelearning.presentation.registration

import com.example.languagelearning.data.remote.dto.RegisteredUser

sealed interface RegistrationState {
    data object Idle : RegistrationState
    data object Loading : RegistrationState
    data class Success(val user: RegisteredUser) : RegistrationState
    data class Error(val message: String) : RegistrationState
}
