package com.example.languagelearning.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.languagelearning.data.local.TokenManager
import com.example.languagelearning.data.local.UserInfo
import com.example.languagelearning.data.local.UserLevelInfo
import com.example.languagelearning.data.repository.ProgressRepository
import com.example.languagelearning.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val userRepository: UserRepository,
    private val progressRepository: ProgressRepository,
) : ViewModel() {

    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    val userInfo: StateFlow<UserInfo?> = _userInfo.asStateFlow()

    private val _studyTimeSeconds = MutableStateFlow(0)
    val studyTimeSeconds: StateFlow<Int> = _studyTimeSeconds.asStateFlow()

    private val _levelInfo = MutableStateFlow(UserLevelInfo(0, 0, null))
    val levelInfo: StateFlow<UserLevelInfo> = _levelInfo.asStateFlow()

    private val _avatarUploadMessage = MutableStateFlow<String?>(null)
    val avatarUploadMessage: StateFlow<String?> = _avatarUploadMessage.asStateFlow()

    init {
        loadUserProfile()
        viewModelScope.launch {
            progressRepository.getWeeklyStudyTime().onSuccess { w ->
                val totalSecs = w.mon + w.tue + w.wed + w.thu + w.fri + w.sat + w.sun
                _studyTimeSeconds.value = totalSecs
            }.onFailure {
                tokenManager.studyTimeFlow.collect { seconds ->
                    _studyTimeSeconds.value = seconds
                }
            }
        }
        viewModelScope.launch {
            tokenManager.userLevelFlow.collect { lvl ->
                _levelInfo.value = lvl
            }
        }
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            // Сначала отображаем локальные данные, если они есть
            val localUser = tokenManager.userInfoFlow.firstOrNull()

            if (localUser != null &&
                localUser.name.isNotBlank() &&
                !localUser.name.contains("null", ignoreCase = true)
            ) {
                _userInfo.value = localUser
            }

            // Получаем актуальный профиль с Backend
            userRepository.getMe()
                .onSuccess { me ->

                    val fullName = me.fullName

                    if (fullName.isNotBlank() &&
                        !fullName.contains("null", ignoreCase = true)
                    ) {
                        tokenManager.saveUserInfo(
                            userId = me.id,
                            userName = fullName,
                            email = me.email
                        )

                        _userInfo.value = UserInfo(
                            id = me.id,
                            name = fullName,
                            email = me.email
                        )
                    }

                    // Обновляем URL аватара из ответа Backend
                    val avatarPath = me.avatarUrl

                    if (!avatarPath.isNullOrBlank()) {
                        val fullAvatarUrl = when {
                            avatarPath.startsWith("http://") ||
                                    avatarPath.startsWith("https://") -> {
                                avatarPath
                            }

                            else -> {
                                "http://10.0.2.2:3000/${avatarPath.trimStart('/')}"
                            }
                        }

                        tokenManager.saveAvatarUrl(fullAvatarUrl)
                    }
                }
                .onFailure { error ->
                    android.util.Log.e(
                        "ProfileViewModel",
                        "Failed to load user profile",
                        error
                    )
                }
        }
    }

    fun uploadAvatarFile(file: File) {
        viewModelScope.launch {
            userRepository.updateAvatar(file)
                .onSuccess { resp ->
                    val serverPath = resp.resolvedAvatarUrl

                    if (serverPath.isNullOrBlank()) {
                        _avatarUploadMessage.value =
                            "Server did not return avatar URL"
                        return@onSuccess
                    }

                    val fullUrl = when {
                        serverPath.startsWith("http") -> serverPath
                        else -> "http://10.0.2.2:3000/${serverPath.trimStart('/')}"
                    }

                    tokenManager.saveAvatarUrl(fullUrl)

                    _avatarUploadMessage.value =
                        "Avatar updated successfully!"
                }
                .onFailure { error ->
                    _avatarUploadMessage.value =
                        error.message ?: "Failed to upload avatar"
                }
        }
    }

    fun clearMessage() {
        _avatarUploadMessage.value = null
    }

    fun logout() {
        viewModelScope.launch {
            tokenManager.clearToken()
        }
    }
}
