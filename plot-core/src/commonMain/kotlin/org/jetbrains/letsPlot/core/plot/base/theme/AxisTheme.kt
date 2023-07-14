/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.theme

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.layout.Margins
import org.jetbrains.letsPlot.core.plot.base.layout.TextJustification

interface AxisTheme {
    val axis: String

    fun isOntop(): Boolean = false

    fun showLine(): Boolean

    fun showTickMarks(): Boolean

    fun showLabels(): Boolean

    fun showTitle(): Boolean

    fun showTooltip(): Boolean

    fun titleStyle(): ThemeTextStyle

    fun titleJustification(): TextJustification

    fun titleMargins(): Margins

    fun lineWidth(): Double

    fun lineColor(): Color

    fun tickMarkColor(): Color

    fun labelStyle(): ThemeTextStyle

    fun rotateLabels(): Boolean

    fun labelAngle(): Double

    fun tickMarkWidth(): Double

    fun tickMarkLength(): Double

    fun tickLabelMargins(): Margins

    fun tickLabelDistance(isHorizontalOrientation: Boolean): Double {
        var result = when {
            isHorizontalOrientation -> tickLabelMargins().height()
            else -> tickLabelMargins().width()
        }
        if (showTickMarks()) {
            result += tickMarkLength()
        }
        return result
    }

    fun tooltipFill(): Color
    fun tooltipColor(): Color
    fun tooltipStrokeWidth(): Double

    fun tooltipTextStyle(): ThemeTextStyle
}
