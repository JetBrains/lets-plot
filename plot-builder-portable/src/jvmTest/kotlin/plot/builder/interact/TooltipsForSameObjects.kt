/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact

import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.builder.GeomLayer
import jetbrains.datalore.plot.builder.interact.TestUtil.assertGeneralTooltips
import jetbrains.datalore.plot.builder.interact.TestUtil.createTooltipSpecs
import jetbrains.datalore.plot.builder.interact.TestUtil.findLookupResults
import jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocator
import jetbrains.datalore.plot.config.PlotConfigClientSideUtil
import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plot.server.config.PlotConfigServerSide
import kotlin.test.Test


class TooltipsForSameObjects {

    @Test
    fun `locator should take the last closest object of the layer`() {
        // Two point objects with same coordinates in one layer:
        // if there are more than one closest object should take the last - this way tooltip will be shown for the top object, not for the object lying beneath.

        val spec = """{
            "data": {
                "x": [ ${COORD.x}, ${COORD.x} ],
                "y": [ ${COORD.y}, ${COORD.y} ],
                "letter": [ "A", "B" ]
            },
            "kind": "plot",
            "layers": [
                {
                    "geom": "point",
                    "mapping": {
                        "x": "x",
                        "y": "y",
                        "color": "letter"
                    },
                    "tooltips": { "tooltip_lines": [ "@letter" ] }
                }
            ]
        }""".trimMargin()

        val layer = createGeomLayers(spec).single()
        val targetLocator = LayerTargetLocator(
            GeomKind.POINT,
            layer.locatorLookupSpec,
            layer.contextualMapping,
            listOf(FIRST_TARGET, SECOND_TARGET)
        )
        val lookupResults = findLookupResults(listOf(targetLocator), COORD)
        val tooltipSpecs = createTooltipSpecs(lookupResults)
        assertGeneralTooltips(
            tooltipSpecs,
            "B"
        )
    }

    @Test
    fun `tooltip should be taken for the object of the second layer`() {
        // Two point objects with same coordinates in different layers:
        // tooltip of the last layer will be shown

        val spec = """{
            "data": {
                "x": [ ${COORD.x} ],
                "y": [ ${COORD.y} ],
                "letterA": [ "A" ],
                "letterB": [ "B" ]
            },
            "kind": "plot",
            "layers": [
                {
                    "geom": "point",
                    "mapping": {
                        "x": "x",
                        "y": "y",
                        "color": "letterA"
                    },
                    "tooltips": { "tooltip_lines": [ "@letterA" ] }
                },
                {
                    "geom": "point",
                    "mapping": {
                        "x": "x",
                        "y": "y",
                        "color": "letterB"
                    },
                    "tooltips": { "tooltip_lines": [ "@letterB" ] }
                }
            ]
        }""".trimMargin()

        val layers = createGeomLayers(spec)
        val targetLocators = layers.map { layer ->
            LayerTargetLocator(
                GeomKind.POINT,
                layer.locatorLookupSpec,
                layer.contextualMapping,
                listOf(FIRST_TARGET)
            )
        }
        val lookupResults = findLookupResults(targetLocators, COORD)
        val tooltipSpecs = createTooltipSpecs(lookupResults)
        assertGeneralTooltips(
            tooltipSpecs,
            "B"
        )
    }

    companion object {
        private val COORD = TestUtil.point(10.0, 10.0)
        private const val FIRST_POINT_KEY = 0
        private const val SECOND_POINT_KEY = 1
        private val FIRST_TARGET = TestUtil.pointTarget(FIRST_POINT_KEY, COORD)
        private val SECOND_TARGET = TestUtil.pointTarget(SECOND_POINT_KEY, COORD)

        private fun createGeomLayers(spec: String): List<GeomLayer> {
            val plotSpec = PlotConfigServerSide.processTransform(parsePlotSpec(spec))
            return PlotConfigClientSideUtil.createPlotAssembler(plotSpec).layersByTile.single()
        }
    }
}