package com.example.languagelearning.presentation.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.languagelearning.data.local.TokenManager
import com.example.languagelearning.data.remote.dto.ProgressDto
import com.example.languagelearning.data.repository.ProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ProgressState {
    data object Loading : ProgressState
    data class Success(val progressList: List<ProgressDto>) : ProgressState
    data class Error(val message: String) : ProgressState
}

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val progressRepository: ProgressRepository,
    private val tokenManager: TokenManager,
) : ViewModel() {

    private val _state = MutableStateFlow<ProgressState>(ProgressState.Loading)
    val state: StateFlow<ProgressState> = _state.asStateFlow()

    private val _weeklyStudyTime = MutableStateFlow(com.example.languagelearning.data.local.WeeklyStudyTime(0, 0, 0, 0, 0, 0, 0))
    val weeklyStudyTime: StateFlow<com.example.languagelearning.data.local.WeeklyStudyTime> = _weeklyStudyTime.asStateFlow()

    init {
        loadUserProgress()
        viewModelScope.launch {
            tokenManager.weeklyStudyTimeFlow.collect { time ->
                _weeklyStudyTime.value = time
            }
        }
    }

    fun loadUserProgress() {
        viewModelScope.launch {
            _state.value = ProgressState.Loading
            val user = tokenManager.userInfoFlow.firstOrNull()
            val userId = user?.id ?: 1

            val result = progressRepository.getProgress(userId)
            result.onSuccess { progressList ->
                _state.value = ProgressState.Success(progressList)
            }.onFailure { throwable ->
                _state.value = ProgressState.Error(throwable.message ?: "Failed to load progress")
            }
        }
    }
}
