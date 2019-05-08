package jetbrains.datalore.visualization.plot.gog.plot.scale

import jetbrains.datalore.visualization.plot.gog.core.data.DataFrameUtil
import jetbrains.datalore.visualization.plot.gog.core.render.Aes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DefaultMapperProviderTest {
    @Test
    fun mapId() {
        val serie = listOf("a", "a", "b", "c")
        val d = mapOf("var" to serie)
        val df = DataFrameUtil.fromMap(d)

        val variable = df.variables().iterator().next()

        val mapperProvider = DefaultMapperProvider[Aes.MAP_ID]
        val mapper = mapperProvider.createDiscreteMapper(df, variable)

        val mapped = arrayOf(0.0, 0.0, 1.0, 2.0).map { v -> mapper.apply(v) as String }
        assertEquals(serie, mapped)
    }

    @Test
    fun everyAesHasMapperProvider() {
        for (aes in Aes.values()) {
            assertTrue(DefaultMapperProvider.hasDefault(aes), "Aes " + aes.name() + " has MapperProvider")
        }
    }
}