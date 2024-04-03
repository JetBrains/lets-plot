/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.theme

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.render.linetype.LineType

interface TooltipsTheme {
    fun tooltipColor(): Color
    fun tooltipFill(): Color
    fun tooltipStrokeWidth(): Double
    fun tooltipLineType(): LineType

    fun textStyle(): ThemeTextStyle
    fun titleStyle(): ThemeTextStyle
    fun labelStyle(): ThemeTextStyle

    fun show(): Boolean
}