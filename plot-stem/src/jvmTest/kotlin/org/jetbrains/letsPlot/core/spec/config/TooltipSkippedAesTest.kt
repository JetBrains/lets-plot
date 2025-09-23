/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import demoAndTestShared.TestingGeomLayersBuilder.getSingleGeomLayer
import org.jetbrains.letsPlot.core.spec.config.TooltipTestUtil.assertGeneralTooltip
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

        val layer = getSingleGeomLayer(spec)
        assertGeneralTooltip(
            layer,
            expectedLines = listOf("2")
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

        val layer = getSingleGeomLayer(spec)
        // No tooltips
        assertGeneralTooltip(
            layer,
            expectedLines = emptyList()
        )
    }

    private val layerSpec = """
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
        ]"""

    @Test
    fun `should skip duplicated as_discrete mappings`() {
        val spec = """
            |{
            |  "data": {
            |    "category": [ "Slice 1", "Slice 2", "Slice 3", "Slice 4", "Slice 5", "Slice 6", "Slice 7", "Slice 8", "Slice 9" ], 
            |    "value": [ 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0 ]
            |  }, 
            |  "data_meta": {
            |    "series_annotations": [
            |      { "type": "str", "column": "category" }, 
            |      { "type": "float", "column": "value" }
            |    ]
            |  }, 
            |  "kind": "plot", 
            |  "layers": [
            |    {
            |      "geom": "point", 
            |      "mapping": { "color": "category", "fill": "category", "x": "value" }, 
            |      "data_meta": {
            |        "mapping_annotations": [
            |          { "parameters": { "label": "category" }, "aes": "color", "annotation": "as_discrete" }, 
            |          { "parameters": { "label": "category" }, "aes": "fill", "annotation": "as_discrete" }
            |        ]
            |      }
            |    }
            |  ]
            |}""".trimMargin()

        val layer = getSingleGeomLayer(spec)
        assertGeneralTooltip(
            layer,
            expectedLines = listOf("category: Slice 1")
        )
    }

    @Test
    fun `should skip duplicated mappings`() {
        val spec = """{
            $layerSpec
        }"""

        val layer = getSingleGeomLayer(spec)
        assertGeneralTooltip(
            layer,
            expectedLines = listOf("z: 5")
        )
    }

    @Test
    fun `when same var mapped twice as continuous and discrete - should use continuous value`() {
        val spec = """{
            $layerSpec,
            "scales": [
                {
                    "aesthetic": "size",
                    "discrete": true
                }
            ]   
        }"""

        val layer = getSingleGeomLayer(spec)
        assertGeneralTooltip(
            layer,
            expectedLines = listOf("z: 5")
        )
    }

    @Test
    fun `should skip duplicated mappings - use the defined by scale`() {
        run {
            val spec = """{
            $layerSpec,
            "scales": [ 
                {
                    "name": "Color",
                    "aesthetic": "color"
                }
            ]
        }"""
            val layer = getSingleGeomLayer(spec)
            assertGeneralTooltip(
                layer,
                expectedLines = listOf("Color: 5")
            )
        }
        run {
            val spec = """{
            $layerSpec,
            "scales": [ 
                {
                    "name": "Size",
                    "aesthetic": "size"
                }
            ]
        }"""
            val layer = getSingleGeomLayer(spec)
            assertGeneralTooltip(
                layer,
                expectedLines = listOf("Size: 5")
            )
        }
    }
}