/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.layout

import jetbrains.datalore.plot.builder.guide.Orientation

data class AxisLayoutInfoQuad(
    val left: AxisLayoutInfo?,
    val right: AxisLayoutInfo?,
    val top: AxisLayoutInfo?,
    val bottom: AxisLayoutInfo?,
) {
    val hAxisTitleOrientation: Orientation = if (bottom != null) Orientation.BOTTOM else Orientation.TOP
    val vAxisTitleOrientation: Orientation = if (left != null) Orientation.LEFT else Orientation.RIGHT

    fun withHAxisLength(length: Double): AxisLayoutInfoQuad {
        return AxisLayoutInfoQuad(
            left = left,
            right = right,
            top = top?.withAxisLength(length),
            bottom = bottom?.withAxisLength(length),
        )
    }

    fun withVAxisLength(length: Double): AxisLayoutInfoQuad {
        return AxisLayoutInfoQuad(
            left = left?.withAxisLength(length),
            right = right?.withAxisLength(length),
            top = top,
            bottom = bottom,
        )
    }

    companion object {
        val EMPTY: AxisLayoutInfoQuad = AxisLayoutInfoQuad(null, null, null, null)
    }
}
