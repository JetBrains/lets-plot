/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.point.symbol

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.datamodel.svg.dom.slim.SvgSlimElements
import org.jetbrains.letsPlot.datamodel.svg.dom.slim.SvgSlimShape

class CircleGlyph(location: DoubleVector, width: Double) : SingletonGlyph(location, width) {

    override fun createShape(location: DoubleVector, width: Double): SvgSlimShape {
        return SvgSlimElements.circle(location.x, location.y, width / 2)
    }
}
