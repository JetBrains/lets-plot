/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.point.symbol

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTransform
import org.jetbrains.letsPlot.datamodel.svg.dom.slim.SvgSlimGroup
import org.jetbrains.letsPlot.datamodel.svg.dom.slim.SvgSlimShape

abstract class SingletonGlyph : Glyph {
    private val myShape: SvgSlimShape

    protected constructor(shape: SvgSlimShape) {
        myShape = shape
    }

    protected constructor(location: DoubleVector, width: Double) {
        myShape = createShape(location, width)
    }

    protected abstract fun createShape(location: DoubleVector, width: Double): SvgSlimShape

    override fun update(
        fill: Color,
        fillAlpha: Double,
        stroke: Color,
        strokeAlpha: Double,
        strokeWidth: Double,
        transform: SvgTransform?
    ) {
        myShape.setFill(fill, fillAlpha)
        myShape.setStroke(stroke, strokeAlpha)
        myShape.setStrokeWidth(strokeWidth)
        transform?.let { myShape.setTransform(it) }
    }

    override fun appendTo(g: SvgSlimGroup) {
        myShape.appendTo(g)
    }
}
