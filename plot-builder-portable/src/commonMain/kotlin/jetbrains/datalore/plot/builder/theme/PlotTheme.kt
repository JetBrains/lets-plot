/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.theme

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.builder.layout.Margins
import jetbrains.datalore.plot.builder.layout.TextJustification
import jetbrains.datalore.vis.TextStyle

interface PlotTheme {
    fun showBackground(): Boolean
    fun backgroundColor(): Color
    fun backgroundFill(): Color
    fun backgroundStrokeWidth(): Double
    fun titleStyle(): TextStyle
    fun subtitleStyle(): TextStyle
    fun captionStyle(): TextStyle
    fun textWidthScale(): Double
    fun textColor(): Color

    fun titleJustification(): TextJustification
    fun subtitleJustification(): TextJustification
    fun captionJustification(): TextJustification

    fun titleMargins(): Margins
    fun subtitleMargins(): Margins
    fun captionMargins(): Margins
}
