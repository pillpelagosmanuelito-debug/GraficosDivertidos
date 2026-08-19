package com.educalab.graficosdivertidos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.educalab.graficosdivertidos.data.local.entity.BadgeEntity
import com.educalab.graficosdivertidos.data.local.entity.ProgressEntity
import com.educalab.graficosdivertidos.data.local.entity.UserBadgeEntity
import com.educalab.graficosdivertidos.domain.model.ModuleKey
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgress(progress: ProgressEntity)

    @Query("SELECT * FROM progress WHERE userId = :userId ORDER BY moduleKey ASC")
    fun observeProgressForUser(userId: Long): Flow<List<ProgressEntity>>

    @Query("SELECT * FROM progress WHERE userId = :userId AND moduleKey = :moduleKey LIMIT 1")
    suspend fun getProgress(userId: Long, moduleKey: ModuleKey): ProgressEntity?
}

@Dao
interface BadgeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBadges(badges: List<BadgeEntity>)

    @Query("SELECT COUNT(*) FROM badge")
    suspend fun countBadges(): Int

    @Query("SELECT * FROM badge ORDER BY code ASC")
    fun observeAllBadges(): Flow<List<BadgeEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun unlockBadge(userBadge: UserBadgeEntity)

    @Query("SELECT badgeCode FROM user_badge WHERE userId = :userId")
    suspend fun getUnlockedCodes(userId: Long): List<String>

    @Query("SELECT * FROM user_badge WHERE userId = :userId ORDER BY unlockedAt DESC")
    fun observeUnlockedForUser(userId: Long): Flow<List<UserBadgeEntity>>
}
