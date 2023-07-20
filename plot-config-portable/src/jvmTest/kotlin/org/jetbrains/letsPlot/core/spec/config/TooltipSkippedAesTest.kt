/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.plot.config.TestUtil.getSingleGeomLayer
import jetbrains.datalore.plot.config.TooltipTestUtil.assertGeneralTooltip
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
            expectedLines = listOf("2.00")
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
    fun `should skip duplicated mappings`() {
        val spec = """{
            $layerSpec
        }"""

        val layer = getSingleGeomLayer(spec)
        assertGeneralTooltip(
            layer,
            expectedLines = listOf("z: 5.00")
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
            expectedLines = listOf("z: 5.00")
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
                expectedLines = listOf("Color: 5.00")
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
                expectedLines = listOf("Size: 5.00")
            )
        }
    }
}