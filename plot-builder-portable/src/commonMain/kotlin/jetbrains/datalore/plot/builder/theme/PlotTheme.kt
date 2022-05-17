/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.theme

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.FontFace

interface PlotTheme {
    fun showBackground(): Boolean
    fun backgroundColor(): Color
    fun backgroundFill(): Color
    fun backgroundStrokeWidth(): Double
    fun titleColor(): Color
    fun titleFontFace(): FontFace
    fun subtitleColor(): Color
    fun subtitleFontFace(): FontFace
    fun captionColor(): Color
    fun captionFontFace(): FontFace
    fun textColor(): Color
}
