/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import demoAndTestShared.TestingGeomLayersBuilder.createSingleTileGeomLayers
import demoAndTestShared.parsePlotSpec
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupSpace
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator.LookupStrategy
import org.jetbrains.letsPlot.core.plot.builder.GeomLayer
import kotlin.test.Test
import kotlin.test.assertEquals

class LookupStrategyOnMultilayerPlotTest {

    private fun makePlotSpec(withLayer: String?) = """{
        'data': {
            'x': [0, 1, 2, 0, 1, 2],
            'y': [0, 0.5, 0.2, 1, 1.2, 0.7],
            'g': ['a', 'a', 'a', 'b', 'b', 'b']
        },
        'mapping': {'x': 'x', 'y': 'y'},
        'kind': 'plot',
        'layers': [
            {
                'geom': 'line', 
                'mapping': {'color': 'g'}
            }
            ${withLayer?.let { ", { $it }" } ?: ""}
        ]
    }""".trimIndent()

    @Test
    fun singleLayerPlot() {
        val layers = createSingleTileGeomLayers(parsePlotSpec(makePlotSpec(null)))
        assertEquals(1, layers.size)
        assertLookup_HoverX(layers.single())
    }

    @Test
    fun `add layer with mapping - may have tooltips - should switch to XY`() {
        val pointLayer = "'geom': 'point'"
        val layers = createSingleTileGeomLayers(parsePlotSpec(makePlotSpec(pointLayer)))
        assertEquals(2, layers.size)
        assertLookup_NearestXY(layers.first())
    }

    @Test
    fun `add layer with mapping, but hide tooltips - should keep original lookup strategy`() {
        val pointLayer = "'geom': 'point', 'tooltips': 'none'"
        val layers = createSingleTileGeomLayers(parsePlotSpec(makePlotSpec(pointLayer)))
        assertEquals(2, layers.size)
        assertLookup_HoverX(layers.first())
    }

    @Test
    fun `add layer without mapping - should keep original lookup strategy`() {
        val pointLayer = "'geom': 'point', 'x': 1, 'y': 0.8"
        val layers = createSingleTileGeomLayers(parsePlotSpec(makePlotSpec(pointLayer)))
        assertEquals(2, layers.size)
        assertLookup_HoverX(layers.first())
    }

    @Test
    fun `add layer without mapping, but with specified tooltips - should switch to XY`() {
        val pointLayer = "'geom': 'point', 'x': 1, 'y': 0.8, 'tooltips': { 'lines': ['Tooltip'] }"
        val layers = createSingleTileGeomLayers(parsePlotSpec(makePlotSpec(pointLayer)))
        assertEquals(2, layers.size)
        assertLookup_NearestXY(layers.first())
    }

    companion object {
        fun assertLookup_HoverX(layer: GeomLayer) {
            assertEquals(LookupSpace.X, layer.locatorLookupSpec.lookupSpace)
            assertEquals(LookupStrategy.HOVER, layer.locatorLookupSpec.lookupStrategy)
        }

        fun assertLookup_NearestXY(layer: GeomLayer) {
            assertEquals(LookupSpace.XY, layer.locatorLookupSpec.lookupSpace)
            assertEquals(LookupStrategy.NEAREST, layer.locatorLookupSpec.lookupStrategy)
        }
    }
}