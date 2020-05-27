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

class TooltipConfigTest {

    private val data = mapOf(
        Aes.X.name to listOf(0.5, 1.0, 1.5),
        Aes.FILL.name to listOf(1, 2, 3),
        Aes.COLOR.name to listOf('a', 'b', 'c')
    )

    @Test
    fun noTooltipList() {
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
            .haveTooltipList(null)
    }

    @Test
    fun withTooltipAesList() {
        val plotOpts = mutableMapOf(
            MAPPING to data,
            Option.Plot.LAYERS to listOf(
                mapOf(
                    Option.Layer.GEOM to Option.GeomName.POINT,
                    Option.Layer.TOOLTIPS to mapOf(
                        Option.LayerTooltips.LINES to listOf("aes@" + Aes.COLOR.name, "aes@" + Aes.FILL.name)
                    )
                )
            )
        )
        val layerConfigs = ServerSideTestUtil.createLayerConfigsWithoutEncoding(plotOpts)
        SingleLayerAssert.assertThat(layerConfigs)
            .haveTooltipList(listOf(Aes.COLOR.name, Aes.FILL.name))
    }

    @Test
    fun withEmptyTooltipList() {
        val plotOpts = mutableMapOf(
            MAPPING to data,
            Option.Plot.LAYERS to listOf(
                mapOf(
                    Option.Layer.GEOM to Option.GeomName.POINT,
                    Option.Layer.TOOLTIPS to mapOf(
                            Option.LayerTooltips.LINES to emptyList<String>()
                    )
                )
            )
        )
        val layerConfigs = ServerSideTestUtil.createLayerConfigsWithoutEncoding(plotOpts)
        SingleLayerAssert.assertThat(layerConfigs)
            .haveTooltipList(listOf())
    }


    @Test
    fun withVariables() {
        val dataVars = data + mapOf(
            "VAR1" to listOf("text1", "text2", "text3"),
            "VAR2" to listOf(2.0, 2.5, 3.0)
        )

        val plotOpts = mutableMapOf(
            MAPPING to dataVars,
            Option.Plot.LAYERS to listOf(
                mapOf(
                    Option.Layer.GEOM to Option.GeomName.POINT,
                    Option.Layer.TOOLTIPS to mapOf(
                        Option.LayerTooltips.LINES to listOf("aes@" + Aes.COLOR.name, "aes@" + Aes.FILL.name,  "VAR1",  "VAR2")
                    )
                )
            )
        )
        val layerConfigs = ServerSideTestUtil.createLayerConfigsWithoutEncoding(plotOpts)
        SingleLayerAssert.assertThat(layerConfigs)
            .haveTooltipList(listOf(Aes.COLOR.name, Aes.FILL.name, "VAR1", "VAR2"))
    }

}