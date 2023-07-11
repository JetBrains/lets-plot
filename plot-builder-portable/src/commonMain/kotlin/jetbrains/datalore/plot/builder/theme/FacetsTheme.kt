/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.theme

import org.jetbrains.letsPlot.commons.values.Color

interface FacetsTheme {
    fun showStrip(): Boolean
    fun showStripBackground(): Boolean

    fun stripFill(): Color
    fun stripColor(): Color
    fun stripStrokeWidth(): Double
    fun stripTextStyle(): ThemeTextStyle
}
