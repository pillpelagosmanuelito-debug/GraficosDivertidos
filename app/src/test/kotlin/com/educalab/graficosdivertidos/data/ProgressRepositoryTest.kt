package com.educalab.graficosdivertidos.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.educalab.graficosdivertidos.data.local.AppDatabase
import com.educalab.graficosdivertidos.data.local.seed.DatabaseSeeder
import com.educalab.graficosdivertidos.data.repository.ContentRepository
import com.educalab.graficosdivertidos.data.repository.ProfileRepository
import com.educalab.graficosdivertidos.data.repository.ProgressRepository
import com.educalab.graficosdivertidos.domain.model.ModuleKey
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Verifica, contra Room real en memoria, que registrar un intento
 * recalcula progreso, XP y desbloqueo de insignias en una sola transacción
 * consistente (sin dejar al usuario "a medio actualizar" si algo falla).
 */
@RunWith(RobolectricTestRunner::class)
class ProgressRepositoryTest {

    private lateinit var db: AppDatabase
    private lateinit var contentRepository: ContentRepository
    private lateinit var progressRepository: ProgressRepository
    private lateinit var profileRepository: ProfileRepository
    private var userId: Long = 0L

    @Before
    fun setUp() = runTest {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val seeder = DatabaseSeeder(db.datasetDao(), db.chartDao(), db.graphErrorDao(), db.comparisonDao(), db.badgeDao())
        seeder.seedIfNeeded()
        contentRepository = ContentRepository(db.datasetDao(), db.chartDao(), db.graphErrorDao(), db.comparisonDao())
        progressRepository = ProgressRepository(db, db.chartDao(), db.graphErrorDao(), db.comparisonDao(), db.progressDao(), db.badgeDao(), db.profileDao())
        profileRepository = ProfileRepository(db.profileDao())
        userId = profileRepository.ensureProfileExists("Test", "avatar_01", 1000L)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `registrar un acierto otorga xp mayor que cero`() = runTest {
        val exercise = contentRepository.getExercisesForModule(ModuleKey.BARRAS).first()
        val result = progressRepository.recordExerciseAttempt(userId, exercise, exercise.correctAnswer, isCorrect = true, now = 1L)
        assertThat(result.isCorrect).isTrue()
        assertThat(result.xpAwarded).isGreaterThan(0)
    }

    @Test
    fun `registrar un acierto desbloquea la insignia de primer grafico`() = runTest {
        val exercise = contentRepository.getExercisesForModule(ModuleKey.BARRAS).first()
        val result = progressRepository.recordExerciseAttempt(userId, exercise, exercise.correctAnswer, isCorrect = true, now = 1L)
        assertThat(result.newlyEarnedBadges).contains("badge_primer_grafico")
    }

    @Test
    fun `un fallo no otorga la insignia de primer grafico`() = runTest {
        val exercise = contentRepository.getExercisesForModule(ModuleKey.BARRAS).first()
        val wrongAnswer = listOf(-1)
        val result = progressRepository.recordExerciseAttempt(userId, exercise, wrongAnswer, isCorrect = false, now = 1L)
        assertThat(result.isCorrect).isFalse()
        assertThat(result.newlyEarnedBadges).doesNotContain("badge_primer_grafico")
    }

    @Test
    fun `el progreso del modulo aumenta tras un intento correcto`() = runTest {
        val exercise = contentRepository.getExercisesForModule(ModuleKey.BARRAS).first()
        progressRepository.recordExerciseAttempt(userId, exercise, exercise.correctAnswer, isCorrect = true, now = 1L)
        val progress = db.progressDao().getProgress(userId, ModuleKey.BARRAS)
        assertThat(progress).isNotNull()
        assertThat(progress!!.completedCount).isEqualTo(1)
    }

    @Test
    fun `una misma insignia no se otorga dos veces`() = runTest {
        val exercises = contentRepository.getExercisesForModule(ModuleKey.BARRAS)
        val first = progressRepository.recordExerciseAttempt(userId, exercises[0], exercises[0].correctAnswer, true, now = 1L)
        val second = progressRepository.recordExerciseAttempt(userId, exercises[1], exercises[1].correctAnswer, true, now = 2L)
        assertThat(first.newlyEarnedBadges).contains("badge_primer_grafico")
        assertThat(second.newlyEarnedBadges).doesNotContain("badge_primer_grafico")
    }

    @Test
    fun `la racha se reinicia despues de un fallo`() = runTest {
        val exercises = contentRepository.getExercisesForModule(ModuleKey.BARRAS)
        progressRepository.recordExerciseAttempt(userId, exercises[0], exercises[0].correctAnswer, true, now = 1L)
        progressRepository.recordExerciseAttempt(userId, exercises[1], listOf(-1), false, now = 2L)
        val stats = db.profileDao().getStats(userId)
        assertThat(stats!!.currentStreak).isEqualTo(0)
    }

    @Test
    fun `el segundo intento del mismo ejercicio no cuenta como primer intento`() = runTest {
        val exercise = contentRepository.getExercisesForModule(ModuleKey.BARRAS).first()
        progressRepository.recordExerciseAttempt(userId, exercise, listOf(-1), isCorrect = false, now = 1L)
        val result = progressRepository.recordExerciseAttempt(userId, exercise, exercise.correctAnswer, isCorrect = true, now = 2L)
        val attempts = db.chartDao().getAttemptsForExercise(userId, exercise.id)
        assertThat(attempts).hasSize(2)
        assertThat(attempts.last().firstTry).isFalse()
    }

    @Test
    fun `guardar una configuracion en el constructor actualiza su progreso`() = runTest {
        val builderRepository = com.educalab.graficosdivertidos.data.repository.BuilderRepository(db.chartDao())
        val dataset = contentRepository.getAllDatasets().first()
        builderRepository.saveConfiguration(
            userId, dataset.id, com.educalab.graficosdivertidos.domain.model.ChartType.BARRAS,
            "Mi gráfico de prueba", dataset.points.map { it.label }, true, true, null, now = 1L,
        )
        progressRepository.recomputeBuilderProgress(userId, now = 2L)
        val progress = db.progressDao().getProgress(userId, ModuleKey.CONSTRUCTOR)
        assertThat(progress).isNotNull()
        assertThat(progress!!.completedCount).isEqualTo(1)
    }
}
