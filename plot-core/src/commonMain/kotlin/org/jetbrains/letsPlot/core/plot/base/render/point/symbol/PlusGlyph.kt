/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.point.symbol

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.datamodel.svg.dom.slim.SvgSlimElements

internal class PlusGlyph(location: DoubleVector, size: Double) : TwoShapeGlyph() {

    init {
        val half = size / 2
        val ox = location.x - half
        val oy = location.y - half
        val hLine = SvgSlimElements.line(
                0 + ox,
                half + oy,
                size + ox,
                half + oy)
        val vLine = SvgSlimElements.line(
                half + ox,
                0 + oy,
                half + ox,
                size + oy)

        setShapes(hLine, vLine)
    }
}
