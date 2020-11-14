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

class CrosshairComponent() : SvgComponent() {
    override fun buildComponent() {
    }

    internal fun addHorizontal(coord: DoubleVector, geomBounds: DoubleRectangle) {
        SvgLineElement(geomBounds.left, coord.y, geomBounds.right, coord.y).apply {
            add(this)
            strokeColor().set(Color.LIGHT_GRAY)
            strokeWidth().set(1.0)
        }
    }

    internal fun addVertical(coord: DoubleVector, geomBounds: DoubleRectangle) {
        SvgLineElement(coord.x, geomBounds.bottom, coord.x, geomBounds.top).apply {
            add(this)
            strokeColor().set(Color.LIGHT_GRAY)
            strokeWidth().set(1.0)
        }
    }
}