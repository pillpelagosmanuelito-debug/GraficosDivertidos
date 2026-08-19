package com.educalab.graficosdivertidos.domain.logic

import com.educalab.graficosdivertidos.domain.model.ChartConfigurationModel
import com.educalab.graficosdivertidos.domain.model.DataPointModel
import com.educalab.graficosdivertidos.domain.model.DomainPoint
import com.educalab.graficosdivertidos.domain.model.PictogramCount
import com.educalab.graficosdivertidos.domain.model.PieSlice
import com.educalab.graficosdivertidos.domain.model.ValidationIssue
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * Motor matemático de gráficos: escalado de ejes, normalización a
 * porcentajes, ángulos de sectores circulares, conteo de pictogramas y
 * coordenadas para líneas/barras.
 *
 * Es Kotlin puro (sin Android ni Compose) para poder probarlo con JUnit
 * normal, tal como exige la especificación del proyecto.
 */
object ChartMathEngine {

    /**
     * Calcula un máximo de eje "bonito" (nice number) que sea igual o mayor
     * que [maxValue], de forma que las marcas del eje caigan en números
     * fáciles de leer para un niño de 9-13 años (5, 10, 20, 25, 50, 100...).
     */
    fun niceAxisMax(maxValue: Double, tickCount: Int = 5): Double {
        if (maxValue <= 0.0) return tickCount.toDouble()
        val roughStep = maxValue / tickCount
        val niceStep = niceStep(roughStep)
        val niceMax = niceStep * tickCount
        return if (niceMax < maxValue) niceStep * (tickCount + 1) else niceMax
    }

    /** Redondea [rawStep] al "nice number" más cercano hacia arriba (1, 2, 2.5, 5, 10 * 10^n). */
    fun niceStep(rawStep: Double): Double {
        if (rawStep <= 0.0) return 1.0
        val exponent = floor(ln(rawStep) / ln(10.0))
        val magnitude = 10.0.pow(exponent)
        val fraction = rawStep / magnitude
        val niceFraction = when {
            fraction <= 1.0 -> 1.0
            fraction <= 2.0 -> 2.0
            fraction <= 2.5 -> 2.5
            fraction <= 5.0 -> 5.0
            else -> 10.0
        }
        return niceFraction * magnitude
    }

    /** Devuelve las marcas del eje (incluyendo 0) hasta [axisMax], en pasos iguales. */
    fun axisTicks(axisMax: Double, tickCount: Int = 5): List<Double> {
        if (axisMax <= 0.0) return listOf(0.0)
        val step = axisMax / tickCount
        return (0..tickCount).map { round2(it * step) }
    }

    /** Convierte un valor de dato a una posición en píxeles dentro de [pixelMax] (0 = base). */
    fun scaleToPixels(value: Double, axisMax: Double, pixelMax: Float): Float {
        if (axisMax <= 0.0) return 0f
        val clamped = value.coerceIn(0.0, axisMax)
        return (clamped / axisMax * pixelMax).toFloat()
    }

    /**
     * Normaliza una lista de valores a porcentajes que suman 100 (o lo más
     * cercano posible, ajustando el residuo en el valor mayor para evitar
     * errores de redondeo acumulados). Si todos los valores son 0 o la lista
     * está vacía, reparte el 100% en partes iguales para no dividir por cero.
     */
    fun normalizeToPercentages(values: List<Double>): List<Double> {
        if (values.isEmpty()) return emptyList()
        val total = values.sum()
        if (total <= 0.0) {
            val equalShare = round2(100.0 / values.size)
            return List(values.size) { equalShare }
        }
        val rawPercentages = values.map { it / total * 100.0 }
        val rounded = rawPercentages.map { round2(it) }.toMutableList()
        val diff = round2(100.0 - rounded.sum())
        if (diff != 0.0) {
            val maxIndex = values.indices.maxByOrNull { values[it] } ?: 0
            rounded[maxIndex] = round2(rounded[maxIndex] + diff)
        }
        return rounded
    }

    /**
     * Calcula los sectores (ángulo inicial y de barrido) de un gráfico
     * circular a partir de una lista de puntos de datos. Si el total es 0,
     * reparte el círculo en partes iguales (misma salvaguarda que
     * [normalizeToPercentages]) para evitar un gráfico vacío o un NaN.
     */
    fun pieSlices(points: List<DataPointModel>): List<PieSlice> {
        if (points.isEmpty()) return emptyList()
        val percentages = normalizeToPercentages(points.map { it.value })
        var cursor = 0f
        return points.mapIndexed { index, point ->
            val sweep = (percentages[index] / 100.0 * 360.0).toFloat()
            val slice = PieSlice(
                label = point.label,
                value = point.value,
                percentage = percentages[index],
                startAngleDeg = cursor,
                sweepAngleDeg = sweep,
            )
            cursor += sweep
            slice
        }
    }

    /**
     * Calcula cuántos iconos completos y qué fracción parcial representan un
     * [value] dado un [unitPerIcon] (p.ej. "cada icono = 5 mascotas").
     * Devuelve fullIcons=0 y partialFraction=0 si unitPerIcon no es positivo,
     * evitando una división por cero.
     */
    fun pictogramCount(value: Double, unitPerIcon: Double): PictogramCount {
        if (unitPerIcon <= 0.0) return PictogramCount(0, 0.0, unitPerIcon)
        val exact = value / unitPerIcon
        val full = floor(exact).toInt().coerceAtLeast(0)
        val partial = round2(exact - full)
        return PictogramCount(full, partial, unitPerIcon)
    }

    /** Sugiere una unidad por icono "bonita" para que el número de iconos quede entre 3 y 10. */
    fun suggestPictogramUnit(maxValue: Double): Double {
        if (maxValue <= 0.0) return 1.0
        var unit = niceStep(maxValue / 6.0)
        if (unit <= 0.0) unit = 1.0
        return unit
    }

    /** Ancho de barra y separación entre barras para que quepan en [availableWidth]. */
    fun barLayout(availableWidth: Float, count: Int, minSpacingRatio: Float = 0.35f): BarLayout {
        if (count <= 0 || availableWidth <= 0f) return BarLayout(0f, 0f)
        val unit = availableWidth / (count + count * minSpacingRatio)
        val barWidth = unit
        val spacing = unit * minSpacingRatio
        return BarLayout(barWidth = barWidth, spacing = spacing)
    }

    data class BarLayout(val barWidth: Float, val spacing: Float)

    /**
     * Calcula las coordenadas (x,y) de cada punto de una serie para un
     * gráfico de líneas, con y=0 en la parte superior (convención de canvas)
     * y creciendo hacia abajo; el llamador de UI decide cómo invertirlo.
     */
    fun linePoints(values: List<Double>, axisMax: Double, width: Float, height: Float): List<DomainPoint> {
        if (values.isEmpty() || width <= 0f) return emptyList()
        val stepX = if (values.size > 1) width / (values.size - 1) else 0f
        return values.mapIndexed { index, value ->
            val x = stepX * index
            val normalized = if (axisMax > 0.0) (value / axisMax).coerceIn(0.0, 1.0) else 0.0
            val y = height - (normalized * height).toFloat()
            DomainPoint(x, y)
        }
    }

    /**
     * Un eje se considera "truncado" (engañoso) cuando su mínimo visible es
     * mayor que 0 pero los datos también incluyen valores cercanos a ese
     * mínimo, exagerando visualmente las diferencias.
     */
    fun isAxisTruncated(axisMin: Double, dataMin: Double): Boolean {
        return axisMin > 0.0 && axisMin <= dataMin
    }

    /** Valida una configuración construida en el módulo Constructor. */
    fun validateChartConfiguration(
        config: ChartConfigurationModel,
        availableCategories: List<String>,
    ): List<ValidationIssue> {
        val issues = mutableListOf<ValidationIssue>()
        if (config.title.isBlank()) {
            issues += ValidationIssue("title", "Ponle un título a tu gráfico.")
        } else if (config.title.length > 60) {
            issues += ValidationIssue("title", "El título es muy largo; hazlo más corto.")
        }
        if (config.categoryOrder.isEmpty()) {
            issues += ValidationIssue("categories", "Elige al menos 2 categorías para tu gráfico.")
        } else if (config.categoryOrder.size < 2) {
            issues += ValidationIssue("categories", "Necesitas al menos 2 categorías para comparar.")
        }
        val duplicates = config.categoryOrder.groupingBy { it }.eachCount().filter { it.value > 1 }
        if (duplicates.isNotEmpty()) {
            issues += ValidationIssue("categories", "Hay una categoría repetida: ${duplicates.keys.first()}.")
        }
        val unknown = config.categoryOrder.filter { it !in availableCategories }
        if (unknown.isNotEmpty()) {
            issues += ValidationIssue("categories", "Esa categoría no pertenece a este conjunto de datos.")
        }
        if (config.axisMax != null && config.axisMax <= 0.0) {
            issues += ValidationIssue("scale", "La escala debe ser mayor que cero.")
        }
        return issues
    }

    private fun round2(value: Double): Double = (value * 100.0).roundToInt() / 100.0
}
