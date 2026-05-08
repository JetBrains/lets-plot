/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.point.symbol

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTransform
import org.jetbrains.letsPlot.datamodel.svg.dom.slim.SvgSlimGroup

internal class GlyphPair(private val myG1: Glyph, private val myG2: Glyph) :
    Glyph {

    override fun update(
        fill: Color,
        stroke: Color,
        strokeWidth: Double,
        transform: SvgTransform?
    ) {
        myG1.update(fill, stroke, strokeWidth, transform)
        myG2.update(fill, stroke, strokeWidth, transform)
    }

    override fun appendTo(g: SvgSlimGroup) {
        myG1.appendTo(g)
        myG2.appendTo(g)
    }
}
