/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import demoAndTestShared.TestingGeomLayersBuilder.getSingleGeomLayer
import org.jetbrains.letsPlot.core.spec.config.TooltipTestUtil.assertGeneralTooltip
import kotlin.test.Test

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
        assertGeneralTooltip(
            layer,
            expectedLines = listOf("xintercept: 0.25")
        )
    }

    @Test
    fun `tooltip with constant value`() {
        val spec = makePlotSpec("""
            "geom": "vline",
            "xintercept": 0.25
        """)
        val layer = getSingleGeomLayer(spec)
        assertGeneralTooltip(
            layer,
            expectedLines = listOf("0.25")
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
        assertGeneralTooltip(
            layer,
            expectedLines = listOf("0.25")
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
        assertGeneralTooltip(
            layer,
            expectedLines = listOf("0.25")
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
        assertGeneralTooltip(
            layer,
            expectedLines = emptyList()
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
        assertGeneralTooltip(
            layer,
            expectedLines = listOf("0.25", "1.0")
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
        assertGeneralTooltip(
            layer,
            expectedLines = listOf("mean = 0.25")
        )
    }

    @Test
    fun `multiline default tooltip should have labels`() {
        val spec = makePlotSpec("""
            "geom": "vline",
            "xintercept": 0.25,
            "mapping": {
                "size": [ 1.0 ]
            }
        """)
        val layer = getSingleGeomLayer(spec)
        assertGeneralTooltip(
            layer,
            expectedLines = listOf( "size: 1.00", "xintercept: 0.25")
        )
    }

    @Test
    fun `multiline default tooltip with specified format`() {
        val spec = makePlotSpec("""
            "geom": "vline",
            "xintercept": 0.25,
            "mapping": {
                "size": [ 1.0 ]
            },
            "tooltips": { 
                "formats": [
                    { "field": "^xintercept", "format": ".3f" },
                    { "field": "^size", "format": "d" }
                ]
            } 
        """)
        val layer = getSingleGeomLayer(spec)
        assertGeneralTooltip(
            layer,
            expectedLines = listOf( "size: 1", "xintercept: 0.250")
        )
    }

    @Test
    fun `one-line specified tooltip - '@' should add label`() {
        val spec = makePlotSpec("""
            "geom": "vline",
            "xintercept": 0.25,
            "tooltips": {"lines": ["@|^xintercept"]}
        """)
        val layer = getSingleGeomLayer(spec)
        assertGeneralTooltip(
            layer,
            expectedLines = listOf("xintercept: 0.25")
        )
    }

    @Test
    fun `multiline specified tooltip - '@' should add label`() {
        val spec = makePlotSpec("""
            "geom": "vline",
            "xintercept": 0.25,
            "mapping": {
                "size": [ 1.0 ]
            },
            "tooltips": {"lines": ["@|^xintercept", "@|^size"]}
        """)
        val layer = getSingleGeomLayer(spec)
        assertGeneralTooltip(
            layer,
            expectedLines = listOf("xintercept: 0.25",  "size: 1.00")
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
}