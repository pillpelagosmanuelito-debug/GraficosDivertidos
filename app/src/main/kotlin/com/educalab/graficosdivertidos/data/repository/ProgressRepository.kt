package com.educalab.graficosdivertidos.data.repository

import androidx.room.withTransaction
import com.educalab.graficosdivertidos.data.local.AppDatabase
import com.educalab.graficosdivertidos.data.local.dao.BadgeDao
import com.educalab.graficosdivertidos.data.local.dao.ChartDao
import com.educalab.graficosdivertidos.data.local.dao.ComparisonDao
import com.educalab.graficosdivertidos.data.local.dao.GraphErrorDao
import com.educalab.graficosdivertidos.data.local.dao.ProfileDao
import com.educalab.graficosdivertidos.data.local.dao.ProgressDao
import com.educalab.graficosdivertidos.data.local.entity.ChartAttemptEntity
import com.educalab.graficosdivertidos.data.local.entity.ComparisonAttemptEntity
import com.educalab.graficosdivertidos.data.local.entity.GraphErrorAttemptEntity
import com.educalab.graficosdivertidos.data.local.entity.ProgressEntity
import com.educalab.graficosdivertidos.data.local.entity.UserBadgeEntity
import com.educalab.graficosdivertidos.data.local.entity.UserStatsEntity
import com.educalab.graficosdivertidos.domain.logic.GamificationEngine
import com.educalab.graficosdivertidos.domain.model.AttemptResult
import com.educalab.graficosdivertidos.domain.model.BadgeModel
import com.educalab.graficosdivertidos.domain.model.ExerciseModel
import com.educalab.graficosdivertidos.domain.model.GraphErrorType
import com.educalab.graficosdivertidos.domain.model.ModuleKey
import com.educalab.graficosdivertidos.domain.model.ModuleSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

private val BASE_MODULES = listOf(ModuleKey.BARRAS, ModuleKey.PICTOGRAMAS, ModuleKey.LINEAS, ModuleKey.CIRCULAR)

/**
 * Registra intentos (ejercicios, retos del detective y del comparador) y
 * recalcula, en una única transacción de Room, el progreso por módulo, las
 * estadísticas agregadas del usuario y las insignias que corresponda
 * desbloquear. Toda recompensa nace de una acción real guardada en la base
 * de datos: nada se otorga fuera de esta ruta.
 */
class ProgressRepository(
    private val db: AppDatabase,
    private val chartDao: ChartDao,
    private val graphErrorDao: GraphErrorDao,
    private val comparisonDao: ComparisonDao,
    private val progressDao: ProgressDao,
    private val badgeDao: BadgeDao,
    private val profileDao: ProfileDao,
) {

    suspend fun recordExerciseAttempt(
        userId: Long,
        exercise: ExerciseModel,
        selectedAnswer: List<Int>,
        isCorrect: Boolean,
        now: Long,
    ): AttemptResult = db.withTransaction {
        val previousAttempts = chartDao.getAttemptsForExercise(userId, exercise.id)
        val firstTry = previousAttempts.isEmpty()
        val xp = GamificationEngine.xpForAttempt(isCorrect, firstTry, exercise.difficulty)

        chartDao.insertAttempt(
            ChartAttemptEntity(
                exerciseId = exercise.id,
                userId = userId,
                selectedAnswer = selectedAnswer,
                isCorrect = isCorrect,
                firstTry = firstTry,
                attemptAt = now,
                xpAwarded = xp,
            ),
        )

        recomputeModuleProgress(userId, exercise.moduleKey, now)
        val newBadges = recomputeUserStatsAndBadges(userId, xp, now)

        AttemptResult(
            isCorrect = isCorrect,
            explanation = if (isCorrect) exercise.explanationCorrect else exercise.explanationIncorrect,
            xpAwarded = xp,
            newlyEarnedBadges = newBadges,
        )
    }

    suspend fun recordErrorChallengeAttempt(
        userId: Long,
        challengeId: Long,
        selectedError: GraphErrorType,
        isCorrect: Boolean,
        explanation: String,
        difficulty: Int,
        now: Long,
    ): AttemptResult = db.withTransaction {
        val xp = GamificationEngine.xpForAttempt(isCorrect, firstTry = true, difficulty = difficulty)
        graphErrorDao.insertAttempt(
            GraphErrorAttemptEntity(
                challengeId = challengeId,
                userId = userId,
                selectedErrorType = selectedError,
                isCorrect = isCorrect,
                attemptAt = now,
                xpAwarded = xp,
            ),
        )
        recomputeDetectiveProgress(userId, now)
        val newBadges = recomputeUserStatsAndBadges(userId, xp, now)
        AttemptResult(isCorrect, explanation, xp, newBadges)
    }

    suspend fun recordComparisonAttempt(
        userId: Long,
        challengeId: Long,
        selectedSide: String,
        isCorrect: Boolean,
        explanation: String,
        difficulty: Int,
        now: Long,
    ): AttemptResult = db.withTransaction {
        val xp = GamificationEngine.xpForAttempt(isCorrect, firstTry = true, difficulty = difficulty)
        comparisonDao.insertAttempt(
            ComparisonAttemptEntity(
                challengeId = challengeId,
                userId = userId,
                selectedSide = selectedSide,
                isCorrect = isCorrect,
                attemptAt = now,
                xpAwarded = xp,
            ),
        )
        recomputeComparatorProgress(userId, now)
        val newBadges = recomputeUserStatsAndBadges(userId, xp, now)
        AttemptResult(isCorrect, explanation, xp, newBadges)
    }

    /** Se llama tras guardar un gráfico en el Constructor (no hay "acierto/error", pero sí progreso). */
    suspend fun recomputeBuilderProgress(userId: Long, now: Long) = db.withTransaction {
        val saved = chartDao.countConfigurationsForUser(userId)
        val total = 8 // meta de referencia: 8 gráficos guardados para "completar" el módulo
        val state = GamificationEngine.computeModuleState(
            isUnlocked = true,
            completedCount = saved.coerceAtMost(total),
            totalCount = total,
            accuracy = 1.0,
        )
        progressDao.upsertProgress(
            ProgressEntity(
                userId = userId,
                moduleKey = ModuleKey.CONSTRUCTOR,
                completedCount = saved.coerceAtMost(total),
                totalCount = total,
                correctCount = saved.coerceAtMost(total),
                attemptsCount = saved,
                state = state,
                updatedAt = now,
            ),
        )
        recomputeUserStatsAndBadges(userId, xpDelta = 0, now = now)
        Unit
    }

    private suspend fun recomputeModuleProgress(userId: Long, moduleKey: ModuleKey, now: Long) {
        val total = chartDao.getExercisesForModule(moduleKey).size
        val completed = chartDao.countCompletedExercisesInModule(userId, moduleKey)
        val attempts = chartDao.countAttemptsInModule(userId, moduleKey)
        val correct = chartDao.countCorrectInModule(userId, moduleKey)
        val accuracy = GamificationEngine.accuracy(correct, attempts)
        val state = GamificationEngine.computeModuleState(true, completed, total, accuracy)
        progressDao.upsertProgress(
            ProgressEntity(userId, moduleKey, completed, total, correct, attempts, state, now),
        )
    }

    private suspend fun recomputeDetectiveProgress(userId: Long, now: Long) {
        val total = graphErrorDao.countChallenges()
        val completed = graphErrorDao.countDistinctSolvedByUser(userId)
        val attempts = graphErrorDao.countAttemptsByUser(userId)
        val correct = graphErrorDao.countSolvedByUser(userId)
        val accuracy = GamificationEngine.accuracy(correct, attempts)
        val state = GamificationEngine.computeModuleState(true, completed, total, accuracy)
        progressDao.upsertProgress(
            ProgressEntity(userId, ModuleKey.DETECTIVE, completed, total, correct, attempts, state, now),
        )
    }

    private suspend fun recomputeComparatorProgress(userId: Long, now: Long) {
        val total = comparisonDao.countChallenges()
        val completed = comparisonDao.countDistinctSolvedByUser(userId)
        val correct = comparisonDao.countSolvedByUser(userId)
        val accuracy = GamificationEngine.accuracy(correct, completed.coerceAtLeast(correct))
        val state = GamificationEngine.computeModuleState(true, completed, total, accuracy)
        progressDao.upsertProgress(
            ProgressEntity(userId, ModuleKey.COMPARADOR, completed, total, correct, completed, state, now),
        )
    }

    private suspend fun recomputeUserStatsAndBadges(userId: Long, xpDelta: Int, now: Long): List<String> {
        val results = chartDao.getAllResultsOrderedByTime(userId)
        val currentStreak = GamificationEngine.currentStreak(results)
        val bestStreakInHistory = GamificationEngine.bestStreak(results)
        val previousStats = profileDao.getStats(userId)
        val newTotalXp = (previousStats?.totalXp ?: 0) + xpDelta
        val newBestStreak = maxOf(previousStats?.bestStreak ?: 0, bestStreakInHistory)
        val exercisesCompleted = chartDao.countDistinctCompletedExercisesTotal(userId)
        val firstTryCorrect = chartDao.countFirstTryCorrectTotal(userId)

        val barsProgress = progressDao.getProgress(userId, ModuleKey.BARRAS)
        val modulesMastered = BASE_MODULES.count {
            progressDao.getProgress(userId, it)?.state == com.educalab.graficosdivertidos.domain.model.ModuleState.DOMINADO
        }
        val allBaseCompleted = BASE_MODULES.all {
            val p = progressDao.getProgress(userId, it)
            p != null && p.totalCount > 0 && p.completedCount >= p.totalCount
        }
        val errorSolved = graphErrorDao.countDistinctSolvedByUser(userId)
        val builderSaved = chartDao.countConfigurationsForUser(userId)
        val comparisonsSolved = comparisonDao.countDistinctSolvedByUser(userId)
        val perfectModules = BASE_MODULES.count {
            val p = progressDao.getProgress(userId, it)
            p != null && p.totalCount > 0 && p.completedCount >= p.totalCount && p.attemptsCount == p.completedCount
        }

        val totalStars = firstTryCorrect

        val stats = UserStatsEntity(
            userId = userId,
            totalXp = newTotalXp,
            totalStars = totalStars,
            currentStreak = currentStreak,
            bestStreak = newBestStreak,
            exercisesCompleted = exercisesCompleted,
            updatedAt = now,
        )
        profileDao.upsertStats(stats)

        val snapshot = GamificationEngine.UserStatsSnapshot(
            exercisesCompleted = exercisesCompleted,
            exercisesCorrectFirstTry = firstTryCorrect,
            barModuleMastered = barsProgress?.state == com.educalab.graficosdivertidos.domain.model.ModuleState.DOMINADO,
            errorChallengesSolved = errorSolved,
            builderChartsSaved = builderSaved,
            comparisonsCompleted = comparisonsSolved,
            bestStreakEver = newBestStreak,
            modulesMastered = modulesMastered,
            perfectSessionsCount = perfectModules,
            allModulesCompleted = allBaseCompleted && errorSolved >= 30 && comparisonsSolved >= 20 && builderSaved >= 5,
        )
        val alreadyEarned = badgeDao.getUnlockedCodes(userId).toSet()
        val newlyEarned = GamificationEngine.evaluateNewlyEarnedBadges(snapshot, alreadyEarned)
        newlyEarned.forEach { code ->
            badgeDao.unlockBadge(UserBadgeEntity(userId = userId, badgeCode = code, unlockedAt = now))
        }
        return newlyEarned
    }

    private val moduleTitles: Map<ModuleKey, String> = mapOf(
        ModuleKey.BARRAS to "Barras",
        ModuleKey.PICTOGRAMAS to "Pictogramas",
        ModuleKey.LINEAS to "Líneas",
        ModuleKey.CIRCULAR to "Circular",
        ModuleKey.CONSTRUCTOR to "Constructor",
        ModuleKey.COMPARADOR to "Comparador",
        ModuleKey.DETECTIVE to "Detective",
        ModuleKey.DESAFIOS to "Desafíos",
    )

    private val advancedModules = setOf(ModuleKey.CONSTRUCTOR, ModuleKey.COMPARADOR, ModuleKey.DETECTIVE, ModuleKey.DESAFIOS)

    /**
     * Los 4 módulos base siempre están disponibles desde el inicio (hay
     * contenido suficiente para empezar de inmediato). Los módulos
     * avanzados (Constructor, Comparador, Detective, Desafíos) se
     * desbloquean en cuanto el usuario haya iniciado al menos un módulo
     * base: así la progresión se siente real sin retrasar la diversión.
     */
    fun observeModuleSummaries(userId: Long): Flow<List<ModuleSummary>> {
        return progressDao.observeProgressForUser(userId).map { entries ->
            val byModule = entries.associateBy { it.moduleKey }
            val completedBasics = BASE_MODULES.count { (byModule[it]?.completedCount ?: 0) > 0 }
            ModuleKey.entries.map { key ->
                val p = byModule[key]
                val isUnlocked = key !in advancedModules || GamificationEngine.isAdvancedModuleUnlocked(completedBasics)
                val state = when {
                    !isUnlocked -> com.educalab.graficosdivertidos.domain.model.ModuleState.BLOQUEADO
                    else -> p?.state ?: com.educalab.graficosdivertidos.domain.model.ModuleState.DISPONIBLE
                }
                ModuleSummary(
                    moduleKey = key,
                    title = moduleTitles.getValue(key),
                    state = state,
                    completedCount = p?.completedCount ?: 0,
                    totalCount = p?.totalCount ?: 0,
                    accuracy = GamificationEngine.accuracy(p?.correctCount ?: 0, p?.attemptsCount ?: 0),
                )
            }
        }
    }

    fun observeBadges(userId: Long): Flow<List<BadgeModel>> {
        return badgeDao.observeAllBadges().combine(badgeDao.observeUnlockedForUser(userId)) { all, unlocked ->
            val unlockedMap = unlocked.associateBy { it.badgeCode }
            all.map { badge ->
                val ub = unlockedMap[badge.code]
                BadgeModel(
                    code = badge.code,
                    title = badge.title,
                    description = badge.description,
                    iconKey = badge.iconKey,
                    criteriaText = badge.criteriaText,
                    unlocked = ub != null,
                    unlockedAt = ub?.unlockedAt,
                )
            }
        }
    }
}
