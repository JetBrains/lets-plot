/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.point.PointShapeSvg
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement

internal class PointLegendKeyElementFactory(private val fatten: Double = 1.0) :
    LegendKeyElementFactory {

    override fun createKeyElement(p: DataPointAesthetics, size: DoubleVector): SvgGElement {
        val location = DoubleVector(size.x / 2, size.y / 2)
        val shape = p.shape()!!
        val slimObject = PointShapeSvg.create(shape, location, p, fatten, verticallyAligned = true)
        return GeomBase.Companion.wrap(slimObject)
    }

    override fun minimumKeySize(p: DataPointAesthetics): DoubleVector {
        val shape = p.shape()!!
        val shapeSize = shape.size(p, fatten)
        val strokeWidth = shape.strokeWidth(p)
        val size = shapeSize + strokeWidth + 2.0
        return DoubleVector(size, size)
    }
}
