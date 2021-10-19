/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.theme

import jetbrains.datalore.base.values.Color

/**
 * Plotting area, drawn underneath plot.
 */
interface PanelTheme {
    fun showRect(): Boolean
    fun rectColor(): Color
    fun rectFill(): Color
    fun rectStrokeWidth(): Double

    fun gridX(flipAxis: Boolean = false): PanelGridTheme
    fun gridY(flipAxis: Boolean = false): PanelGridTheme
}