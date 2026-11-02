package com.example.languagelearning.presentation.mainMenu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.languagelearning.data.local.TokenManager
import com.example.languagelearning.data.local.UserInfo
import com.example.languagelearning.data.repository.CourseRepository
import com.example.languagelearning.data.repository.ProgressRepository
import com.example.languagelearning.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainMenuViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val progressRepository: ProgressRepository,
    private val userRepository: UserRepository,
    private val tokenManager: TokenManager,
) : ViewModel() {

    private val _state = MutableStateFlow<MainMenuState>(MainMenuState.Loading)
    val state: StateFlow<MainMenuState> = _state.asStateFlow()

    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    val userInfo: StateFlow<UserInfo?> = _userInfo.asStateFlow()

    private val _completedLessonsCount = MutableStateFlow(0)
    val completedLessonsCount: StateFlow<Int> = _completedLessonsCount.asStateFlow()

    init {
        loadUserData()
        loadCourses()
        loadCompletedLessons()
    }

    fun loadUserData() {
        viewModelScope.launch {
            val localUser = tokenManager.userInfoFlow.firstOrNull()

            if (localUser != null && localUser.name.isNotBlank()) {
                _userInfo.value = localUser
            }

            userRepository.getMe().onSuccess { me ->
                val fullName = me.fullName

                tokenManager.saveUserInfo(
                    userId = me.id,
                    userName = fullName,
                    email = me.email
                )

                _userInfo.value = UserInfo(
                    me.id,
                    fullName,
                    me.email
                )
            }
        }
    }

    private suspend fun resolveUserId(): Int? {
        val user = tokenManager.userInfoFlow.firstOrNull()

        if (user != null && user.id > 0) {
            return user.id
        }

        val me = userRepository.getMe().getOrNull() ?: return null

        if (me.id <= 0) return null

        val fullName = me.fullName

        tokenManager.saveUserInfo(
            userId = me.id,
            userName = fullName,
            email = me.email
        )

        _userInfo.value = UserInfo(
            me.id,
            fullName,
            me.email
        )

        return me.id
    }

    fun loadCompletedLessons() {
        viewModelScope.launch {
            val userId = resolveUserId() ?: return@launch

            progressRepository.getCompletedLessons(userId)
                .onSuccess { count ->
                    _completedLessonsCount.value = count
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
                _state.value = MainMenuState.Error(
                    throwable.message ?: "Failed to load courses"
                )
            }
        }
    }
}
