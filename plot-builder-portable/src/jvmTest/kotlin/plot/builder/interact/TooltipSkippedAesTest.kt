/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.builder.GeomLayer
import jetbrains.datalore.plot.builder.interact.TestUtil.assertGeneralTooltips
import jetbrains.datalore.plot.builder.interact.TestUtil.assertNoGeneralTooltips
import jetbrains.datalore.plot.builder.interact.TestUtil.createTooltipSpecs
import jetbrains.datalore.plot.builder.interact.TestUtil.findLookupResults
import jetbrains.datalore.plot.builder.interact.loc.LayerTargetLocator
import jetbrains.datalore.plot.config.PlotConfigClientSideUtil
import jetbrains.datalore.plot.parsePlotSpec
import jetbrains.datalore.plot.server.config.PlotConfigServerSide
import kotlin.test.Test


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
        val targetLocator = LayerTargetLocator(
            GeomKind.BAR,
            layer.locatorLookupSpec,
            layer.contextualMapping,
            listOf(TestUtil.rectTarget(0, RECT))
        )
        val lookupResults = findLookupResults(listOf(targetLocator), COORD)
        val tooltipSpecs = createTooltipSpecs(lookupResults)

        assertGeneralTooltips(
            tooltipSpecs,
            "2.00"
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

        val tooltipSpecs = buildTooltipSpecs(spec)
        assertGeneralTooltips(
            tooltipSpecs,
            "z: 5.00"
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

        val tooltipSpecs = buildTooltipSpecs(spec)
        assertNoGeneralTooltips(tooltipSpecs)
    }

    companion object {
        private val COORD = TestUtil.point(10.0, 10.0)
        private val RECT = DoubleRectangle(0.0, 0.0, 20.0, 20.0)

        private fun createGeomLayers(spec: String): List<GeomLayer> {
            val plotSpec = PlotConfigServerSide.processTransform(parsePlotSpec(spec))
            return PlotConfigClientSideUtil.createPlotAssembler(plotSpec).layersByTile.single()
        }

        private fun buildTooltipSpecs(spec: String): List<TooltipSpec> {
            val layer = createGeomLayers(spec).single()
            val targetLocator = LayerTargetLocator(
                GeomKind.POINT,
                layer.locatorLookupSpec,
                layer.contextualMapping,
                listOf(TestUtil.pointTarget(0, COORD))
            )
            val lookupResults = findLookupResults(listOf(targetLocator), COORD)
            return createTooltipSpecs(lookupResults)
        }
    }
}