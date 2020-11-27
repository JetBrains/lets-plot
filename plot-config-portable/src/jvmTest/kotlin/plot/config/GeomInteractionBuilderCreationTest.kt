/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.builder.interact.GeomInteractionBuilder
import jetbrains.datalore.plot.builder.theme.DefaultTheme
import jetbrains.datalore.plot.builder.tooltip.MappingValue
import jetbrains.datalore.plot.builder.tooltip.TooltipLine
import jetbrains.datalore.plot.config.*
import jetbrains.datalore.plot.config.Option.Plot.LAYERS
import jetbrains.datalore.plot.config.Option.PlotBase.MAPPING
import jetbrains.datalore.plot.config.Option.Layer.GEOM
import jetbrains.datalore.plot.server.config.PlotConfigServerSide
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class GeomInteractionBuilderCreationTest {

    private val data = mapOf(
        Aes.X.name to listOf(1.0),
        Aes.Y.name to listOf(1.0)
    )

    @Test
    fun `check aes list for tooltip`() {
        val mappedData = data + mapOf(
            Aes.FILL.name to listOf(4.0)
        )
        val plotOpts = mutableMapOf(
            MAPPING to mappedData,
            LAYERS to listOf(
                mapOf(
                    GEOM to Option.GeomName.HISTOGRAM
                )
            )
        )
        val builder = createGeomInteractionBuilder(plotOpts)
        val expectedAesList = listOf(Aes.X, Aes.Y, Aes.FILL)
        val aesListForTooltip = getAesListInTooltip(builder.tooltipLines)
        assertAesList(expectedAesList, aesListForTooltip)
    }

    @Test
    fun `should not duplicate var to axis and generic tooltip`() {
        val mappedData = mapOf(
            Aes.X.name to listOf(4.0),
            Aes.FILL.name to Aes.X.name
        )

        val plotOpts = mutableMapOf(
            MAPPING to mappedData,
            LAYERS to listOf(
                mapOf(
                    GEOM to Option.GeomName.HISTOGRAM
                )
            )
        )
        val builder = createGeomInteractionBuilder(plotOpts)

        val aesListForTooltip = getAesListInTooltip(builder.tooltipLines)
        assertFalse { aesListForTooltip.contains(Aes.FILL) }
        val expectedAesList = listOf(Aes.X, Aes.Y)
        assertAesList(expectedAesList, aesListForTooltip)
    }

    @Test
    fun `should skip discrete mappings`() {
        val mappedData = data + mapOf(
            Aes.X.name to listOf(4.0),
            Aes.FILL.name to listOf('a'),
            Aes.COLOR.name to listOf('a'),
            Aes.SIZE.name to listOf(1.0)
        )
        val plotOpts = mutableMapOf(
            MAPPING to mappedData,
            LAYERS to listOf(
                mapOf(
                    GEOM to Option.GeomName.HISTOGRAM
                )
            )
        )
        val builder = createGeomInteractionBuilder(plotOpts)

        val aesListForTooltip = getAesListInTooltip(builder.tooltipLines)
        assertFalse { aesListForTooltip.contains(Aes.FILL) }
        assertFalse { aesListForTooltip.contains(Aes.COLOR) }
        val expectedAesList = listOf(Aes.X, Aes.Y, Aes.SIZE)
        assertAesList(expectedAesList, aesListForTooltip)

    }

    @Test
    fun `should skip duplicated mappings`() {
        val v = "v" to listOf(4.0)
        val mappedData = data + v + mapOf(
            Aes.FILL.name to "v",
            Aes.COLOR.name to "v"
        )

        val plotOpts = mutableMapOf(
            MAPPING to mappedData,
            LAYERS to listOf(
                mapOf(
                    GEOM to Option.GeomName.POINT
                )
            )
        )
        val builder = createGeomInteractionBuilder(plotOpts)

        val aesListForTooltip = getAesListInTooltip(builder.tooltipLines)
        assertFalse { aesListForTooltip.contains(Aes.FILL) }
        val expectedAesList = listOf(Aes.X, Aes.Y, Aes.COLOR)
        assertAesList(expectedAesList, aesListForTooltip)
    }

    private fun createGeomInteractionBuilder(plotOpts: MutableMap<String, Any>) : GeomInteractionBuilder {
            val plotSpec = PlotConfigServerSide.processTransform(plotOpts)
            val plotConfig = PlotConfigClientSide.create(plotSpec)
            val layerConfig = PlotConfigClientSide.create(plotSpec).layerConfigs.first()
            return GeomInteractionUtil.createGeomInteractionBuilder(
                layerConfig = layerConfig,
                scaleMap =  plotConfig.scaleMap,
                multilayer = false,
                isLiveMap = false,
                theme = DefaultTheme()
            )
    }
    private fun getAesListInTooltip(tooltipLines: List<TooltipLine>): List<Aes<*>> {
        return tooltipLines.flatMap { line ->
            line.fields.filterIsInstance<MappingValue>().map(MappingValue::aes)
        }
    }

    private fun assertAesList(expectedList: List<Aes<*>>, actualList: List<Aes<*>>) {
        assertEquals(expectedList.size, actualList.size)
        expectedList.forEach { aes ->
            assertTrue(aes in actualList, "No tooltips for aes = ${aes.name}")
        }
    }
}