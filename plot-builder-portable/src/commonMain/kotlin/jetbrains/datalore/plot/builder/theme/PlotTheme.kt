/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.theme

import org.jetbrains.letsPlot.commons.values.Color
import jetbrains.datalore.plot.builder.layout.Margins
import jetbrains.datalore.plot.builder.layout.TextJustification

interface PlotTheme {
    fun showBackground(): Boolean
    fun backgroundColor(): Color
    fun backgroundFill(): Color
    fun backgroundStrokeWidth(): Double
    fun titleStyle(): ThemeTextStyle
    fun subtitleStyle(): ThemeTextStyle
    fun captionStyle(): ThemeTextStyle

    fun textColor(): Color
    fun textStyle(): ThemeTextStyle

    fun titleJustification(): TextJustification
    fun subtitleJustification(): TextJustification
    fun captionJustification(): TextJustification

    fun titleMargins(): Margins
    fun subtitleMargins(): Margins
    fun captionMargins(): Margins
}
