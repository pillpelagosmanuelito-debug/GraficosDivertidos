package com.educalab.graficosdivertidos.data.repository

import com.educalab.graficosdivertidos.data.local.entity.DataPointEntity
import com.educalab.graficosdivertidos.data.local.entity.DatasetEntity
import com.educalab.graficosdivertidos.domain.model.DataPointModel
import com.educalab.graficosdivertidos.domain.model.DatasetModel

fun DatasetEntity.toDomain(points: List<DataPointEntity>): DatasetModel = DatasetModel(
    id = id,
    title = title,
    category = category,
    unit = unit,
    icon = iconKey,
    points = points.sortedBy { it.orderIndex }.map { DataPointModel(it.label, it.value, it.orderIndex) },
)
