/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.FontFace
import org.jetbrains.letsPlot.commons.values.FontFamily
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.PlotContext
import org.jetbrains.letsPlot.core.plot.base.layout.TextJustification
import org.jetbrains.letsPlot.core.plot.base.layout.Thickness
import org.jetbrains.letsPlot.core.plot.base.render.linetype.NamedLineType
import org.jetbrains.letsPlot.core.plot.base.theme.AxisTheme
import org.jetbrains.letsPlot.core.plot.base.theme.ThemeTextStyle
import org.jetbrains.letsPlot.core.plot.base.tooltip.ContextualMapping
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTarget
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint
import org.jetbrains.letsPlot.core.plot.builder.GeomLayer
import org.jetbrains.letsPlot.core.plot.builder.assemble.TestingPlotContext
import org.jetbrains.letsPlot.core.plot.builder.presentation.Defaults
import org.jetbrains.letsPlot.core.plot.builder.tooltip.spec.TooltipSpec
import org.jetbrains.letsPlot.core.plot.builder.tooltip.spec.TooltipSpec.Line
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
        override fun lineType() = NamedLineType.SOLID
        override fun tickMarkLineType() = NamedLineType.SOLID
        override fun labelStyle(): ThemeTextStyle = TODO("Not yet implemented")
        override fun rotateLabels() = TODO("Not yet implemented")
        override fun labelAngle() = TODO("Not yet implemented")
        override fun tickMarkWidth() = TODO("Not yet implemented")
        override fun tickMarkLength() = TODO("Not yet implemented")
        override fun tickLabelMargins() = Thickness()
        override fun tooltipFill() = Color.WHITE
        override fun tooltipColor() = Color.BLACK
        override fun tooltipStrokeWidth() = 1.0
        override fun tooltipLineType() = NamedLineType.SOLID
        override fun tooltipTextStyle(): ThemeTextStyle =
            ThemeTextStyle(
//                    Defaults.FONT_FAMILY_NORMAL,
                FontFamily.SERIF,
                FontFace.NORMAL,
                Defaults.Common.Tooltip.AXIS_TOOLTIP_FONT_SIZE,
                Color.GRAY
            )

    }

    private fun createGeneralTooltipSpecs(
        contextualMapping: ContextualMapping,
        ctx: PlotContext,
        hitIndex: Int = 0
    ): List<TooltipSpec> {
        return createTooltipSpecs(hitIndex, TipLayoutHint.cursorTooltip(coord = DoubleVector.ZERO), ctx, contextualMapping)
    }

    private fun createXAxisTooltipSpecs(
        contextualMapping: ContextualMapping,
        ctx: PlotContext,
        hitIndex: Int
    ): List<TooltipSpec> {
        return createTooltipSpecs(hitIndex, TipLayoutHint.xAxisTooltip(coord = DoubleVector.ZERO), ctx, contextualMapping)
    }

    private fun createTooltipSpecs(
        hitIndex: Int,
        tipLayoutHint: TipLayoutHint,
        ctx: PlotContext,
        contextualMapping: ContextualMapping,
        aesTipLayoutHints: Map<Aes<*>, TipLayoutHint> = emptyMap()
    ): List<TooltipSpec> {
        val factory =
            TooltipSpecFactory(contextualMapping, DoubleVector.ZERO, flippedAxis = false, axisTheme, axisTheme)
        return factory.create(
            GeomTarget(
                hitIndex = hitIndex,
                tipLayoutHint = tipLayoutHint,
                aesTipLayoutHints = aesTipLayoutHints
            ),
            ctx
        )
    }

    internal fun assertXAxisTooltip(layer: GeomLayer, expectedLines: List<String>, hitIndex: Int = 0) {
        val ctx = TestingPlotContext.create(layer)
        val tooltipSpecs = createXAxisTooltipSpecs(layer.createContextualMapping(), ctx, hitIndex)
        val actualXAxisTooltip = tooltipSpecs.filter { it.layoutHint.kind == TipLayoutHint.Kind.X_AXIS_TOOLTIP }

        assertEquals(expectedLines.isEmpty(), actualXAxisTooltip.isEmpty())

        if (actualXAxisTooltip.isNotEmpty()) {
            val actualLines = actualXAxisTooltip.single().lines.map(Line::toString)
            assertEquals(expectedLines.size, actualLines.size)
            expectedLines.zip(actualLines).forEach { (expected, actual) -> assertEquals(expected, actual) }
        }
    }

    internal fun assertGeneralTooltip(layer: GeomLayer, expectedLines: List<String>) {
        val ctx = TestingPlotContext.create(layer)
        val tooltipSpecs = createGeneralTooltipSpecs(layer.createContextualMapping(), ctx)
        val actualGeneralTooltips = tooltipSpecs.filterNot(TooltipSpec::isSide)

        assertEquals(expectedLines.isEmpty(), actualGeneralTooltips.isEmpty())

        if (actualGeneralTooltips.isNotEmpty()) {
            val actualLines = actualGeneralTooltips.single().lines.map(Line::toString)
            assertEquals(expectedLines.size, actualLines.size)
            expectedLines.zip(actualLines).forEach { (expected, actual) -> assertEquals(expected, actual) }
        }
    }
}