/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.theme

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.layout.Thickness
import org.jetbrains.letsPlot.core.plot.base.render.linetype.LineType

/**
 * Plotting area, drawn underneath plot.
 */
interface PanelTheme {
    fun showRect(): Boolean
    fun rectColor(): Color
    fun rectFill(): Color
    fun rectStrokeWidth(): Double
    fun rectLineType(): LineType

    fun showBorder(): Boolean
    fun borderColor(): Color
    fun borderWidth(): Double
    fun borderIsOntop(): Boolean
    fun borderLineType(): LineType

    fun gridX(flipAxis: Boolean = false): PanelGridTheme
    fun gridY(flipAxis: Boolean = false): PanelGridTheme

    fun inset(): Thickness
}