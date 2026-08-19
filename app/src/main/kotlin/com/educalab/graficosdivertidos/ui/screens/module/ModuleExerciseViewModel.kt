package com.educalab.graficosdivertidos.ui.screens.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.educalab.graficosdivertidos.data.repository.ContentRepository
import com.educalab.graficosdivertidos.data.repository.ProgressRepository
import com.educalab.graficosdivertidos.domain.model.AttemptResult
import com.educalab.graficosdivertidos.domain.model.ExerciseModel
import com.educalab.graficosdivertidos.domain.model.InteractionType
import com.educalab.graficosdivertidos.domain.model.ModuleKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max

data class ExerciseUiState(
    val loading: Boolean = true,
    val exercises: List<ExerciseModel> = emptyList(),
    val currentIndex: Int = 0,
    val pendingSelection: List<Int> = emptyList(),
    val pendingOrder: List<Int>? = null,
    val estimateValue: Float = 0f,
    val submitted: Boolean = false,
    val lastResult: AttemptResult? = null,
    val sessionComplete: Boolean = false,
    val correctInSession: Int = 0,
) {
    val currentExercise: ExerciseModel? get() = exercises.getOrNull(currentIndex)
    val progressFraction: Float get() = if (exercises.isEmpty()) 0f else (currentIndex + (if (submitted) 1 else 0)).toFloat() / exercises.size
}

/**
 * Conduce una sesión de ejercicios. Si [moduleKey] es null, funciona como
 * cola de repaso mixta (módulo Desafíos): trae ejercicios pendientes de
 * varios módulos base en lugar de uno solo.
 */
class ModuleExerciseViewModel(
    private val userId: Long,
    private val moduleKey: ModuleKey?,
    private val contentRepository: ContentRepository,
    private val progressRepository: ProgressRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExerciseUiState())
    val uiState: StateFlow<ExerciseUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val exercises = if (moduleKey != null) {
                contentRepository.getExercisesForModule(moduleKey).shuffled(kotlin.random.Random(userId + moduleKey.ordinal))
            } else {
                contentRepository.getPendingReviewExercises(userId)
            }
            _uiState.update { it.copy(loading = false, exercises = exercises) }
            resetEstimateSeed()
        }
    }

    private fun resetEstimateSeed() {
        val exercise = _uiState.value.currentExercise ?: return
        if (exercise.interactionType == InteractionType.ESTIMAR_VALOR) {
            _uiState.update { it.copy(estimateValue = 0f) }
        }
    }

    fun selectSingle(index: Int) {
        if (_uiState.value.submitted) return
        _uiState.update { it.copy(pendingSelection = listOf(index)) }
    }

    fun setOrder(order: List<Int>) {
        if (_uiState.value.submitted) return
        _uiState.update { it.copy(pendingOrder = order) }
    }

    fun setEstimate(value: Float) {
        if (_uiState.value.submitted) return
        _uiState.update { it.copy(estimateValue = value) }
    }

    fun canSubmit(): Boolean {
        val state = _uiState.value
        val exercise = state.currentExercise ?: return false
        return when (exercise.interactionType) {
            InteractionType.SELECCION_EN_GRAFICO, InteractionType.COMPARAR_PUNTOS, InteractionType.OPCION_MULTIPLE ->
                state.pendingSelection.isNotEmpty()
            InteractionType.ORDENAR_CATEGORIAS -> state.pendingOrder != null
            InteractionType.ESTIMAR_VALOR -> true
        }
    }

    fun submit() {
        val state = _uiState.value
        val exercise = state.currentExercise ?: return
        if (state.submitted || !canSubmit()) return

        val (isCorrect, selectedAnswer) = evaluate(exercise, state)

        viewModelScope.launch {
            val result = progressRepository.recordExerciseAttempt(
                userId = userId,
                exercise = exercise,
                selectedAnswer = selectedAnswer,
                isCorrect = isCorrect,
                now = System.currentTimeMillis(),
            )
            _uiState.update {
                it.copy(
                    submitted = true,
                    lastResult = result,
                    correctInSession = it.correctInSession + if (isCorrect) 1 else 0,
                )
            }
        }
    }

    private fun evaluate(exercise: ExerciseModel, state: ExerciseUiState): Pair<Boolean, List<Int>> {
        return when (exercise.interactionType) {
            InteractionType.SELECCION_EN_GRAFICO, InteractionType.COMPARAR_PUNTOS, InteractionType.OPCION_MULTIPLE -> {
                val selected = state.pendingSelection
                (selected == exercise.correctAnswer) to selected
            }
            InteractionType.ORDENAR_CATEGORIAS -> {
                val order = state.pendingOrder.orEmpty()
                (order == exercise.correctAnswer) to order
            }
            InteractionType.ESTIMAR_VALOR -> {
                val trueValueCents = exercise.correctAnswer.firstOrNull() ?: 0
                val enteredCents = (state.estimateValue * 100).toInt()
                val tolerance = max(50, (abs(trueValueCents) * 0.15).toInt())
                val correct = abs(trueValueCents - enteredCents) <= tolerance
                correct to listOf(enteredCents)
            }
        }
    }

    fun nextExercise() {
        val state = _uiState.value
        val nextIndex = state.currentIndex + 1
        if (nextIndex >= state.exercises.size) {
            _uiState.update { it.copy(sessionComplete = true) }
        } else {
            _uiState.update {
                it.copy(
                    currentIndex = nextIndex,
                    pendingSelection = emptyList(),
                    pendingOrder = null,
                    estimateValue = 0f,
                    submitted = false,
                    lastResult = null,
                )
            }
        }
    }

    fun restartSession() {
        _uiState.update {
            it.copy(
                currentIndex = 0,
                pendingSelection = emptyList(),
                pendingOrder = null,
                estimateValue = 0f,
                submitted = false,
                lastResult = null,
                sessionComplete = false,
                correctInSession = 0,
                exercises = it.exercises.shuffled(),
            )
        }
    }
}
