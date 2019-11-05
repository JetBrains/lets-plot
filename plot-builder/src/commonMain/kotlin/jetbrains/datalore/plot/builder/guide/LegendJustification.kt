/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.guide

/**
 * anchor point for positioning legend inside plot ("center" or two-element numeric vector)
 */
class LegendJustification(val x: Double, val y: Double) {
    companion object {
        val CENTER = jetbrains.datalore.plot.builder.guide.LegendJustification(0.5, 0.5)
    }
}
