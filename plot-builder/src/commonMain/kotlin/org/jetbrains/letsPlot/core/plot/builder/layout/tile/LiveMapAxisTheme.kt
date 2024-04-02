/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.layout.tile

import org.jetbrains.letsPlot.commons.unsupported.UNSUPPORTED
import org.jetbrains.letsPlot.commons.values.FontFace
import org.jetbrains.letsPlot.commons.values.FontFamily
import org.jetbrains.letsPlot.core.plot.base.layout.TextJustification
import org.jetbrains.letsPlot.core.plot.base.layout.Thickness
import org.jetbrains.letsPlot.core.plot.base.render.linetype.NamedLineType
import org.jetbrains.letsPlot.core.plot.base.theme.AxisTheme
import org.jetbrains.letsPlot.core.plot.base.theme.ThemeTextStyle
import org.jetbrains.letsPlot.core.plot.builder.presentation.Defaults

internal class LiveMapAxisTheme : AxisTheme {
    override val axis: String
        get() = UNSUPPORTED()

    override fun showLine(): Boolean = false

    override fun showTickMarks(): Boolean = false

    override fun showLabels(): Boolean = false

    override fun showTitle(): Boolean = false

    override fun showTooltip(): Boolean = false

    override fun titleStyle(): ThemeTextStyle = ThemeTextStyle(
        family = FontFamily.SERIF,
        face = FontFace.NORMAL,
        size = Defaults.Plot.Axis.TITLE_FONT_SIZE,
        color = Defaults.TEXT_COLOR
    )

    override fun titleJustification() = TextJustification(0.5, 1.0)

    override fun titleMargins() = Thickness()

    override fun lineWidth() = 1.0

    override fun lineColor() = Defaults.Plot.Axis.LINE_COLOR

    override fun tickMarkColor() = Defaults.Plot.Axis.LINE_COLOR

    override fun lineType() = NamedLineType.SOLID

    override fun tickMarkLineType() = NamedLineType.SOLID

    override fun labelStyle(): ThemeTextStyle = ThemeTextStyle(
        family = FontFamily.SERIF,
        face = FontFace.NORMAL,
        size = Defaults.Plot.Axis.TICK_FONT_SIZE,
        color = Defaults.TEXT_COLOR
    )

    override fun rotateLabels(): Boolean = false

    override fun labelAngle(): Double = Double.NaN

    override fun tickMarkWidth() = 1.0

    override fun tickMarkLength() = 4.0

    override fun tickLabelMargins() = Thickness(0.0, 0.0, 0.0, 0.0)

    override fun tooltipFill() = Defaults.Common.Tooltip.AXIS_TOOLTIP_COLOR

    override fun tooltipColor() = Defaults.Common.Tooltip.LIGHT_TEXT_COLOR

    override fun tooltipStrokeWidth() = 1.0

    override fun tooltipTextStyle(): ThemeTextStyle = ThemeTextStyle(
        family = FontFamily.SERIF,
        face = FontFace.NORMAL,
        size = Defaults.Common.Tooltip.AXIS_TOOLTIP_FONT_SIZE,
        color = Defaults.Common.Tooltip.LIGHT_TEXT_COLOR
    )
}