/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.theme

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.vis.TextStyle

interface PlotTheme {
    fun showBackground(): Boolean
    fun backgroundColor(): Color
    fun backgroundFill(): Color
    fun backgroundStrokeWidth(): Double
    fun titleTextStyle(): TextStyle
    fun subtitleTextStyle(): TextStyle
    fun captionTextStyle(): TextStyle
    fun textColor(): Color
}
