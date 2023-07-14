/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.guide

/**
 * anchor point for positioning legend inside plot ("center" or two-element numeric vector)
 */
class LegendJustification(val x: Double, val y: Double) {
    companion object {
        val CENTER = LegendJustification(0.5, 0.5)
    }
}
