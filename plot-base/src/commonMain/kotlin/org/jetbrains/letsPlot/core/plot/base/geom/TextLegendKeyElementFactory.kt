/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.aes.AestheticsUtil
import org.jetbrains.letsPlot.core.plot.base.geom.util.TextUtil
import org.jetbrains.letsPlot.core.plot.base.geom.util.TextUtil.angle
import org.jetbrains.letsPlot.core.plot.base.geom.util.TextUtil.fontSize
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.svg.Label
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text
import org.jetbrains.letsPlot.core.plot.base.render.text.LineBoxMetrics
import org.jetbrains.letsPlot.core.plot.base.render.text.TextBlockLayout
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgRectElement

internal class TextLegendKeyElementFactory(
    private val haloWidth: Double = 0.0,
    private val haloColor: Color? = null
) :
    LegendKeyElementFactory {

    override fun createKeyElement(p: DataPointAesthetics, size: DoubleVector): SvgGElement {
        return createKeyElement(p, size, keyFill = null)
    }

    override fun createKeyElement(p: DataPointAesthetics, size: DoubleVector, keyFill: Color?): SvgGElement {
        val rect = SvgRectElement(0.0, 0.0, size.x, size.y)
        AestheticsUtil.updateFill(rect, p)

        val label = Label("a")
        TextUtil.decorateLabelStyle(label, p, 1.0, true)
        label.setTextLayout(TextBlockLayout.uniform(label.linesCount(), LineBoxMetrics.fromBoxHeight(fontSize(p, 1.0))))
        label.setHorizontalAnchor(Text.HorizontalAnchor.MIDDLE)
        label.setVerticalAnchor(Text.VerticalAnchor.CENTER)
        label.rotate(angle(p.angle()!!))
        label.moveTo(size.x / 2, size.y / 2)

        val g = SvgGElement()
        g.children().add(rect)
        val effectiveHaloColor = haloColor ?: keyFill ?: p.fill()
        if (haloWidth > 0.0 && effectiveHaloColor != null) {
            val haloLabel = Label("a")
            TextUtil.decorateHalo(haloLabel, p, effectiveHaloColor, haloWidth)
            haloLabel.setTextLayout(TextBlockLayout.uniform(haloLabel.linesCount(), LineBoxMetrics.fromBoxHeight(fontSize(p, 1.0))))
            haloLabel.setHorizontalAnchor(Text.HorizontalAnchor.MIDDLE)
            haloLabel.setVerticalAnchor(Text.VerticalAnchor.CENTER)
            haloLabel.rotate(angle(p.angle()!!))
            haloLabel.moveTo(size.x / 2, size.y / 2)
            g.children().add(haloLabel.rootGroup)
        }
        g.children().add(label.rootGroup)
        return g
    }

    override fun minimumKeySize(p: DataPointAesthetics): DoubleVector {
        val strokeWidth = AesScaling.strokeWidth(p)
        return DoubleVector(4.0, strokeWidth + 4)
    }
}
