package jetbrains.datalore.visualization.plot.gog.server.config

import jetbrains.datalore.visualization.plot.base.Aes
import jetbrains.datalore.visualization.plot.gog.config.Option.GeomName
import jetbrains.datalore.visualization.plot.gog.config.Option.Layer
import jetbrains.datalore.visualization.plot.gog.config.Option.Layer.GEOM
import jetbrains.datalore.visualization.plot.gog.config.Option.Plot.DATA
import jetbrains.datalore.visualization.plot.gog.config.Option.Plot.LAYERS
import jetbrains.datalore.visualization.plot.gog.config.Option.Plot.MAPPING
import kotlin.test.Test

class DataVectorsInAesMappingTest {

    @Test
    fun aesInGGPlotNoData() {
        val inputVector = listOf(1.0, 5.0)

        val aes = mapOf("x" to inputVector)

        val plotOpts = mutableMapOf(
                MAPPING to aes,
                LAYERS to listOf(mapOf(
                        GEOM to GeomName.POINT   // layer without mapping or data
                ))
        )

        val layers = ServerSideTestUtil.createLayerConfigsWithoutEncoding(plotOpts)
        SingleLayerAssert.assertThat(layers)
                .haveBinding(Aes.X, "x")
                .haveDataVector("x", inputVector)
    }

    @Test
    fun aesAndDataInGGPlot() {
        val data = mapOf("x" to listOf(1.0, 1.0, 1))

        // aes specify different data vector for 'x' aes
        val inputVector = listOf(1.0, 5.0, 8.0)
        val aes = mapOf("x" to inputVector)

        val plotOpts = mutableMapOf(
                DATA to data,
                MAPPING to aes,
                LAYERS to listOf(mapOf(
                        GEOM to GeomName.POINT   // layer without mapping or data
                ))
        )

        val layers = ServerSideTestUtil.createLayerConfigsWithoutEncoding(plotOpts)
        SingleLayerAssert.assertThat(layers)
                .haveBinding(Aes.X, "x1")
                .haveDataVector("x1", inputVector)
    }

    @Test
    fun aesInLayerNoData() {
        val inputVector = listOf(1.0, 5.0)
        val aes = mapOf("x" to inputVector)

        val plotOpts = mutableMapOf<String, Any>(
                LAYERS to listOf(mapOf(
                        GEOM to GeomName.POINT,
                        Layer.MAPPING to aes
                ))
        )

        val layers = ServerSideTestUtil.createLayerConfigsWithoutEncoding(plotOpts)
        SingleLayerAssert.assertThat(layers)
                .haveBinding(Aes.X, "x")
                .haveDataVector("x", inputVector)
    }

    @Test
    fun aesAndDataInLayer() {
        val data = mapOf("x" to listOf(1.0, 1.0, 1))

        // aes specify different data vector for 'x' aes
        val inputVector = listOf(1.0, 5.0, 3.0)
        val aes = mapOf("x" to inputVector)

        val plotOpts = mutableMapOf<String, Any>(
                LAYERS to listOf(mapOf(
                        GEOM to GeomName.POINT,
                        Layer.MAPPING to aes,
                        Layer.DATA to data
                ))
        )

        val layers = ServerSideTestUtil.createLayerConfigsWithoutEncoding(plotOpts)
        SingleLayerAssert.assertThat(layers)
                .haveBinding(Aes.X, "x1")
                .haveDataVector("x1", inputVector)
    }
}
