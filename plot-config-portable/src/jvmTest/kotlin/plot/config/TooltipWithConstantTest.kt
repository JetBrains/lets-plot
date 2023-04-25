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
        val spec = makePlotSpec("""
            "geom": "vline",
            "mapping": {
                "xintercept": [ 0.25 ]
            }
        """)
        val layer = getSingleGeomLayer(spec)
        assertEquals(
            expected = listOf("xintercept: 0.25"),
            actual = getTooltipLines(layer)
        )
    }

    @Test
    fun `tooltip with constant value`() {
        val spec = makePlotSpec("""
            "geom": "vline",
            "xintercept": 0.25
        """)
        val layer = getSingleGeomLayer(spec)

        assertEquals(
            expected = listOf("0.25"),
            actual = getTooltipLines(layer)
        )
    }

    @Test
    fun `should add positionals only`() {
        val spec = makePlotSpec("""
            "geom": "vline",
            "xintercept": 0.25,
            "size": 1
        """)
        val layer = getSingleGeomLayer(spec)

        assertEquals(
            expected = listOf("0.25"),
            actual = getTooltipLines(layer)
        )
    }

    @Test
    fun `should use value from constant`() {
        val spec = makePlotSpec("""
            "geom": "vline",
            "mapping": {
                "xintercept": [ 0.10 ]
            },
            "xintercept": 0.25
        """)
        val layer = getSingleGeomLayer(spec)

        assertEquals(
            expected = listOf("0.25"),
            actual = getTooltipLines(layer)
        )
    }

    @Test
    fun `should not add positional constants for geoms other than hline or vline`() {
        val spec = makePlotSpec("""
            "geom": "point",
            "x": 0,
            "y": 0
        """)
        val layer = getSingleGeomLayer(spec)

        assertEquals(
            expected = emptyList(),
            actual = getTooltipLines(layer)
        )
    }

    @Test
    fun `add constant 'size' to tooltip`() {
        val spec = makePlotSpec("""
            "geom": "vline",
            "xintercept": 0.25,
            "size": 1.0,
            "tooltips": {"lines": ["^xintercept", "^size"]}
        """)
        val layer = getSingleGeomLayer(spec)

        assertEquals(
            expected = listOf("0.25", "1.0"),
            actual = getTooltipLines(layer)
        )
    }

    @Test
    fun `specify format for the constant`() {
        val spec = makePlotSpec("""
            "geom": "vline",
            "xintercept": 0.25,
            "tooltips": {"formats": [{ "field": "^xintercept", "format": "mean = {.2f}" } ] } 
        """)
        val layer = getSingleGeomLayer(spec)

        assertEquals(
            expected = listOf("mean = 0.25"),
            actual = getTooltipLines(layer)
        )
    }

    private fun makePlotSpec(layerSpec: String): String {
        return """
        {
            "kind": "plot",
            "layers": [ 
                {  
                    $layerSpec  
                }
            ]
        }""".trimIndent()
    }

    private fun getTooltipLines(geomLayer: GeomLayer): List<String> {
        val ctx = TestingPlotContext.create(geomLayer)
        return geomLayer.createContextualMapping().getDataPoints(index = 0, ctx)
            .map { dataPoint -> Line.withLabelAndValue(dataPoint.label, dataPoint.value) }
            .map(TooltipSpec.Line::toString)
    }
}