/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.render.point.PointShapeSvg
import jetbrains.datalore.vis.svg.SvgGElement
import jetbrains.datalore.vis.svg.slim.SvgSlimElements

internal class PointLegendKeyElementFactory : LegendKeyElementFactory {

    override fun createKeyElement(p: DataPointAesthetics, size: DoubleVector): SvgGElement {
        val location = DoubleVector(size.x / 2, size.y / 2)
        val shape = p.shape()!!
//        val slimObject = shape.create(location, p)
        val slimObject = PointShapeSvg.create(shape, location, p)
        val slimGroup = SvgSlimElements.g(1)
        slimObject.appendTo(slimGroup)
        return GeomBase.wrap(slimGroup)
    }

    override fun minimumKeySize(p: DataPointAesthetics): DoubleVector {
        val shape = p.shape()!!
        val shapeSize = shape.size(p)
        val strokeWidth = shape.strokeWidth(p)
        val size = shapeSize + strokeWidth + 2.0
        return DoubleVector(size, size)
    }
}
