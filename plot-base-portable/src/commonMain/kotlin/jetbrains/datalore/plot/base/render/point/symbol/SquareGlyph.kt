/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.render.point.symbol

import jetbrains.datalore.base.geometry.DoubleVector
import org.jetbrains.letsPlot.datamodel.svg.dom.slim.SvgSlimElements
import org.jetbrains.letsPlot.datamodel.svg.dom.slim.SvgSlimShape

internal class SquareGlyph(location: DoubleVector, size: Double) : SingletonGlyph(location, size) {

    override fun createShape(location: DoubleVector, width: Double): SvgSlimShape {
        return SvgSlimElements.rect(
                location.x - width / 2,
                location.y - width / 2,
                width,
                width)
    }
}
