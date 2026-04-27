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
        // LegendComponent subtracts 2 px before passing the size into createKeyElement().
        private const val LEGEND_KEY_INSET_COMPENSATION = 2.0
    }

    override val supportsKeySizeMultiplier: Boolean
        get() = true

    override fun createKeyElement(p: DataPointAesthetics, size: DoubleVector): SvgGElement {
        val strokeWidth = AesScaling.strokeWidth(p)
        val rect = SvgRectElement(
            strokeWidth / 2,
            strokeWidth / 2,
            maxOf(0.0, size.x - strokeWidth),
            maxOf(0.0, size.y - strokeWidth)
        )
        GeomHelper.decorate(rect, p)

        val g = SvgGElement()
        g.children().add(rect)
        return g
    }

    override fun minimumKeySize(p: DataPointAesthetics): DoubleVector {
        val strokeWidth = AesScaling.strokeWidth(p)
        val cellSize = MIN_INTERIOR_SIZE + 2 * strokeWidth
        return DoubleVector(
            cellSize + LEGEND_KEY_INSET_COMPENSATION,
            cellSize + LEGEND_KEY_INSET_COMPENSATION
        )
    }
}
