/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.DefaultTheme
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.AXIS_TEXT
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.AXIS_TOOLTIP
import org.jetbrains.letsPlot.core.plot.builder.defaultTheme.values.ThemeOption.ELEMENT_BLANK
import org.jetbrains.letsPlot.core.plot.builder.presentation.DefaultFontFamilyRegistry
import org.jetbrains.letsPlot.core.plot.builder.tooltip.LinePattern
import org.jetbrains.letsPlot.core.plot.builder.tooltip.conf.GeomInteractionBuilder
import org.jetbrains.letsPlot.core.plot.builder.tooltip.data.MappingField
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.Option.Layer.GEOM
import org.jetbrains.letsPlot.core.spec.Option.Meta
import org.jetbrains.letsPlot.core.spec.Option.Plot.LAYERS
import org.jetbrains.letsPlot.core.spec.Option.Plot.SCALES
import org.jetbrains.letsPlot.core.spec.Option.PlotBase.MAPPING
import org.jetbrains.letsPlot.core.spec.Option.Scale.AES
import org.jetbrains.letsPlot.core.spec.Option.Scale.SCALE_MAPPER_KIND
import org.jetbrains.letsPlot.core.spec.Option.Theme.TOOLTIP_RECT
import org.jetbrains.letsPlot.core.spec.front.GeomInteractionUtil
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

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
        val builder = histogramInteractionBuilder(mappedData)
        val expectedAesList = listOf(
            Aes.X,
            Aes.Y,
            Aes.FILL
        )
        val aesListForTooltip = getAesListInTooltip(builder.tooltipLines)
        assertAesList(expectedAesList, aesListForTooltip)
    }

    @Test
    fun `should not duplicate var to axis and generic tooltip`() {
        fun checkDuplicatedWithAxis(mappedData: Map<String, Any>) {
            val builder = histogramInteractionBuilder(mappedData)
            val aesListForTooltip = getAesListInTooltip(builder.tooltipLines)
            assertNoTooltipForAes(Aes.FILL, aesListForTooltip)
            val expectedAesList =
                listOf(Aes.X, Aes.Y)
            assertAesList(expectedAesList, aesListForTooltip)
        }

        run {
            val mappedData = mapOf(
                Aes.X.name to listOf(4.0),
                Aes.FILL.name to Aes.X.name
            )
            checkDuplicatedWithAxis(mappedData)
        }

        run {
            // discrete var with multiple factors enough for the tooltip
            val mappedData = mapOf(
                Aes.X.name to listOf('a', 'b', 'c', 'd', 'e'),
                Aes.FILL.name to Aes.X.name
            )
            checkDuplicatedWithAxis(mappedData)
        }
    }

    @Test
    fun `should skip discrete mappings (small number of factors)`() {
        val mappedData = data + mapOf(
            Aes.X.name to listOf(4.0),
            Aes.FILL.name to listOf('a'),
            Aes.COLOR.name to listOf('a'),
            Aes.SIZE.name to listOf(1.0)
        )
        val builder = histogramInteractionBuilder(mappedData)
        val aesListForTooltip = getAesListInTooltip(builder.tooltipLines)
        assertNoTooltipForAes(Aes.FILL, aesListForTooltip)
        assertNoTooltipForAes(Aes.COLOR, aesListForTooltip)
        val expectedAesList = listOf(
            Aes.X,
            Aes.Y,
            Aes.SIZE
        )
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
            assertTooltipForAes(Aes.FILL, aesListForTooltip)
        }
        run {
            val builder = tileWithBrewerScale(useBrewerScale = false, useContinuousVars = true)
            val aesListForTooltip = getAesListInTooltip(builder.tooltipLines)
            assertTooltipForAes(Aes.FILL, aesListForTooltip)
        }
    }

    @Test
    fun `discrete var should not be in tooltip (small number of factors)`() {
        run {
            val builder = tileWithBrewerScale(useBrewerScale = true, useContinuousVars = false)
            val aesListForTooltip = getAesListInTooltip(builder.tooltipLines)
            assertNoTooltipForAes(Aes.FILL, aesListForTooltip)
        }
        run {
            val builder = tileWithBrewerScale(useBrewerScale = false, useContinuousVars = false)
            val aesListForTooltip = getAesListInTooltip(builder.tooltipLines)
            assertNoTooltipForAes(Aes.FILL, aesListForTooltip)
        }
    }

    @Test
    fun `tooltips depending on number of factors`() {
        run {
            val builder = histogramInteractionBuilder(
                mapOf(
                    Aes.X.name to listOf('a', 'b', 'c', 'd', 'e'),
                    Aes.FILL.name to listOf(
                        'a',
                        'b',
                        'c',
                        'd',
                        'e'
                    ) // factors.size = 5 - enough to show in the tooltip
                )
            )
            val aesListForTooltip = getAesListInTooltip(builder.tooltipLines)
            assertTooltipForAes(Aes.FILL, aesListForTooltip)
        }
        run {
            val builder = histogramInteractionBuilder(
                mapOf(
                    Aes.X.name to listOf('a', 'b', 'c', 'd', 'e'),
                    Aes.FILL.name to listOf(
                        'a',
                        'b',
                        'a',
                        'b',
                        'c'
                    ) // factors.size = 3 - not enough
                )
            )
            val aesListForTooltip = getAesListInTooltip(builder.tooltipLines)
            assertNoTooltipForAes(Aes.FILL, aesListForTooltip)
        }
    }

    @Test
    fun `the total number of factors from two layers is enough to show the tooltip`() {
        val layer1 = mapOf(
            GEOM to Option.GeomName.POINT,
            MAPPING to mapOf(
                Aes.X.name to listOf(0, 1, 2),
                Aes.Y.name to listOf(0, 0, 0),
                Aes.COLOR.name to listOf('a', 'b', 'c')
            )
        )
        val layer2 = mapOf(
            GEOM to Option.GeomName.POINT,
            MAPPING to mapOf(
                Aes.X.name to listOf(3, 4, 5),
                Aes.Y.name to listOf(0, 0, 0),
                Aes.COLOR.name to listOf('d', 'e', 'f')
            )
        )
        val plotOpts = mutableMapOf(
            Meta.KIND to Meta.Kind.PLOT,
            LAYERS to listOf(layer1, layer2)
        )

        val plotConfig = transformToClientPlotConfig(plotOpts)
        val scaleMap = plotConfig.createScales()
        plotConfig.layerConfigs.forEach { layerConfig ->
            val builder = GeomInteractionUtil.createGeomInteractionBuilder(
                layerConfig = layerConfig,
                scaleMap = scaleMap,
                multilayerWithTooltips = false,
                isLiveMap = false,
                isPolarCoordSystem = false,
                theme = DefaultTheme.minimal2()
            )
            val tooltipLines = builder.tooltipLines
            val aesListForTooltip = getAesListInTooltip(tooltipLines)
            assertAesList(
                listOf(
                    Aes.X,
                    Aes.Y,
                    Aes.COLOR
                ),
                aesListForTooltip
            )
        }
    }

    @Test
    fun `X axis tooltip should be always shown (univariate function)`() {
        fun assertAxisXHasTooltip(xMapping: Pair<String, List<Any>>) {
            val builder = histogramInteractionBuilder(mapOf(xMapping))
            val aesListForTooltip = getAesListInAxisTooltip(builder.tooltipLines)
            assertTooltipForAes(Aes.X, aesListForTooltip)
        }

        // discrete - not depend on factors
        assertAxisXHasTooltip(Aes.X.name to listOf('a'))
        assertAxisXHasTooltip(Aes.X.name to listOf('a', 'b', 'c', 'd', 'e'))

        // continuous
        assertAxisXHasTooltip(Aes.X.name to listOf(0.0))
    }

    @Test
    fun `use 'theme' to control axis tooltips`() {
        run {
            // default: X axis tooltip + Y value in the general tooltip
            val tooltipLines = histogramInteractionBuilder(data, themeOpts = emptyMap()).tooltipLines

            val axis = getAesListInAxisTooltip(tooltipLines)
            assertTooltipForAes(Aes.X, axis)

            val general = getAesListInGeneralTooltip(tooltipLines)
            assertTooltipForAes(Aes.Y, general)
        }
        run {
            // if axis tooltip is hidden - remove value also from the general tooltip
            val hideAxisTooltips = mapOf(
                AXIS_TOOLTIP + "_x" to ELEMENT_BLANK,
                AXIS_TOOLTIP + "_y" to ELEMENT_BLANK,
            )
            val tooltipLines = histogramInteractionBuilder(data, themeOpts = hideAxisTooltips).tooltipLines

            val axis = getAesListInAxisTooltip(tooltipLines)
            assertNoTooltipForAes(Aes.X, axis)

            val general = getAesListInGeneralTooltip(tooltipLines)
            assertNoTooltipForAes(Aes.Y, general)
        }
        run {
            // if axis tick labels are hidden - not show axis tooltip but this value can be in the general tooltip
            val hideAxisTickLabels = mapOf(
                AXIS_TEXT + "_x" to ELEMENT_BLANK,
                AXIS_TEXT + "_y" to ELEMENT_BLANK,
            )
            val tooltipLines = histogramInteractionBuilder(data, themeOpts = hideAxisTickLabels).tooltipLines

            val axis = getAesListInAxisTooltip(tooltipLines)
            assertNoTooltipForAes(Aes.X, axis)

            val general = getAesListInGeneralTooltip(tooltipLines)
            assertTooltipForAes(Aes.Y, general)
        }
    }

    @Test
    fun `use 'theme' to control general and side tooltips`() {
        val mappedData = data + mapOf(
            Aes.FILL.name to listOf(4.0)
        )

        run {   // default
            val tooltipLines = geomInteractionBuilder(
                mappedData,
                geom = Option.GeomName.BOX_PLOT,
                themeOpts = emptyMap()
            ).tooltipLines

            val axis = getAesListInAxisTooltip(tooltipLines)
            assertTooltipForAes(Aes.X, axis)

            val general = getAesListInGeneralTooltip(tooltipLines)
            assertTooltipForAes(Aes.FILL, general)

            val side = getAesListInSideTooltips(tooltipLines)
            assertAesList(listOf(Aes.YMAX, Aes.UPPER, Aes.MIDDLE, Aes.LOWER, Aes.YMIN), side)
        }

        run {   // theme(tooltip='blank') => hide general and side tooltips, axis tooltips are visible
            val tooltipLines = geomInteractionBuilder(
                mappedData,
                geom = Option.GeomName.BOX_PLOT,
                themeOpts = mapOf(TOOLTIP_RECT to ELEMENT_BLANK)
            ).tooltipLines

            val axis = getAesListInAxisTooltip(tooltipLines)
            assertTooltipForAes(Aes.X, axis)

            val general = getAesListInGeneralTooltip(tooltipLines)
            assertTrue(general.isEmpty())

            val side = getAesListInSideTooltips(tooltipLines)
            assertTrue(side.isEmpty())
        }
    }

    @Test
    fun `quantile should be in tooltip if it is mapped to FILL`() {

        fun areaRidgePlotOpts(withFillMapping: Boolean): MutableMap<String, Any> {
            val mapping: Map<String, Any> = mapOf(
                Aes.X.name to listOf(0, 1, 2),
                Aes.Y.name to listOf(0, 1, 0)
            ).let {
                if (withFillMapping) {
                    it + mapOf(Aes.FILL.name to "..quantile..")
                } else {
                    it
                }
            }
            val layer = mapOf(
                GEOM to "area_ridges",
                MAPPING to mapping,
                Option.Geom.AreaRidges.QUANTILE_LINES to true
            )
            return mutableMapOf(
                Meta.KIND to Meta.Kind.PLOT,
                LAYERS to listOf(layer)
            )
        }

        run {
            val builder = createGeomInteractionBuilder(areaRidgePlotOpts(withFillMapping = false))
            val aesListForTooltip = getAesListInTooltip(builder.tooltipLines)
            assertAesList(
                listOf(Aes.HEIGHT, Aes.X),
                aesListForTooltip
            )
        }
        run {
            val builder = createGeomInteractionBuilder(areaRidgePlotOpts(withFillMapping = true))
            val aesListForTooltip = getAesListInTooltip(builder.tooltipLines)
            assertAesList(
                listOf(
                    Aes.HEIGHT,
                    Aes.FILL,
                    Aes.X
                ),
                aesListForTooltip
            )
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
            Meta.KIND to Meta.Kind.PLOT,
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

    private fun geomInteractionBuilder(
        mappedData: Map<String, Any>,
        themeOpts: Map<String, Any> = emptyMap(),
        geom: String
    ): GeomInteractionBuilder {
        val plotOpts = mutableMapOf(
            Meta.KIND to Meta.Kind.PLOT,
            MAPPING to mappedData,
            LAYERS to listOf(
                mapOf(
                    GEOM to geom
                )
            )
        )
        return createGeomInteractionBuilder(
            plotOpts,
            theme = ThemeConfig(themeOpts, DefaultFontFamilyRegistry()).theme
        )
    }

    private fun histogramInteractionBuilder(
        mappedData: Map<String, Any>,
        themeOpts: Map<String, Any> = emptyMap()
    ) = geomInteractionBuilder(mappedData, themeOpts, Option.GeomName.HISTOGRAM)

    private fun createGeomInteractionBuilder(
        plotOpts: MutableMap<String, Any>,
        theme: Theme = DefaultTheme.minimal2()
    ): GeomInteractionBuilder {
        val plotConfig = transformToClientPlotConfig(plotOpts)
        val scaleMap = plotConfig.createScales()

        val layerConfig = plotConfig.layerConfigs.first()
        return GeomInteractionUtil.createGeomInteractionBuilder(
            layerConfig = layerConfig,
            scaleMap = scaleMap,
            multilayerWithTooltips = false,
            isLiveMap = false,
            isPolarCoordSystem = false,
            theme
        )
    }

    private fun getAesListInTooltip(tooltipLines: List<LinePattern>): List<Aes<*>> {
        return tooltipLines.flatMap { line ->
            line.fields.filterIsInstance<MappingField>().map(MappingField::aes)
        }
    }

    private fun getAesListInAxisTooltip(tooltipLines: List<LinePattern>): List<Aes<*>> {
        return tooltipLines.flatMap { line ->
            line.fields.filterIsInstance<MappingField>().filter(MappingField::isAxis).map(MappingField::aes)
        }
    }

    private fun getAesListInGeneralTooltip(tooltipLines: List<LinePattern>): List<Aes<*>> {
        return tooltipLines.flatMap { line ->
            line.fields.filterIsInstance<MappingField>().filterNot(MappingField::isSide).map(MappingField::aes)
        }
    }

    private fun getAesListInSideTooltips(tooltipLines: List<LinePattern>): List<Aes<*>> {
        return tooltipLines.flatMap { line ->
            line.fields.filterIsInstance<MappingField>().filter { it.isSide && !it.isAxis }.map(MappingField::aes)
        }
    }

    private fun assertAesList(
        expectedList: List<Aes<*>>,
        actualList: List<Aes<*>>
    ) {
        assertEquals(expectedList.size, actualList.size)
        expectedList.forEach { aes ->
            assertTooltipForAes(aes, actualList)
        }
    }

    private fun assertNoTooltipForAes(
        aes: Aes<*>,
        aesListForTooltip: List<Aes<*>>
    ) {
        assertFalse(
            aes in aesListForTooltip,
            "Aes '${aes.name}' should not be in tooltips, actual list: $aesListForTooltip"
        )
    }

    private fun assertTooltipForAes(
        aes: Aes<*>,
        aesListForTooltip: List<Aes<*>>
    ) {
        assertTrue(aes in aesListForTooltip, "No tooltips for aes = '${aes.name}', actual list: $aesListForTooltip")
    }
}