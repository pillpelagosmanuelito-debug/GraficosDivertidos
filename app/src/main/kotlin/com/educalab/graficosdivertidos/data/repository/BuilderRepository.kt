package com.educalab.graficosdivertidos.data.repository

import com.educalab.graficosdivertidos.data.local.dao.ChartDao
import com.educalab.graficosdivertidos.data.local.entity.ChartConfigurationEntity
import com.educalab.graficosdivertidos.domain.model.ChartConfigurationModel
import com.educalab.graficosdivertidos.domain.model.ChartType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Gráficos construidos y guardados por el usuario en el módulo Constructor. */
class BuilderRepository(private val chartDao: ChartDao) {

    fun observeSavedConfigurations(userId: Long): Flow<List<ChartConfigurationModel>> {
        return chartDao.observeConfigurationsForUser(userId).map { list ->
            list.map {
                ChartConfigurationModel(
                    datasetId = it.datasetId,
                    chartType = it.chartType,
                    title = it.title,
                    categoryOrder = it.categoryOrder,
                    showLabels = it.showLabels,
                    showLegend = it.showLegend,
                    axisMax = it.axisMax,
                )
            }
        }
    }

    suspend fun saveConfiguration(
        userId: Long,
        datasetId: Long,
        chartType: ChartType,
        title: String,
        categoryOrder: List<String>,
        showLabels: Boolean,
        showLegend: Boolean,
        axisMax: Double?,
        now: Long,
    ): Long {
        return chartDao.insertConfiguration(
            ChartConfigurationEntity(
                userId = userId,
                datasetId = datasetId,
                chartType = chartType,
                title = title,
                categoryOrder = categoryOrder,
                showLabels = showLabels,
                showLegend = showLegend,
                axisMax = axisMax,
                createdAt = now,
            ),
        )
    }

    suspend fun deleteConfiguration(id: Long) = chartDao.deleteConfiguration(id)

    suspend fun countSaved(userId: Long) = chartDao.countConfigurationsForUser(userId)
}
