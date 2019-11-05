/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.render.point.symbol

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.vis.svg.slim.SvgSlimElements
import jetbrains.datalore.vis.svg.slim.SvgSlimShape

class CircleGlyph(location: DoubleVector, width: Double) : SingletonGlyph(location, width) {

    override fun createShape(location: DoubleVector, width: Double): SvgSlimShape {
        return SvgSlimElements.circle(location.x, location.y, width / 2)
    }
}
