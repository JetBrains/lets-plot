/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.render.linetype.LineType
import org.jetbrains.letsPlot.core.plot.base.render.svg.StrokeDashArraySupport
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgRectElement

interface LegendKeyElementFactory {
    fun createKeyElement(p: DataPointAesthetics, size: DoubleVector): SvgGElement

    fun minimumKeySize(p: DataPointAesthetics): DoubleVector {
        val strokeWidth = AesScaling.strokeWidth(p)
        val size = 2 * strokeWidth + 4
        return DoubleVector(size, size)
    }

    companion object {
        fun createBackgroundRect(
            keySize: DoubleVector,
            color: Color,
            fill: Color,
            strokeWidth: Double,
            lineType: LineType,
        ): SvgRectElement {
            val keyBounds = DoubleRectangle(DoubleVector.ZERO, keySize)
            val rect = SvgRectElement(keyBounds)
            rect.fillColor().set(fill)
            rect.strokeColor().set(color)
            rect.strokeWidth().set(strokeWidth)
            StrokeDashArraySupport.apply(rect, strokeWidth, lineType)
            return rect
        }
    }
}
