/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.builder.defaultTheme.DefaultTheme
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.AXIS_TEXT
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.AXIS_TOOLTIP
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.ELEMENT_BLANK
import jetbrains.datalore.plot.builder.interact.GeomInteractionBuilder
import jetbrains.datalore.plot.builder.presentation.DefaultFontFamilyRegistry
import jetbrains.datalore.plot.builder.theme.Theme
import jetbrains.datalore.plot.builder.tooltip.MappingValue
import jetbrains.datalore.plot.builder.tooltip.TooltipLine
import jetbrains.datalore.plot.config.GeomInteractionUtil
import jetbrains.datalore.plot.config.Option
import jetbrains.datalore.plot.config.Option.Layer.GEOM
import jetbrains.datalore.plot.config.Option.Meta
import jetbrains.datalore.plot.config.Option.Plot.LAYERS
import jetbrains.datalore.plot.config.Option.Plot.SCALES
import jetbrains.datalore.plot.config.Option.PlotBase.MAPPING
import jetbrains.datalore.plot.config.Option.Scale.AES
import jetbrains.datalore.plot.config.Option.Scale.SCALE_MAPPER_KIND
import jetbrains.datalore.plot.config.theme.ThemeConfig
import jetbrains.datalore.plot.config.transformToClientPlotConfig
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
        val expectedAesList = listOf(Aes.X, Aes.Y, Aes.FILL)
        val aesListForTooltip = getAesListInTooltip(builder.tooltipLines)
        assertAesList(expectedAesList, aesListForTooltip)
    }

    @Test
    fun `should not duplicate var to axis and generic tooltip`() {
        fun checkDuplicatedWithAxis(mappedData: Map<String, Any>) {
            val builder = histogramInteractionBuilder(mappedData)
            val aesListForTooltip = getAesListInTooltip(builder.tooltipLines)
            assertNoTooltipForAes(Aes.FILL, aesListForTooltip)
            val expectedAesList = listOf(Aes.X, Aes.Y)
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
                    Aes.FILL.name to listOf('a', 'b', 'c', 'd', 'e') // factors.size = 5 - enough to show in the tooltip
                )
            )
            val aesListForTooltip = getAesListInTooltip(builder.tooltipLines)
            assertTooltipForAes(Aes.FILL, aesListForTooltip)
        }
        run {
            val builder = histogramInteractionBuilder(
                mapOf(
                    Aes.X.name to listOf('a', 'b', 'c', 'd', 'e'),
                    Aes.FILL.name to listOf('a', 'b', 'a', 'b', 'c') // factors.size = 3 - not enough
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
        plotConfig.layerConfigs.forEach { layerConfig ->
            val builder = GeomInteractionUtil.createGeomInteractionBuilder(
                layerConfig = layerConfig,
                scaleMap = plotConfig.scaleMap,
                multilayerWithTooltips = false,
                isLiveMap = false,
                theme = DefaultTheme.minimal2()
            )
            val tooltipLines = builder.tooltipLines
            val aesListForTooltip = getAesListInTooltip(tooltipLines)
            assertAesList(
                listOf(Aes.X, Aes.Y, Aes.COLOR),
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
    fun `use 'theme' to control tooltips`() {
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
            val hideTooltips = mapOf(
                AXIS_TOOLTIP + "_x" to ELEMENT_BLANK,
                AXIS_TOOLTIP + "_y" to ELEMENT_BLANK,
            )
            val tooltipLines = histogramInteractionBuilder(data, themeOpts = hideTooltips).tooltipLines

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
                listOf(Aes.HEIGHT, Aes.FILL, Aes.X),
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

    private fun histogramInteractionBuilder(
        mappedData: Map<String, Any>,
        themeOpts: Map<String, Any> = emptyMap()
    ): GeomInteractionBuilder {
        val plotOpts = mutableMapOf(
            Meta.KIND to Meta.Kind.PLOT,
            MAPPING to mappedData,
            LAYERS to listOf(
                mapOf(
                    GEOM to Option.GeomName.HISTOGRAM
                )
            )
        )
        return createGeomInteractionBuilder(
            plotOpts,
            theme = ThemeConfig(themeOpts, DefaultFontFamilyRegistry()).theme
        )
    }

    private fun createGeomInteractionBuilder(
        plotOpts: MutableMap<String, Any>,
        theme: Theme = DefaultTheme.minimal2()
    ): GeomInteractionBuilder {
        val plotConfig = transformToClientPlotConfig(plotOpts)
        val layerConfig = plotConfig.layerConfigs.first()
        return GeomInteractionUtil.createGeomInteractionBuilder(
            layerConfig = layerConfig,
            scaleMap = plotConfig.scaleMap,
            multilayerWithTooltips = false,
            isLiveMap = false,
            theme
        )
    }

    private fun getAesListInTooltip(tooltipLines: List<TooltipLine>): List<Aes<*>> {
        return tooltipLines.flatMap { line ->
            line.fields.filterIsInstance<MappingValue>().map(MappingValue::aes)
        }
    }

    private fun getAesListInAxisTooltip(tooltipLines: List<TooltipLine>): List<Aes<*>> {
        return tooltipLines.flatMap { line ->
            line.fields.filterIsInstance<MappingValue>().filter(MappingValue::isAxis).map(MappingValue::aes)
        }
    }

    private fun getAesListInGeneralTooltip(tooltipLines: List<TooltipLine>): List<Aes<*>> {
        return tooltipLines.flatMap { line ->
            line.fields.filterIsInstance<MappingValue>().filterNot(MappingValue::isOutlier).map(MappingValue::aes)
        }
    }

    private fun assertAesList(expectedList: List<Aes<*>>, actualList: List<Aes<*>>) {
        assertEquals(expectedList.size, actualList.size)
        expectedList.forEach { aes ->
            assertTooltipForAes(aes, actualList)
        }
    }

    private fun assertNoTooltipForAes(aes: Aes<*>, aesListForTooltip: List<Aes<*>>) {
        assertFalse(
            aes in aesListForTooltip,
            "Aes '${aes.name}' should not be in tooltips, actual list: $aesListForTooltip"
        )
    }

    private fun assertTooltipForAes(aes: Aes<*>, aesListForTooltip: List<Aes<*>>) {
        assertTrue(aes in aesListForTooltip, "No tooltips for aes = '${aes.name}', actual list: $aesListForTooltip")
    }
}