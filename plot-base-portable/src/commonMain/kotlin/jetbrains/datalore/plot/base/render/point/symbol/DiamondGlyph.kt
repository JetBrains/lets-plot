/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.render.point.symbol

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.vis.svg.slim.SvgSlimElements
import jetbrains.datalore.vis.svg.slim.SvgSlimShape

class DiamondGlyph(location: DoubleVector, width: Double) : SingletonGlyph(location, width) {

    override fun createShape(location: DoubleVector, size: Double): SvgSlimShape {
        val half = size / 2
        val x = doubleArrayOf(half, size, half, 0.0)
        val y = doubleArrayOf(0.0, half, size, half)
        val ox = location.x - half
        val oy = location.y - half
        for (i in 0..3) {
            x[i] = ox + x[i]
            y[i] = oy + y[i]
        }

        val pathData = GlyphUtil.buildPathData(
            x.asList(),
            y.asList()
        )
        return SvgSlimElements.path(pathData)
    }
}
