package com.example.languagelearning.presentation.registration

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.languagelearning.data.local.TokenManager
import com.example.languagelearning.data.remote.dto.RegisterRequestDTO
import com.example.languagelearning.data.remote.dto.RegisteredUser
import com.example.languagelearning.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface RegistrationState {
    data object Idle : RegistrationState
    data object Loading : RegistrationState
    data class Success(val user: RegisteredUser) : RegistrationState
    data class Error(val message: String) : RegistrationState
}

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager,
) : ViewModel() {

    private var firstName = ""
    private var lastName = ""
    private var email = ""

    private val _state = MutableStateFlow<RegistrationState>(RegistrationState.Idle)
    val state: StateFlow<RegistrationState> = _state.asStateFlow()

    fun validateAndSetPersonalData(
        firstName: String,
        lastName: String,
        email: String,
    ): String? {
        val trimmedFirstName = firstName.trim()
        val trimmedLastName = lastName.trim()
        val trimmedEmail = email.trim()

        if (trimmedFirstName.isBlank()) {
            return "Enter your first name"
        }
        if (trimmedLastName.isBlank()) {
            return "Enter your last name"
        }
        if (trimmedEmail.isBlank() || !isValidEmail(trimmedEmail)) {
            return "Enter a valid email address"
        }

        this.firstName = trimmedFirstName
        this.lastName = trimmedLastName
        this.email = trimmedEmail
        return null
    }

    fun register(
        password: String,
        confirmPassword: String,
    ) {
        if (_state.value is RegistrationState.Loading) return

        if (password.isBlank()) {
            showError("Enter a password")
            return
        }

        if (password != confirmPassword) {
            showError("Passwords don't match")
            return
        }

        viewModelScope.launch {
            _state.value = RegistrationState.Loading

            val result = repository.register(
                RegisterRequestDTO(
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    password = password,
                ),
            )

            result.onSuccess { response ->
                val user = response.user
                tokenManager.saveSession(
                    token = "registered_session_token",
                    userId = user.id,
                    userName = "${user.firstName} ${user.lastName ?: ""}".trim(),
                    email = user.email,
                )
                tokenManager.saveGoal(6)
                _state.value = RegistrationState.Success(user)
            }.onFailure { throwable ->
                showError(throwable.message ?: "Registration failed. Try again.")
            }
        }
    }

    fun resetState() {
        _state.value = RegistrationState.Idle
    }

    private fun showError(message: String) {
        _state.value = RegistrationState.Error(message)
    }

    private fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".") && !email.contains(" ")
    }
}
