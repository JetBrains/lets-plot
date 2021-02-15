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
import jetbrains.datalore.plot.config.Option.Plot.SCALES
import jetbrains.datalore.plot.config.Option.Scale.AES
import jetbrains.datalore.plot.config.Option.Scale.SCALE_MAPPER_KIND
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
    fun `originally continuous var should be in tooltip`() {
        // Issue #241: Tooltip should appear if the mapped data is continuous
        // With color_fill_brewer:
        // after GuideMappers.continuousToDiscrete() tooltip may not display required originally continuous data.
        run {
            val builder = tileWithBrewerScale(useBrewerScale = true, useContinuousVars = true)
            val aesListForTooltip = getAesListInTooltip(builder.tooltipLines)
            assertTrue { Aes.FILL in aesListForTooltip }
        }
        run {
            val builder = tileWithBrewerScale(useBrewerScale = false, useContinuousVars = true)
            val aesListForTooltip = getAesListInTooltip(builder.tooltipLines)
            assertTrue { Aes.FILL in aesListForTooltip }
        }
    }

    @Test
    fun `discrete var should not be in tooltip`() {
        run {
            val builder = tileWithBrewerScale(useBrewerScale = true, useContinuousVars = false)
            val aesListForTooltip = getAesListInTooltip(builder.tooltipLines)
            assertFalse { Aes.FILL in aesListForTooltip }
        }
        run {
            val builder = tileWithBrewerScale(useBrewerScale = false, useContinuousVars = false)
            val aesListForTooltip = getAesListInTooltip(builder.tooltipLines)
            assertFalse { Aes.FILL in aesListForTooltip }
        }
    }

    private fun tileWithBrewerScale(useBrewerScale: Boolean, useContinuousVars: Boolean): GeomInteractionBuilder {
        val mappedData = mapOf(
            Aes.X.name to listOf(0),
            Aes.FILL.name to if (useContinuousVars) {
                listOf(0.1)
            } else {
                listOf('a')
            }
        )
        val scales = if (useBrewerScale) {
            listOf(
                mapOf(
                    AES to Aes.FILL.name,
                    SCALE_MAPPER_KIND to "color_brewer"
                )
            )
        } else {
            emptyList()
        }
        val plotOpts = mutableMapOf(
            MAPPING to mappedData,
            LAYERS to listOf(
                mapOf(
                    GEOM to "tile"
                )
            ),
            SCALES to scales
        )
        return createGeomInteractionBuilder(plotOpts)
    }

    private fun createGeomInteractionBuilder(plotOpts: MutableMap<String, Any>): GeomInteractionBuilder {
        val plotSpec = PlotConfigServerSide.processTransform(plotOpts)
        val plotConfig = PlotConfigClientSide.create(plotSpec)
        val layerConfig = PlotConfigClientSide.create(plotSpec).layerConfigs.first()
        return GeomInteractionUtil.createGeomInteractionBuilder(
            layerConfig = layerConfig,
            scaleMap = plotConfig.scaleMap,
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