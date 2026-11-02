
package com.example.languagelearning.presentation.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.languagelearning.data.local.TokenManager
import com.example.languagelearning.data.remote.dto.LoginRequestDto
import com.example.languagelearning.data.remote.dto.RegisterRequestDTO
import com.example.languagelearning.data.repository.AuthRepository
import com.example.languagelearning.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val userRepository: UserRepository,
    private val tokenManager: TokenManager,
) : ViewModel() {

    private var firstName = ""
    private var lastName = ""
    private var email = ""

    private val _state =
        MutableStateFlow<RegistrationState>(RegistrationState.Idle)

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
                )
            )

            result.onSuccess { response ->
                val loginResult = repository.login(
                    LoginRequestDto(
                        email = email,
                        password = password
                    )
                )

                loginResult.onSuccess { loginResponse ->
                    val token = loginResponse.token

                    if (token.isNullOrBlank()) {
                        showError("Registration succeeded, but login returned no token.")
                        return@onSuccess
                    }

                    val user = loginResponse.user

                    if (user != null) {
                        tokenManager.saveSession(
                            token = token,
                            userId = user.id,
                            userName = "${user.firstName} ${user.lastName ?: ""}".trim(),
                            email = user.email
                        )
                    } else {
                        tokenManager.saveToken(token)

                        userRepository.getMe()
                            .onSuccess { me ->
                                tokenManager.saveUserInfo(
                                    userId = me.id,
                                    userName = "${me.firstName} ${me.lastName ?: ""}".trim(),
                                    email = me.email
                                )
                            }
                            .onFailure { throwable ->
                                showError(
                                    throwable.message ?: "Could not load user profile."
                                )
                                return@onSuccess
                            }
                    }

                    tokenManager.saveGoal(6)
                    _state.value = RegistrationState.Success(response.user)
                }.onFailure { throwable ->
                    showError(
                        "Registration succeeded, but automatic login failed: ${throwable.message}"
                    )
                }
            }.onFailure { throwable ->
                showError(
                    throwable.message ?: "Registration failed. Try again."
                )
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
        return email.contains("@") &&
                email.contains(".") &&
                !email.contains(" ")
    }
}
