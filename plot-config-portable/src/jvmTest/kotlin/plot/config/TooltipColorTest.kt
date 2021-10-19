/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.interact.ContextualMapping
import jetbrains.datalore.plot.base.interact.GeomTarget
import jetbrains.datalore.plot.base.interact.TipLayoutHint
import jetbrains.datalore.plot.builder.GeomLayer
import jetbrains.datalore.plot.builder.interact.TooltipSpec
import jetbrains.datalore.plot.builder.interact.TooltipSpecFactory
import jetbrains.datalore.plot.builder.presentation.Defaults
import jetbrains.datalore.plot.builder.theme.AxisTheme
import jetbrains.datalore.plot.config.Option.Layer.TOOLTIP_COLOR
import jetbrains.datalore.plot.config.Option.Layer.TOOLTIP_LINES
import jetbrains.datalore.plot.config.TestUtil.buildPointLayer
import kotlin.test.Test
import kotlin.test.assertEquals

class TooltipColorTest {
    @Test
    fun `default tooltip color will be used`() {
        val layer = buildGeomPointLayer(tooltipColor = null)
        assertGeneralTooltipColor(layer, DEFAULT_COLOR)
    }

    @Test
    fun `override the default tooltip color`() {
        val layer = buildGeomPointLayer(tooltipColor = "red")
        assertGeneralTooltipColor(layer, Color.RED)
    }

    companion object {
        val axisTheme = object : AxisTheme {
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
            override fun tooltipFill() = Defaults.Common.Tooltip.AXIS_TOOLTIP_COLOR
            override fun tooltipColor() = Defaults.Common.Tooltip.AXIS_TOOLTIP_COLOR
            override fun tooltipStrokeWidth() = 1.0
            override fun tooltipTextColor() = Defaults.Common.Tooltip.LIGHT_TEXT_COLOR
        }

        val DEFAULT_COLOR = Color.BLACK

        private fun buildGeomPointLayer(
            tooltipColor: String?
        ): GeomLayer {
            val tooltips = mapOf(
                TOOLTIP_LINES to listOf("text"),
                TOOLTIP_COLOR to tooltipColor
            )
             return buildPointLayer(
                data = emptyMap(),
                mapping = mapOf(Aes.X.name to listOf(1.0), Aes.Y.name to listOf(1.0)),
                tooltips = tooltips
            )
        }

        private fun createTooltipSpecs(contextualMapping: ContextualMapping): List<TooltipSpec> {
            val factory = TooltipSpecFactory(contextualMapping, DoubleVector.ZERO, flippedAxis = false, axisTheme, axisTheme)
            return factory.create(
                GeomTarget(
                    hitIndex = 0,
                    tipLayoutHint = TipLayoutHint.cursorTooltip(DoubleVector.ZERO, DEFAULT_COLOR),
                    aesTipLayoutHints = emptyMap()
                )
            )
        }

        private fun assertGeneralTooltipColor(layer: GeomLayer, expectedColor: Color) {
            val tooltipSpecs = createTooltipSpecs(layer.contextualMapping)
            tooltipSpecs
                .filterNot(TooltipSpec::isOutlier)
                .forEach { tooltipSpec -> assertEquals(expectedColor, tooltipSpec.fill) }
        }
    }
}