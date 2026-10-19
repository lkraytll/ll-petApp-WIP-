package com.example.languagelearning.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.languagelearning.data.local.TokenManager
import com.example.languagelearning.data.remote.dto.RegisteredUser
import com.example.languagelearning.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val tokenManager: TokenManager,
) : ViewModel() {

    private val _state = MutableStateFlow<ProfileEditState>(ProfileEditState.Idle)
    val state: StateFlow<ProfileEditState> = _state.asStateFlow()

    private val _userProfile = MutableStateFlow<RegisteredUser?>(null)
    val userProfile: StateFlow<RegisteredUser?> = _userProfile.asStateFlow()

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _state.value = ProfileEditState.Loading
            val result = userRepository.getMe()
            result.onSuccess { user ->
                _userProfile.value = user
                _state.value = ProfileEditState.Idle
            }.onFailure {
                // Fallback to local session user info if remote fails
                tokenManager.userInfoFlow.collect { localUser ->
                    if (localUser != null) {
                        val names = localUser.name.split(" ")
                        val fName = names.getOrNull(0) ?: ""
                        val lName = if (names.size > 1) names.subList(1, names.size).joinToString(" ") else ""
                        _userProfile.value = RegisteredUser(localUser.id, fName, lName, localUser.email)
                    }
                    _state.value = ProfileEditState.Idle
                }
            }
        }
    }

    fun updateProfile(firstName: String, lastName: String, email: String) {
        val fName = firstName.trim()
        val lName = lastName.trim()
        val mail = email.trim()

        if (fName.isBlank()) {
            _state.value = ProfileEditState.Error("First name cannot be empty")
            return
        }

        viewModelScope.launch {
            _state.value = ProfileEditState.Loading
            val result = userRepository.updateMe(
                firstName = fName,
                lastName = if (lName.isNotBlank()) lName else null,
                email = if (mail.isNotBlank()) mail else null,
            )

            result.onSuccess { user ->
                _userProfile.value = user
                tokenManager.saveSession(
                    token = "", // Keep current token in DataStore
                    userId = user.id,
                    userName = "${user.firstName} ${user.lastName ?: ""}".trim(),
                    email = user.email,
                )
                _state.value = ProfileEditState.ProfileUpdated(user, "Profile updated successfully!")
            }.onFailure { throwable ->
                _state.value = ProfileEditState.Error(throwable.message ?: "Failed to update profile")
            }
        }
    }

    fun changePassword(currentPass: String, newPass: String) {
        val currentP = currentPass.trim()
        val newP = newPass.trim()

        if (currentP.isBlank() || newP.isBlank()) {
            _state.value = ProfileEditState.Error("Current and new passwords are required")
            return
        }

        if (newP.length < 8) {
            _state.value = ProfileEditState.Error("New password must be at least 8 characters")
            return
        }

        viewModelScope.launch {
            _state.value = ProfileEditState.Loading
            val result = userRepository.changePassword(currentP, newP)

            result.onSuccess { msg ->
                _state.value = ProfileEditState.PasswordChanged(msg)
            }.onFailure { throwable ->
                _state.value = ProfileEditState.Error(throwable.message ?: "Failed to change password")
            }
        }
    }

    fun resetState() {
        _state.value = ProfileEditState.Idle
    }
}
