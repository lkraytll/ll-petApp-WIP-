package com.example.languagelearning.presentation.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.languagelearning.data.local.TokenManager
import com.example.languagelearning.data.local.WeeklyStudyTime
import com.example.languagelearning.data.remote.dto.DailyProgressDto
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
class ProgressViewModel @Inject constructor(
    private val progressRepository: ProgressRepository,
    private val userRepository: UserRepository,
    private val tokenManager: TokenManager,
) : ViewModel() {

    private val _state = MutableStateFlow<ProgressState>(ProgressState.Loading)
    val state: StateFlow<ProgressState> = _state.asStateFlow()

    private val _weeklyStudyTime = MutableStateFlow(WeeklyStudyTime(0, 0, 0, 0, 0, 0, 0))
    val weeklyStudyTime: StateFlow<WeeklyStudyTime> = _weeklyStudyTime.asStateFlow()

    private val _dailyProgress = MutableStateFlow<DailyProgressDto?>(null)
    val dailyProgress: StateFlow<DailyProgressDto?> = _dailyProgress.asStateFlow()

    init {
        loadUserProgress()
    }

    private suspend fun resolveUserId(): Int {
        val user = tokenManager.userInfoFlow.firstOrNull()
        if (user != null && user.id > 0) {
            return user.id
        }
        val meResult = userRepository.getMe()
        val me = meResult.getOrNull()
        if (me != null && me.id > 0) {
            tokenManager.saveUserInfo(
                userId = me.id,
                userName = "${me.firstName} ${me.lastName ?: ""}".trim(),
                email = me.email
            )
            return me.id
        }
        return 1
    }

    fun loadUserProgress() {
        viewModelScope.launch {
            tokenManager.checkAndResetWeeklyProgress()

            _state.value = ProgressState.Loading
            val userId = resolveUserId()

            val weeklyResult = progressRepository.getWeeklyStudyTime()
            weeklyResult.onSuccess { w ->
                _weeklyStudyTime.value = WeeklyStudyTime(
                    mon = w.mon, tue = w.tue, wed = w.wed,
                    thu = w.thu, fri = w.fri, sat = w.sat, sun = w.sun
                )
            }.onFailure {
                val localTime = tokenManager.weeklyStudyTimeFlow.firstOrNull()
                if (localTime != null) {
                    _weeklyStudyTime.value = localTime
                }
            }

            val result = progressRepository.getProgress(userId)
            result.onSuccess { progressList ->
                _state.value = ProgressState.Success(progressList)
            }.onFailure { throwable ->
                _state.value = ProgressState.Error(throwable.message ?: "Failed to load progress")
            }
        }
    }
}
