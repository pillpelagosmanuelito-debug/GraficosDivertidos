package com.educalab.graficosdivertidos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.educalab.graficosdivertidos.data.local.entity.UserProfileEntity
import com.educalab.graficosdivertidos.data.local.entity.UserStatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: UserProfileEntity): Long

    @Update
    suspend fun updateProfile(profile: UserProfileEntity)

    @Query("SELECT * FROM user_profile WHERE id = :userId LIMIT 1")
    fun observeProfile(userId: Long): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = :userId LIMIT 1")
    suspend fun getProfile(userId: Long): UserProfileEntity?

    @Query("SELECT * FROM user_profile ORDER BY id ASC LIMIT 1")
    suspend fun getFirstProfileOrNull(): UserProfileEntity?

    @Query("SELECT COUNT(*) FROM user_profile")
    suspend fun countProfiles(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStats(stats: UserStatsEntity)

    @Query("SELECT * FROM user_stats WHERE userId = :userId LIMIT 1")
    fun observeStats(userId: Long): Flow<UserStatsEntity?>

    @Query("SELECT * FROM user_stats WHERE userId = :userId LIMIT 1")
    suspend fun getStats(userId: Long): UserStatsEntity?
}
