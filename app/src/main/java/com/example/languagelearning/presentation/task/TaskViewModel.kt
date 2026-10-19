package com.example.languagelearning.presentation.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.languagelearning.data.local.GoalInfo
import com.example.languagelearning.data.local.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val tokenManager: TokenManager,
) : ViewModel() {

    private val _goalInfo = MutableStateFlow(GoalInfo(targetDays = 6, completedDays = 1, streakDays = 6))
    val goalInfo: StateFlow<GoalInfo> = _goalInfo.asStateFlow()

    init {
        viewModelScope.launch {
            tokenManager.goalInfoFlow.collect { info ->
                _goalInfo.value = info
            }
        }
    }

    fun setGoal(targetDays: Int) {
        viewModelScope.launch {
            tokenManager.saveGoal(targetDays)
        }
    }
}
