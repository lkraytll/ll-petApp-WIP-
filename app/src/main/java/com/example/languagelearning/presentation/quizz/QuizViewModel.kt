package com.example.languagelearning.presentation.quizz

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.languagelearning.data.local.TokenManager
import com.example.languagelearning.data.remote.dto.LessonDto
import com.example.languagelearning.data.remote.dto.QuestionDto
import com.example.languagelearning.data.repository.CourseRepository
import com.example.languagelearning.data.repository.ProgressRepository
import com.example.languagelearning.data.repository.TtsRepository
import com.example.languagelearning.data.repository.UserRepository
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
    private val courseRepository: CourseRepository,
    private val userRepository: UserRepository,
    private val ttsRepository: TtsRepository,
    private val tokenManager: TokenManager,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val activeLessonId: Int = savedStateHandle["lessonId"] ?: 1

    private val _state = MutableStateFlow<QuizState>(QuizState.Idle)
    val state: StateFlow<QuizState> = _state.asStateFlow()

    private val _currentLesson = MutableStateFlow<LessonDto?>(null)
    val currentLesson: StateFlow<LessonDto?> = _currentLesson.asStateFlow()

    private val _questions = MutableStateFlow<List<QuestionDto>>(emptyList())
    val questions: StateFlow<List<QuestionDto>> = _questions.asStateFlow()

    private val _multipleChoiceQuestions = MutableStateFlow<List<QuestionDto>>(emptyList())
    val multipleChoiceQuestions: StateFlow<List<QuestionDto>> = _multipleChoiceQuestions.asStateFlow()

    private val _translationByImageQuestions = MutableStateFlow<List<QuestionDto>>(emptyList())
    val translationByImageQuestions: StateFlow<List<QuestionDto>> = _translationByImageQuestions.asStateFlow()

    private val _imageByTextQuestions = MutableStateFlow<List<QuestionDto>>(emptyList())
    val imageByTextQuestions: StateFlow<List<QuestionDto>> = _imageByTextQuestions.asStateFlow()

    private val _handwrittenTranslationQuestions = MutableStateFlow<List<QuestionDto>>(emptyList())
    val handwrittenTranslationQuestions: StateFlow<List<QuestionDto>> = _handwrittenTranslationQuestions.asStateFlow()

    init {
        loadAllLessonQuestions(activeLessonId)
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

    fun loadAllLessonQuestions(lessonId: Int) {
        viewModelScope.launch {
            courseRepository.getLesson(lessonId).onSuccess { lesson ->
                _currentLesson.value = lesson
                val qList = lesson.questions
                if (!qList.isNullOrEmpty()) {
                    _questions.value = qList
                }
            }

            courseRepository.getMultipleChoice(lessonId).onSuccess { list ->
                if (list.isNotEmpty()) _multipleChoiceQuestions.value = list
            }

            courseRepository.getTranslationByImage(lessonId).onSuccess { list ->
                if (list.isNotEmpty()) _translationByImageQuestions.value = list
            }

            courseRepository.getImageByText(lessonId).onSuccess { list ->
                if (list.isNotEmpty()) _imageByTextQuestions.value = list
            }

            courseRepository.getHandwrittenTranslation(lessonId).onSuccess { list ->
                if (list.isNotEmpty()) _handwrittenTranslationQuestions.value = list
            }
        }
    }

    fun checkQuestionAnswer(
        questionId: Int,
        optionId: Int? = null,
        answer: String? = null,
        onResult: (Boolean, String) -> Unit,
    ) {
        viewModelScope.launch {
            val result = courseRepository.checkAnswer(questionId, optionId, answer)
            result.onSuccess { checkResp ->
                onResult(checkResp.correct, checkResp.message)
            }.onFailure { throwable ->
                onResult(false, throwable.message ?: "Failed to verify answer")
            }
        }
    }

    fun speak(text: String) {
        viewModelScope.launch {
            ttsRepository.synthesizeAndPlay(text)
        }
    }

    fun stopSpeech() {
        ttsRepository.stopAudio()
    }

    override fun onCleared() {
        super.onCleared()
        ttsRepository.stopAudio()
    }

    fun completeLesson(lessonId: Int = activeLessonId, isCompleted: Boolean = true, score: Int = 100) {
        viewModelScope.launch {
            _state.value = QuizState.Submitting
            val userId = resolveUserId()

            val result = progressRepository.updateProgress(
                userId = userId,
                lessonId = lessonId,
                isCompleted = isCompleted,
                score = score,
            )

            result.onSuccess {
                tokenManager.addXp(100)
                _state.value = QuizState.Completed("Lesson completed successfully!")
            }.onFailure { throwable ->
                _state.value = QuizState.Error(throwable.message ?: "Failed to save progress.")
            }
        }
    }
}
