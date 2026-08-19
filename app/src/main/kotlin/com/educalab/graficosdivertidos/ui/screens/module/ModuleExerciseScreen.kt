package com.educalab.graficosdivertidos.ui.screens.module

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.educalab.graficosdivertidos.domain.model.ExerciseModel
import com.educalab.graficosdivertidos.domain.model.InteractionType
import com.educalab.graficosdivertidos.domain.model.ModuleKey
import com.educalab.graficosdivertidos.ui.components.AnimatedProgressBar
import com.educalab.graficosdivertidos.ui.components.ChartLegend
import com.educalab.graficosdivertidos.ui.components.ChartRenderer
import com.educalab.graficosdivertidos.ui.components.ChoiceButton
import com.educalab.graficosdivertidos.ui.components.DragOrderList
import com.educalab.graficosdivertidos.ui.components.FeedbackBanner
import com.educalab.graficosdivertidos.ui.components.GrafiFloating
import com.educalab.graficosdivertidos.ui.components.GrafiPose

fun moduleTitle(moduleKey: ModuleKey): String = when (moduleKey) {
    ModuleKey.BARRAS -> "Barras"
    ModuleKey.PICTOGRAMAS -> "Pictogramas"
    ModuleKey.LINEAS -> "Líneas"
    ModuleKey.CIRCULAR -> "Circular"
    else -> moduleKey.name
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleExerciseScreen(
    title: String,
    viewModel: ModuleExerciseViewModel,
    onBack: () -> Unit,
    onFinished: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(androidx.compose.material.icons.Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
            )
        },
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when {
                state.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                state.exercises.isEmpty() -> EmptyReviewContent(onExit = onFinished)
                state.sessionComplete -> SessionCompleteContent(
                    correct = state.correctInSession,
                    total = state.exercises.size,
                    onRestart = viewModel::restartSession,
                    onExit = onFinished,
                )
                else -> {
                    val exercise = state.currentExercise
                    if (exercise != null) {
                        ExerciseContent(
                            exercise = exercise,
                            state = state,
                            viewModel = viewModel,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExerciseContent(
    exercise: ExerciseModel,
    state: ExerciseUiState,
    viewModel: ModuleExerciseViewModel,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            AnimatedProgressBar(progress = state.progressFraction)
            Text(
                "Reto ${state.currentIndex + 1} de ${state.exercises.size}",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
        item {
            Text(exercise.chart.title, style = MaterialTheme.typography.titleLarge)
            Text(exercise.prompt, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 4.dp))
        }
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.15f)
                    .padding(vertical = 4.dp),
            ) {
                ChartRenderer(
                    chartType = exercise.chart.chartType,
                    points = exercise.chart.dataset.points,
                    unitLabel = exercise.chart.dataset.unit,
                    modifier = Modifier.fillMaxSize(),
                    highlightIndex = if (exercise.interactionType == InteractionType.SELECCION_EN_GRAFICO) {
                        state.pendingSelection.firstOrNull()
                    } else null,
                    onElementTapped = if (exercise.interactionType == InteractionType.SELECCION_EN_GRAFICO && !state.submitted) {
                        { idx -> viewModel.selectSingle(idx) }
                    } else null,
                )
            }
            if (exercise.chart.showLegend) {
                ChartLegend(points = exercise.chart.dataset.points, modifier = Modifier.padding(top = 4.dp))
            }
        }
        item {
            InteractionArea(exercise = exercise, state = state, viewModel = viewModel)
        }
        state.lastResult?.let { result ->
            item {
                FeedbackBanner(isCorrect = result.isCorrect, explanation = result.explanation, xpAwarded = result.xpAwarded)
            }
        }
        item {
            ActionRow(state = state, viewModel = viewModel)
        }
    }
}

@Composable
private fun InteractionArea(exercise: ExerciseModel, state: ExerciseUiState, viewModel: ModuleExerciseViewModel) {
    when (exercise.interactionType) {
        InteractionType.SELECCION_EN_GRAFICO -> {
            Text(
                "Toca directamente sobre el gráfico para responder.",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        InteractionType.COMPARAR_PUNTOS, InteractionType.OPCION_MULTIPLE -> {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                exercise.options.forEachIndexed { index, option ->
                    val resultTint = resultTintFor(state, exercise, index)
                    ChoiceButton(
                        text = option,
                        selected = state.pendingSelection.firstOrNull() == index,
                        enabled = !state.submitted,
                        resultTint = resultTint,
                        onClick = { viewModel.selectSingle(index) },
                    )
                }
            }
        }
        InteractionType.ORDENAR_CATEGORIAS -> {
            var workingOrder by remember(exercise.id) { mutableStateOf(exercise.options.indices.toList()) }
            Text("Mantén presionado y arrastra para ordenar:", style = MaterialTheme.typography.bodyMedium)
            DragOrderList(
                items = workingOrder,
                itemLabel = { exercise.options[it] },
                onReordered = { newOrder ->
                    workingOrder = newOrder
                    viewModel.setOrder(newOrder)
                },
            )
        }
        InteractionType.ESTIMAR_VALOR -> {
            val maxEstimate = remember(exercise.id) {
                (com.educalab.graficosdivertidos.domain.logic.ChartMathEngine
                    .niceAxisMax(exercise.chart.dataset.maxValue) * 1.2).toFloat()
            }
            Text(
                "Tu estimación: ${"%.1f".format(state.estimateValue)} ${exercise.chart.dataset.unit}",
                style = MaterialTheme.typography.titleMedium,
            )
            Slider(
                value = state.estimateValue,
                onValueChange = { viewModel.setEstimate(it) },
                valueRange = 0f..maxEstimate.coerceAtLeast(1f),
                enabled = !state.submitted,
            )
        }
    }
}

private fun resultTintFor(state: ExerciseUiState, exercise: ExerciseModel, index: Int): androidx.compose.ui.graphics.Color? {
    if (!state.submitted) return null
    val isThisCorrect = exercise.correctAnswer.contains(index)
    val isThisSelected = state.pendingSelection.contains(index)
    return when {
        isThisCorrect -> androidx.compose.ui.graphics.Color(0xFF2FB170)
        isThisSelected && !isThisCorrect -> androidx.compose.ui.graphics.Color(0xFFE5484D)
        else -> null
    }
}

@Composable
private fun ActionRow(state: ExerciseUiState, viewModel: ModuleExerciseViewModel) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
        if (!state.submitted) {
            Button(onClick = viewModel::submit, enabled = viewModel.canSubmit()) {
                Text("Comprobar")
            }
        } else {
            Button(onClick = viewModel::nextExercise) {
                Text("Siguiente")
            }
        }
    }
}

@Composable
private fun EmptyReviewContent(onExit: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        GrafiFloating(pose = GrafiPose.CELEBRA, size = 130.dp)
        Text("¡No tienes retos pendientes!", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(top = 12.dp))
        Text(
            "Ya resolviste todo lo disponible por ahora. Vuelve más tarde o explora otro módulo.",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 4.dp),
        )
        Button(onClick = onExit, modifier = Modifier.padding(top = 16.dp)) { Text("Volver al Estudio") }
    }
}

@Composable
private fun SessionCompleteContent(correct: Int, total: Int, onRestart: () -> Unit, onExit: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        GrafiFloating(pose = GrafiPose.CELEBRA, size = 140.dp)
        Text("¡Sesión completada!", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(top = 12.dp))
        Text(
            "Acertaste $correct de $total retos.",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 4.dp),
        )
        androidx.compose.foundation.layout.Row(modifier = Modifier.padding(top = 20.dp)) {
            Button(onClick = onExit) { Text("Volver al Estudio") }
            androidx.compose.foundation.layout.Spacer(Modifier.width(12.dp))
            androidx.compose.material3.OutlinedButton(onClick = onRestart) { Text("Practicar de nuevo") }
        }
    }
}
