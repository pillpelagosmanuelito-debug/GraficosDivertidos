package com.educalab.graficosdivertidos

import android.app.Application
import com.educalab.graficosdivertidos.data.local.AppDatabase
import com.educalab.graficosdivertidos.data.local.seed.DatabaseSeeder
import com.educalab.graficosdivertidos.data.repository.BuilderRepository
import com.educalab.graficosdivertidos.data.repository.ContentRepository
import com.educalab.graficosdivertidos.data.repository.ProfileRepository
import com.educalab.graficosdivertidos.data.repository.ProgressRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Contenedor de dependencias manual y punto de entrada de la app. No se usa
 * Hilt/Dagger para mantener el proyecto simple de compilar y auditar; con 7
 * repositorios el costo de una inyección manual es bajo.
 */
class GraficosDivertidosApp : Application() {

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }

    val seeder: DatabaseSeeder by lazy {
        DatabaseSeeder(
            datasetDao = database.datasetDao(),
            chartDao = database.chartDao(),
            graphErrorDao = database.graphErrorDao(),
            comparisonDao = database.comparisonDao(),
            badgeDao = database.badgeDao(),
        )
    }

    val profileRepository: ProfileRepository by lazy { ProfileRepository(database.profileDao()) }

    val contentRepository: ContentRepository by lazy {
        ContentRepository(database.datasetDao(), database.chartDao(), database.graphErrorDao(), database.comparisonDao())
    }

    val progressRepository: ProgressRepository by lazy {
        ProgressRepository(
            db = database,
            chartDao = database.chartDao(),
            graphErrorDao = database.graphErrorDao(),
            comparisonDao = database.comparisonDao(),
            progressDao = database.progressDao(),
            badgeDao = database.badgeDao(),
            profileDao = database.profileDao(),
        )
    }

    val builderRepository: BuilderRepository by lazy { BuilderRepository(database.chartDao()) }

    override fun onCreate() {
        super.onCreate()
        // El seeding ocurre en una corrutina de aplicación; MainActivity espera
        // a que termine (a través del ViewModel raíz) antes de mostrar contenido.
    }
}
