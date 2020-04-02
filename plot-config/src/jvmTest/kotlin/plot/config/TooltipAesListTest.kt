/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.config.Option.PlotBase.MAPPING
import jetbrains.datalore.plot.server.config.ServerSideTestUtil
import jetbrains.datalore.plot.server.config.SingleLayerAssert
import kotlin.test.Test

class TooltipAesListTest {

    private val data = mapOf(
        Aes.X.name to listOf(0.5, 1.0, 1.5),
        Aes.FILL.name to listOf(1, 2, 3),
        Aes.COLOR.name to listOf('a', 'b', 'c')
    )

    @Test
    fun noTooltipAesList() {
        val plotOpts = mutableMapOf(
            MAPPING to data,
            Option.Plot.LAYERS to listOf(
                mapOf(
                    Option.Layer.GEOM to Option.GeomName.POINT
                )
            )
        )
        val layerConfigs = ServerSideTestUtil.createLayerConfigsWithoutEncoding(plotOpts)
        SingleLayerAssert.assertThat(layerConfigs)
            .haveTooltipAesList(null)
    }

    @Test
    fun withTooltipAesList() {
        val plotOpts = mutableMapOf(
            MAPPING to data,
            Option.Plot.LAYERS to listOf(
                mapOf(
                    Option.Layer.GEOM to Option.GeomName.POINT,
                    Option.Layer.TOOLTIP to listOf(Aes.COLOR.name, Aes.FILL.name)
                )
            )
        )
        val layerConfigs = ServerSideTestUtil.createLayerConfigsWithoutEncoding(plotOpts)
        SingleLayerAssert.assertThat(layerConfigs)
            .haveTooltipAesList(listOf(Aes.COLOR, Aes.FILL))
    }

    @Test
    fun withEmptyTooltipAesList() {
        val plotOpts = mutableMapOf(
            MAPPING to data,
            Option.Plot.LAYERS to listOf(
                mapOf(
                    Option.Layer.GEOM to Option.GeomName.POINT,
                    Option.Layer.TOOLTIP to emptyList<String>()
                )
            )
        )
        val layerConfigs = ServerSideTestUtil.createLayerConfigsWithoutEncoding(plotOpts)
        SingleLayerAssert.assertThat(layerConfigs)
            .haveTooltipAesList(listOf())
    }
}