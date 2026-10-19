package com.example.languagelearning.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.languagelearning.data.local.TokenManager
import com.example.languagelearning.data.local.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val tokenManager: TokenManager,
) : ViewModel() {

    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    val userInfo: StateFlow<UserInfo?> = _userInfo.asStateFlow()

    private val _studyTimeSeconds = MutableStateFlow(0)
    val studyTimeSeconds: StateFlow<Int> = _studyTimeSeconds.asStateFlow()

    init {
        viewModelScope.launch {
            tokenManager.userInfoFlow.collect { user ->
                _userInfo.value = user
            }
        }
        viewModelScope.launch {
            tokenManager.studyTimeFlow.collect { seconds ->
                _studyTimeSeconds.value = seconds
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            tokenManager.clearToken()
        }
    }
}
