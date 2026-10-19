package com.example.languagelearning.presentation.progress

import com.example.languagelearning.data.remote.dto.ProgressDto

sealed interface ProgressState {
    data object Loading : ProgressState
    data class Success(val progressList: List<ProgressDto>) : ProgressState
    data class Error(val message: String) : ProgressState
}
