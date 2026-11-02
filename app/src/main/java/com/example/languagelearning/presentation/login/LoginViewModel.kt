
package com.example.languagelearning.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.languagelearning.data.local.TokenManager
import com.example.languagelearning.data.remote.dto.LoginRequestDto
import com.example.languagelearning.data.repository.AuthRepository
import com.example.languagelearning.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val tokenManager: TokenManager,
) : ViewModel() {

    private val _state =
        MutableStateFlow<LoginState>(LoginState.Idle)

    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun login(email: String, password: String) {
        val trimmedEmail = email.trim()

        if (trimmedEmail.isBlank() || !isValidEmail(trimmedEmail)) {
            _state.value = LoginState.Error("Enter a valid email address")
            return
        }

        if (password.isBlank()) {
            _state.value = LoginState.Error("Enter your password")
            return
        }

        viewModelScope.launch {
            _state.value = LoginState.Loading

            val result = authRepository.login(
                LoginRequestDto(
                    email = trimmedEmail,
                    password = password
                )
            )

            result.onSuccess { response ->
                val token = response.token

                if (token.isNullOrBlank()) {
                    _state.value = LoginState.Error(
                        "Server returned an empty token."
                    )
                    return@onSuccess
                }

                val user = response.user

                if (user != null) {
                    tokenManager.saveSession(
                        token = token,
                        userId = user.id,
                        userName = "${user.firstName} ${user.lastName ?: ""}".trim(),
                        email = user.email
                    )

                    _state.value = LoginState.Success(response)
                } else {
                    tokenManager.saveToken(token)

                    userRepository.getMe()
                        .onSuccess { me ->
                            tokenManager.saveUserInfo(
                                userId = me.id,
                                userName = "${me.firstName} ${me.lastName ?: ""}".trim(),
                                email = me.email
                            )

                            _state.value = LoginState.Success(response)
                        }
                        .onFailure { throwable ->
                            _state.value = LoginState.Error(
                                throwable.message ?: "Could not load user profile."
                            )
                        }
                }
            }.onFailure { throwable ->
                _state.value = LoginState.Error(
                    throwable.message ?: "Login failed. Try again."
                )
            }
        }
    }

    fun resetState() {
        _state.value = LoginState.Idle
    }

    private fun isValidEmail(email: String): Boolean {
        return email.contains("@") &&
                email.contains(".") &&
                !email.contains(" ")
    }
}
