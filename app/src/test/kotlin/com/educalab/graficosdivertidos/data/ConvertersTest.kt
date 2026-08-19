package com.educalab.graficosdivertidos.data

import com.educalab.graficosdivertidos.data.local.converters.Converters
import com.educalab.graficosdivertidos.data.local.converters.IntListCodec
import com.educalab.graficosdivertidos.data.local.converters.StringListCodec
import com.educalab.graficosdivertidos.domain.model.ChartType
import com.educalab.graficosdivertidos.domain.model.GraphErrorType
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ConvertersTest {

    private val converters = Converters()

    @Test
    fun `StringListCodec conserva el orden y el contenido`() {
        val original = listOf("Rojo", "Verde", "Azul")
        val encoded = StringListCodec.encode(original)
        val decoded = StringListCodec.decode(encoded)
        assertThat(decoded).isEqualTo(original)
    }

    @Test
    fun `StringListCodec con lista vacia decodifica a lista vacia`() {
        assertThat(StringListCodec.decode(null)).isEmpty()
        assertThat(StringListCodec.decode("")).isEmpty()
    }

    @Test
    fun `IntListCodec conserva el orden de los indices`() {
        val original = listOf(3, 1, 0, 2)
        val encoded = IntListCodec.encode(original)
        assertThat(IntListCodec.decode(encoded)).isEqualTo(original)
    }

    @Test
    fun `IntListCodec ignora texto invalido sin lanzar excepcion`() {
        assertThat(IntListCodec.decode("1,abc,3")).isEqualTo(listOf(1, 3))
    }

    @Test
    fun `Converters de ChartType son reversibles`() {
        ChartType.entries.forEach { type ->
            assertThat(converters.toChartType(converters.fromChartType(type))).isEqualTo(type)
        }
    }

    @Test
    fun `Converters de GraphErrorType son reversibles`() {
        GraphErrorType.entries.forEach { type ->
            assertThat(converters.toGraphErrorType(converters.fromGraphErrorType(type))).isEqualTo(type)
        }
    }

    @Test
    fun `Converters de listas de texto con etiquetas largas no pierden datos`() {
        val labels = listOf("Categoría con acentos áéíóú", "Ñoño", "12345")
        val roundTrip = converters.toStringList(converters.fromStringList(labels))
        assertThat(roundTrip).isEqualTo(labels)
    }
}
