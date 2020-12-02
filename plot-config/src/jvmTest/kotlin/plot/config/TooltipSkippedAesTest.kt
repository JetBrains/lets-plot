/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.interact.ContextualMapping
import jetbrains.datalore.plot.base.interact.GeomTarget
import jetbrains.datalore.plot.base.interact.TipLayoutHint
import jetbrains.datalore.plot.builder.GeomLayer
import jetbrains.datalore.plot.builder.interact.TooltipSpec
import jetbrains.datalore.plot.builder.interact.TooltipSpecFactory
import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plot.server.config.PlotConfigServerSide
import kotlin.test.Test
import kotlin.test.assertEquals


class TooltipSkippedAesTest {

    @Test
    fun `remove discrete duplicated mappings`() {
        val spec = """{
            "kind": "plot",
            "data": {
                "time": ["Lunch", "Lunch"] 
            },
            "mapping": {
                "x": "time",
                "y": "..count..",
                "fill": "..count.."
            },
            "layers": [ { "geom": "bar" } ],
            "scales": [
                {
                    "aesthetic": "fill",
                    "discrete": true
                }
            ]
        }"""

        val layer = createGeomLayers(spec).single()
        val tooltipSpecs = createTooltipSpecs(layer.contextualMapping)
        assertGeneralTooltips(
            tooltipSpecs,
            expectedLines = listOf("2.00")
        )
    }

    @Test
    fun `should skip duplicated mappings`() {
        val spec = """{
            "kind": "plot",
            "data": {
                  "x": [1],
                  "y": [1],
                  "z": [5]
             },
             "mapping": {
                  "x": "x",
                  "y": "y"
            },
            "layers": [
                {
                  "geom": "point",
                   "mapping": {
                       "color": "z",
                       "size" : "z"
                   }
                }
            ]
        }"""

        val layer = createGeomLayers(spec).single()
        val tooltipSpecs = createTooltipSpecs(layer.contextualMapping)
        assertGeneralTooltips(
            tooltipSpecs,
            expectedLines = listOf("z: 5.00")
        )
    }

    @Test
    fun `should skip discrete mappings`() {
        val spec = """{
            "kind": "plot",
            "data": {
                  "x": [1],
                  "y": [1],
                  "z": ["a"]
             },
             "mapping": {
                  "x": "x",
                  "y": "y"
            },
            "layers": [
                {
                  "geom": "point",
                   "mapping": {
                       "color": "z",
                       "size" : "z"
                   }
                }
            ]
        }"""

        val layer = createGeomLayers(spec).single()
        val tooltipSpecs = createTooltipSpecs(layer.contextualMapping)
        // No tooltips
        assertGeneralTooltips(
            tooltipSpecs,
            expectedLines = emptyList()
        )
    }

    companion object {

        private fun createGeomLayers(spec: String): List<GeomLayer> {
            val plotSpec = PlotConfigServerSide.processTransform(parsePlotSpec(spec))
            return PlotConfigClientSideUtil.createPlotAssembler(plotSpec).layersByTile.single()
        }

        private fun createTooltipSpecs(contextualMapping: ContextualMapping): List<TooltipSpec> {
            val factory = TooltipSpecFactory(contextualMapping, DoubleVector.ZERO)
            return factory.create(
                GeomTarget(
                    hitIndex = 0,
                    tipLayoutHint = TipLayoutHint.cursorTooltip(DoubleVector.ZERO, Color.BLACK),
                    aesTipLayoutHints = emptyMap()
                )
            )
        }

        private fun assertGeneralTooltips(tooltipSpecs: List<TooltipSpec>, expectedLines: List<String>) {
            val actualGeneralTooltips = tooltipSpecs.filterNot(TooltipSpec::isOutlier)
            assertEquals(expectedLines.isEmpty(), actualGeneralTooltips.isEmpty())
            if (actualGeneralTooltips.isNotEmpty()) {
                val actualLines = actualGeneralTooltips.single().lines.map(TooltipSpec.Line::toString)
                assertEquals(expectedLines.size, actualLines.size)
                expectedLines.zip(actualLines).forEach { (expected, actual) -> assertEquals(expected, actual) }
            }
        }
    }
}