/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom.legend

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.geom.util.GeomHelper
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgRectElement

class GenericLegendKeyElementFactory : LegendKeyElementFactory {
    override fun createKeyElement(p: DataPointAesthetics, size: DoubleVector): SvgGElement {
        // rect with background (to show fill) and stroke (to show color)
        val rect = SvgRectElement(0.0, 0.0, size.x, size.y)
        GeomHelper.decorate(rect, p)
        rect.strokeWidth().set(1.5) // set thickness

        val g = SvgGElement()
        g.children().add(rect)
        return g
    }
}
