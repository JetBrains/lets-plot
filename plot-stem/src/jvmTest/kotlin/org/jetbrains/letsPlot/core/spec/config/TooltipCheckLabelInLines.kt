/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import demoAndTestShared.TestingGeomLayersBuilder
import org.jetbrains.letsPlot.core.spec.config.TooltipTestUtil.assertGeneralTooltip
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
        val layer = TestingGeomLayersBuilder.getSingleGeomLayer(spec)
        assertGeneralTooltip(
            layer,
            expectedLines = listOf("10")
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
        val layer = TestingGeomLayersBuilder.getSingleGeomLayer(spec)
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
        val layer = TestingGeomLayersBuilder.getSingleGeomLayer(spec)
        assertGeneralTooltip(
            layer,
            expectedLines = listOf("y: 10", "n: 20")
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
        val layer = TestingGeomLayersBuilder.getSingleGeomLayer(spec)
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
        val layer = TestingGeomLayersBuilder.getSingleGeomLayer(spec)
        assertGeneralTooltip(
            layer,
            expectedLines = listOf("y: 10")
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
        val layer = TestingGeomLayersBuilder.getSingleGeomLayer(spec)
        assertGeneralTooltip(
            layer,
            expectedLines = listOf("y: 10")
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
        val layer = TestingGeomLayersBuilder.getSingleGeomLayer(spec)
        assertGeneralTooltip(
            layer,
            expectedLines = listOf("10")
        )
    }

    @Test
    fun `default with side tooltips - general will be one-line without label`() {
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
        val layer = TestingGeomLayersBuilder.getSingleGeomLayer(spec)
        assertGeneralTooltip(
            layer,
            expectedLines = listOf("10")
        )
    }

    @Test
    fun `variable 'b' in as_discrete color mapping should deduce proper DataType formatter`() {
        // Note that as_discrete produces a new variable with the name "color.b". Because of this DataType
        // resolution may fail (there is no "color.b" in series_annotations) and DataType.UNKNOWN.formatter
        // (i.e., toString()) will be used with the result "10.0" instead of "10".

        val spec = """
                |{
                |  "kind": "plot",
                |  "layers": [
                |    {
                |      "geom": "point",
                |      "data": {
                |        "a": [ 1.0, 2.0, 3.0, 4.0, 5.0 ],
                |        "b": [ 10.0, 20.0, 30.0, 40.0, 50.0 ]
                |      },
                |      "mapping": { "x": "a", "y": "b", "color": "b" },
                |      "data_meta": {
                |        "series_annotations": [
                |          { "type": "int", "column": "a" },
                |          { "type": "int", "column": "b" }
                |        ],
                |        "mapping_annotations": [
                |          {
                |            "parameters": { "label": "b" },
                |            "aes": "color",
                |            "annotation": "as_discrete"
                |          }
                |        ]
                |      }
                |    }
                |  ]
                |}""".trimMargin()

        val layer = TestingGeomLayersBuilder.getSingleGeomLayer(spec)
        assertGeneralTooltip(
            layer,
            expectedLines = listOf("b: 10")
        )
    }
}
