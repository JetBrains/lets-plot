/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom.legend

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.aes.AesScaling
import jetbrains.datalore.plot.base.geom.util.GeomHelper
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.vis.svg.SvgGElement
import jetbrains.datalore.vis.svg.SvgLineElement

internal class VLineLegendKeyElementFactory :
    LegendKeyElementFactory {

    override fun createKeyElement(p: DataPointAesthetics, size: DoubleVector): SvgGElement {
        val line = SvgLineElement(size.x / 2, 0.0, size.x / 2, size.y)
        GeomHelper.decorate(line, p)
        val g = SvgGElement()
        g.children().add(line)
        return g
    }

    override fun minimumKeySize(p: DataPointAesthetics): DoubleVector {
        val strokeWidth = AesScaling.lineStrokeWidth(p)
        return DoubleVector(strokeWidth + 4, 4.0)
    }
}
