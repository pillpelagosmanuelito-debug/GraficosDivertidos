package com.educalab.graficosdivertidos.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.educalab.graficosdivertidos.data.local.converters.Converters
import com.educalab.graficosdivertidos.data.local.dao.BadgeDao
import com.educalab.graficosdivertidos.data.local.dao.ChartDao
import com.educalab.graficosdivertidos.data.local.dao.ComparisonDao
import com.educalab.graficosdivertidos.data.local.dao.DatasetDao
import com.educalab.graficosdivertidos.data.local.dao.GraphErrorDao
import com.educalab.graficosdivertidos.data.local.dao.ProfileDao
import com.educalab.graficosdivertidos.data.local.dao.ProgressDao
import com.educalab.graficosdivertidos.data.local.entity.BadgeEntity
import com.educalab.graficosdivertidos.data.local.entity.ChartAttemptEntity
import com.educalab.graficosdivertidos.data.local.entity.ChartConfigurationEntity
import com.educalab.graficosdivertidos.data.local.entity.ChartDefinitionEntity
import com.educalab.graficosdivertidos.data.local.entity.ChartExerciseEntity
import com.educalab.graficosdivertidos.data.local.entity.ComparisonAttemptEntity
import com.educalab.graficosdivertidos.data.local.entity.ComparisonChallengeEntity
import com.educalab.graficosdivertidos.data.local.entity.DataPointEntity
import com.educalab.graficosdivertidos.data.local.entity.DatasetEntity
import com.educalab.graficosdivertidos.data.local.entity.GraphErrorAttemptEntity
import com.educalab.graficosdivertidos.data.local.entity.GraphErrorChallengeEntity
import com.educalab.graficosdivertidos.data.local.entity.ProgressEntity
import com.educalab.graficosdivertidos.data.local.entity.UserBadgeEntity
import com.educalab.graficosdivertidos.data.local.entity.UserProfileEntity
import com.educalab.graficosdivertidos.data.local.entity.UserStatsEntity

@Database(
    entities = [
        UserProfileEntity::class,
        UserStatsEntity::class,
        DatasetEntity::class,
        DataPointEntity::class,
        ChartDefinitionEntity::class,
        ChartExerciseEntity::class,
        ChartAttemptEntity::class,
        ChartConfigurationEntity::class,
        GraphErrorChallengeEntity::class,
        GraphErrorAttemptEntity::class,
        ComparisonChallengeEntity::class,
        ComparisonAttemptEntity::class,
        ProgressEntity::class,
        BadgeEntity::class,
        UserBadgeEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun datasetDao(): DatasetDao
    abstract fun chartDao(): ChartDao
    abstract fun graphErrorDao(): GraphErrorDao
    abstract fun comparisonDao(): ComparisonDao
    abstract fun progressDao(): ProgressDao
    abstract fun badgeDao(): BadgeDao

    companion object {
        const val DATABASE_NAME = "graficos_divertidos.db"

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: android.content.Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME,
                ).build().also { instance = it }
            }
        }
    }
}
