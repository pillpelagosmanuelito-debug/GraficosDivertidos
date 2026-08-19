package com.educalab.graficosdivertidos.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.educalab.graficosdivertidos.domain.logic.ChartMathEngine
import com.educalab.graficosdivertidos.domain.model.ChartType
import com.educalab.graficosdivertidos.domain.model.DataPointModel
import com.educalab.graficosdivertidos.ui.theme.ChartPalette

/**
 * Elige el Canvas correcto según [chartType]. Es el punto único donde la UI
 * decide "cómo se ve" un gráfico; toda la matemática vive en
 * [ChartMathEngine], no aquí.
 */
@Composable
fun ChartRenderer(
    chartType: ChartType,
    points: List<DataPointModel>,
    unitLabel: String,
    modifier: Modifier = Modifier,
    axisMinOverride: Double? = null,
    unitPerIconOverride: Double? = null,
    highlightIndex: Int? = null,
    onElementTapped: ((Int) -> Unit)? = null,
) {
    when (chartType) {
        ChartType.BARRAS -> BarChartCanvas(
            points = points,
            unitLabel = unitLabel,
            modifier = modifier,
            axisMinOverride = axisMinOverride,
            highlightIndex = highlightIndex,
            onBarTapped = onElementTapped,
        )
        ChartType.LINEAS -> LineChartCanvas(
            points = points,
            modifier = modifier,
            highlightIndex = highlightIndex,
            onPointTapped = onElementTapped,
        )
        ChartType.CIRCULAR -> PieChartCanvas(
            points = points,
            modifier = modifier,
            highlightIndex = highlightIndex,
            onSliceTapped = onElementTapped,
        )
        ChartType.PICTOGRAMA -> {
            val unit = unitPerIconOverride ?: ChartMathEngine.suggestPictogramUnit(points.maxOfOrNull { it.value } ?: 1.0)
            PictogramChartCanvas(
                points = points,
                unitPerIcon = unit,
                modifier = modifier,
                highlightIndex = highlightIndex,
                onRowTapped = onElementTapped,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChartLegend(points: List<DataPointModel>, modifier: Modifier = Modifier) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        points.forEachIndexed { index, point ->
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(ChartPalette[index % ChartPalette.size]),
                )
                androidx.compose.foundation.layout.Spacer(Modifier.size(4.dp))
                Text(point.label, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
