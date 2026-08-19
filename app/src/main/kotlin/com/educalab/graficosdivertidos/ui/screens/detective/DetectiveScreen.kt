package com.educalab.graficosdivertidos.ui.screens.detective

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.educalab.graficosdivertidos.domain.model.GraphErrorType
import com.educalab.graficosdivertidos.ui.components.AnimatedProgressBar
import com.educalab.graficosdivertidos.ui.components.ChartRenderer
import com.educalab.graficosdivertidos.ui.components.ChoiceButton
import com.educalab.graficosdivertidos.ui.components.FeedbackBanner
import com.educalab.graficosdivertidos.ui.components.GrafiFloating
import com.educalab.graficosdivertidos.ui.components.GrafiPose

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetectiveScreen(viewModel: DetectiveViewModel, onBack: () -> Unit, onFinished: () -> Unit) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detective de gráficos") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Volver") } },
            )
        },
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when {
                state.loading -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
                state.sessionComplete -> Column(
                    Modifier.fillMaxSize().padding(24.dp),
                    Arrangement.Center,
                    Alignment.CenterHorizontally,
                ) {
                    GrafiFloating(GrafiPose.INVESTIGA, size = 140.dp)
                    Text("¡Casos resueltos!", style = MaterialTheme.typography.headlineSmall)
                    Text(
                        "Detectaste ${state.correctInSession} de ${state.challenges.size} gráficos engañosos.",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    androidx.compose.foundation.layout.Row(Modifier.padding(top = 16.dp)) {
                        Button(onClick = onFinished) { Text("Volver al Estudio") }
                        androidx.compose.foundation.layout.Spacer(Modifier.width(12.dp))
                        androidx.compose.material3.OutlinedButton(onClick = viewModel::restart) { Text("Otro caso") }
                    }
                }
                else -> state.current?.let { challenge ->
                    LazyColumn(
                        Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        item {
                            AnimatedProgressBar(progress = state.progressFraction)
                            Text("Caso ${state.currentIndex + 1} de ${state.challenges.size}", style = MaterialTheme.typography.labelMedium)
                        }
                        item {
                            Text(
                                "\"${challenge.displayedTitle}\"",
                                style = MaterialTheme.typography.titleLarge,
                            )
                            Text(
                                "Grafi sospecha que algo no está bien en este gráfico. ¿Qué tipo de error tiene?",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 4.dp),
                            )
                        }
                        item {
                            Box(Modifier.fillMaxWidth().aspectRatio(1.2f)) {
                                ChartRenderer(
                                    chartType = challenge.chartType,
                                    points = challenge.dataset.points.let { pts ->
                                        if (challenge.omittedCategoryLabel != null) {
                                            pts.filterNot { it.label == challenge.omittedCategoryLabel }
                                        } else pts
                                    },
                                    unitLabel = challenge.dataset.unit,
                                    axisMinOverride = challenge.axisMinOverride,
                                    unitPerIconOverride = challenge.unitPerIconOverride,
                                    modifier = Modifier.fillMaxSize(),
                                )
                            }
                        }
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                challenge.allErrorOptions.forEach { errorType ->
                                    val tint = if (state.submitted) {
                                        when {
                                            errorType == challenge.errorType -> androidx.compose.ui.graphics.Color(0xFF2FB170)
                                            errorType == state.selectedError -> androidx.compose.ui.graphics.Color(0xFFE5484D)
                                            else -> null
                                        }
                                    } else null
                                    ChoiceButton(
                                        text = errorType.etiqueta,
                                        selected = state.selectedError == errorType,
                                        enabled = !state.submitted,
                                        resultTint = tint,
                                        onClick = { viewModel.select(errorType) },
                                    )
                                }
                            }
                        }
                        state.lastResult?.let { result ->
                            item { FeedbackBanner(result.isCorrect, result.explanation, result.xpAwarded) }
                        }
                        item {
                            Box(Modifier.fillMaxWidth(), Alignment.CenterEnd) {
                                if (!state.submitted) {
                                    Button(onClick = viewModel::submit, enabled = state.selectedError != null) { Text("Comprobar") }
                                } else {
                                    Button(onClick = viewModel::next) { Text("Siguiente caso") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
