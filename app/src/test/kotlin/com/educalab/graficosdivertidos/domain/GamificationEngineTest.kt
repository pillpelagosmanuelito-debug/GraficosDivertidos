package com.educalab.graficosdivertidos.domain

import com.educalab.graficosdivertidos.domain.logic.GamificationEngine
import com.educalab.graficosdivertidos.domain.model.ModuleState
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class GamificationEngineTest {

    @Test
    fun `levelForXp devuelve nivel 1 con cero xp`() {
        assertThat(GamificationEngine.levelForXp(0)).isEqualTo(1)
    }

    @Test
    fun `levelForXp sube de nivel al alcanzar el umbral`() {
        val xpForLevel2 = GamificationEngine.xpRequiredForLevel(2)
        assertThat(GamificationEngine.levelForXp(xpForLevel2)).isEqualTo(2)
        assertThat(GamificationEngine.levelForXp(xpForLevel2 - 1)).isEqualTo(1)
    }

    @Test
    fun `levelProgressFraction esta entre 0 y 1`() {
        val fraction = GamificationEngine.levelProgressFraction(75)
        assertThat(fraction).isAtLeast(0f)
        assertThat(fraction).isAtMost(1f)
    }

    @Test
    fun `xpForAttempt otorga mas xp en primer intento que en reintento`() {
        val firstTry = GamificationEngine.xpForAttempt(correct = true, firstTry = true, difficulty = 2)
        val retry = GamificationEngine.xpForAttempt(correct = true, firstTry = false, difficulty = 2)
        assertThat(firstTry).isGreaterThan(retry)
    }

    @Test
    fun `xpForAttempt otorga xp minimo de participacion si la respuesta es incorrecta`() {
        val xp = GamificationEngine.xpForAttempt(correct = false, firstTry = true, difficulty = 3)
        assertThat(xp).isEqualTo(1)
    }

    @Test
    fun `computeModuleState bloqueado cuando no esta desbloqueado`() {
        val state = GamificationEngine.computeModuleState(isUnlocked = false, completedCount = 5, totalCount = 5, accuracy = 1.0)
        assertThat(state).isEqualTo(ModuleState.BLOQUEADO)
    }

    @Test
    fun `computeModuleState disponible sin intentos`() {
        val state = GamificationEngine.computeModuleState(isUnlocked = true, completedCount = 0, totalCount = 10, accuracy = 0.0)
        assertThat(state).isEqualTo(ModuleState.DISPONIBLE)
    }

    @Test
    fun `computeModuleState iniciado con progreso parcial`() {
        val state = GamificationEngine.computeModuleState(isUnlocked = true, completedCount = 3, totalCount = 10, accuracy = 0.5)
        assertThat(state).isEqualTo(ModuleState.INICIADO)
    }

    @Test
    fun `computeModuleState dominado con todo completo y buena precision`() {
        val state = GamificationEngine.computeModuleState(isUnlocked = true, completedCount = 10, totalCount = 10, accuracy = 0.9)
        assertThat(state).isEqualTo(ModuleState.DOMINADO)
    }

    @Test
    fun `computeModuleState completado con todo terminado pero precision baja`() {
        val state = GamificationEngine.computeModuleState(isUnlocked = true, completedCount = 10, totalCount = 10, accuracy = 0.5)
        assertThat(state).isEqualTo(ModuleState.COMPLETADO)
    }

    @Test
    fun `currentStreak cuenta los aciertos finales consecutivos`() {
        val streak = GamificationEngine.currentStreak(listOf(false, true, true, true))
        assertThat(streak).isEqualTo(3)
    }

    @Test
    fun `currentStreak es cero si el ultimo intento fallo`() {
        val streak = GamificationEngine.currentStreak(listOf(true, true, false))
        assertThat(streak).isEqualTo(0)
    }

    @Test
    fun `bestStreak encuentra la racha mas larga aunque no sea la actual`() {
        val best = GamificationEngine.bestStreak(listOf(true, true, true, false, true))
        assertThat(best).isEqualTo(3)
    }

    @Test
    fun `accuracy con cero intentos no lanza division por cero`() {
        assertThat(GamificationEngine.accuracy(0, 0)).isEqualTo(0.0)
    }

    @Test
    fun `accuracy calcula la proporcion de aciertos`() {
        assertThat(GamificationEngine.accuracy(3, 4)).isEqualTo(0.75)
    }

    @Test
    fun `evaluateNewlyEarnedBadges no repite insignias ya obtenidas`() {
        val snapshot = GamificationEngine.UserStatsSnapshot(
            exercisesCompleted = 5, exercisesCorrectFirstTry = 5, barModuleMastered = false,
            errorChallengesSolved = 0, builderChartsSaved = 0, comparisonsCompleted = 0,
            bestStreakEver = 0, modulesMastered = 0, perfectSessionsCount = 0, allModulesCompleted = false,
        )
        val newly = GamificationEngine.evaluateNewlyEarnedBadges(snapshot, alreadyEarned = setOf("badge_primer_grafico"))
        assertThat(newly).doesNotContain("badge_primer_grafico")
    }

    @Test
    fun `evaluateNewlyEarnedBadges otorga la insignia de racha al llegar a 5`() {
        val snapshot = GamificationEngine.UserStatsSnapshot(
            exercisesCompleted = 0, exercisesCorrectFirstTry = 0, barModuleMastered = false,
            errorChallengesSolved = 0, builderChartsSaved = 0, comparisonsCompleted = 0,
            bestStreakEver = 5, modulesMastered = 0, perfectSessionsCount = 0, allModulesCompleted = false,
        )
        val newly = GamificationEngine.evaluateNewlyEarnedBadges(snapshot, alreadyEarned = emptySet())
        assertThat(newly).contains("badge_racha_5")
    }

    @Test
    fun `isAdvancedModuleUnlocked exige al menos un modulo base completado`() {
        assertThat(GamificationEngine.isAdvancedModuleUnlocked(0)).isFalse()
        assertThat(GamificationEngine.isAdvancedModuleUnlocked(1)).isTrue()
    }

    @Test
    fun `confidenceScore con cero intentos es cero`() {
        assertThat(GamificationEngine.confidenceScore(0, 0)).isEqualTo(0)
    }

    @Test
    fun `confidenceScore nunca supera 100`() {
        assertThat(GamificationEngine.confidenceScore(50, 50)).isAtMost(100)
    }
}
