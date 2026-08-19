package com.educalab.graficosdivertidos.data.local.seed

import com.educalab.graficosdivertidos.domain.model.ChartType
import com.educalab.graficosdivertidos.domain.model.GraphErrorType
import com.educalab.graficosdivertidos.domain.model.InteractionType
import com.educalab.graficosdivertidos.domain.model.ModuleKey

/**
 * DTOs de contenido semilla. Son objetos Kotlin sencillos (no entidades Room)
 * que [com.educalab.graficosdivertidos.data.local.seed.DatabaseSeeder] vuelca
 * a la base de datos real la primera vez que se abre la app. El contenido en
 * sí vive en [SeedContent], generado con `tools/generate_seed_content.py`
 * para que sea mantenible sin escribir a mano cientos de líneas repetitivas.
 */

data class SeedDataset(
    val key: String,
    val title: String,
    val category: String,
    val unit: String,
    val iconKey: String,
    val points: List<Pair<String, Double>>,
)

data class SeedChartDefinition(
    val key: String,
    val datasetKey: String,
    val chartType: ChartType,
    val title: String,
    val moduleKey: ModuleKey,
    val showLabels: Boolean = true,
    val showLegend: Boolean = true,
    val axisMax: Double? = null,
)

data class SeedExercise(
    val chartDefinitionKey: String,
    val moduleKey: ModuleKey,
    val interactionType: InteractionType,
    val prompt: String,
    val correctAnswer: List<Int>,
    val options: List<String>,
    val explanationCorrect: String,
    val explanationIncorrect: String,
    val difficulty: Int = 1,
)

data class SeedErrorChallenge(
    val datasetKey: String,
    val chartType: ChartType,
    val displayedTitle: String,
    val errorType: GraphErrorType,
    val axisMinOverride: Double? = null,
    val unitPerIconOverride: Double? = null,
    val omittedCategoryLabel: String? = null,
    val explanation: String,
    val difficulty: Int = 1,
)

data class SeedComparison(
    val datasetKey: String,
    val chartTypeA: ChartType,
    val chartTypeB: ChartType,
    val question: String,
    val betterSide: String,
    val explanation: String,
    val difficulty: Int = 1,
)

data class SeedBadge(
    val code: String,
    val title: String,
    val description: String,
    val iconKey: String,
    val criteriaText: String,
)
