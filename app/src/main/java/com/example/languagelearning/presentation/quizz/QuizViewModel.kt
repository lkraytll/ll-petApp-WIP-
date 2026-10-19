package com.example.languagelearning.presentation.quizz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.languagelearning.data.local.TokenManager
import com.example.languagelearning.data.repository.ProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val progressRepository: ProgressRepository,
    private val tokenManager: TokenManager,
    private val ttsManager: com.example.languagelearning.data.tts.TtsManager
) : ViewModel() {

    private val _state = MutableStateFlow<QuizState>(QuizState.Idle)
    val state: StateFlow<QuizState> = _state.asStateFlow()

    fun speak(text: String) {
        viewModelScope.launch {
            ttsManager.speak(text)
        }
    }

    fun stopSpeech() {
        ttsManager.stop()
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.stop()
    }

    fun completeLesson(lessonId: Int = 1, isCompleted: Boolean = true, score: Int = 100) {
        viewModelScope.launch {
            _state.value = QuizState.Submitting
            val user = tokenManager.userInfoFlow.firstOrNull()
            val userId = user?.id ?: 1

            val result = progressRepository.updateProgress(
                userId = userId,
                lessonId = lessonId,
                isCompleted = isCompleted,
                score = score,
            )

            result.onSuccess {
                _state.value = QuizState.Completed("Lesson completed successfully!")
            }.onFailure { throwable ->
                _state.value = QuizState.Error(throwable.message ?: "Failed to save progress.")
            }
        }
    }
}
