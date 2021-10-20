/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.render.svg.SvgComponent
import jetbrains.datalore.vis.svg.SvgLineElement

class CrosshairComponent(
    private val coord: DoubleVector,
    private val geomBounds: DoubleRectangle,
    private val showHorizontal: Boolean,
    private val showVertical: Boolean
) : SvgComponent() {
    override fun buildComponent() {
        if (showVertical) {
            addLine(coord.x, geomBounds.bottom, coord.x, geomBounds.top)
        }

        if (showHorizontal) {
            addLine(geomBounds.left, coord.y, geomBounds.right, coord.y)
        }
    }

    private fun addLine(x1: Double, y1: Double, x2: Double, y2: Double) {
        SvgLineElement(x1, y1, x2, y2).apply {
            add(this)
            strokeColor().set(Color.WHITE)
            strokeWidth().set(1.5)
        }

        SvgLineElement(x1, y1, x2, y2).apply {
            add(this)
            strokeColor().set(Color.GRAY)
            strokeWidth().set(1.0)
        }
    }
}
