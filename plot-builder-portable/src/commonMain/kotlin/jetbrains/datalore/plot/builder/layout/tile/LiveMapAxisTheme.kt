/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout.tile

import jetbrains.datalore.base.unsupported.UNSUPPORTED
import jetbrains.datalore.base.values.FontFace
import jetbrains.datalore.plot.builder.presentation.Defaults
import jetbrains.datalore.plot.builder.theme.AxisTheme

internal class LiveMapAxisTheme : AxisTheme {
    override val axis: String
        get() = UNSUPPORTED()

    override fun showLine(): Boolean = false

    override fun showTickMarks(): Boolean = false

    override fun showLabels(): Boolean = false

    override fun showTitle(): Boolean = false

    override fun showTooltip(): Boolean = false

    override fun titleColor() = Defaults.TEXT_COLOR

    override fun titleFontFace() = FontFace.NORMAL

    override fun lineWidth() = 1.0

    override fun lineColor() = Defaults.Plot.Axis.LINE_COLOR

    override fun tickMarkColor() = Defaults.Plot.Axis.LINE_COLOR

    override fun labelColor() = Defaults.TEXT_COLOR

    override fun labelFontFace() = FontFace.NORMAL

    override fun tickMarkWidth() = 1.0

    override fun tickMarkLength() = 4.0

    override fun tooltipFill() = Defaults.Common.Tooltip.AXIS_TOOLTIP_COLOR

    override fun tooltipColor() = Defaults.Common.Tooltip.LIGHT_TEXT_COLOR

    override fun tooltipStrokeWidth() = 1.0

    override fun tooltipTextColor() = Defaults.Common.Tooltip.LIGHT_TEXT_COLOR

    override fun tooltipFontFace() = FontFace.NORMAL
}