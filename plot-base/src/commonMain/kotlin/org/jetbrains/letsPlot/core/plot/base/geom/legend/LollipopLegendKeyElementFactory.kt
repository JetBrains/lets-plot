/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.legend

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.geom.GeomBase
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.point.NamedShape
import org.jetbrains.letsPlot.core.plot.base.render.point.PointShapeSvg
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgLineElement

class LollipopLegendKeyElementFactory(
    private val fatten: Double = 1.0,
    private val stickLength: Double = 5.0
) :
    LegendKeyElementFactory {
    override fun createKeyElement(p: DataPointAesthetics, size: DoubleVector): SvgGElement {
        val g = SvgGElement()

        val location = DoubleVector(size.x / 2, (size.y - stickLength) / 2)
        val slimObject = PointShapeSvg.create(p.shape()!!, location, p, fatten)
        val circle = GeomBase.Companion.wrap(slimObject)
        g.children().add(circle)

        val radius = radiusPx(p)
        val line = SvgLineElement(location.x, location.y + radius, location.x, location.y + radius + stickLength)
        GeomHelper.decorate(line, p, strokeScaler = AesScaling::lineWidth)
        g.children().add(line)

        return g
    }

    override fun minimumKeySize(p: DataPointAesthetics): DoubleVector {
        val shape = p.shape()!!
        val shapeSize = shape.size(p, fatten)
        val strokeWidth = shape.strokeWidth(p)
        val size = shapeSize + strokeWidth + 2.0
        return DoubleVector(size, size)
    }

    private fun radiusPx(p: DataPointAesthetics): Double {
        val shape = p.shape()!!
        val shapeCoeff = when (shape) {
            NamedShape.STICK_PLUS,
            NamedShape.STICK_STAR,
            NamedShape.STICK_CROSS -> 0.0

            else -> 1.0
        }
        return (shape.size(p, fatten) + shapeCoeff * shape.strokeWidth(p)) / 2.0
    }
}