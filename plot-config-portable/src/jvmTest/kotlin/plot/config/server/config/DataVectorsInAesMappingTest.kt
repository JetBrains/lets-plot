/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.config.Option.GeomName
import jetbrains.datalore.plot.config.Option.Layer.GEOM
import jetbrains.datalore.plot.config.Option.Meta.KIND
import jetbrains.datalore.plot.config.Option.Meta.Kind.PLOT
import jetbrains.datalore.plot.config.Option.Plot.LAYERS
import jetbrains.datalore.plot.config.Option.PlotBase.DATA
import jetbrains.datalore.plot.config.Option.PlotBase.MAPPING
import kotlin.test.Test

class DataVectorsInAesMappingTest {

    @Test
    fun aesInGGPlotNoData() {
        val inputVector = listOf(1.0, 5.0)

        val aes = mapOf("x" to inputVector)

        val plotOpts = mutableMapOf(
            KIND to PLOT,
            MAPPING to aes,
            LAYERS to listOf(
                mapOf(
                    GEOM to GeomName.POINT   // layer without mapping or data
                )
            )
        )

        val layers = ServerSideTestUtil.createLayerConfigsBeforeDataUpdate(plotOpts)
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
            KIND to PLOT,
            DATA to data,
            MAPPING to aes,
            LAYERS to listOf(
                mapOf(
                    GEOM to GeomName.POINT   // layer without mapping or data
                )
            )
        )

        val layers = ServerSideTestUtil.createLayerConfigsBeforeDataUpdate(plotOpts)
        SingleLayerAssert.assertThat(layers)
            .haveBinding(Aes.X, "x1")
            .haveDataVector("x1", inputVector)
    }

    @Test
    fun aesInLayerNoData() {
        val inputVector = listOf(1.0, 5.0)
        val aes = mapOf("x" to inputVector)

        val plotOpts = mutableMapOf<String, Any>(
            KIND to PLOT,
            LAYERS to listOf(
                mapOf(
                    GEOM to GeomName.POINT,
                    MAPPING to aes
                )
            )
        )

        val layers = ServerSideTestUtil.createLayerConfigsBeforeDataUpdate(plotOpts)
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
            KIND to PLOT,
            LAYERS to listOf(
                mapOf(
                    GEOM to GeomName.POINT,
                    MAPPING to aes,
                    DATA to data
                )
            )
        )

        val layers = ServerSideTestUtil.createLayerConfigsBeforeDataUpdate(plotOpts)
        SingleLayerAssert.assertThat(layers)
            .haveBinding(Aes.X, "x1")
            .haveDataVector("x1", inputVector)
    }

    @Test
    fun intConvertedToDouble() {
        val inputVector = listOf(1.0, 2.0, 3)
        val aes = mapOf("x" to inputVector)

        val plotOpts = mutableMapOf<String, Any>(
            KIND to PLOT,
            LAYERS to listOf(
                mapOf(
                    GEOM to GeomName.POINT,
                    MAPPING to aes,
                )
            )
        )

        val expectedVector = inputVector.map { it.toDouble() }

        val layers = ServerSideTestUtil.createLayerConfigsBeforeDataUpdate(plotOpts)
        SingleLayerAssert.assertThat(layers)
            .haveBinding(Aes.X, "x")
            .haveDataVector("x", expectedVector)
    }
}
