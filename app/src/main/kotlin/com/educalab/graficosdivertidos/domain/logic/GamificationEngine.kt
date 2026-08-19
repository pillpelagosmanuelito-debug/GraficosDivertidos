package com.educalab.graficosdivertidos.domain.logic

import com.educalab.graficosdivertidos.domain.model.ModuleState
import kotlin.math.max
import kotlin.math.sqrt

/**
 * Reglas de progreso y gamificación: niveles por XP, estado visual de cada
 * módulo y condiciones reales (no decorativas) para desbloquear insignias.
 * Todo se calcula a partir de acciones registradas por el usuario; nada se
 * concede "porque sí".
 */
object GamificationEngine {

    /** XP necesaria acumulada para alcanzar el nivel [level] (secuencia creciente cuadrática). */
    fun xpRequiredForLevel(level: Int): Int {
        require(level >= 1)
        return 50 * level * (level + 1) / 2
    }

    /** Nivel actual a partir del XP total acumulado (nivel 1 en adelante). */
    fun levelForXp(totalXp: Int): Int {
        var level = 1
        while (xpRequiredForLevel(level + 1) <= totalXp) level++
        return level
    }

    /** Progreso (0f..1f) dentro del nivel actual, para dibujar una barra de XP. */
    fun levelProgressFraction(totalXp: Int): Float {
        val level = levelForXp(totalXp)
        val floor = xpRequiredForLevel(level)
        val ceiling = xpRequiredForLevel(level + 1)
        if (ceiling <= floor) return 1f
        return ((totalXp - floor).toFloat() / (ceiling - floor)).coerceIn(0f, 1f)
    }

    /** XP otorgada por completar un ítem, según si acertó a la primera y su dificultad. */
    fun xpForAttempt(correct: Boolean, firstTry: Boolean, difficulty: Int): Int {
        if (!correct) return 1 // pequeño XP de participación, nunca castigo
        val base = 8 + difficulty * 3
        return if (firstTry) base else max(3, base / 2)
    }

    /**
     * Estado visual de un módulo. "Dominado" exige terminar todo el
     * contenido disponible con al menos 85% de aciertos; "Completado" solo
     * exige terminar todo; "Iniciado" cuando hay progreso parcial.
     */
    fun computeModuleState(
        isUnlocked: Boolean,
        completedCount: Int,
        totalCount: Int,
        accuracy: Double,
    ): ModuleState {
        if (!isUnlocked) return ModuleState.BLOQUEADO
        if (totalCount <= 0) return ModuleState.DISPONIBLE
        return when {
            completedCount <= 0 -> ModuleState.DISPONIBLE
            completedCount < totalCount -> ModuleState.INICIADO
            accuracy >= 0.85 -> ModuleState.DOMINADO
            else -> ModuleState.COMPLETADO
        }
    }

    /**
     * Un módulo secundario (constructor, comparador, detective, desafíos) se
     * desbloquea cuando el usuario terminó al menos [minCompletedBasics] de
     * los 4 módulos base de tipos de gráfico. Devuelve true/false, nunca
     * bloquea por tiempo ni por pago.
     */
    fun isAdvancedModuleUnlocked(completedBasicModules: Int, minCompletedBasics: Int = 1): Boolean {
        return completedBasicModules >= minCompletedBasics
    }

    /** Racha de aciertos consecutivos a partir de una secuencia cronológica de resultados. */
    fun currentStreak(resultsOldestFirst: List<Boolean>): Int {
        var streak = 0
        for (result in resultsOldestFirst.asReversed()) {
            if (result) streak++ else break
        }
        return streak
    }

    fun bestStreak(resultsOldestFirst: List<Boolean>): Int {
        var best = 0
        var current = 0
        for (result in resultsOldestFirst) {
            current = if (result) current + 1 else 0
            best = max(best, current)
        }
        return best
    }

    /** Precisión global (0.0..1.0); devuelve 0.0 con 0 intentos para evitar NaN. */
    fun accuracy(correct: Int, total: Int): Double {
        if (total <= 0) return 0.0
        return correct.toDouble() / total.toDouble()
    }

    /**
     * Snapshot inmutable de estadísticas del usuario usado para evaluar
     * insignias. Se construye en el repositorio a partir de datos reales de
     * Room (nunca en memoria efímera).
     */
    data class UserStatsSnapshot(
        val exercisesCompleted: Int,
        val exercisesCorrectFirstTry: Int,
        val barModuleMastered: Boolean,
        val errorChallengesSolved: Int,
        val builderChartsSaved: Int,
        val comparisonsCompleted: Int,
        val bestStreakEver: Int,
        val modulesMastered: Int,
        val perfectSessionsCount: Int,
        val allModulesCompleted: Boolean,
    )

    /** Reglas de insignias: código -> condición real basada en el snapshot. */
    private val badgeRules: List<Pair<String, (UserStatsSnapshot) -> Boolean>> = listOf(
        "badge_primer_grafico" to { s -> s.exercisesCompleted >= 1 },
        "badge_maestro_barras" to { s -> s.barModuleMastered },
        "badge_ojo_de_lince" to { s -> s.errorChallengesSolved >= 10 },
        "badge_constructor_experto" to { s -> s.builderChartsSaved >= 5 },
        "badge_detective_grafico" to { s -> s.errorChallengesSolved >= 20 },
        "badge_comparador_agudo" to { s -> s.comparisonsCompleted >= 10 },
        "badge_racha_5" to { s -> s.bestStreakEver >= 5 },
        "badge_explorador_datos" to { s -> s.exercisesCompleted >= 25 },
        "badge_precision_total" to { s -> s.perfectSessionsCount >= 1 },
        "badge_leyenda_del_estudio" to { s -> s.allModulesCompleted && s.modulesMastered >= 4 },
    )

    /** Devuelve los códigos de insignia que corresponden otorgar y aún no están en [alreadyEarned]. */
    fun evaluateNewlyEarnedBadges(snapshot: UserStatsSnapshot, alreadyEarned: Set<String>): List<String> {
        return badgeRules
            .filter { (code, rule) -> code !in alreadyEarned && rule(snapshot) }
            .map { it.first }
    }

    /** Utilidad para mostrar "nivel de confianza" en el comparador (0..100) a partir de aciertos. */
    fun confidenceScore(correct: Int, total: Int): Int {
        if (total <= 0) return 0
        val raw = accuracy(correct, total) * 100.0
        // suaviza los extremos con muestras pequeñas usando una raíz para no mostrar 100% con 1 intento
        val sampleFactor = sqrt(total.coerceAtMost(10).toDouble() / 10.0)
        return (raw * (0.6 + 0.4 * sampleFactor)).toInt().coerceIn(0, 100)
    }
}
