package com.educalab.graficosdivertidos.ui.screens.detective

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.educalab.graficosdivertidos.data.repository.ContentRepository
import com.educalab.graficosdivertidos.data.repository.ProgressRepository
import com.educalab.graficosdivertidos.domain.model.AttemptResult
import com.educalab.graficosdivertidos.domain.model.ErrorChallengeModel
import com.educalab.graficosdivertidos.domain.model.GraphErrorType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetectiveUiState(
    val loading: Boolean = true,
    val challenges: List<ErrorChallengeModel> = emptyList(),
    val currentIndex: Int = 0,
    val selectedError: GraphErrorType? = null,
    val submitted: Boolean = false,
    val lastResult: AttemptResult? = null,
    val sessionComplete: Boolean = false,
    val correctInSession: Int = 0,
) {
    val current: ErrorChallengeModel? get() = challenges.getOrNull(currentIndex)
    val progressFraction: Float get() = if (challenges.isEmpty()) 0f else (currentIndex + (if (submitted) 1 else 0)).toFloat() / challenges.size
}

/** Módulo "Detective de gráficos engañosos": el usuario identifica QUÉ tipo de error tiene el gráfico mostrado. */
class DetectiveViewModel(
    private val userId: Long,
    private val contentRepository: ContentRepository,
    private val progressRepository: ProgressRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetectiveUiState())
    val uiState: StateFlow<DetectiveUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val challenges = contentRepository.getErrorChallenges().shuffled(kotlin.random.Random(userId))
            _uiState.update { it.copy(loading = false, challenges = challenges) }
        }
    }

    fun select(error: GraphErrorType) {
        if (_uiState.value.submitted) return
        _uiState.update { it.copy(selectedError = error) }
    }

    fun submit() {
        val state = _uiState.value
        val challenge = state.current ?: return
        val selected = state.selectedError ?: return
        val isCorrect = selected == challenge.errorType
        viewModelScope.launch {
            val result = progressRepository.recordErrorChallengeAttempt(
                userId = userId,
                challengeId = challenge.id,
                selectedError = selected,
                isCorrect = isCorrect,
                explanation = challenge.explanation,
                difficulty = challenge.difficulty,
                now = System.currentTimeMillis(),
            )
            _uiState.update {
                it.copy(submitted = true, lastResult = result, correctInSession = it.correctInSession + if (isCorrect) 1 else 0)
            }
        }
    }

    fun next() {
        val state = _uiState.value
        val nextIndex = state.currentIndex + 1
        if (nextIndex >= state.challenges.size) {
            _uiState.update { it.copy(sessionComplete = true) }
        } else {
            _uiState.update { it.copy(currentIndex = nextIndex, selectedError = null, submitted = false, lastResult = null) }
        }
    }

    fun restart() {
        _uiState.update {
            it.copy(
                currentIndex = 0, selectedError = null, submitted = false, lastResult = null,
                sessionComplete = false, correctInSession = 0, challenges = it.challenges.shuffled(),
            )
        }
    }
}
