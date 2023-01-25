/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.FontFace
import jetbrains.datalore.base.values.FontFamily
import jetbrains.datalore.plot.base.PlotContext
import jetbrains.datalore.plot.base.interact.ContextualMapping
import jetbrains.datalore.plot.base.interact.GeomTarget
import jetbrains.datalore.plot.base.interact.TipLayoutHint.Companion.cursorTooltip
import jetbrains.datalore.plot.builder.assemble.TestingPlotContext
import jetbrains.datalore.plot.builder.interact.TooltipSpec
import jetbrains.datalore.plot.builder.interact.TooltipSpecFactory
import jetbrains.datalore.plot.builder.layout.Margins
import jetbrains.datalore.plot.builder.layout.TextJustification
import jetbrains.datalore.plot.builder.presentation.Defaults
import jetbrains.datalore.plot.builder.theme.AxisTheme
import jetbrains.datalore.plot.builder.theme.ThemeTextStyle
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
        val ctx = TestingPlotContext.create(layer)
        val tooltipSpecs = createTooltipSpecs(layer.createContextualMapping(), ctx)
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
        val ctx = TestingPlotContext.create(layer)
        val tooltipSpecs = createTooltipSpecs(layer.createContextualMapping(), ctx)
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
        val ctx = TestingPlotContext.create(layer)
        val tooltipSpecs = createTooltipSpecs(layer.createContextualMapping(), ctx)
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
        val ctx = TestingPlotContext.create(layer)
        val tooltipSpecs = createTooltipSpecs(layer.createContextualMapping(), ctx)
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
            val ctx = TestingPlotContext.create(layer)
            val tooltipSpecs = createTooltipSpecs(layer.createContextualMapping(), ctx)
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
            val ctx = TestingPlotContext.create(layer)
            val tooltipSpecs = createTooltipSpecs(layer.createContextualMapping(), ctx)
            assertGeneralTooltips(
                tooltipSpecs,
                expectedLines = listOf("Size: 5.00")
            )
        }
    }

    companion object {
        private val axisTheme = object : AxisTheme {
            override val axis: String
                get() = TODO("Not yet implemented")

            override fun showLine() = TODO("Not yet implemented")
            override fun showTickMarks() = TODO("Not yet implemented")
            override fun showLabels() = TODO("Not yet implemented")
            override fun showTitle() = TODO("Not yet implemented")
            override fun showTooltip() = TODO("Not yet implemented")
            override fun titleStyle(): ThemeTextStyle = TODO("Not yet implemented")
            override fun titleJustification() = TextJustification(0.5, 1.0)
            override fun titleMargins() = Margins()
            override fun lineWidth() = TODO("Not yet implemented")
            override fun lineColor() = TODO("Not yet implemented")
            override fun tickMarkColor() = TODO("Not yet implemented")
            override fun labelStyle(): ThemeTextStyle = TODO("Not yet implemented")
            override fun labelAngle(): Double? = null
            override fun tickMarkWidth() = TODO("Not yet implemented")
            override fun tickMarkLength() = TODO("Not yet implemented")
            override fun tickLabelMargins() = Margins()
            override fun tooltipFill() = Color.WHITE
            override fun tooltipColor() = Color.BLACK
            override fun tooltipStrokeWidth() = 1.0
            override fun tooltipTextStyle(): ThemeTextStyle =
                ThemeTextStyle(
//                    Defaults.FONT_FAMILY_NORMAL,
                    FontFamily.SERIF,
                    FontFace.NORMAL,
                    Defaults.Common.Tooltip.AXIS_TOOLTIP_FONT_SIZE,
                    Color.GRAY
                )

        }

        private fun createTooltipSpecs(contextualMapping: ContextualMapping, ctx: PlotContext): List<TooltipSpec> {
            val factory =
                TooltipSpecFactory(contextualMapping, DoubleVector.ZERO, flippedAxis = false, axisTheme, axisTheme)
            return factory.create(
                GeomTarget(
                    hitIndex = 0,
                    tipLayoutHint = cursorTooltip(DoubleVector.ZERO),
                    aesTipLayoutHints = emptyMap()
                ),
                ctx
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