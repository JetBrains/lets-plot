/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.theme

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.vis.TextStyle

interface TooltipsTheme {
    fun tooltipColor(): Color
    fun tooltipFill(): Color
    fun tooltipStrokeWidth(): Double

    fun textStyle(): TextStyle
    fun titleTextStyle(): TextStyle
    fun labelTextStyle(): TextStyle
}