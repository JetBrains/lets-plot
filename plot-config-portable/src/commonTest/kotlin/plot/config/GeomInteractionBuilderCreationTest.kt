/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package plot.config

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.config.LayerConfig
import jetbrains.datalore.plot.config.Option
import jetbrains.datalore.plot.config.PlotConfigClientSideUtil
import jetbrains.datalore.plot.server.config.PlotConfigServerSide
import kotlin.test.Test
import kotlin.test.assertEquals

class GeomInteractionBuilderCreationTest {

    private val data = mapOf(
        Aes.X.name to listOf(1.0),
        Aes.Y.name to listOf(1.0)
    )

    @Test
    fun checkAesListsTest() {
        val mappedData = data + mapOf(
            Aes.COLOR.name to listOf('a')
        )
        val plotOpts = mutableMapOf(
            Option.Plot.MAPPING to mappedData,
            Option.Plot.LAYERS to listOf(
                mapOf(
                    Option.Layer.GEOM to Option.GeomName.HISTOGRAM
                )
            )
        )
        val layerConfig = createLayerConfig(plotOpts)

        val builder = PlotConfigClientSideUtil.createGeomInteractionBuilder(
            layerConfig,
            emptyList(),
            false
        )

        val expectedAxisList = listOf(Aes.X)
        val expectedAesListCount = (layerConfig.geomProto.renders() - expectedAxisList).size

        assertAesListCount(expectedAxisList.size, builder.axisAesList)
        assertAesListCount(expectedAesListCount, builder.aesListForTooltip)
    }


    @Test
    fun shouldSkipMapIdMapping() {
        val mappedData = data + mapOf(
           Aes.MAP_ID.name to listOf('a')
        )
        val plotOpts = mutableMapOf(
            Option.Plot.MAPPING to mappedData,
            Option.Plot.LAYERS to listOf(
                mapOf(
                    Option.Layer.GEOM to Option.GeomName.POINT
                )
            )
        )
        val layerConfig = createLayerConfig(plotOpts)

        val builder = PlotConfigClientSideUtil.createGeomInteractionBuilder(
            layerConfig,
            emptyList(),
            false
        )

        val expectedAxisList = listOf(Aes.X, Aes.Y)
        // without Aes.MAP_ID:
        val expectedAesListCount = (layerConfig.geomProto.renders() - expectedAxisList).size - 1

        assertAesListCount(expectedAxisList.size, builder.axisAesList)
        assertAesListCount(expectedAesListCount, builder.aesListForTooltip)
    }

     @Test
    fun shouldSkipMapIdMappingAndAxisVisibilityIsFalse() {
       val mappedData = data + mapOf(
           Aes.MAP_ID.name to listOf('a')
       )

        val plotOpts = mutableMapOf(
            Option.Plot.MAPPING to mappedData,
            Option.Plot.LAYERS to listOf(
                mapOf(
                    Option.Layer.GEOM to Option.GeomName.POLYGON
                )
            )
        )
        val layerConfig = createLayerConfig(plotOpts)

        val builder = PlotConfigClientSideUtil.createGeomInteractionBuilder(
            layerConfig,
            emptyList(),
            false
        )

        // builder's axis tooltip visibility is false:
        val expectedAxisCount = 0
        // without Aes.MAP_ID:
        val expectedAesListCount = (layerConfig.geomProto.renders() - listOf(Aes.X, Aes.Y)).size - 1

        assertAesListCount(expectedAxisCount, builder.axisAesList)
        assertAesListCount(expectedAesListCount, builder.aesListForTooltip)

    }

    @Test
    fun shouldNotDuplicateVarToAxisAndGenericTooltip() {
        val mappedData = mapOf(
            Aes.X.name to listOf(4.0),
            Aes.FILL.name to Aes.X.name
        )

        val plotOpts = mutableMapOf(
            Option.Plot.MAPPING to mappedData,
            Option.Plot.LAYERS to listOf(
                mapOf(
                    Option.Layer.GEOM to Option.GeomName.HISTOGRAM
                )
            )
        )
        val layerConfig = createLayerConfig(plotOpts)

        val builder = PlotConfigClientSideUtil.createGeomInteractionBuilder(
            layerConfig,
            emptyList(),
            false
        )

        val expectedAxisList = listOf(Aes.X)
        // without duplicated Aes.FILL:
        val expectedAesListCount = (layerConfig.geomProto.renders() - expectedAxisList).size - 1

        assertAesListCount(expectedAxisList.size, builder.axisAesList)
        assertAesListCount(expectedAesListCount, builder.aesListForTooltip)
    }


    private fun createLayerConfig(plotOpts: MutableMap<String, Any>): LayerConfig {
        val plotSpec = PlotConfigServerSide.processTransform(plotOpts)
        return PlotConfigServerSide(plotSpec).layerConfigs.first()
    }

    internal fun assertAesListCount(expectedCount: Int, aesList: List<Aes<*>>) {
        assertEquals(expectedCount, aesList.size)
    }
}