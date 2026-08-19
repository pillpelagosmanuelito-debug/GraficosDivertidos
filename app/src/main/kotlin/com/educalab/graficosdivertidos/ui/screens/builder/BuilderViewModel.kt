package com.educalab.graficosdivertidos.ui.screens.builder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.educalab.graficosdivertidos.data.repository.BuilderRepository
import com.educalab.graficosdivertidos.data.repository.ContentRepository
import com.educalab.graficosdivertidos.data.repository.ProgressRepository
import com.educalab.graficosdivertidos.domain.logic.ChartMathEngine
import com.educalab.graficosdivertidos.domain.model.ChartConfigurationModel
import com.educalab.graficosdivertidos.domain.model.ChartType
import com.educalab.graficosdivertidos.domain.model.DatasetModel
import com.educalab.graficosdivertidos.domain.model.ValidationIssue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class BuilderStep { DATASET, TIPO, TITULO, CATEGORIAS, ETIQUETAS, ESCALA, LEYENDA, VISTA_PREVIA }

data class BuilderUiState(
    val loading: Boolean = true,
    val datasets: List<DatasetModel> = emptyList(),
    val step: BuilderStep = BuilderStep.DATASET,
    val selectedDataset: DatasetModel? = null,
    val chartType: ChartType = ChartType.BARRAS,
    val title: String = "",
    val categoryOrder: List<String> = emptyList(),
    val showLabels: Boolean = true,
    val showLegend: Boolean = true,
    val axisMax: Double? = null,
    val savedMessage: String? = null,
    val savedCount: Int = 0,
) {
    val stepIndex: Int get() = BuilderStep.entries.indexOf(step)
    val stepCount: Int get() = BuilderStep.entries.size
}

/** Módulo Constructor: dataset → tipo → título → categorías → etiquetas → escala → leyenda → vista previa animada. */
class BuilderViewModel(
    private val userId: Long,
    private val contentRepository: ContentRepository,
    private val builderRepository: BuilderRepository,
    private val progressRepository: ProgressRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(BuilderUiState())
    val uiState: StateFlow<BuilderUiState> = _uiState.asStateFlow()

    val savedConfigurations = builderRepository.observeSavedConfigurations(userId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            val datasets = contentRepository.getAllDatasets()
            _uiState.update { it.copy(loading = false, datasets = datasets) }
        }
    }

    fun selectDataset(dataset: DatasetModel) {
        _uiState.update {
            it.copy(
                selectedDataset = dataset,
                categoryOrder = dataset.points.map { p -> p.label },
                title = dataset.title,
                axisMax = null,
            )
        }
    }

    fun selectChartType(type: ChartType) = _uiState.update { it.copy(chartType = type) }
    fun setTitle(title: String) = _uiState.update { it.copy(title = title) }
    fun setCategoryOrder(order: List<String>) = _uiState.update { it.copy(categoryOrder = order) }
    fun removeCategory(label: String) = _uiState.update { it.copy(categoryOrder = it.categoryOrder - label) }
    fun restoreCategory(label: String) {
        val dataset = _uiState.value.selectedDataset ?: return
        if (label !in _uiState.value.categoryOrder && dataset.points.any { it.label == label }) {
            _uiState.update { it.copy(categoryOrder = it.categoryOrder + label) }
        }
    }
    fun toggleLabels() = _uiState.update { it.copy(showLabels = !it.showLabels) }
    fun toggleLegend() = _uiState.update { it.copy(showLegend = !it.showLegend) }
    fun setAxisMax(value: Double?) = _uiState.update { it.copy(axisMax = value) }

    fun suggestedAxisMax(): Double {
        val dataset = _uiState.value.selectedDataset ?: return 10.0
        return ChartMathEngine.niceAxisMax(dataset.maxValue)
    }

    fun goToStep(step: BuilderStep) = _uiState.update { it.copy(step = step) }

    fun nextStep() {
        val order = BuilderStep.entries
        val current = order.indexOf(_uiState.value.step)
        if (current < order.lastIndex) _uiState.update { it.copy(step = order[current + 1]) }
    }

    fun previousStep() {
        val order = BuilderStep.entries
        val current = order.indexOf(_uiState.value.step)
        if (current > 0) _uiState.update { it.copy(step = order[current - 1]) }
    }

    fun validationIssues(): List<ValidationIssue> {
        val state = _uiState.value
        val dataset = state.selectedDataset ?: return listOf(ValidationIssue("dataset", "Elige un conjunto de datos."))
        val config = ChartConfigurationModel(
            datasetId = dataset.id,
            chartType = state.chartType,
            title = state.title,
            categoryOrder = state.categoryOrder,
            showLabels = state.showLabels,
            showLegend = state.showLegend,
            axisMax = state.axisMax,
        )
        return ChartMathEngine.validateChartConfiguration(config, dataset.points.map { it.label })
    }

    fun canSave(): Boolean = validationIssues().isEmpty()

    fun save() {
        val state = _uiState.value
        val dataset = state.selectedDataset ?: return
        if (!canSave()) return
        viewModelScope.launch {
            builderRepository.saveConfiguration(
                userId = userId,
                datasetId = dataset.id,
                chartType = state.chartType,
                title = state.title,
                categoryOrder = state.categoryOrder,
                showLabels = state.showLabels,
                showLegend = state.showLegend,
                axisMax = state.axisMax,
                now = System.currentTimeMillis(),
            )
            progressRepository.recomputeBuilderProgress(userId, System.currentTimeMillis())
            val count = builderRepository.countSaved(userId)
            _uiState.update { it.copy(savedMessage = "¡Guardado! Ya tienes $count gráficos en tu colección.", savedCount = count) }
        }
    }

    fun startNewChart() {
        _uiState.update {
            it.copy(
                step = BuilderStep.DATASET,
                selectedDataset = null,
                title = "",
                categoryOrder = emptyList(),
                axisMax = null,
                savedMessage = null,
            )
        }
    }
}
