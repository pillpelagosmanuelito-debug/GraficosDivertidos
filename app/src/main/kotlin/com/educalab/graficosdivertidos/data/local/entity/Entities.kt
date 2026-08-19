package com.educalab.graficosdivertidos.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.educalab.graficosdivertidos.domain.model.ChartType
import com.educalab.graficosdivertidos.domain.model.GraphErrorType
import com.educalab.graficosdivertidos.domain.model.InteractionType
import com.educalab.graficosdivertidos.domain.model.ModuleKey
import com.educalab.graficosdivertidos.domain.model.ModuleState

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val alias: String,
    val avatarKey: String,
    val createdAt: Long,
    val lastOpenedAt: Long,
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val onboardingCompleted: Boolean = false,
)

@Entity(tableName = "dataset")
data class DatasetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val category: String,
    val unit: String,
    val iconKey: String,
    val isSeed: Boolean = true,
)

@Entity(
    tableName = "data_point",
    foreignKeys = [
        ForeignKey(
            entity = DatasetEntity::class,
            parentColumns = ["id"],
            childColumns = ["datasetId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("datasetId")],
)
data class DataPointEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val datasetId: Long,
    val label: String,
    val value: Double,
    val orderIndex: Int,
)

/** Gráfico "de referencia" ya configurado (usado en los módulos de tipo fijo, comparador y detective). */
@Entity(
    tableName = "chart_definition",
    foreignKeys = [
        ForeignKey(
            entity = DatasetEntity::class,
            parentColumns = ["id"],
            childColumns = ["datasetId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("datasetId"), Index("moduleKey")],
)
data class ChartDefinitionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val datasetId: Long,
    val chartType: ChartType,
    val title: String,
    val showLabels: Boolean = true,
    val showLegend: Boolean = true,
    val axisMax: Double? = null,
    val moduleKey: ModuleKey,
)

@Entity(
    tableName = "chart_exercise",
    foreignKeys = [
        ForeignKey(
            entity = ChartDefinitionEntity::class,
            parentColumns = ["id"],
            childColumns = ["chartDefinitionId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("chartDefinitionId"), Index("moduleKey")],
)
data class ChartExerciseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val chartDefinitionId: Long,
    val moduleKey: ModuleKey,
    val interactionType: InteractionType,
    val prompt: String,
    /** Índices de opción correcta, valor esperado codificado o índice de categoría, según [interactionType]. */
    val correctAnswer: List<Int>,
    /** Textos de las opciones cuando aplica (opción múltiple / selección en gráfico). */
    val options: List<String>,
    val explanationCorrect: String,
    val explanationIncorrect: String,
    val difficulty: Int = 1,
)

@Entity(
    tableName = "chart_attempt",
    foreignKeys = [
        ForeignKey(
            entity = ChartExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = UserProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("exerciseId"), Index("userId")],
)
data class ChartAttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val exerciseId: Long,
    val userId: Long,
    val selectedAnswer: List<Int>,
    val isCorrect: Boolean,
    val firstTry: Boolean,
    val attemptAt: Long,
    val xpAwarded: Int,
)

/** Configuración de gráfico guardada por el usuario en el módulo Constructor. */
@Entity(
    tableName = "chart_configuration",
    foreignKeys = [
        ForeignKey(
            entity = DatasetEntity::class,
            parentColumns = ["id"],
            childColumns = ["datasetId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = UserProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("datasetId"), Index("userId")],
)
data class ChartConfigurationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val userId: Long,
    val datasetId: Long,
    val chartType: ChartType,
    val title: String,
    val categoryOrder: List<String>,
    val showLabels: Boolean,
    val showLegend: Boolean,
    val axisMax: Double?,
    val createdAt: Long,
)

@Entity(
    tableName = "graph_error_challenge",
    foreignKeys = [
        ForeignKey(
            entity = DatasetEntity::class,
            parentColumns = ["id"],
            childColumns = ["datasetId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("datasetId")],
)
data class GraphErrorChallengeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val datasetId: Long,
    val chartType: ChartType,
    val displayedTitle: String,
    val errorType: GraphErrorType,
    /** Para EJE_TRUNCADO: mínimo de eje simulado. Null si no aplica. */
    val axisMinOverride: Double? = null,
    /** Para PICTOGRAMA_SIN_ESCALA / etc: unidad por icono a mostrar (0 = sin escala). */
    val unitPerIconOverride: Double? = null,
    /** Para DATOS_FALTANTES: etiqueta de categoría que se oculta a propósito. */
    val omittedCategoryLabel: String? = null,
    val explanation: String,
    val difficulty: Int = 1,
)

@Entity(
    tableName = "graph_error_attempt",
    foreignKeys = [
        ForeignKey(
            entity = GraphErrorChallengeEntity::class,
            parentColumns = ["id"],
            childColumns = ["challengeId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = UserProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("challengeId"), Index("userId")],
)
data class GraphErrorAttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val challengeId: Long,
    val userId: Long,
    val selectedErrorType: GraphErrorType,
    val isCorrect: Boolean,
    val attemptAt: Long,
    val xpAwarded: Int,
)

@Entity(
    tableName = "comparison_challenge",
    foreignKeys = [
        ForeignKey(
            entity = DatasetEntity::class,
            parentColumns = ["id"],
            childColumns = ["datasetId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("datasetId")],
)
data class ComparisonChallengeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val datasetId: Long,
    val chartTypeA: ChartType,
    val chartTypeB: ChartType,
    val question: String,
    /** "A" o "B": cuál de las dos representaciones comunica mejor este dataset. */
    val betterSide: String,
    val explanation: String,
    val difficulty: Int = 1,
)

@Entity(
    tableName = "comparison_attempt",
    foreignKeys = [
        ForeignKey(
            entity = ComparisonChallengeEntity::class,
            parentColumns = ["id"],
            childColumns = ["challengeId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = UserProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("challengeId"), Index("userId")],
)
data class ComparisonAttemptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val challengeId: Long,
    val userId: Long,
    val selectedSide: String,
    val isCorrect: Boolean,
    val attemptAt: Long,
    val xpAwarded: Int,
)

/** Progreso por módulo y usuario: estado visual, completados y precisión. */
@Entity(
    tableName = "progress",
    primaryKeys = ["userId", "moduleKey"],
    foreignKeys = [
        ForeignKey(
            entity = UserProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("userId")],
)
data class ProgressEntity(
    val userId: Long,
    val moduleKey: ModuleKey,
    val completedCount: Int = 0,
    val totalCount: Int = 0,
    val correctCount: Int = 0,
    val attemptsCount: Int = 0,
    val state: ModuleState = ModuleState.DISPONIBLE,
    val updatedAt: Long = 0L,
)

/** Estadísticas agregadas del usuario (XP total, racha, estrellas) — vista de conjunto para Home/Perfil. */
@Entity(
    tableName = "user_stats",
    foreignKeys = [
        ForeignKey(
            entity = UserProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class UserStatsEntity(
    @PrimaryKey val userId: Long,
    val totalXp: Int = 0,
    val totalStars: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val exercisesCompleted: Int = 0,
    val updatedAt: Long = 0L,
)

@Entity(tableName = "badge")
data class BadgeEntity(
    @PrimaryKey val code: String,
    val title: String,
    val description: String,
    val iconKey: String,
    val criteriaText: String,
)

@Entity(
    tableName = "user_badge",
    primaryKeys = ["userId", "badgeCode"],
    foreignKeys = [
        ForeignKey(
            entity = UserProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = BadgeEntity::class,
            parentColumns = ["code"],
            childColumns = ["badgeCode"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("userId"), Index("badgeCode")],
)
data class UserBadgeEntity(
    val userId: Long,
    val badgeCode: String,
    val unlockedAt: Long,
)
