/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.config.TooltipTestUtil.assertGeneralTooltip
import kotlin.test.Test


class TooltipCheckLabelInLines {

    private val myData = """{ 
        "x": ["a"],
        "y": [ 10 ],
        "n": [ 20 ] 
    }""".trimIndent()

    @Test
    fun `default one-line tooltip with mapped to Y axis - no label`() {
        val spec = """
        {
          "data": $myData,
          "kind": "plot",
          "mapping": { "x" : "x", "y" : "y" },
          "layers": [
            {
                "geom" : "bar", "stat": "identity"
            }
          ]
        }""".trimIndent()
        val layer = TestUtil.getSingleGeomLayer(spec)
        assertGeneralTooltip(
            layer,
            expectedLines = listOf("10.00")
        )
    }

    @Test
    fun `default one-line tooltip with mapped to Y axis and specified format - no label`() {
        val spec = """
        {
          "data": $myData,
          "kind": "plot",
          "mapping": { "x" : "x", "y" : "y" },
          "layers": [
            {
                "geom" : "bar", "stat": "identity",
                "tooltips": {"formats": [{ "field": "^y", "format": ".1f" }] } 
            }
          ]
        }""".trimIndent()
        val layer = TestUtil.getSingleGeomLayer(spec)
        assertGeneralTooltip(
            layer,
            expectedLines = listOf("10.0")
        )
    }

    @Test
    fun `default multiline tooltip with mapped to Y axis - all lines with labels`() {
        val spec = """
        {
          "data": $myData,
          "kind": "plot",
          "mapping": { "x" : "x", "y" : "y", "fill": "n" },
          "layers": [
            {
                "geom" : "bar", "stat": "identity"
            }
          ]
        }""".trimIndent()
        val layer = TestUtil.getSingleGeomLayer(spec)
        assertGeneralTooltip(
            layer,
            expectedLines = listOf("y: 10.00", "n: 20.00")
        )
    }

    @Test
    fun `default multiline tooltip with mapped to Y axis and specified formats - all lines with labels`() {
        val spec = """
        {
          "data": $myData,
          "kind": "plot",
          "mapping": { "x" : "x", "y" : "y", "fill": "n" },
          "layers": [
            {
                "geom" : "bar", "stat": "identity",
                "tooltips": {
                  "formats": 
                     [
                       { "field": "^y", "format": ".1f" },
                       { "field": "^fill", "format": "d" }
                     ] 
                } 
            }
          ]
        }""".trimIndent()
        val layer = TestUtil.getSingleGeomLayer(spec)
        assertGeneralTooltip(
            layer,
            expectedLines = listOf("y: 10.0", "n: 20")
        )
    }

    @Test
    fun `specify tooltips with variable list - use default label`() {
        val spec = """
        {
          "data": $myData,
          "kind": "plot",
          "mapping": { "x" : "x", "y" : "y" },
          "layers": [
            {
                "geom" : "bar", "stat": "identity",
                "tooltips": { "variables": ["y"] }
            }
          ]
        }""".trimIndent()
        val layer = TestUtil.getSingleGeomLayer(spec)
        assertGeneralTooltip(
            layer,
            expectedLines = listOf("y: 10.00")
        )
    }

    @Test
    fun `specify tooltip via lines with macro @ - use default label`() {
        val spec = """
        {
          "data": $myData,
          "kind": "plot",
          "mapping": { "x" : "x", "y" : "y" },
          "layers": [
            {
                "geom" : "bar", "stat": "identity",
                "tooltips": { "lines": [ "@|^y" ] }
            }
          ]
        }""".trimIndent()
        val layer = TestUtil.getSingleGeomLayer(spec)
        assertGeneralTooltip(
            layer,
            expectedLines = listOf("y: 10.00")
        )
    }

    @Test
    fun `specify tooltip without labels`() {
        val spec = """
        {
          "data": $myData,
          "kind": "plot",
          "mapping": { "x" : "x", "y" : "y" },
          "layers": [
            {
                "geom" : "bar", "stat": "identity",
                "tooltips": { "lines": [ "|^y" ] }
            }
          ]
        }""".trimIndent()
        val layer = TestUtil.getSingleGeomLayer(spec)
        assertGeneralTooltip(
            layer,
            expectedLines = listOf("10.00")
        )
    }

    @Test
    fun `default tooltips with outliers - general will be one-line without label`() {
        val spec = """
        {
          "data": { 
              "x": ["a"],
              "y": [ 10 ],
              "ymin": [ 5 ],
              "ymax": [ 15 ]
          },
          "kind": "plot",
          "mapping": { "x" : "x", "y" : "y" },
          "layers": [
            {
                "geom" : "pointrange",
                "mapping": { "ymin" : "ymin", "ymax" : "ymax" }
            }
          ]
        }""".trimIndent()
        val layer = TestUtil.getSingleGeomLayer(spec)
        assertGeneralTooltip(
            layer,
            expectedLines = listOf("10.0")
        )
    }

    @Test
    fun `the label in one-line general tooltip is equal to Y axis and we show it`() {
        val spec = """
        {
          "data": { 
              "x": [ 1, 2, 3, 4, 5 ],
              "y": [ 10, 20, 30, 40, 50 ]
          },
          "kind": "plot",
          "mapping": { "x" : "x", "y" : "y", "fill" : "y" },
          "data_meta": { 
             "mapping_annotations": [ {
                 "aes": "fill",
                 "annotation": "as_discrete",
                 "parameters": { "label": "y" }
             } ] 
          },
          "layers": [
            {
                "geom" : "point"
            }
          ]
        }""".trimIndent()
        val layer = TestUtil.getSingleGeomLayer(spec)
        assertGeneralTooltip(
            layer,
            expectedLines = listOf("y: 10.0")
        )
    }
}