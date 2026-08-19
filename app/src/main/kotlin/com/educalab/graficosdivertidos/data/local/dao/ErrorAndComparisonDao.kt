package com.educalab.graficosdivertidos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.educalab.graficosdivertidos.data.local.entity.ComparisonAttemptEntity
import com.educalab.graficosdivertidos.data.local.entity.ComparisonChallengeEntity
import com.educalab.graficosdivertidos.data.local.entity.GraphErrorAttemptEntity
import com.educalab.graficosdivertidos.data.local.entity.GraphErrorChallengeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GraphErrorDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertChallenges(challenges: List<GraphErrorChallengeEntity>): List<Long>

    @Query("SELECT COUNT(*) FROM graph_error_challenge")
    suspend fun countChallenges(): Int

    @Query("SELECT * FROM graph_error_challenge ORDER BY id ASC")
    fun observeChallenges(): Flow<List<GraphErrorChallengeEntity>>

    @Query("SELECT * FROM graph_error_challenge ORDER BY id ASC")
    suspend fun getChallenges(): List<GraphErrorChallengeEntity>

    @Query("SELECT * FROM graph_error_challenge WHERE id = :id LIMIT 1")
    suspend fun getChallenge(id: Long): GraphErrorChallengeEntity?

    @Insert
    suspend fun insertAttempt(attempt: GraphErrorAttemptEntity): Long

    @Query("SELECT COUNT(*) FROM graph_error_attempt WHERE userId = :userId AND isCorrect = 1")
    suspend fun countSolvedByUser(userId: Long): Int

    @Query("SELECT COUNT(DISTINCT challengeId) FROM graph_error_attempt WHERE userId = :userId AND isCorrect = 1")
    suspend fun countDistinctSolvedByUser(userId: Long): Int

    @Query("SELECT COUNT(*) FROM graph_error_attempt WHERE userId = :userId")
    suspend fun countAttemptsByUser(userId: Long): Int

    @Query(
        """
        SELECT gec.id FROM graph_error_challenge gec
        WHERE gec.id NOT IN
        (SELECT challengeId FROM graph_error_attempt WHERE userId = :userId AND isCorrect = 1)
        ORDER BY gec.id ASC
        """,
    )
    suspend fun getPendingChallengeIds(userId: Long): List<Long>
}

@Dao
interface ComparisonDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertChallenges(challenges: List<ComparisonChallengeEntity>): List<Long>

    @Query("SELECT COUNT(*) FROM comparison_challenge")
    suspend fun countChallenges(): Int

    @Query("SELECT * FROM comparison_challenge ORDER BY id ASC")
    fun observeChallenges(): Flow<List<ComparisonChallengeEntity>>

    @Query("SELECT * FROM comparison_challenge WHERE id = :id LIMIT 1")
    suspend fun getChallenge(id: Long): ComparisonChallengeEntity?

    @Insert
    suspend fun insertAttempt(attempt: ComparisonAttemptEntity): Long

    @Query("SELECT COUNT(*) FROM comparison_attempt WHERE userId = :userId AND isCorrect = 1")
    suspend fun countSolvedByUser(userId: Long): Int

    @Query("SELECT COUNT(DISTINCT challengeId) FROM comparison_attempt WHERE userId = :userId AND isCorrect = 1")
    suspend fun countDistinctSolvedByUser(userId: Long): Int
}
