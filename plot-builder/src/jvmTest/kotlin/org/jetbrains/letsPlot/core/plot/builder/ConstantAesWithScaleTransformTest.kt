/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder

import demoAndTestShared.TestingGeomLayersBuilder
import demoAndTestShared.parsePlotSpec
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Tests for issue #706:
 * A constant non-positional aesthetic (e.g. alpha=0.5) must NOT be run through
 * the scale's continuous transform.
 *
 * The constant is already in the aesthetic output space ("alpha=0.5 means 50% opacity"),
 * so applying log10 / sqrt / reverse to it is wrong.
 */
class ConstantAesWithScaleTransformTest {

    private fun buildLayers(spec: String) =
        TestingGeomLayersBuilder.createSingleTileGeomLayers(parsePlotSpec(spec))

    private fun alphaOfFirstLayer(spec: String): Double? {
        val layer = buildLayers(spec)[0]
        val aes = PlotUtil.DemoAndTest.layerAestheticsWithoutLayout(layer)
        return aes.dataPointAt(0).alpha()
    }

    private fun sizeOfSecondLayer(spec: String): Double? {
        val layer = buildLayers(spec)[1]
        val aes = PlotUtil.DemoAndTest.layerAestheticsWithoutLayout(layer)
        return aes.dataPointAt(0).size()
    }

    private fun twoLayerSpec(constantAlpha: Double?, trans: String): String {
        val constantPart = if (constantAlpha != null) """"alpha": $constantAlpha,""" else ""
        return """
            {
              "kind": "plot",
              "data": {
                "x": [-3, -2, -1, 0, 1, 2, 3],
                "y": [-3, -2, -1, 0, 1, 2, 3],
                "v": [-9, -4, -1, 0, 1, 4, 9]
              },
              "mapping": {"x": "x", "y": "y"},
              "layers": [
                {
                  "geom": "point",
                  $constantPart
                  "size": 10
                },
                {
                  "geom": "point",
                  "mapping": {"alpha": "v"}
                }
              ],
              "scales": [
                {
                  "aesthetic": "alpha",
                  "trans": "$trans"
                }
              ]
            }
        """.trimIndent()
    }

    @Test
    fun `constant alpha is not distorted by log10 transform`() {
        // Before the fix: log10(0.5) ~= -0.301, clamped to 0.0 (layer invisible).
        // After the fix: the constant 0.5 is returned as-is.
        val alpha = alphaOfFirstLayer(twoLayerSpec(constantAlpha = 0.5, trans = "log10"))
        assertNotNull(alpha)
        assertEquals(0.5, alpha, absoluteTolerance = 1e-6)
    }

    @Test
    fun `constant alpha is not distorted by sqrt transform`() {
        // Before the fix: sqrt(0.5) ~= 0.707, wrong value but fell inside [0,1].
        // After the fix: the constant 0.5 is returned as-is.
        val alpha = alphaOfFirstLayer(twoLayerSpec(constantAlpha = 0.5, trans = "sqrt"))
        assertNotNull(alpha)
        assertEquals(0.5, alpha, absoluteTolerance = 1e-6)
    }

    @Test
    fun `constant alpha is not distorted by reverse transform`() {
        // Before the fix: reverse(0.5) = -0.5, clamped to 0.0.
        // After the fix: the constant 0.5 is returned as-is.
        val alpha = alphaOfFirstLayer(twoLayerSpec(constantAlpha = 0.5, trans = "reverse"))
        assertNotNull(alpha)
        assertEquals(0.5, alpha, absoluteTolerance = 1e-6)
    }

    @Test
    fun `default alpha is not distorted by log10 transform`() {
        // Layer 1 has no explicit constant, so it uses the default alpha (~= 1.0).
        // Before the fix: log10(default) ~= -4.8e-5, clamped to 0.0 (nearly transparent). Wrong.
        // After the fix: the default is returned as-is, near 1.0.
        val alpha = alphaOfFirstLayer(twoLayerSpec(constantAlpha = null, trans = "log10"))
        assertNotNull(alpha)
        assert(alpha > 0.99) { "Expected default alpha near 1.0 but got $alpha - likely distorted by log10" }
    }

    @Test
    fun `mapped alpha with log10 still works correctly`() {
        // Regression: the mapped-alpha layer (Layer 2) must still apply the transform+mapper.
        // Use all-positive v so every point is in the log10 domain, keeping assertions clean.
        val spec = """
            {
              "kind": "plot",
              "data": {"x": [1, 2, 3], "y": [1, 2, 3], "v": [1, 10, 100]},
              "mapping": {"x": "x", "y": "y"},
              "layers": [
                {"geom": "point", "alpha": 0.5, "size": 10},
                {"geom": "point", "mapping": {"alpha": "v"}}
              ],
              "scales": [{"aesthetic": "alpha", "trans": "log10"}]
            }
        """.trimIndent()
        val layer1 = buildLayers(spec)[1]
        val aes = PlotUtil.DemoAndTest.layerAestheticsWithoutLayout(layer1)

        // v=1 -> log10(1)=0, smallest -> lowest alpha; v=100 -> log10(100)=2, largest -> highest alpha
        val alphaMin = aes.dataPointAt(0).alpha()  // v=1
        val alphaMax = aes.dataPointAt(2).alpha()  // v=100

        assertNotNull(alphaMin)
        assertNotNull(alphaMax)
        assert(alphaMin >= 0.1) { "Expected alpha(v=1) >= 0.1 but got $alphaMin" }
        assert(alphaMax > alphaMin) { "Expected alpha(v=100) > alpha(v=1) but got $alphaMax vs $alphaMin" }
    }

    @Test
    fun `constant alpha overrides mapping without scale transform distortion`() {
        val spec = """
            {
              "kind": "plot",
              "data": {"x": [1, 2, 3], "y": [1, 2, 3], "v": [1, 10, 100]},
              "mapping": {"x": "x", "y": "y"},
              "layers": [
                {
                  "geom": "point",
                  "mapping": {"alpha": "v"},
                  "alpha": 0.5,
                  "size": 10
                }
              ],
              "scales": [{"aesthetic": "alpha", "trans": "log10"}]
            }
        """.trimIndent()

        val alpha = alphaOfFirstLayer(spec)
        assertNotNull(alpha)
        assertEquals(0.5, alpha, absoluteTolerance = 1e-6)
    }

    @Test
    fun `single constant alpha layer no transform no distortion`() {
        // Simplest regression: one layer, constant alpha, no transform.
        val spec = """
            {
              "kind": "plot",
              "data": {"x": [1, 2], "y": [1, 2]},
              "mapping": {"x": "x", "y": "y"},
              "layers": [{"geom": "point", "alpha": 0.7}]
            }
        """.trimIndent()
        val alpha = alphaOfFirstLayer(spec)
        assertNotNull(alpha)
        assertEquals(0.7, alpha, absoluteTolerance = 1e-6)
    }

    @Test
    fun `constant size still uses scale transform when layer has no size mapper`() {
        val spec = """
            {
              "kind": "plot",
              "data": {"x": [0], "y": [1], "v": [10000000]},
              "layers": [
                {
                  "geom": "point",
                  "mapping": {"x": "x", "y": "y", "size": "v"}
                },
                {
                  "geom": "text",
                  "mapping": {"x": "x", "y": "y", "label": "v"},
                  "size": 10000000
                }
              ],
              "scales": [{"aesthetic": "size", "trans": "symlog"}]
            }
        """.trimIndent()

        val size = sizeOfSecondLayer(spec)
        assertNotNull(size)
        assertEquals(8.0, size, absoluteTolerance = 1e-6)
    }
}
