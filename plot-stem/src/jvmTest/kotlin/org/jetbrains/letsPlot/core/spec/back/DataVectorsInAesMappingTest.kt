/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.spec.Option.GeomName
import org.jetbrains.letsPlot.core.spec.Option.Layer.GEOM
import org.jetbrains.letsPlot.core.spec.Option.Meta.KIND
import org.jetbrains.letsPlot.core.spec.Option.Meta.Kind.PLOT
import org.jetbrains.letsPlot.core.spec.Option.Plot.LAYERS
import org.jetbrains.letsPlot.core.spec.Option.PlotBase.DATA
import org.jetbrains.letsPlot.core.spec.Option.PlotBase.MAPPING
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

        val layers = BackendTestUtil.createLayerConfigsBeforeDataUpdate(plotOpts)
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

        val layers = BackendTestUtil.createLayerConfigsBeforeDataUpdate(plotOpts)
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

        val layers = BackendTestUtil.createLayerConfigsBeforeDataUpdate(plotOpts)
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

        val layers = BackendTestUtil.createLayerConfigsBeforeDataUpdate(plotOpts)
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

        val layers = BackendTestUtil.createLayerConfigsBeforeDataUpdate(plotOpts)
        SingleLayerAssert.assertThat(layers)
            .haveBinding(Aes.X, "x")
            .haveDataVector("x", expectedVector)
    }
}
