/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.theme

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.guide.LegendDirection
import org.jetbrains.letsPlot.core.plot.base.guide.LegendJustification
import org.jetbrains.letsPlot.core.plot.base.guide.LegendPosition
import org.jetbrains.letsPlot.core.plot.base.layout.TextJustification
import org.jetbrains.letsPlot.core.plot.base.render.linetype.LineType

interface LegendTheme {
    fun keySize(): Double

    /**
     * extra space added around legend (px, no support for ggplot 'units')
     */
    fun margin(): Double

    /**
     * space around legend content (px)
     * this is not part of ggplot specs
     */
    fun padding(): Double

    fun position(): LegendPosition

    fun justification(): LegendJustification

    fun direction(): LegendDirection

    fun showTitle(): Boolean

    fun titleStyle(): ThemeTextStyle
    fun titleJustification(): TextJustification
    fun textStyle(): ThemeTextStyle
    fun showBackground(): Boolean
    fun backgroundColor(): Color
    fun backgroundFill(): Color
    fun backgroundStrokeWidth(): Double
    fun backgroundLineType(): LineType
}
