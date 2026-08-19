package com.educalab.graficosdivertidos.data.local.converters

import androidx.room.TypeConverter
import com.educalab.graficosdivertidos.domain.model.ChartType
import com.educalab.graficosdivertidos.domain.model.GraphErrorType
import com.educalab.graficosdivertidos.domain.model.InteractionType
import com.educalab.graficosdivertidos.domain.model.ModuleKey
import com.educalab.graficosdivertidos.domain.model.ModuleState

/**
 * Convertidores de Room. Las listas de primitivos (categorías, opciones,
 * respuestas) se guardan como texto delimitado en lugar de JSON: son listas
 * simples de cadenas/números y esto evita depender de `org.json` (que en
 * pruebas JVM puras sin Robolectric solo existe como stub y lanza
 * excepción), manteniendo la lógica 100% testeable con JUnit normal.
 */
class Converters {

    // --- listas de texto (categorías seleccionadas, opciones de respuesta) ---
    @TypeConverter
    fun fromStringList(value: List<String>?): String = StringListCodec.encode(value.orEmpty())

    @TypeConverter
    fun toStringList(value: String?): List<String> = StringListCodec.decode(value)

    // --- listas de enteros (índices de orden, respuestas de opción múltiple) ---
    @TypeConverter
    fun fromIntList(value: List<Int>?): String = IntListCodec.encode(value.orEmpty())

    @TypeConverter
    fun toIntList(value: String?): List<Int> = IntListCodec.decode(value)

    // --- enums de dominio ---
    @TypeConverter
    fun fromChartType(value: ChartType): String = value.name

    @TypeConverter
    fun toChartType(value: String): ChartType = ChartType.valueOf(value)

    @TypeConverter
    fun fromGraphErrorType(value: GraphErrorType): String = value.name

    @TypeConverter
    fun toGraphErrorType(value: String): GraphErrorType = GraphErrorType.valueOf(value)

    @TypeConverter
    fun fromInteractionType(value: InteractionType): String = value.name

    @TypeConverter
    fun toInteractionType(value: String): InteractionType = InteractionType.valueOf(value)

    @TypeConverter
    fun fromModuleKey(value: ModuleKey): String = value.name

    @TypeConverter
    fun toModuleKey(value: String): ModuleKey = ModuleKey.valueOf(value)

    @TypeConverter
    fun fromModuleState(value: ModuleState): String = value.name

    @TypeConverter
    fun toModuleState(value: String): ModuleState = ModuleState.valueOf(value)
}

/** Codec de lista de cadenas usando un delimitador poco común, sin dependencias externas. */
object StringListCodec {
    private const val DELIMITER = "§§"

    fun encode(values: List<String>): String = values.joinToString(DELIMITER)

    fun decode(raw: String?): List<String> {
        if (raw.isNullOrEmpty()) return emptyList()
        return raw.split(DELIMITER).filter { it.isNotEmpty() }
    }
}

/** Codec de lista de enteros, reutilizando [StringListCodec] para el partido de cadenas. */
object IntListCodec {
    fun encode(values: List<Int>): String = values.joinToString(",")

    fun decode(raw: String?): List<Int> {
        if (raw.isNullOrEmpty()) return emptyList()
        return raw.split(",").mapNotNull { it.trim().toIntOrNull() }
    }
}
