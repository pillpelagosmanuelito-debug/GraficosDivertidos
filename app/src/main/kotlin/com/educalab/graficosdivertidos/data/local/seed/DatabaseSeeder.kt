package com.educalab.graficosdivertidos.data.local.seed

import com.educalab.graficosdivertidos.data.local.dao.BadgeDao
import com.educalab.graficosdivertidos.data.local.dao.ChartDao
import com.educalab.graficosdivertidos.data.local.dao.ComparisonDao
import com.educalab.graficosdivertidos.data.local.dao.DatasetDao
import com.educalab.graficosdivertidos.data.local.dao.GraphErrorDao
import com.educalab.graficosdivertidos.data.local.entity.BadgeEntity
import com.educalab.graficosdivertidos.data.local.entity.ChartDefinitionEntity
import com.educalab.graficosdivertidos.data.local.entity.ChartExerciseEntity
import com.educalab.graficosdivertidos.data.local.entity.ComparisonChallengeEntity
import com.educalab.graficosdivertidos.data.local.entity.DataPointEntity
import com.educalab.graficosdivertidos.data.local.entity.DatasetEntity
import com.educalab.graficosdivertidos.data.local.entity.GraphErrorChallengeEntity

/**
 * Vuelca [SeedContent] a la base de datos Room la primera vez que se abre la
 * app, para que una instalación nueva ya se sienta completa (30 datasets, 50
 * retos interpretativos, 30 gráficos con errores, 20 comparaciones y 10
 * insignias). Es idempotente: si ya hay datasets, no vuelve a insertar nada.
 */
class DatabaseSeeder(
    private val datasetDao: DatasetDao,
    private val chartDao: ChartDao,
    private val graphErrorDao: GraphErrorDao,
    private val comparisonDao: ComparisonDao,
    private val badgeDao: BadgeDao,
) {
    suspend fun seedIfNeeded() {
        if (datasetDao.countDatasets() > 0) return

        // 1) Datasets + puntos, guardando el id real asignado por Room por cada key.
        val datasetIdByKey = HashMap<String, Long>()
        for (seedDataset in SeedContent.datasets) {
            val entity = DatasetEntity(
                title = seedDataset.title,
                category = seedDataset.category,
                unit = seedDataset.unit,
                iconKey = seedDataset.iconKey,
                isSeed = true,
            )
            val points = seedDataset.points.mapIndexed { index, (label, value) ->
                DataPointEntity(datasetId = 0L, label = label, value = value, orderIndex = index)
            }
            val id = datasetDao.insertDatasetWithPoints(entity, points)
            datasetIdByKey[seedDataset.key] = id
        }

        // 2) Definiciones de gráfico (referencian datasets por id real).
        val defIdByKey = HashMap<String, Long>()
        val defEntities = SeedContent.chartDefinitions.map {
            ChartDefinitionEntity(
                datasetId = datasetIdByKey.getValue(it.datasetKey),
                chartType = it.chartType,
                title = it.title,
                moduleKey = it.moduleKey,
                axisMax = it.axisMax,
            )
        }
        val defIds = chartDao.insertDefinitions(defEntities)
        SeedContent.chartDefinitions.forEachIndexed { index, seedDef ->
            defIds.getOrNull(index)?.let { defIdByKey[seedDef.key] = it }
        }

        // 3) Ejercicios interpretativos, uno por chartDefinitionKey.
        val exerciseEntities = SeedContent.exercises.mapNotNull { seedExercise ->
            val defId = defIdByKey[seedExercise.chartDefinitionKey] ?: return@mapNotNull null
            ChartExerciseEntity(
                chartDefinitionId = defId,
                moduleKey = seedExercise.moduleKey,
                interactionType = seedExercise.interactionType,
                prompt = seedExercise.prompt,
                correctAnswer = seedExercise.correctAnswer,
                options = seedExercise.options,
                explanationCorrect = seedExercise.explanationCorrect,
                explanationIncorrect = seedExercise.explanationIncorrect,
                difficulty = seedExercise.difficulty,
            )
        }
        chartDao.insertExercises(exerciseEntities)

        // 4) Gráficos deliberadamente engañosos para el Detective.
        val errorEntities = SeedContent.errorChallenges.map {
            GraphErrorChallengeEntity(
                datasetId = datasetIdByKey.getValue(it.datasetKey),
                chartType = it.chartType,
                displayedTitle = it.displayedTitle,
                errorType = it.errorType,
                axisMinOverride = it.axisMinOverride,
                unitPerIconOverride = it.unitPerIconOverride,
                omittedCategoryLabel = it.omittedCategoryLabel,
                explanation = it.explanation,
                difficulty = it.difficulty,
            )
        }
        graphErrorDao.insertChallenges(errorEntities)

        // 5) Retos del Comparador.
        val comparisonEntities = SeedContent.comparisons.map {
            ComparisonChallengeEntity(
                datasetId = datasetIdByKey.getValue(it.datasetKey),
                chartTypeA = it.chartTypeA,
                chartTypeB = it.chartTypeB,
                question = it.question,
                betterSide = it.betterSide,
                explanation = it.explanation,
                difficulty = it.difficulty,
            )
        }
        comparisonDao.insertChallenges(comparisonEntities)

        // 6) Catálogo de insignias (el desbloqueo real se calcula en GamificationEngine).
        val badgeEntities = SeedContent.badges.map {
            BadgeEntity(
                code = it.code,
                title = it.title,
                description = it.description,
                iconKey = it.iconKey,
                criteriaText = it.criteriaText,
            )
        }
        badgeDao.insertBadges(badgeEntities)
    }
}
