/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.render.point.symbol

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.datamodel.svg.dom.slim.SvgSlimElements
import org.jetbrains.letsPlot.datamodel.svg.dom.slim.SvgSlimShape

class DiamondGlyph(location: DoubleVector, width: Double) : SingletonGlyph(location, width) {

    override fun createShape(location: DoubleVector, width: Double): SvgSlimShape {
        val half = width / 2
        val ox = location.x - half
        val oy = location.y - half

        val x = listOf(half, width, half, 0.0).map { it + ox }
        val y = listOf(0.0, half, width, half).map { it + oy }

        val pathData = GlyphUtil.buildPathData(x, y)
        return SvgSlimElements.path(pathData)
    }
}
