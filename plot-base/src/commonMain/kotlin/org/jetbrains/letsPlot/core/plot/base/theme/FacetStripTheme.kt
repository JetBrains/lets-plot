/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.theme

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.layout.TextJustification
import org.jetbrains.letsPlot.core.plot.base.layout.Thickness
import org.jetbrains.letsPlot.core.plot.base.render.linetype.LineType

interface FacetStripTheme {
    fun showStrip(): Boolean
    fun showStripBackground(): Boolean

    fun stripFill(): Color
    fun stripColor(): Color
    fun stripStrokeWidth(): Double
    fun stripLineType(): LineType
    fun stripTextStyle(): ThemeTextStyle
    fun stripMargins(): Thickness
    fun stripTextJustification(): TextJustification
}