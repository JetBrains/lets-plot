/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.builder.GeomLayer
import jetbrains.datalore.plot.builder.assemble.TestingPlotContext
import jetbrains.datalore.plot.builder.interact.TooltipSpec
import jetbrains.datalore.plot.builder.interact.TooltipSpec.Line
import jetbrains.datalore.plot.config.TestUtil.getSingleGeomLayer
import kotlin.test.Test
import kotlin.test.assertEquals

class TooltipWithConstantTest {

    @Test
    fun `tooltip with value from aes mapping`() {
        val layerSpec = """
            "geom": "vline",
            "mapping": {
                "xintercept": [ 0.25 ]
            }
        """
        val layer = buildGeomLayer(layerSpec)
        assertEquals(
            expected = listOf("xintercept: 0.25"),
            actual = getTooltipLines(layer)
        )
    }

    @Test
    fun `tooltip with constant value`() {
        val layerSpec = """
            "geom": "vline",
            "xintercept": 0.25
        """
        val layer = buildGeomLayer(layerSpec)

        assertEquals(
            expected = listOf("0.25"),
            actual = getTooltipLines(layer)
        )
    }

    @Test
    fun `should add positionals only`() {
        val layerSpec = """
            "geom": "vline",
            "xintercept": 0.25,
            "size": 1
        """
        val layer = buildGeomLayer(layerSpec)

        assertEquals(
            expected = listOf("0.25"),
            actual = getTooltipLines(layer)
        )
    }

    @Test
    fun `should use value from constant`() {
        val layerSpec = """
            "geom": "vline",
            "mapping": {
                "xintercept": [ 0.10 ]
            },
            "xintercept": 0.25
        """
        val layer = buildGeomLayer(layerSpec)

        assertEquals(
            expected = listOf("0.25"),
            actual = getTooltipLines(layer)
        )
    }

    @Test
    fun `should not add positional constants for geoms other than hline or vline`() {
        val layerSpec = """
            "geom": "point",
            "x": 0,
            "y": 0
        """
        val layer = buildGeomLayer(layerSpec)

        assertEquals(
            expected = emptyList(),
            actual = getTooltipLines(layer)
        )
    }

    private fun buildGeomLayer(layerSpec: String): GeomLayer {
        val spec = """{
            "data": { "x": [0.0, 0.5] },
            "mapping": { "x": "x" },
            "kind": "plot",
            "layers": [  { $layerSpec } ]
        }"""
        return getSingleGeomLayer(spec)
    }

    private fun getTooltipLines(geomLayer: GeomLayer): List<String> {
        val ctx = TestingPlotContext.create(geomLayer)
        return geomLayer.createContextualMapping().getDataPoints(index = 0, ctx)
            .map { dataPoint -> Line.withLabelAndValue(dataPoint.label, dataPoint.value) }
            .map(TooltipSpec.Line::toString)
    }
}