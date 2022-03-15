/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.interact.ContextualMapping
import jetbrains.datalore.plot.base.interact.GeomTarget
import jetbrains.datalore.plot.base.interact.TipLayoutHint.Companion.cursorTooltip
import jetbrains.datalore.plot.builder.interact.TooltipSpec
import jetbrains.datalore.plot.builder.interact.TooltipSpecFactory
import jetbrains.datalore.plot.builder.theme.AxisTheme
import jetbrains.datalore.plot.config.TestUtil.getSingleGeomLayer
import kotlin.test.Test
import kotlin.test.assertEquals


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
        val tooltipSpecs = createTooltipSpecs(layer.contextualMapping)
        assertGeneralTooltips(
            tooltipSpecs,
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
        val tooltipSpecs = createTooltipSpecs(layer.contextualMapping)
        // No tooltips
        assertGeneralTooltips(
            tooltipSpecs,
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
        val tooltipSpecs = createTooltipSpecs(layer.contextualMapping)
        assertGeneralTooltips(
            tooltipSpecs,
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
        val tooltipSpecs = createTooltipSpecs(layer.contextualMapping)
        assertGeneralTooltips(
            tooltipSpecs,
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
            val tooltipSpecs = createTooltipSpecs(layer.contextualMapping)
            assertGeneralTooltips(
                tooltipSpecs,
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
            val tooltipSpecs = createTooltipSpecs(layer.contextualMapping)
            assertGeneralTooltips(
                tooltipSpecs,
                expectedLines = listOf("Size: 5.00")
            )
        }
    }

    companion object {
        private val axisTheme = object : AxisTheme {
            override fun showLine() = TODO("Not yet implemented")
            override fun showTickMarks() = TODO("Not yet implemented")
            override fun showLabels() = TODO("Not yet implemented")
            override fun showTitle() = TODO("Not yet implemented")
            override fun showTooltip() = TODO("Not yet implemented")
            override fun titleColor() = TODO("Not yet implemented")
            override fun lineWidth() = TODO("Not yet implemented")
            override fun lineColor() = TODO("Not yet implemented")
            override fun tickMarkColor() = TODO("Not yet implemented")
            override fun labelColor() = TODO("Not yet implemented")
            override fun tickMarkWidth() = TODO("Not yet implemented")
            override fun tickMarkLength() = TODO("Not yet implemented")
            override fun tooltipFill() = Color.WHITE
            override fun tooltipColor() = Color.BLACK
            override fun tooltipStrokeWidth() = 1.0
            override fun tooltipTextColor() = Color.GRAY
        }

        private fun createTooltipSpecs(contextualMapping: ContextualMapping): List<TooltipSpec> {
            val factory = TooltipSpecFactory(contextualMapping, DoubleVector.ZERO, flippedAxis = false, axisTheme, axisTheme)
            return factory.create(
                GeomTarget(
                    hitIndex = 0,
                    tipLayoutHint = cursorTooltip(DoubleVector.ZERO),
                    aesTipLayoutHints = emptyMap()
                )
            )
        }

        private fun assertGeneralTooltips(tooltipSpecs: List<TooltipSpec>, expectedLines: List<String>) {
            val actualGeneralTooltips = tooltipSpecs.filterNot(TooltipSpec::isOutlier)
            assertEquals(expectedLines.isEmpty(), actualGeneralTooltips.isEmpty())
            if (actualGeneralTooltips.isNotEmpty()) {
                val actualLines = actualGeneralTooltips.single().lines.map(TooltipSpec.Line::toString)
                assertEquals(expectedLines.size, actualLines.size)
                expectedLines.zip(actualLines).forEach { (expected, actual) -> assertEquals(expected, actual) }
            }
        }
    }
}