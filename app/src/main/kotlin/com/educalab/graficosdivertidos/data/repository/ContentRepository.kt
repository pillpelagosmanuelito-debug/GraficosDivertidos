package com.educalab.graficosdivertidos.data.repository

import com.educalab.graficosdivertidos.data.local.dao.ChartDao
import com.educalab.graficosdivertidos.data.local.dao.ComparisonDao
import com.educalab.graficosdivertidos.data.local.dao.DatasetDao
import com.educalab.graficosdivertidos.data.local.dao.GraphErrorDao
import com.educalab.graficosdivertidos.domain.model.ChartDefinitionModel
import com.educalab.graficosdivertidos.domain.model.ComparisonModel
import com.educalab.graficosdivertidos.domain.model.DatasetModel
import com.educalab.graficosdivertidos.domain.model.ErrorChallengeModel
import com.educalab.graficosdivertidos.domain.model.ExerciseModel
import com.educalab.graficosdivertidos.domain.model.ModuleKey
import kotlinx.coroutines.flow.first

/**
 * Acceso de solo lectura al contenido semilla (datasets, definiciones de
 * gráfico, ejercicios, retos del detective y del comparador), ya convertido
 * a modelos de dominio listos para la UI.
 */
class ContentRepository(
    private val datasetDao: DatasetDao,
    private val chartDao: ChartDao,
    private val graphErrorDao: GraphErrorDao,
    private val comparisonDao: ComparisonDao,
) {
    suspend fun getDataset(id: Long): DatasetModel? {
        val entity = datasetDao.getDataset(id) ?: return null
        val points = datasetDao.getPoints(id)
        return entity.toDomain(points)
    }

    suspend fun getAllDatasets(): List<DatasetModel> {
        // La cantidad de datasets semilla es pequeña (30), por lo que cargar
        // todo en memoria para la Galería/Constructor es aceptable.
        val entities = datasetDao.observeDatasets().first()
        return entities.map { entity -> entity.toDomain(datasetDao.getPoints(entity.id)) }
    }

    private suspend fun toChartDefinitionModel(defId: Long): ChartDefinitionModel? {
        val def = chartDao.getDefinition(defId) ?: return null
        val dataset = getDataset(def.datasetId) ?: return null
        return ChartDefinitionModel(
            id = def.id,
            dataset = dataset,
            chartType = def.chartType,
            title = def.title,
            showLabels = def.showLabels,
            showLegend = def.showLegend,
            axisMax = def.axisMax,
            moduleKey = def.moduleKey,
        )
    }

    suspend fun getExercisesForModule(moduleKey: ModuleKey): List<ExerciseModel> {
        return chartDao.getExercisesForModule(moduleKey).mapNotNull { exercise ->
            val chart = toChartDefinitionModel(exercise.chartDefinitionId) ?: return@mapNotNull null
            ExerciseModel(
                id = exercise.id,
                chart = chart,
                moduleKey = exercise.moduleKey,
                interactionType = exercise.interactionType,
                prompt = exercise.prompt,
                correctAnswer = exercise.correctAnswer,
                options = exercise.options,
                explanationCorrect = exercise.explanationCorrect,
                explanationIncorrect = exercise.explanationIncorrect,
                difficulty = exercise.difficulty,
            )
        }
    }

    /**
     * Cola de repaso para el módulo Desafíos: ejercicios que el usuario aún
     * no ha resuelto correctamente en ninguno de los 4 módulos base,
     * mezclados y acotados a un tamaño de sesión razonable (5-20 minutos).
     */
    suspend fun getPendingReviewExercises(userId: Long, maxItems: Int = 12): List<ExerciseModel> {
        val baseModules = listOf(
            com.educalab.graficosdivertidos.domain.model.ModuleKey.BARRAS,
            com.educalab.graficosdivertidos.domain.model.ModuleKey.PICTOGRAMAS,
            com.educalab.graficosdivertidos.domain.model.ModuleKey.LINEAS,
            com.educalab.graficosdivertidos.domain.model.ModuleKey.CIRCULAR,
        )
        val pendingIds = baseModules.flatMap { chartDao.getPendingOrFailedExerciseIds(userId, it) }
        val chosen = if (pendingIds.size > maxItems) pendingIds.shuffled().take(maxItems) else pendingIds.shuffled()
        return chosen.mapNotNull { getExercise(it) }
    }

    suspend fun getExercise(id: Long): ExerciseModel? {
        val exercise = chartDao.getExercise(id) ?: return null
        val chart = toChartDefinitionModel(exercise.chartDefinitionId) ?: return null
        return ExerciseModel(
            id = exercise.id,
            chart = chart,
            moduleKey = exercise.moduleKey,
            interactionType = exercise.interactionType,
            prompt = exercise.prompt,
            correctAnswer = exercise.correctAnswer,
            options = exercise.options,
            explanationCorrect = exercise.explanationCorrect,
            explanationIncorrect = exercise.explanationIncorrect,
            difficulty = exercise.difficulty,
        )
    }

    suspend fun getErrorChallenges(): List<ErrorChallengeModel> {
        return graphErrorDao.getChallenges().mapNotNull { challenge ->
            val dataset = getDataset(challenge.datasetId) ?: return@mapNotNull null
            ErrorChallengeModel(
                id = challenge.id,
                dataset = dataset,
                chartType = challenge.chartType,
                displayedTitle = challenge.displayedTitle,
                errorType = challenge.errorType,
                axisMinOverride = challenge.axisMinOverride,
                unitPerIconOverride = challenge.unitPerIconOverride,
                omittedCategoryLabel = challenge.omittedCategoryLabel,
                explanation = challenge.explanation,
                difficulty = challenge.difficulty,
            )
        }
    }

    suspend fun getErrorChallenge(id: Long): ErrorChallengeModel? {
        val challenge = graphErrorDao.getChallenge(id) ?: return null
        val dataset = getDataset(challenge.datasetId) ?: return null
        return ErrorChallengeModel(
            id = challenge.id,
            dataset = dataset,
            chartType = challenge.chartType,
            displayedTitle = challenge.displayedTitle,
            errorType = challenge.errorType,
            axisMinOverride = challenge.axisMinOverride,
            unitPerIconOverride = challenge.unitPerIconOverride,
            omittedCategoryLabel = challenge.omittedCategoryLabel,
            explanation = challenge.explanation,
            difficulty = challenge.difficulty,
        )
    }

    suspend fun getComparisons(): List<ComparisonModel> {
        return comparisonDao.observeChallenges().first().mapNotNull { challenge ->
            val dataset = getDataset(challenge.datasetId) ?: return@mapNotNull null
            ComparisonModel(
                id = challenge.id,
                dataset = dataset,
                chartTypeA = challenge.chartTypeA,
                chartTypeB = challenge.chartTypeB,
                question = challenge.question,
                betterSide = challenge.betterSide,
                explanation = challenge.explanation,
                difficulty = challenge.difficulty,
            )
        }
    }

    suspend fun getComparison(id: Long): ComparisonModel? {
        val challenge = comparisonDao.getChallenge(id) ?: return null
        val dataset = getDataset(challenge.datasetId) ?: return null
        return ComparisonModel(
            id = challenge.id,
            dataset = dataset,
            chartTypeA = challenge.chartTypeA,
            chartTypeB = challenge.chartTypeB,
            question = challenge.question,
            betterSide = challenge.betterSide,
            explanation = challenge.explanation,
            difficulty = challenge.difficulty,
        )
    }
}
