package com.educalab.graficosdivertidos.ui.screens.comparator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.educalab.graficosdivertidos.data.repository.ContentRepository
import com.educalab.graficosdivertidos.data.repository.ProgressRepository
import com.educalab.graficosdivertidos.domain.model.AttemptResult
import com.educalab.graficosdivertidos.domain.model.ComparisonModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ComparatorUiState(
    val loading: Boolean = true,
    val challenges: List<ComparisonModel> = emptyList(),
    val currentIndex: Int = 0,
    val selectedSide: String? = null,
    val submitted: Boolean = false,
    val lastResult: AttemptResult? = null,
    val sessionComplete: Boolean = false,
    val correctInSession: Int = 0,
) {
    val current: ComparisonModel? get() = challenges.getOrNull(currentIndex)
    val progressFraction: Float get() = if (challenges.isEmpty()) 0f else (currentIndex + (if (submitted) 1 else 0)).toFloat() / challenges.size
}

/** Módulo Comparador: dos representaciones del mismo dataset; el usuario elige cuál comunica mejor. */
class ComparatorViewModel(
    private val userId: Long,
    private val contentRepository: ContentRepository,
    private val progressRepository: ProgressRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ComparatorUiState())
    val uiState: StateFlow<ComparatorUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val challenges = contentRepository.getComparisons().shuffled(kotlin.random.Random(userId))
            _uiState.update { it.copy(loading = false, challenges = challenges) }
        }
    }

    fun select(side: String) {
        if (_uiState.value.submitted) return
        _uiState.update { it.copy(selectedSide = side) }
    }

    fun submit() {
        val state = _uiState.value
        val challenge = state.current ?: return
        val selected = state.selectedSide ?: return
        val isCorrect = selected == challenge.betterSide
        viewModelScope.launch {
            val result = progressRepository.recordComparisonAttempt(
                userId = userId,
                challengeId = challenge.id,
                selectedSide = selected,
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
            _uiState.update { it.copy(currentIndex = nextIndex, selectedSide = null, submitted = false, lastResult = null) }
        }
    }

    fun restart() {
        _uiState.update {
            it.copy(
                currentIndex = 0, selectedSide = null, submitted = false, lastResult = null,
                sessionComplete = false, correctInSession = 0, challenges = it.challenges.shuffled(),
            )
        }
    }
}
