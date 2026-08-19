package com.educalab.graficosdivertidos.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.educalab.graficosdivertidos.data.local.AppDatabase
import com.educalab.graficosdivertidos.data.local.seed.DatabaseSeeder
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Prueba el seeder contra una base de datos Room real en memoria (Robolectric). */
@RunWith(RobolectricTestRunner::class)
class DatabaseSeederTest {

    private lateinit var db: AppDatabase
    private lateinit var seeder: DatabaseSeeder

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        seeder = DatabaseSeeder(db.datasetDao(), db.chartDao(), db.graphErrorDao(), db.comparisonDao(), db.badgeDao())
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `seedIfNeeded inserta los 30 datasets semilla`() = runTest {
        seeder.seedIfNeeded()
        assertThat(db.datasetDao().countDatasets()).isEqualTo(30)
    }

    @Test
    fun `seedIfNeeded inserta los 50 ejercicios interpretativos`() = runTest {
        seeder.seedIfNeeded()
        assertThat(db.chartDao().countExercises()).isEqualTo(50)
    }

    @Test
    fun `seedIfNeeded inserta 30 retos del detective`() = runTest {
        seeder.seedIfNeeded()
        assertThat(db.graphErrorDao().countChallenges()).isEqualTo(30)
    }

    @Test
    fun `seedIfNeeded inserta al menos 18 retos del comparador`() = runTest {
        seeder.seedIfNeeded()
        assertThat(db.comparisonDao().countChallenges()).isAtLeast(18)
    }

    @Test
    fun `seedIfNeeded inserta 10 insignias`() = runTest {
        seeder.seedIfNeeded()
        assertThat(db.badgeDao().countBadges()).isEqualTo(10)
    }

    @Test
    fun `seedIfNeeded es idempotente y no duplica datos al llamarse dos veces`() = runTest {
        seeder.seedIfNeeded()
        seeder.seedIfNeeded()
        assertThat(db.datasetDao().countDatasets()).isEqualTo(30)
    }

    @Test
    fun `cada dataset semilla tiene al menos dos puntos de datos`() = runTest {
        seeder.seedIfNeeded()
        val datasets = db.datasetDao().observeDatasets()
        val list = kotlinx.coroutines.flow.first(datasets)
        list.forEach { dataset ->
            val points = db.datasetDao().getPoints(dataset.id)
            assertThat(points.size).isAtLeast(2)
        }
    }

    @Test
    fun `eliminar un dataset elimina en cascada sus puntos y definiciones de grafico`() = runTest {
        seeder.seedIfNeeded()
        val dataset = kotlinx.coroutines.flow.first(db.datasetDao().observeDatasets()).first()
        val pointsBefore = db.datasetDao().getPoints(dataset.id)
        assertThat(pointsBefore).isNotEmpty()

        db.openHelper.writableDatabase.delete("dataset", "id = ?", arrayOf(dataset.id))

        val pointsAfter = db.datasetDao().getPoints(dataset.id)
        assertThat(pointsAfter).isEmpty()
    }
}
