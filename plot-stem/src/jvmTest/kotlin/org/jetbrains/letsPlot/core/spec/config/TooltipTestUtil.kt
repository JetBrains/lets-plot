/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.FontFace
import org.jetbrains.letsPlot.commons.values.FontFamily
import org.jetbrains.letsPlot.core.plot.base.PlotContext
import org.jetbrains.letsPlot.core.plot.base.layout.TextJustification
import org.jetbrains.letsPlot.core.plot.base.layout.Thickness
import org.jetbrains.letsPlot.core.plot.base.theme.AxisTheme
import org.jetbrains.letsPlot.core.plot.base.theme.ThemeTextStyle
import org.jetbrains.letsPlot.core.plot.base.tooltip.ContextualMapping
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTarget
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint
import org.jetbrains.letsPlot.core.plot.builder.GeomLayer
import org.jetbrains.letsPlot.core.plot.builder.assemble.TestingPlotContext
import org.jetbrains.letsPlot.core.plot.builder.presentation.Defaults
import org.jetbrains.letsPlot.core.plot.builder.tooltip.spec.TooltipSpec
import org.jetbrains.letsPlot.core.plot.builder.tooltip.spec.TooltipSpecFactory
import kotlin.test.assertEquals

object TooltipTestUtil {

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
        override fun titleMargins() = Thickness()
        override fun lineWidth() = TODO("Not yet implemented")
        override fun lineColor() = TODO("Not yet implemented")
        override fun tickMarkColor() = TODO("Not yet implemented")
        override fun labelStyle(): ThemeTextStyle = TODO("Not yet implemented")
        override fun rotateLabels() = TODO("Not yet implemented")
        override fun labelAngle() = TODO("Not yet implemented")
        override fun tickMarkWidth() = TODO("Not yet implemented")
        override fun tickMarkLength() = TODO("Not yet implemented")
        override fun tickLabelMargins() = Thickness()
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
                tipLayoutHint = TipLayoutHint.cursorTooltip(DoubleVector.ZERO),
                aesTipLayoutHints = emptyMap()
            ),
            ctx
        )
    }

    private fun assertGeneralTooltip(tooltipSpecs: List<TooltipSpec>, expectedLines: List<String>) {
        val actualGeneralTooltips = tooltipSpecs.filterNot(TooltipSpec::isSide)
        assertEquals(expectedLines.isEmpty(), actualGeneralTooltips.isEmpty())
        if (actualGeneralTooltips.isNotEmpty()) {
            val actualLines = actualGeneralTooltips.single().lines.map(TooltipSpec.Line::toString)
            assertEquals(expectedLines.size, actualLines.size)
            expectedLines.zip(actualLines).forEach { (expected, actual) -> assertEquals(expected, actual) }
        }
    }

    internal fun assertGeneralTooltip(layer: GeomLayer, expectedLines: List<String>) {
        val ctx = TestingPlotContext.create(layer)
        val tooltipSpecs = createTooltipSpecs(layer.createContextualMapping(), ctx)
        assertGeneralTooltip(
            tooltipSpecs,
            expectedLines
        )
    }
}