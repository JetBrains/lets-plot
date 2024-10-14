/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.theme

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.guide.LegendArrangement
import org.jetbrains.letsPlot.core.plot.base.guide.LegendBoxJustification
import org.jetbrains.letsPlot.core.plot.base.guide.LegendDirection
import org.jetbrains.letsPlot.core.plot.base.guide.LegendJustification
import org.jetbrains.letsPlot.core.plot.base.guide.LegendPosition
import org.jetbrains.letsPlot.core.plot.base.layout.TextJustification
import org.jetbrains.letsPlot.core.plot.base.layout.Thickness
import org.jetbrains.letsPlot.core.plot.base.render.linetype.LineType

interface LegendTheme {
    /**
     * Legend Keys Options
     */
    fun keySize(): DoubleVector
    // background underneath legend keys
    fun showKeyRect(): Boolean
    fun keyRectFill(): Color
    fun keyRectColor(): Color
    fun keyRectStrokeWidth(): Double
    fun keyLineType(): LineType

    fun keySpacing(): DoubleVector

    /**
     * Legend Box Options - full legend area
     */
    fun boxArrangement(): LegendArrangement

    // Space between plotting area and legend box
    fun boxSpacing(): Double

    fun boxJustification(): LegendBoxJustification

    /**
     * Legend Options for each legend
     */

    // Space around legend content (px)
    fun margins(): Thickness

    // Space between legends
    fun spacing(): DoubleVector

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
