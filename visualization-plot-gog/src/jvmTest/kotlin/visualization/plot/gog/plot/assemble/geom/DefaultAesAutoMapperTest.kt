package jetbrains.datalore.visualization.plot.gog.plot.assemble.geom

import jetbrains.datalore.visualization.plot.gog.config.GeoPositionsDataUtil.POINT_X
import jetbrains.datalore.visualization.plot.gog.config.GeoPositionsDataUtil.POINT_Y
import jetbrains.datalore.visualization.plot.gog.config.GeoPositionsDataUtil.RECT_XMAX
import jetbrains.datalore.visualization.plot.gog.config.GeoPositionsDataUtil.RECT_XMIN
import jetbrains.datalore.visualization.plot.gog.config.GeoPositionsDataUtil.RECT_YMAX
import jetbrains.datalore.visualization.plot.gog.config.GeoPositionsDataUtil.RECT_YMIN
import jetbrains.datalore.visualization.plot.gog.core.data.DataFrame
import jetbrains.datalore.visualization.plot.gog.core.data.DataFrame.Variable
import jetbrains.datalore.visualization.plot.gog.core.render.Aes
import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultAesAutoMapperTest {

    @Test
    fun geomPointShouldMapToDefaultVariables() {
        assertEquals(
                listOf(POINT_X, POINT_Y),
                getMappedLabelsForAes(Aes.X, Aes.Y))
    }

    @Test
    fun geomRectShouldMapToDefaultVariables() {
        assertEquals(
                listOf(RECT_XMIN, RECT_YMIN, RECT_XMAX, RECT_YMAX),
                getMappedLabelsForAes(Aes.XMIN, Aes.YMIN, Aes.XMAX, Aes.YMAX))
    }

    companion object {
        private val DATA_FRAME = DataFrame.Builder()
                .put(Variable("foo"), listOf(1.0, 2.0))
                .put(Variable(POINT_Y), listOf(23.0, 13.0))
                .put(Variable(POINT_X), listOf(42.0, 17.0))
                .put(Variable("bar"), listOf(3.0, 4.0))
                .put(Variable(RECT_YMAX), listOf(4.0, 8.0))
                .put(Variable(RECT_YMIN), listOf(3.0, 6.0))
                .put(Variable("baz"), listOf(5.0, 6.0))
                .put(Variable(RECT_XMIN), listOf(1.0, 5.0))
                .put(Variable(RECT_XMAX), listOf(2.0, 7.0))
                .build()

        private fun getMappedLabelsForAes(vararg aes: Aes<*>): List<String> {
            val aesAutoMapper = DefaultAesAutoMapper(listOf(*aes)) { false }
            val mappings = aesAutoMapper.createMapping(DATA_FRAME)
            return aes.map { aesItem -> mappings.getValue(aesItem).name }
        }
    }
}