package com.educalab.graficosdivertidos.ui.screens.comparator

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.educalab.graficosdivertidos.ui.components.AnimatedProgressBar
import com.educalab.graficosdivertidos.ui.components.ChartRenderer
import com.educalab.graficosdivertidos.ui.components.FeedbackBanner
import com.educalab.graficosdivertidos.ui.components.GrafiFloating
import com.educalab.graficosdivertidos.ui.components.GrafiPose

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComparatorScreen(viewModel: ComparatorViewModel, onBack: () -> Unit, onFinished: () -> Unit) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Comparador") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, contentDescription = "Volver") } },
            )
        },
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when {
                state.loading -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
                state.sessionComplete -> Column(
                    Modifier.fillMaxSize().padding(24.dp), Arrangement.Center, Alignment.CenterHorizontally,
                ) {
                    GrafiFloating(GrafiPose.CELEBRA, size = 140.dp)
                    Text("¡Comparaciones completas!", style = MaterialTheme.typography.headlineSmall)
                    Text("Acertaste ${state.correctInSession} de ${state.challenges.size}.", style = MaterialTheme.typography.bodyLarge)
                    androidx.compose.foundation.layout.Row(Modifier.padding(top = 16.dp)) {
                        Button(onClick = onFinished) { Text("Volver al Estudio") }
                        androidx.compose.foundation.layout.Spacer(Modifier.width(12.dp))
                        androidx.compose.material3.OutlinedButton(onClick = viewModel::restart) { Text("Otra ronda") }
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
                            Text("Ronda ${state.currentIndex + 1} de ${state.challenges.size}", style = MaterialTheme.typography.labelMedium)
                        }
                        item {
                            Text(challenge.dataset.title, style = MaterialTheme.typography.titleLarge)
                            Text(challenge.question, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 4.dp))
                        }
                        item {
                            androidx.compose.foundation.layout.Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                ComparisonOption(
                                    label = "A · ${challenge.chartTypeA.etiqueta}",
                                    selected = state.selectedSide == "A",
                                    correctness = if (state.submitted) "A" == challenge.betterSide else null,
                                    isSelected = state.selectedSide == "A",
                                    onClick = { viewModel.select("A") },
                                    enabled = !state.submitted,
                                    modifier = Modifier.weight(1f),
                                ) {
                                    ChartRenderer(challenge.chartTypeA, challenge.dataset.points, challenge.dataset.unit, Modifier.fillMaxSize())
                                }
                                ComparisonOption(
                                    label = "B · ${challenge.chartTypeB.etiqueta}",
                                    selected = state.selectedSide == "B",
                                    correctness = if (state.submitted) "B" == challenge.betterSide else null,
                                    isSelected = state.selectedSide == "B",
                                    onClick = { viewModel.select("B") },
                                    enabled = !state.submitted,
                                    modifier = Modifier.weight(1f),
                                ) {
                                    ChartRenderer(challenge.chartTypeB, challenge.dataset.points, challenge.dataset.unit, Modifier.fillMaxSize())
                                }
                            }
                        }
                        state.lastResult?.let { result ->
                            item { FeedbackBanner(result.isCorrect, result.explanation, result.xpAwarded) }
                        }
                        item {
                            Box(Modifier.fillMaxWidth(), Alignment.CenterEnd) {
                                if (!state.submitted) {
                                    Button(onClick = viewModel::submit, enabled = state.selectedSide != null) { Text("Comprobar") }
                                } else {
                                    Button(onClick = viewModel::next) { Text("Siguiente") }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ComparisonOption(
    label: String,
    selected: Boolean,
    correctness: Boolean?,
    isSelected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    chart: @Composable () -> Unit,
) {
    val borderColor = when {
        correctness == true -> androidx.compose.ui.graphics.Color(0xFF2FB170)
        correctness == false && isSelected -> androidx.compose.ui.graphics.Color(0xFFE5484D)
        selected -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outline
    }
    Surface(
        modifier = modifier
            .aspectRatio(0.85f)
            .clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, borderColor),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(Modifier.padding(8.dp)) {
            Text(label, style = MaterialTheme.typography.labelLarge)
            Box(Modifier.weight(1f).fillMaxWidth()) { chart() }
        }
    }
}
