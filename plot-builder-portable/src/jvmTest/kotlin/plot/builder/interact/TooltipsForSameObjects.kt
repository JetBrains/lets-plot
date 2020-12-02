/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.interact.GeomTargetLocator
import jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocator
import jetbrains.datalore.plot.builder.interact.loc.LocatedTargetsPicker
import jetbrains.datalore.plot.config.PlotConfigClientSideUtil
import jetbrains.datalore.plot.parsePlotSpec
import kotlin.test.Test
import kotlin.test.assertEquals


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
        val lookupResults = findTargets(listOf(targetLocator))
        val tooltipSpecs = createTooltipSpecs(lookupResults)
        assertGeneralTooltipLines(
            tooltipSpecs,
            listOf("B")
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
        val lookupResults = findTargets(targetLocators)
        val tooltipSpecs = createTooltipSpecs(lookupResults)
        assertGeneralTooltipLines(
            tooltipSpecs,
            listOf("B")
        )
    }

    companion object {
        private val COORD = TestUtil.point(10.0, 10.0)
        private const val FIRST_POINT_KEY = 0
        private const val SECOND_POINT_KEY = 1
        private val FIRST_TARGET = TestUtil.pointTarget(FIRST_POINT_KEY, COORD)
        private val SECOND_TARGET = TestUtil.pointTarget(SECOND_POINT_KEY, COORD)

        private fun createGeomLayers(spec: String) =
            PlotConfigClientSideUtil.createPlotAssembler(parsePlotSpec(spec)).layersByTile.single()

        private fun findTargets(targetLocators: List<GeomTargetLocator>): List<GeomTargetLocator.LookupResult> {
            val targetsPicker = LocatedTargetsPicker()
            targetLocators.forEach { locator ->
                val lookupResult = locator.search(COORD)
                lookupResult?.let { targetsPicker.addLookupResult(it) }
            }
            return targetsPicker.picked
        }

        private fun createTooltipSpecs(lookupResults: List<GeomTargetLocator.LookupResult>): List<TooltipSpec> {
            val tooltipSpecs = ArrayList<TooltipSpec>()
            lookupResults.forEach { result ->
                val factory = TooltipSpecFactory(result.contextualMapping, DoubleVector.ZERO)
                result.targets.forEach { geomTarget -> tooltipSpecs.addAll(factory.create(geomTarget)) }
            }
            return tooltipSpecs
        }

        private fun assertGeneralTooltipLines(tooltipSpecs: List<TooltipSpec>, expectedTooltipLines: List<String>) {
            val actualGeneralLines =
                tooltipSpecs.filterNot(TooltipSpec::isOutlier).flatMap { it.lines.map(TooltipSpec.Line::toString) }
            assertEquals(expectedTooltipLines.size, actualGeneralLines.size)
            assertEquals(expectedTooltipLines, actualGeneralLines)
        }
    }
}