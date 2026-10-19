package com.example.languagelearning.presentation.mainMenu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.languagelearning.data.local.TokenManager
import com.example.languagelearning.data.local.UserInfo
import com.example.languagelearning.data.repository.CourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainMenuViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val tokenManager: TokenManager,
    private val ttsManager: com.example.languagelearning.data.tts.TtsManager
) : ViewModel() {

    private val _state = MutableStateFlow<MainMenuState>(MainMenuState.Loading)
    val state: StateFlow<MainMenuState> = _state.asStateFlow()

    fun speak(text: String) {
        viewModelScope.launch {
            ttsManager.speak(text)
        }
    }

    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    val userInfo: StateFlow<UserInfo?> = _userInfo.asStateFlow()

    init {
        loadUserData()
        loadCourses()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            tokenManager.userInfoFlow.collect { user ->
                _userInfo.value = user
            }
        }
    }

    fun loadCourses() {
        viewModelScope.launch {
            _state.value = MainMenuState.Loading
            val result = courseRepository.getCourses()
            result.onSuccess { courses ->
                _state.value = MainMenuState.Success(courses)
            }.onFailure { throwable ->
                _state.value = MainMenuState.Error(throwable.message ?: "Failed to load courses")
            }
        }
    }
}
