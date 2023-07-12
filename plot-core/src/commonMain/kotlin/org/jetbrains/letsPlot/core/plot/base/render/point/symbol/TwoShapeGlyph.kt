/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.point.symbol

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.datamodel.svg.dom.slim.SvgSlimGroup
import org.jetbrains.letsPlot.datamodel.svg.dom.slim.SvgSlimShape

internal abstract class TwoShapeGlyph : MultiShapeGlyph() {
    private var myS1: SvgSlimShape? = null
    private var myS2: SvgSlimShape? = null

    protected fun setShapes(s1: SvgSlimShape, s2: SvgSlimShape) {
        myS1 = s1
        myS2 = s2
    }

    override fun update(fill: Color, fillAlpha: Double, stroke: Color, strokeAlpha: Double, strokeWidth: Double) {
        update(myS1, fill, fillAlpha, stroke, strokeAlpha, strokeWidth)
        update(myS2, fill, fillAlpha, stroke, strokeAlpha, strokeWidth)
    }

    override fun appendTo(g: SvgSlimGroup) {
        myS1!!.appendTo(g)
        myS2!!.appendTo(g)
    }
}
