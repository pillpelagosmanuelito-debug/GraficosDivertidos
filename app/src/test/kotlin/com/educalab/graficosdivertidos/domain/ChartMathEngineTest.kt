package com.educalab.graficosdivertidos.domain

import com.educalab.graficosdivertidos.domain.logic.ChartMathEngine
import com.educalab.graficosdivertidos.domain.model.ChartConfigurationModel
import com.educalab.graficosdivertidos.domain.model.ChartType
import com.educalab.graficosdivertidos.domain.model.DataPointModel
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ChartMathEngineTest {

    // ---------- niceAxisMax / niceStep / axisTicks ----------

    @Test
    fun `niceAxisMax redondea hacia un numero facil de leer`() {
        val result = ChartMathEngine.niceAxisMax(37.0, tickCount = 5)
        assertThat(result % 5.0 == 0.0 || result % 2.0 == 0.0 || result % 10.0 == 0.0).isTrue()
        assertThat(result).isAtLeast(37.0)
    }

    @Test
    fun `niceAxisMax con maximo cero devuelve un valor por defecto positivo`() {
        assertThat(ChartMathEngine.niceAxisMax(0.0)).isGreaterThan(0.0)
    }

    @Test
    fun `niceAxisMax con valores negativos no lanza excepcion y devuelve positivo`() {
        assertThat(ChartMathEngine.niceAxisMax(-5.0)).isGreaterThan(0.0)
    }

    @Test
    fun `axisTicks incluye el cero y termina en el maximo`() {
        val ticks = ChartMathEngine.axisTicks(100.0, tickCount = 5)
        assertThat(ticks.first()).isEqualTo(0.0)
        assertThat(ticks.last()).isEqualTo(100.0)
        assertThat(ticks).hasSize(6)
    }

    @Test
    fun `axisTicks con maximo cero no lanza division por cero`() {
        val ticks = ChartMathEngine.axisTicks(0.0)
        assertThat(ticks).isEqualTo(listOf(0.0))
    }

    // ---------- scaleToPixels ----------

    @Test
    fun `scaleToPixels ubica el valor maximo en el borde del canvas`() {
        val px = ChartMathEngine.scaleToPixels(50.0, 50.0, 200f)
        assertThat(px).isEqualTo(200f)
    }

    @Test
    fun `scaleToPixels con eje en cero no lanza excepcion`() {
        assertThat(ChartMathEngine.scaleToPixels(10.0, 0.0, 200f)).isEqualTo(0f)
    }

    @Test
    fun `scaleToPixels recorta valores fuera de rango`() {
        val px = ChartMathEngine.scaleToPixels(999.0, 100.0, 200f)
        assertThat(px).isEqualTo(200f)
    }

    // ---------- normalizeToPercentages ----------

    @Test
    fun `normalizeToPercentages suma exactamente 100`() {
        val result = ChartMathEngine.normalizeToPercentages(listOf(10.0, 20.0, 30.0))
        assertThat(result.sum()).isWithin(0.001).of(100.0)
    }

    @Test
    fun `normalizeToPercentages con todos los valores en cero reparte por igual`() {
        val result = ChartMathEngine.normalizeToPercentages(listOf(0.0, 0.0, 0.0, 0.0))
        assertThat(result).hasSize(4)
        assertThat(result.sum()).isWithin(0.01).of(100.0)
    }

    @Test
    fun `normalizeToPercentages con lista vacia no lanza excepcion`() {
        assertThat(ChartMathEngine.normalizeToPercentages(emptyList())).isEmpty()
    }

    // ---------- pieSlices ----------

    @Test
    fun `pieSlices reparte 360 grados en total`() {
        val points = listOf(DataPointModel("A", 1.0, 0), DataPointModel("B", 3.0, 1))
        val slices = ChartMathEngine.pieSlices(points)
        val totalSweep = slices.sumOf { it.sweepAngleDeg.toDouble() }
        assertThat(totalSweep).isWithin(0.5).of(360.0)
    }

    @Test
    fun `pieSlices con dataset vacio devuelve lista vacia`() {
        assertThat(ChartMathEngine.pieSlices(emptyList())).isEmpty()
    }

    @Test
    fun `pieSlices con un unico valor ocupa todo el circulo`() {
        val slices = ChartMathEngine.pieSlices(listOf(DataPointModel("Único", 5.0, 0)))
        assertThat(slices).hasSize(1)
        assertThat(slices[0].sweepAngleDeg).isWithin(0.5f).of(360f)
    }

    // ---------- pictogramCount ----------

    @Test
    fun `pictogramCount calcula iconos completos y fraccion parcial`() {
        val count = ChartMathEngine.pictogramCount(value = 13.0, unitPerIcon = 5.0)
        assertThat(count.fullIcons).isEqualTo(2)
        assertThat(count.partialFraction).isWithin(0.01).of(0.6)
    }

    @Test
    fun `pictogramCount con unidad por icono cero no lanza division por cero`() {
        val count = ChartMathEngine.pictogramCount(value = 10.0, unitPerIcon = 0.0)
        assertThat(count.fullIcons).isEqualTo(0)
        assertThat(count.partialFraction).isEqualTo(0.0)
    }

    @Test
    fun `pictogramCount con valor negativo no produce iconos negativos`() {
        val count = ChartMathEngine.pictogramCount(value = -3.0, unitPerIcon = 2.0)
        assertThat(count.fullIcons).isAtLeast(0)
    }

    @Test
    fun `suggestPictogramUnit produce entre 3 y 10 iconos aproximadamente`() {
        val unit = ChartMathEngine.suggestPictogramUnit(60.0)
        val approxIcons = 60.0 / unit
        assertThat(approxIcons).isAtLeast(3.0)
        assertThat(approxIcons).isAtMost(12.0)
    }

    // ---------- barLayout ----------

    @Test
    fun `barLayout con cero categorias no lanza excepcion`() {
        val layout = ChartMathEngine.barLayout(300f, 0)
        assertThat(layout.barWidth).isEqualTo(0f)
    }

    @Test
    fun `barLayout produce barras positivas para varias categorias`() {
        val layout = ChartMathEngine.barLayout(300f, 4)
        assertThat(layout.barWidth).isGreaterThan(0f)
        assertThat(layout.spacing).isGreaterThan(0f)
    }

    // ---------- linePoints ----------

    @Test
    fun `linePoints genera un punto por cada valor`() {
        val points = ChartMathEngine.linePoints(listOf(1.0, 2.0, 3.0), 3.0, 300f, 100f)
        assertThat(points).hasSize(3)
    }

    @Test
    fun `linePoints con lista vacia no lanza excepcion`() {
        assertThat(ChartMathEngine.linePoints(emptyList(), 10.0, 100f, 100f)).isEmpty()
    }

    @Test
    fun `linePoints el valor maximo queda en la parte superior del canvas`() {
        val points = ChartMathEngine.linePoints(listOf(0.0, 10.0), 10.0, 100f, 100f)
        assertThat(points[1].y).isWithin(0.01f).of(0f)
    }

    // ---------- isAxisTruncated ----------

    @Test
    fun `isAxisTruncated detecta un eje que no empieza en cero`() {
        assertThat(ChartMathEngine.isAxisTruncated(axisMin = 10.0, dataMin = 12.0)).isTrue()
    }

    @Test
    fun `isAxisTruncated con eje en cero no se considera truncado`() {
        assertThat(ChartMathEngine.isAxisTruncated(axisMin = 0.0, dataMin = 5.0)).isFalse()
    }

    // ---------- validateChartConfiguration ----------

    @Test
    fun `validateChartConfiguration exige titulo`() {
        val config = ChartConfigurationModel(1, ChartType.BARRAS, "", listOf("A", "B"), true, true, null)
        val issues = ChartMathEngine.validateChartConfiguration(config, listOf("A", "B"))
        assertThat(issues.any { it.field == "title" }).isTrue()
    }

    @Test
    fun `validateChartConfiguration exige al menos 2 categorias`() {
        val config = ChartConfigurationModel(1, ChartType.BARRAS, "Mi gráfico", listOf("A"), true, true, null)
        val issues = ChartMathEngine.validateChartConfiguration(config, listOf("A", "B"))
        assertThat(issues.any { it.field == "categories" }).isTrue()
    }

    @Test
    fun `validateChartConfiguration detecta categorias duplicadas`() {
        val config = ChartConfigurationModel(1, ChartType.BARRAS, "Mi gráfico", listOf("A", "A"), true, true, null)
        val issues = ChartMathEngine.validateChartConfiguration(config, listOf("A", "B"))
        assertThat(issues.any { it.field == "categories" }).isTrue()
    }

    @Test
    fun `validateChartConfiguration valida una configuracion correcta sin errores`() {
        val config = ChartConfigurationModel(1, ChartType.BARRAS, "Mi gráfico", listOf("A", "B"), true, true, 20.0)
        val issues = ChartMathEngine.validateChartConfiguration(config, listOf("A", "B"))
        assertThat(issues).isEmpty()
    }

    @Test
    fun `validateChartConfiguration rechaza escala menor o igual a cero`() {
        val config = ChartConfigurationModel(1, ChartType.BARRAS, "Mi gráfico", listOf("A", "B"), true, true, -1.0)
        val issues = ChartMathEngine.validateChartConfiguration(config, listOf("A", "B"))
        assertThat(issues.any { it.field == "scale" }).isTrue()
    }
}
