/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.guide

/**
 * anchor point for positioning legend inside the area defined by legend_position ('center', 'left', 'right', 'top', 'bottom' or a two-element numeric vector)
 */
class LegendJustification(val x: Double, val y: Double) {
    companion object {
        val CENTER = LegendJustification(0.5, 0.5)
        val LEFT = LegendJustification(0.0, 0.5)
        val RIGHT = LegendJustification(1.0, 0.5)
        val TOP = LegendJustification(0.5, 1.0)
        val BOTTOM = LegendJustification(0.5, 0.0)
    }
}
