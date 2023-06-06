/*
 * Copyright (c) 2023. JetBrains s.r.o.
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
import jetbrains.datalore.plot.base.interact.TipLayoutHint
import jetbrains.datalore.plot.builder.GeomLayer
import jetbrains.datalore.plot.builder.assemble.TestingPlotContext
import jetbrains.datalore.plot.builder.interact.TooltipSpec
import jetbrains.datalore.plot.builder.interact.TooltipSpecFactory
import jetbrains.datalore.plot.builder.layout.Margins
import jetbrains.datalore.plot.builder.layout.TextJustification
import jetbrains.datalore.plot.builder.presentation.Defaults
import jetbrains.datalore.plot.builder.theme.AxisTheme
import jetbrains.datalore.plot.builder.theme.ThemeTextStyle
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
        override fun titleMargins() = Margins()
        override fun lineWidth() = TODO("Not yet implemented")
        override fun lineColor() = TODO("Not yet implemented")
        override fun tickMarkColor() = TODO("Not yet implemented")
        override fun labelStyle(): ThemeTextStyle = TODO("Not yet implemented")
        override fun rotateLabels() = TODO("Not yet implemented")
        override fun labelAngle() = TODO("Not yet implemented")
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