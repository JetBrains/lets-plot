/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom.util

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.aes.AestheticsUtil
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.vis.svg.SvgGElement
import jetbrains.datalore.vis.svg.SvgLineElement
import jetbrains.datalore.vis.svg.SvgRectElement

class GenericLegendKeyElementFactory : LegendKeyElementFactory {
    override fun createKeyElement(p: DataPointAesthetics, size: DoubleVector): SvgGElement {
        // background rect (to show fill)
        val rect = SvgRectElement(0.0, 0.0, size.x, size.y)
        AestheticsUtil.updateFill(rect, p)

        // slash-line (to show stroke)
        val line = SvgLineElement(0.0, size.y, size.x, 0.0)
        GeomHelper.decorate(line, p)

        val g = SvgGElement()
        g.children().add(rect)
        g.children().add(line)
        return g
    }
}
