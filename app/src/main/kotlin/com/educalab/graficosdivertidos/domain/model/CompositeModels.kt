package com.educalab.graficosdivertidos.domain.model

/** Vistas combinadas (join en memoria) usadas por la capa de UI. */

data class ChartDefinitionModel(
    val id: Long,
    val dataset: DatasetModel,
    val chartType: ChartType,
    val title: String,
    val showLabels: Boolean,
    val showLegend: Boolean,
    val axisMax: Double?,
    val moduleKey: ModuleKey,
)

data class ExerciseModel(
    val id: Long,
    val chart: ChartDefinitionModel,
    val moduleKey: ModuleKey,
    val interactionType: InteractionType,
    val prompt: String,
    val correctAnswer: List<Int>,
    val options: List<String>,
    val explanationCorrect: String,
    val explanationIncorrect: String,
    val difficulty: Int,
)

data class ErrorChallengeModel(
    val id: Long,
    val dataset: DatasetModel,
    val chartType: ChartType,
    val displayedTitle: String,
    val errorType: GraphErrorType,
    val axisMinOverride: Double?,
    val unitPerIconOverride: Double?,
    val omittedCategoryLabel: String?,
    val explanation: String,
    val difficulty: Int,
) {
    /** Las 6 opciones posibles se muestran siempre todas; el usuario elige una. */
    val allErrorOptions: List<GraphErrorType> get() = GraphErrorType.entries
}

data class ComparisonModel(
    val id: Long,
    val dataset: DatasetModel,
    val chartTypeA: ChartType,
    val chartTypeB: ChartType,
    val question: String,
    val betterSide: String,
    val explanation: String,
    val difficulty: Int,
)

data class AttemptResult(
    val isCorrect: Boolean,
    val explanation: String,
    val xpAwarded: Int,
    val newlyEarnedBadges: List<String> = emptyList(),
)

data class ModuleSummary(
    val moduleKey: ModuleKey,
    val title: String,
    val state: ModuleState,
    val completedCount: Int,
    val totalCount: Int,
    val accuracy: Double,
)

data class BadgeModel(
    val code: String,
    val title: String,
    val description: String,
    val iconKey: String,
    val criteriaText: String,
    val unlocked: Boolean,
    val unlockedAt: Long?,
)

data class UserStatsModel(
    val totalXp: Int,
    val level: Int,
    val levelProgress: Float,
    val totalStars: Int,
    val currentStreak: Int,
    val bestStreak: Int,
    val exercisesCompleted: Int,
)
