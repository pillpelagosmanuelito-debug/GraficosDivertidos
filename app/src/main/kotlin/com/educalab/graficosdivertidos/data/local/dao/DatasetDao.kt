package com.educalab.graficosdivertidos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.educalab.graficosdivertidos.data.local.entity.DataPointEntity
import com.educalab.graficosdivertidos.data.local.entity.DatasetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DatasetDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDatasets(datasets: List<DatasetEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDataPoints(points: List<DataPointEntity>)

    @Query("SELECT COUNT(*) FROM dataset")
    suspend fun countDatasets(): Int

    @Query("SELECT * FROM dataset ORDER BY id ASC")
    fun observeDatasets(): Flow<List<DatasetEntity>>

    @Query("SELECT * FROM dataset WHERE id = :id LIMIT 1")
    suspend fun getDataset(id: Long): DatasetEntity?

    @Query("SELECT * FROM data_point WHERE datasetId = :datasetId ORDER BY orderIndex ASC")
    suspend fun getPoints(datasetId: Long): List<DataPointEntity>

    @Query("SELECT * FROM data_point WHERE datasetId = :datasetId ORDER BY orderIndex ASC")
    fun observePoints(datasetId: Long): Flow<List<DataPointEntity>>

    @Transaction
    suspend fun insertDatasetWithPoints(dataset: DatasetEntity, points: List<DataPointEntity>): Long {
        val id = insertDatasets(listOf(dataset)).firstOrNull() ?: return -1L
        insertDataPoints(points.map { it.copy(datasetId = id) })
        return id
    }
}
