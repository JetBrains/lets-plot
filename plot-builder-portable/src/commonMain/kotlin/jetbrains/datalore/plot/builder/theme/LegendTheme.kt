/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.theme

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.builder.guide.LegendDirection
import jetbrains.datalore.plot.builder.guide.LegendJustification
import jetbrains.datalore.plot.builder.guide.LegendPosition
import jetbrains.datalore.plot.builder.layout.TextJustification

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

    fun titleStyle(): ThemeTextStyle
    fun titleJustification(): TextJustification
    fun textStyle(): ThemeTextStyle
    fun showBackground(): Boolean
    fun backgroundColor(): Color
    fun backgroundFill(): Color
    fun backgroundStrokeWidth(): Double
}
