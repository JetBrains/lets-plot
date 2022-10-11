/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.aes.AesScaling
import jetbrains.datalore.plot.base.aes.AestheticsUtil
import jetbrains.datalore.plot.base.geom.util.TextUtil
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.render.svg.Text
import jetbrains.datalore.plot.base.render.svg.TextLabel
import jetbrains.datalore.vis.svg.SvgGElement
import jetbrains.datalore.vis.svg.SvgRectElement

internal class TextLegendKeyElementFactory :
    LegendKeyElementFactory {

    override fun createKeyElement(p: DataPointAesthetics, size: DoubleVector): SvgGElement {
        val rect = SvgRectElement(0.0, 0.0, size.x, size.y)
        AestheticsUtil.updateFill(rect, p)

        val label = TextLabel("a")
        TextUtil.decorate(label, p)
        label.setHorizontalAnchor(Text.HorizontalAnchor.MIDDLE)
        label.setVerticalAnchor(Text.VerticalAnchor.CENTER)
        label.moveTo(size.x / 2, size.y / 2)

        val g = SvgGElement()
        g.children().add(rect)
        g.children().add(label.rootGroup)
        return g
    }

    override fun minimumKeySize(p: DataPointAesthetics): DoubleVector {
        val strokeWidth = AesScaling.strokeWidth(p)
        return DoubleVector(4.0, strokeWidth + 4)
    }
}
