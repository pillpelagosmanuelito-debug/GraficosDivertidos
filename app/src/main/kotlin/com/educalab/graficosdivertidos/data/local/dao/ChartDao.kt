package com.educalab.graficosdivertidos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.educalab.graficosdivertidos.data.local.entity.ChartAttemptEntity
import com.educalab.graficosdivertidos.data.local.entity.ChartConfigurationEntity
import com.educalab.graficosdivertidos.data.local.entity.ChartDefinitionEntity
import com.educalab.graficosdivertidos.data.local.entity.ChartExerciseEntity
import com.educalab.graficosdivertidos.domain.model.ModuleKey
import kotlinx.coroutines.flow.Flow

@Dao
interface ChartDao {

    // --- ChartDefinition ---
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDefinitions(defs: List<ChartDefinitionEntity>): List<Long>

    @Query("SELECT COUNT(*) FROM chart_definition")
    suspend fun countDefinitions(): Int

    @Query("SELECT * FROM chart_definition WHERE id = :id LIMIT 1")
    suspend fun getDefinition(id: Long): ChartDefinitionEntity?

    @Query("SELECT * FROM chart_definition WHERE moduleKey = :moduleKey ORDER BY id ASC")
    suspend fun getDefinitionsForModule(moduleKey: ModuleKey): List<ChartDefinitionEntity>

    // --- ChartExercise ---
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExercises(exercises: List<ChartExerciseEntity>): List<Long>

    @Query("SELECT COUNT(*) FROM chart_exercise")
    suspend fun countExercises(): Int

    @Query("SELECT * FROM chart_exercise WHERE moduleKey = :moduleKey ORDER BY id ASC")
    fun observeExercisesForModule(moduleKey: ModuleKey): Flow<List<ChartExerciseEntity>>

    @Query("SELECT * FROM chart_exercise WHERE moduleKey = :moduleKey ORDER BY id ASC")
    suspend fun getExercisesForModule(moduleKey: ModuleKey): List<ChartExerciseEntity>

    @Query("SELECT * FROM chart_exercise WHERE id = :id LIMIT 1")
    suspend fun getExercise(id: Long): ChartExerciseEntity?

    // --- ChartAttempt ---
    @Insert
    suspend fun insertAttempt(attempt: ChartAttemptEntity): Long

    @Query("SELECT * FROM chart_attempt WHERE userId = :userId AND exerciseId = :exerciseId ORDER BY attemptAt ASC")
    suspend fun getAttemptsForExercise(userId: Long, exerciseId: Long): List<ChartAttemptEntity>

    @Query(
        """
        SELECT COUNT(DISTINCT ca.exerciseId) FROM chart_attempt ca
        INNER JOIN chart_exercise ce ON ce.id = ca.exerciseId
        WHERE ca.userId = :userId AND ce.moduleKey = :moduleKey AND ca.isCorrect = 1
        """,
    )
    suspend fun countCompletedExercisesInModule(userId: Long, moduleKey: ModuleKey): Int

    @Query(
        """
        SELECT COUNT(*) FROM chart_attempt ca
        INNER JOIN chart_exercise ce ON ce.id = ca.exerciseId
        WHERE ca.userId = :userId AND ce.moduleKey = :moduleKey
        """,
    )
    suspend fun countAttemptsInModule(userId: Long, moduleKey: ModuleKey): Int

    @Query(
        """
        SELECT COUNT(*) FROM chart_attempt ca
        INNER JOIN chart_exercise ce ON ce.id = ca.exerciseId
        WHERE ca.userId = :userId AND ce.moduleKey = :moduleKey AND ca.isCorrect = 1
        """,
    )
    suspend fun countCorrectInModule(userId: Long, moduleKey: ModuleKey): Int

    @Query("SELECT isCorrect FROM chart_attempt WHERE userId = :userId ORDER BY attemptAt ASC")
    suspend fun getAllResultsOrderedByTime(userId: Long): List<Boolean>

    @Query("SELECT COUNT(DISTINCT exerciseId) FROM chart_attempt WHERE userId = :userId AND isCorrect = 1")
    suspend fun countDistinctCompletedExercisesTotal(userId: Long): Int

    @Query("SELECT COUNT(*) FROM chart_attempt WHERE userId = :userId AND isCorrect = 1 AND firstTry = 1")
    suspend fun countFirstTryCorrectTotal(userId: Long): Int

    @Query(
        """
        SELECT ce.id FROM chart_exercise ce
        WHERE ce.moduleKey = :moduleKey AND ce.id NOT IN
        (SELECT exerciseId FROM chart_attempt WHERE userId = :userId AND isCorrect = 1)
        ORDER BY ce.id ASC
        """,
    )
    suspend fun getPendingOrFailedExerciseIds(userId: Long, moduleKey: ModuleKey): List<Long>

    // --- ChartConfiguration (Constructor) ---
    @Insert
    suspend fun insertConfiguration(config: ChartConfigurationEntity): Long

    @Query("SELECT * FROM chart_configuration WHERE userId = :userId ORDER BY createdAt DESC")
    fun observeConfigurationsForUser(userId: Long): Flow<List<ChartConfigurationEntity>>

    @Query("SELECT COUNT(*) FROM chart_configuration WHERE userId = :userId")
    suspend fun countConfigurationsForUser(userId: Long): Int

    @Query("DELETE FROM chart_configuration WHERE id = :id")
    suspend fun deleteConfiguration(id: Long)
}
