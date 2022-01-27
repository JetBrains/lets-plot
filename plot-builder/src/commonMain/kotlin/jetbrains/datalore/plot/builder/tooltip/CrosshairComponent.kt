/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.render.svg.SvgComponent
import jetbrains.datalore.vis.svg.SvgGraphicsElement.Visibility.HIDDEN
import jetbrains.datalore.vis.svg.SvgGraphicsElement.Visibility.VISIBLE
import jetbrains.datalore.vis.svg.SvgLineElement

class CrosshairComponent : SvgComponent() {
    private val hLine = SvgLineElement()
    private val hOutline = SvgLineElement()

    private val vLine = SvgLineElement()
    private val vOutline = SvgLineElement()

    init {
        fun setStyle(line: SvgLineElement, isOutline: Boolean = false){
            val color = Color.WHITE.takeIf { isOutline } ?: Color.GRAY
            val stroke = 1.5.takeIf { isOutline } ?: 1.0
            line.strokeColor().set(color)
            line.strokeWidth().set(stroke)
        }

        setStyle(hLine)
        setStyle(hOutline, isOutline = true)

        setStyle(vLine)
        setStyle(vOutline, isOutline = true)
    }

    override fun buildComponent() {
        add(hOutline)
        add(hLine)

        add(vOutline)
        add(vLine)
    }

    fun update(coord: DoubleVector, geomBounds: DoubleRectangle, showHorizontal: Boolean, showVertical: Boolean) {
        updateLine(showVertical, isVertical = true, coord.x, geomBounds.bottom, coord.x, geomBounds.top)
        updateLine(showHorizontal, isVertical = false, geomBounds.left, coord.y, geomBounds.right, coord.y)
    }

    private fun updateLine(isVisible: Boolean, isVertical: Boolean, x1: Double, y1: Double, x2: Double, y2: Double) {
        fun update(line: SvgLineElement) {
            line.visibility().set(VISIBLE.takeIf { isVisible } ?: HIDDEN)
            line.x1().set(x1)
            line.y1().set(y1)
            line.x2().set(x2)
            line.y2().set(y2)
        }

        when (isVertical) {
            true -> { update(vLine); update(vOutline) }
            false -> { update(hLine); update(hOutline)}
        }
    }
}
