package com.educalab.graficosdivertidos.ui.screens.builder

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import com.educalab.graficosdivertidos.domain.model.ChartType
import com.educalab.graficosdivertidos.domain.model.DataPointModel
import com.educalab.graficosdivertidos.domain.model.DatasetModel
import com.educalab.graficosdivertidos.ui.components.AnimatedProgressBar
import com.educalab.graficosdivertidos.ui.components.ChartLegend
import com.educalab.graficosdivertidos.ui.components.ChartRenderer
import com.educalab.graficosdivertidos.ui.components.DragOrderList
import com.educalab.graficosdivertidos.ui.components.GrafiFloating
import com.educalab.graficosdivertidos.ui.components.GrafiPose

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuilderScreen(viewModel: BuilderViewModel, onBack: () -> Unit) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Constructor de gráficos") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") } },
            )
        },
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            if (state.loading) {
                Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
                return@Box
            }
            var dragActive by remember { mutableStateOf(false) }
            LazyColumn(
                Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                userScrollEnabled = !dragActive,
            ) {
                item {
                    AnimatedProgressBar(progress = (state.stepIndex + 1).toFloat() / state.stepCount)
                    Text(
                        "Paso ${state.stepIndex + 1} de ${state.stepCount}: ${stepTitle(state.step)}",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 6.dp),
                    )
                }
                item {
                    when (state.step) {
                        BuilderStep.DATASET -> DatasetStep(state.datasets, state.selectedDataset, viewModel::selectDataset)
                        BuilderStep.TIPO -> ChartTypeStep(state.chartType, viewModel::selectChartType)
                        BuilderStep.TITULO -> TitleStep(state.title, viewModel::setTitle)
                        BuilderStep.CATEGORIAS -> CategoriesStep(state, viewModel, onDragActiveChange = { dragActive = it })
                        BuilderStep.ETIQUETAS -> LabelsStep(state.showLabels, viewModel::toggleLabels)
                        BuilderStep.ESCALA -> ScaleStep(state, viewModel)
                        BuilderStep.LEYENDA -> LegendStep(state.showLegend, viewModel::toggleLegend)
                        BuilderStep.VISTA_PREVIA -> PreviewStep(state, viewModel)
                    }
                }
                item { NavigationButtons(state, viewModel) }
            }
        }
    }
}

private fun stepTitle(step: BuilderStep) = when (step) {
    BuilderStep.DATASET -> "Elige tus datos"
    BuilderStep.TIPO -> "Elige el tipo de gráfico"
    BuilderStep.TITULO -> "Ponle un título"
    BuilderStep.CATEGORIAS -> "Elige y ordena las categorías"
    BuilderStep.ETIQUETAS -> "Etiquetas"
    BuilderStep.ESCALA -> "Escala del eje"
    BuilderStep.LEYENDA -> "Leyenda"
    BuilderStep.VISTA_PREVIA -> "Vista previa"
}

@Composable
private fun DatasetStep(datasets: List<DatasetModel>, selected: DatasetModel?, onSelect: (DatasetModel) -> Unit) {
    Column {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(datasets) { dataset ->
                val isSelected = selected?.id == dataset.id
                Surface(
                    modifier = Modifier
                        .size(150.dp)
                        .clickable { onSelect(dataset) },
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
                    border = BorderStroke(2.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline),
                ) {
                    Column(Modifier.padding(10.dp)) {
                        Text(dataset.category, style = MaterialTheme.typography.labelSmall)
                        Text(dataset.title, style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 4.dp))
                        Text("${dataset.points.size} categorías", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ChartTypeStep(selected: ChartType, onSelect: (ChartType) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        ChartType.entries.forEach { type ->
            val isSelected = selected == type
            Surface(
                modifier = Modifier.clickable { onSelect(type) },
                shape = RoundedCornerShape(14.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
                border = BorderStroke(2.dp, if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline),
            ) {
                Text(type.etiqueta, modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp))
            }
        }
    }
}

@Composable
private fun TitleStep(title: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = title,
        onValueChange = { if (it.length <= 60) onChange(it) },
        label = { Text("Título de tu gráfico") },
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun CategoriesStep(state: BuilderUiState, viewModel: BuilderViewModel, onDragActiveChange: (Boolean) -> Unit) {
    val dataset = state.selectedDataset
    Column {
        Text("Desmarca lo que no quieras mostrar y arrastra para ordenar:", style = MaterialTheme.typography.bodyMedium)
        androidx.compose.foundation.layout.Spacer(Modifier.height(8.dp))
        dataset?.points?.forEach { point ->
            val included = point.label in state.categoryOrder
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = included,
                    onCheckedChange = { checked ->
                        if (checked) viewModel.restoreCategory(point.label) else viewModel.removeCategory(point.label)
                    },
                )
                Text(point.label, style = MaterialTheme.typography.bodyMedium)
            }
        }
        androidx.compose.foundation.layout.Spacer(Modifier.height(8.dp))
        DragOrderList(
            items = state.categoryOrder,
            itemLabel = { it },
            onReordered = viewModel::setCategoryOrder,
            onDragActiveChange = onDragActiveChange,
        )
    }
}

@Composable
private fun LabelsStep(showLabels: Boolean, onToggle: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Switch(checked = showLabels, onCheckedChange = { onToggle() })
        Text("Mostrar etiquetas de valor sobre el gráfico", modifier = Modifier.padding(start = 10.dp))
    }
}

@Composable
private fun ScaleStep(state: BuilderUiState, viewModel: BuilderViewModel) {
    val suggested = viewModel.suggestedAxisMax()
    val current = state.axisMax ?: suggested
    Column {
        Text("Escala máxima del eje: ${"%.0f".format(current)}", style = MaterialTheme.typography.bodyLarge)
        Slider(
            value = current.toFloat(),
            onValueChange = { viewModel.setAxisMax(it.toDouble()) },
            valueRange = (suggested * 0.4).toFloat()..(suggested * 2.2).toFloat(),
        )
        Text(
            "Sugerencia: $suggested (evita truncar el eje para no engañar a quien lo lea).",
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun LegendStep(showLegend: Boolean, onToggle: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Switch(checked = showLegend, onCheckedChange = { onToggle() })
        Text("Mostrar leyenda de categorías", modifier = Modifier.padding(start = 10.dp))
    }
}

@Composable
private fun PreviewStep(state: BuilderUiState, viewModel: BuilderViewModel) {
    val dataset = state.selectedDataset ?: return
    val points = state.categoryOrder.mapNotNull { label -> dataset.points.find { it.label == label } }
    Column {
        Text(state.title.ifBlank { dataset.title }, style = MaterialTheme.typography.titleLarge)
        Box(Modifier.fillMaxWidth().aspectRatio(1.1f).padding(vertical = 8.dp)) {
            ChartRenderer(
                chartType = state.chartType,
                points = points,
                unitLabel = dataset.unit,
                axisMinOverride = null,
                modifier = Modifier.fillMaxSize(),
            )
        }
        if (state.showLegend) ChartLegend(points)
        val issues = viewModel.validationIssues()
        if (issues.isNotEmpty()) {
            Column(Modifier.padding(top = 8.dp)) {
                issues.forEach { Text("• ${it.message}", color = androidx.compose.ui.graphics.Color(0xFFE5484D)) }
            }
        }
        Button(onClick = viewModel::save, enabled = issues.isEmpty(), modifier = Modifier.padding(top = 12.dp)) {
            Text("Guardar en mi colección")
        }
        state.savedMessage?.let {
            Row(Modifier.padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                GrafiFloating(GrafiPose.CONSTRUYE, size = 48.dp)
                Text(it, modifier = Modifier.padding(start = 8.dp))
            }
            Button(onClick = viewModel::startNewChart, modifier = Modifier.padding(top = 8.dp)) {
                Text("Construir otro gráfico")
            }
        }
    }
}

@Composable
private fun NavigationButtons(state: BuilderUiState, viewModel: BuilderViewModel) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        if (state.stepIndex > 0) {
            androidx.compose.material3.OutlinedButton(onClick = viewModel::previousStep) { Text("Atrás") }
        } else {
            Box {}
        }
        if (state.step != BuilderStep.VISTA_PREVIA) {
            Button(onClick = viewModel::nextStep, enabled = canAdvance(state)) { Text("Siguiente") }
        }
    }
}

private fun canAdvance(state: BuilderUiState): Boolean = when (state.step) {
    BuilderStep.DATASET -> state.selectedDataset != null
    BuilderStep.TITULO -> state.title.isNotBlank()
    BuilderStep.CATEGORIAS -> state.categoryOrder.size >= 2
    else -> true
}
