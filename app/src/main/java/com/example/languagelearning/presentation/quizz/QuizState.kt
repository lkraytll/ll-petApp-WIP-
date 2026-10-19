package com.example.languagelearning.presentation.quizz

sealed interface QuizState {
    data object Idle : QuizState
    data object Submitting : QuizState
    data class Completed(val message: String) : QuizState
    data class Error(val message: String) : QuizState
}
