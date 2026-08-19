package com.educalab.graficosdivertidos.domain.model

/**
 * Modelos de dominio puros (sin dependencias de Android/Compose/Room) que
 * describen los datos y gráficos del Estudio de Visualización.
 */

enum class ChartType(val etiqueta: String) {
    BARRAS("Barras"),
    PICTOGRAMA("Pictograma"),
    LINEAS("Líneas"),
    CIRCULAR("Circular"),
}

enum class GraphErrorType(val etiqueta: String, val descripcionCorta: String) {
    EJE_TRUNCADO(
        "Eje truncado",
        "El eje no comienza en cero y exagera las diferencias entre barras.",
    ),
    ESCALA_INCONSISTENTE(
        "Escala inconsistente",
        "Los intervalos del eje no son iguales entre sí, así que el gráfico engaña al ojo.",
    ),
    DATOS_FALTANTES(
        "Datos faltantes",
        "Falta una categoría o un periodo, así que la comparación queda incompleta.",
    ),
    TITULO_ENGANOSO(
        "Título engañoso",
        "El título afirma algo que los datos del gráfico no respaldan.",
    ),
    CATEGORIA_INCORRECTA(
        "Categoría incorrecta",
        "Una etiqueta no corresponde al dato que representa.",
    ),
    PICTOGRAMA_SIN_ESCALA(
        "Pictograma sin escala",
        "El pictograma no indica cuánto vale cada icono, así que no se puede leer.",
    ),
}

enum class InteractionType {
    SELECCION_EN_GRAFICO,
    ORDENAR_CATEGORIAS,
    ESTIMAR_VALOR,
    COMPARAR_PUNTOS,
    OPCION_MULTIPLE,
}

enum class ModuleKey {
    BARRAS, PICTOGRAMAS, LINEAS, CIRCULAR, CONSTRUCTOR, COMPARADOR, DETECTIVE, DESAFIOS
}

enum class ModuleState { BLOQUEADO, DISPONIBLE, INICIADO, COMPLETADO, DOMINADO }

data class DataPointModel(
    val label: String,
    val value: Double,
    val order: Int,
)

data class DatasetModel(
    val id: Long,
    val title: String,
    val category: String,
    val unit: String,
    val icon: String,
    val points: List<DataPointModel>,
) {
    val maxValue: Double get() = points.maxOfOrNull { it.value } ?: 0.0
    val total: Double get() = points.sumOf { it.value }
}

/** Punto 2D genérico de dominio (sin depender de androidx.compose.ui.geometry.Offset). */
data class DomainPoint(val x: Float, val y: Float)

data class PieSlice(
    val label: String,
    val value: Double,
    val percentage: Double,
    val startAngleDeg: Float,
    val sweepAngleDeg: Float,
)

data class PictogramCount(
    val fullIcons: Int,
    val partialFraction: Double,
    val unitPerIcon: Double,
)

data class ChartConfigurationModel(
    val datasetId: Long,
    val chartType: ChartType,
    val title: String,
    val categoryOrder: List<String>,
    val showLabels: Boolean,
    val showLegend: Boolean,
    val axisMax: Double?,
)

data class ValidationIssue(val field: String, val message: String)
