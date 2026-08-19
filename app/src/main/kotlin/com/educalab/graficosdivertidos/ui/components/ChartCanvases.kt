package com.educalab.graficosdivertidos.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import com.educalab.graficosdivertidos.domain.logic.ChartMathEngine
import com.educalab.graficosdivertidos.domain.model.DataPointModel
import com.educalab.graficosdivertidos.ui.theme.ChartPalette
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.min

/**
 * Gráficos dibujados a mano con Compose Canvas (sin librerías externas de
 * gráficos), con animación de entrada y detección de toque por región para
 * las mecánicas "toca el elemento correcto" del Estudio.
 */

@Composable
fun BarChartCanvas(
    points: List<DataPointModel>,
    unitLabel: String,
    modifier: Modifier = Modifier,
    axisMinOverride: Double? = null,
    highlightIndex: Int? = null,
    onBarTapped: ((Int) -> Unit)? = null,
) {
    val animation = remember { Animatable(0f) }
    LaunchedEffect(points) {
        animation.snapTo(0f)
        animation.animateTo(1f, animationSpec = tween(700, easing = FastOutSlowInEasing))
    }
    val textMeasurer = rememberTextMeasurer()
    val axisMax = remember(points) { ChartMathEngine.niceAxisMax(points.maxOfOrNull { it.value } ?: 0.0) }
    var barRects by remember { mutableStateOf(emptyList<androidx.compose.ui.geometry.Rect>()) }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(points) {
                detectTapGestures { tapOffset ->
                    val idx = barRects.indexOfFirst { it.contains(tapOffset) }
                    if (idx >= 0) onBarTapped?.invoke(idx)
                }
            },
    ) {
        val leftPad = 46f
        val bottomPad = 42f
        val topPad = 20f
        val chartWidth = size.width - leftPad - 16f
        val chartHeight = size.height - bottomPad - topPad
        val axisMin = axisMinOverride ?: 0.0

        // ejes
        drawLine(Color.Gray.copy(alpha = 0.4f), Offset(leftPad, topPad), Offset(leftPad, size.height - bottomPad), 2f)
        drawLine(Color.Gray.copy(alpha = 0.4f), Offset(leftPad, size.height - bottomPad), Offset(size.width - 8f, size.height - bottomPad), 2f)

        val ticks = ChartMathEngine.axisTicks(axisMax)
        ticks.forEach { tick ->
            val y = size.height - bottomPad - ChartMathEngine.scaleToPixels(tick, axisMax, chartHeight.toFloat())
            drawLine(Color.Gray.copy(alpha = 0.15f), Offset(leftPad, y), Offset(size.width - 8f, y), 1f)
            val label = textMeasurer.measure(formatNum(tick), style = TextStyle(fontSize = 10.sp, color = Color.Gray))
            drawText(label, topLeft = Offset(4f, y - label.size.height / 2))
        }

        val layout = ChartMathEngine.barLayout(chartWidth, points.size)
        val newRects = mutableListOf<androidx.compose.ui.geometry.Rect>()
        points.forEachIndexed { index, point ->
            val barHeightPx = ChartMathEngine.scaleToPixels(point.value - axisMin, axisMax - axisMin, chartHeight.toFloat()) * animation.value
            val x = leftPad + layout.spacing / 2 + index * (layout.barWidth + layout.spacing)
            val top = size.height - bottomPad - barHeightPx
            val rect = androidx.compose.ui.geometry.Rect(x, top, x + layout.barWidth, size.height - bottomPad)
            newRects += rect
            val color = if (highlightIndex == index) Color(0xFFFFB627) else ChartPalette[index % ChartPalette.size]
            drawRoundRect(
                color = color,
                topLeft = rect.topLeft,
                size = androidx.compose.ui.geometry.Size(rect.width, rect.height),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f),
            )
            val label = textMeasurer.measure(point.label, style = TextStyle(fontSize = 10.sp, color = Color.Gray))
            drawText(
                label,
                topLeft = Offset(x + layout.barWidth / 2 - label.size.width / 2, size.height - bottomPad + 6f),
            )
        }
        barRects = newRects
    }
}

@Composable
fun LineChartCanvas(
    points: List<DataPointModel>,
    modifier: Modifier = Modifier,
    highlightIndex: Int? = null,
    onPointTapped: ((Int) -> Unit)? = null,
) {
    val animation = remember { Animatable(0f) }
    LaunchedEffect(points) {
        animation.snapTo(0f)
        animation.animateTo(1f, animationSpec = tween(800, easing = FastOutSlowInEasing))
    }
    val textMeasurer = rememberTextMeasurer()
    val axisMax = remember(points) { ChartMathEngine.niceAxisMax(points.maxOfOrNull { it.value } ?: 0.0) }
    var pointCenters by remember { mutableStateOf(emptyList<Offset>()) }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(points) {
                detectTapGestures { tapOffset ->
                    val idx = pointCenters.indexOfFirst { (it - tapOffset).getDistance() < 40f }
                    if (idx >= 0) onPointTapped?.invoke(idx)
                }
            },
    ) {
        val leftPad = 46f
        val bottomPad = 42f
        val topPad = 20f
        val chartWidth = size.width - leftPad - 16f
        val chartHeight = size.height - bottomPad - topPad

        drawLine(Color.Gray.copy(alpha = 0.4f), Offset(leftPad, topPad), Offset(leftPad, size.height - bottomPad), 2f)
        drawLine(Color.Gray.copy(alpha = 0.4f), Offset(leftPad, size.height - bottomPad), Offset(size.width - 8f, size.height - bottomPad), 2f)

        val domainPts = ChartMathEngine.linePoints(points.map { it.value }, axisMax, chartWidth, chartHeight.toFloat())
        val offsets = domainPts.map { Offset(leftPad + it.x, topPad + it.y) }
        val animatedCount = (offsets.size * animation.value).toInt().coerceIn(0, offsets.size)

        if (animatedCount >= 2) {
            val path = androidx.compose.ui.graphics.Path().apply {
                moveTo(offsets[0].x, offsets[0].y)
                for (i in 1 until animatedCount) lineTo(offsets[i].x, offsets[i].y)
            }
            drawPath(path, color = Color(0xFF17C3B2), style = Stroke(width = 6f, cap = androidx.compose.ui.graphics.StrokeCap.Round))
        }
        offsets.take(animatedCount).forEachIndexed { index, offset ->
            val isHighlighted = highlightIndex == index
            drawCircle(if (isHighlighted) Color(0xFFFFB627) else Color(0xFF6C4BFF), radius = if (isHighlighted) 12f else 8f, center = offset)
            drawCircle(Color.White, radius = 3f, center = offset)
            val label = textMeasurer.measure(points[index].label, style = TextStyle(fontSize = 9.sp, color = Color.Gray))
            drawText(label, topLeft = Offset(offset.x - label.size.width / 2, size.height - bottomPad + 6f))
        }
        pointCenters = offsets
    }
}

@Composable
fun PieChartCanvas(
    points: List<DataPointModel>,
    modifier: Modifier = Modifier,
    highlightIndex: Int? = null,
    onSliceTapped: ((Int) -> Unit)? = null,
) {
    val animation = remember { Animatable(0f) }
    LaunchedEffect(points) {
        animation.snapTo(0f)
        animation.animateTo(1f, animationSpec = tween(800, easing = FastOutSlowInEasing))
    }
    val slices = remember(points) { ChartMathEngine.pieSlices(points) }
    var center by remember { mutableStateOf(Offset.Zero) }
    var radius by remember { mutableStateOf(0f) }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(points) {
                detectTapGestures { tapOffset ->
                    val dx = tapOffset.x - center.x
                    val dy = tapOffset.y - center.y
                    val dist = hypot(dx, dy)
                    if (dist <= radius) {
                        var angle = Math.toDegrees(atan2(dy, dx).toDouble()).toFloat()
                        if (angle < 0) angle += 360f
                        val idx = slices.indexOfFirst { angle >= it.startAngleDeg && angle < it.startAngleDeg + it.sweepAngleDeg }
                        if (idx >= 0) onSliceTapped?.invoke(idx)
                    }
                }
            },
    ) {
        val diameter = min(size.width, size.height) * 0.75f
        radius = diameter / 2
        center = Offset(size.width / 2, size.height / 2 - 10f)
        val topLeft = Offset(center.x - radius, center.y - radius)

        slices.forEachIndexed { index, slice ->
            val sweep = slice.sweepAngleDeg * animation.value
            val color = if (highlightIndex == index) Color(0xFFFFB627) else ChartPalette[index % ChartPalette.size]
            drawArc(
                color = color,
                startAngle = slice.startAngleDeg,
                sweepAngle = sweep,
                useCenter = true,
                topLeft = topLeft,
                size = androidx.compose.ui.geometry.Size(diameter, diameter),
            )
        }
        drawCircle(Color.White.copy(alpha = 0.001f), radius = radius, center = center, style = Stroke(width = 3f))
    }
}

@Composable
fun PictogramChartCanvas(
    points: List<DataPointModel>,
    unitPerIcon: Double,
    modifier: Modifier = Modifier,
    highlightIndex: Int? = null,
    onRowTapped: ((Int) -> Unit)? = null,
) {
    val animation = remember { Animatable(0f) }
    LaunchedEffect(points) {
        animation.snapTo(0f)
        animation.animateTo(1f, animationSpec = tween(700, easing = FastOutSlowInEasing))
    }
    val textMeasurer = rememberTextMeasurer()
    var rowRects by remember { mutableStateOf(emptyList<androidx.compose.ui.geometry.Rect>()) }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(points) {
                detectTapGestures { tapOffset ->
                    val idx = rowRects.indexOfFirst { it.contains(tapOffset) }
                    if (idx >= 0) onRowTapped?.invoke(idx)
                }
            },
    ) {
        val leftLabelWidth = 96f
        val rowHeight = size.height / points.size.coerceAtLeast(1)
        val iconSize = (rowHeight * 0.55f).coerceAtMost(34f)
        val newRects = mutableListOf<androidx.compose.ui.geometry.Rect>()

        points.forEachIndexed { index, point ->
            val rowTop = index * rowHeight
            val label = textMeasurer.measure(point.label, style = TextStyle(fontSize = 11.sp, color = Color.Gray))
            drawText(label, topLeft = Offset(0f, rowTop + rowHeight / 2 - label.size.height / 2))

            val count = if (unitPerIcon > 0.0) {
                ChartMathEngine.pictogramCount(point.value, unitPerIcon)
            } else null

            val rowRect = androidx.compose.ui.geometry.Rect(
                leftLabelWidth, rowTop, size.width, rowTop + rowHeight,
            )
            newRects += rowRect
            val highlight = highlightIndex == index
            val color = if (highlight) Color(0xFFFFB627) else Color(0xFFFF6B6B)

            if (count == null) {
                // Sin escala: se dibuja un único icono ambiguo grande (para el reto del Detective).
                drawRoundRect(
                    color = color.copy(alpha = 0.4f),
                    topLeft = Offset(leftLabelWidth + 6f, rowTop + rowHeight / 2 - iconSize / 2),
                    size = androidx.compose.ui.geometry.Size(iconSize, iconSize),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f),
                )
            } else {
                val visibleFull = (count.fullIcons * animation.value).toInt().coerceAtMost(count.fullIcons)
                for (i in 0 until visibleFull) {
                    val x = leftLabelWidth + 6f + i * (iconSize + 6f)
                    if (x + iconSize > size.width) break
                    drawRoundRect(
                        color = color,
                        topLeft = Offset(x, rowTop + rowHeight / 2 - iconSize / 2),
                        size = androidx.compose.ui.geometry.Size(iconSize, iconSize),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f),
                    )
                }
                if (count.partialFraction > 0.05 && animation.value > 0.9f) {
                    val x = leftLabelWidth + 6f + visibleFull * (iconSize + 6f)
                    if (x + iconSize * count.partialFraction.toFloat() <= size.width) {
                        drawRoundRect(
                            color = color.copy(alpha = 0.6f),
                            topLeft = Offset(x, rowTop + rowHeight / 2 - iconSize / 2),
                            size = androidx.compose.ui.geometry.Size(iconSize * count.partialFraction.toFloat(), iconSize),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f),
                        )
                    }
                }
            }
        }
        rowRects = newRects
    }
}

private fun formatNum(value: Double): String {
    return if (value == value.toLong().toDouble()) value.toLong().toString() else String.format("%.1f", value)
}
