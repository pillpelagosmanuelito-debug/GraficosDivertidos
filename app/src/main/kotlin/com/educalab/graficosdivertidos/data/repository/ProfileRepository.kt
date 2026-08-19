package com.educalab.graficosdivertidos.data.repository

import com.educalab.graficosdivertidos.data.local.dao.ProfileDao
import com.educalab.graficosdivertidos.data.local.entity.UserProfileEntity
import com.educalab.graficosdivertidos.data.local.entity.UserStatsEntity
import com.educalab.graficosdivertidos.domain.logic.GamificationEngine
import com.educalab.graficosdivertidos.domain.model.UserStatsModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Perfil local del niño/a: alias, avatar y preferencias. Nunca datos personales reales. */
class ProfileRepository(private val profileDao: ProfileDao) {

    /** Crea el perfil único de la app si aún no existe, y devuelve su id. */
    suspend fun ensureProfileExists(defaultAlias: String, defaultAvatar: String, now: Long): Long {
        val existing = profileDao.getFirstProfileOrNull()
        if (existing != null) return existing.id
        val id = profileDao.insertProfile(
            UserProfileEntity(
                alias = defaultAlias,
                avatarKey = defaultAvatar,
                createdAt = now,
                lastOpenedAt = now,
                onboardingCompleted = false,
            ),
        )
        profileDao.upsertStats(UserStatsEntity(userId = id, updatedAt = now))
        return id
    }

    suspend fun getProfile(userId: Long) = profileDao.getProfile(userId)

    fun observeProfile(userId: Long): Flow<UserProfileEntity?> = profileDao.observeProfile(userId)

    suspend fun updateAliasAndAvatar(userId: Long, alias: String, avatarKey: String) {
        val current = profileDao.getProfile(userId) ?: return
        profileDao.updateProfile(current.copy(alias = alias.trim().ifBlank { current.alias }, avatarKey = avatarKey))
    }

    suspend fun completeOnboarding(userId: Long) {
        val current = profileDao.getProfile(userId) ?: return
        profileDao.updateProfile(current.copy(onboardingCompleted = true))
    }

    suspend fun setSoundEnabled(userId: Long, enabled: Boolean) {
        val current = profileDao.getProfile(userId) ?: return
        profileDao.updateProfile(current.copy(soundEnabled = enabled))
    }

    suspend fun setHapticsEnabled(userId: Long, enabled: Boolean) {
        val current = profileDao.getProfile(userId) ?: return
        profileDao.updateProfile(current.copy(hapticsEnabled = enabled))
    }

    suspend fun touchLastOpened(userId: Long, now: Long) {
        val current = profileDao.getProfile(userId) ?: return
        profileDao.updateProfile(current.copy(lastOpenedAt = now))
    }

    fun observeStats(userId: Long): Flow<UserStatsModel> {
        return profileDao.observeStats(userId).map { entity ->
            val stats = entity ?: UserStatsEntity(userId = userId)
            UserStatsModel(
                totalXp = stats.totalXp,
                level = GamificationEngine.levelForXp(stats.totalXp),
                levelProgress = GamificationEngine.levelProgressFraction(stats.totalXp),
                totalStars = stats.totalStars,
                currentStreak = stats.currentStreak,
                bestStreak = stats.bestStreak,
                exercisesCompleted = stats.exercisesCompleted,
            )
        }
    }
}
