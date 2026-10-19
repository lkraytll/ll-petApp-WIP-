package com.example.languagelearning.presentation.mainMenu

import com.example.languagelearning.data.remote.dto.CourseDto

sealed interface MainMenuState {
    data object Loading : MainMenuState
    data class Success(val courses: List<CourseDto>) : MainMenuState
    data class Error(val message: String) : MainMenuState
}
