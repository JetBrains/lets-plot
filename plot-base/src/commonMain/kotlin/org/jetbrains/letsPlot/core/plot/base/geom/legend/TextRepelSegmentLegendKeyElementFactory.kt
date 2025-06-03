/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.legend

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.geom.TextRepelGeom.Companion.toSegmentAes
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgLineElement

internal class TextRepelSegmentLegendKeyElementFactory : LegendKeyElementFactory {

    override fun createKeyElement(p: DataPointAesthetics, size: DoubleVector): SvgGElement {
        val line = SvgLineElement(0.0, size.y / 2, size.x, size.y / 2)
        GeomHelper.decorate(line, toSegmentAes(p))
        val g = SvgGElement()
        g.children().add(line)
        return g
    }

    override fun minimumKeySize(p: DataPointAesthetics): DoubleVector {
        val strokeWidth = AesScaling.strokeWidth(p)
        return DoubleVector(4.0, strokeWidth + 4)
    }
}
