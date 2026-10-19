package com.example.languagelearning.presentation.settings

import com.example.languagelearning.data.remote.dto.RegisteredUser

sealed interface ProfileEditState {
    data object Idle : ProfileEditState
    data object Loading : ProfileEditState
    data class ProfileUpdated(val user: RegisteredUser, val message: String) : ProfileEditState
    data class PasswordChanged(val message: String) : ProfileEditState
    data class Error(val message: String) : ProfileEditState
}
