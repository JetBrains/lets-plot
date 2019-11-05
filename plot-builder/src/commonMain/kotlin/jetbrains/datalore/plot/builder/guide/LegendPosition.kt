/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.guide

/**
 * the position of legends ("none", "left", "right", "bottom", "top", or two-element numeric vector)
 */
class LegendPosition(val x: Double, val y: Double) {

    val isFixed: Boolean
        get() = this === jetbrains.datalore.plot.builder.guide.LegendPosition.Companion.LEFT || this === jetbrains.datalore.plot.builder.guide.LegendPosition.Companion.RIGHT ||
                this === jetbrains.datalore.plot.builder.guide.LegendPosition.Companion.TOP || this === jetbrains.datalore.plot.builder.guide.LegendPosition.Companion.BOTTOM

    val isHidden: Boolean
        get() = this === jetbrains.datalore.plot.builder.guide.LegendPosition.Companion.NONE

    val isOverlay: Boolean
        get() = !(isFixed || isHidden)

    companion object {
        val RIGHT = jetbrains.datalore.plot.builder.guide.LegendPosition(1.0, 0.5)
        val LEFT = jetbrains.datalore.plot.builder.guide.LegendPosition(0.0, 0.5)
        val TOP = jetbrains.datalore.plot.builder.guide.LegendPosition(0.5, 1.0)
        val BOTTOM = jetbrains.datalore.plot.builder.guide.LegendPosition(0.5, 1.0)
        val NONE = jetbrains.datalore.plot.builder.guide.LegendPosition(Double.NaN, Double.NaN)
    }
}
