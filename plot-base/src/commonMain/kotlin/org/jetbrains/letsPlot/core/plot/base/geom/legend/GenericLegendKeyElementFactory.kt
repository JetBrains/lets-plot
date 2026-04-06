/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.legend

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgRectElement

class GenericLegendKeyElementFactory : LegendKeyElementFactory {
    companion object {
        private const val MIN_INTERIOR_SIZE = 6.0
    }

    override fun createKeyElement(p: DataPointAesthetics, size: DoubleVector): SvgGElement {
        val sw = AesScaling.strokeWidth(p)

        val rect = SvgRectElement(
            sw / 2,
            sw / 2,
            maxOf(0.0, size.x - sw),
            maxOf(0.0, size.y - sw)
        )
        GeomHelper.decorate(rect, p)

        return SvgGElement().apply {
            children().add(rect)
        }
    }

    override fun minimumKeySize(p: DataPointAesthetics): DoubleVector {
        val sw = AesScaling.strokeWidth(p)
        val cellSize = MIN_INTERIOR_SIZE + 2 * sw

        // If LegendComponent subtracts 2 before passing size into createKeyElement(),
        // compensate here:
        return DoubleVector(cellSize + 2, cellSize + 2)
//        return DoubleVector(cellSize, cellSize)
    }
}
