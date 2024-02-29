/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.base.GeomContext
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.TextUtil
import org.jetbrains.letsPlot.core.plot.base.render.svg.MultilineLabel
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathDataBuilder
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgUtils


class LabelGeom : TextGeom() {

    var paddingFactor: Double = 0.25    //  Amount of padding around label
    var radiusFactor: Double = 0.15     //  Radius of rounded corners
    var borderWidth: Double = 1.0       //  Size of label border
    var alphaStroke: Boolean = false    //  Apply alpha to text and border

    override fun buildTextComponent(
        p: DataPointAesthetics,
        location: DoubleVector,
        text: String,
        sizeUnitRatio: Double,
        ctx: GeomContext,
        boundsCenter: DoubleVector?
    ): SvgGElement {
        // text size estimation
        val textSize = TextUtil.measure(text, p, ctx, sizeUnitRatio)

        val hAnchor = TextUtil.hAnchor(p, location, boundsCenter)
        val vAnchor = TextUtil.vAnchor(p, location, boundsCenter)

        // Background rectangle
        val fontSize = TextUtil.fontSize(p, sizeUnitRatio)
        val padding = fontSize * paddingFactor
        val rectangle = rectangleForText(location, textSize, padding, hAnchor, vAnchor)
        val backgroundRect = SvgPathElement().apply {
            d().set(
                roundedRectangle(rectangle, radiusFactor * rectangle.height).build()
            )
        }
        GeomHelper.decorate(backgroundRect, p, applyAlphaToAll = alphaStroke)
        backgroundRect.strokeWidth().set(borderWidth)

        // Text element
        val label = MultilineLabel(text)
        TextUtil.decorate(label, p, sizeUnitRatio, applyAlpha = alphaStroke)

        val xPosition = when (hAnchor) {
            Text.HorizontalAnchor.LEFT -> location.x + padding
            Text.HorizontalAnchor.RIGHT -> location.x - padding
            Text.HorizontalAnchor.MIDDLE -> location.x
        }
        val textPosition = DoubleVector(
            xPosition,
            rectangle.origin.y + padding + fontSize * 0.8 // top-align the first line
        )
        label.setHorizontalAnchor(hAnchor)
        label.moveTo(textPosition)

        // group elements and apply rotation
        val g = SvgGElement()
        g.children().add(backgroundRect)
        g.children().add(label.rootGroup)

        // rotate all
        SvgUtils.transformRotate(g, TextUtil.angle(p), location.x, location.y)

        return g
    }

    private fun rectangleForText(
        location: DoubleVector,
        textSize: DoubleVector,
        padding: Double,
        hAnchor: Text.HorizontalAnchor,
        vAnchor: Text.VerticalAnchor
    ): DoubleRectangle {
        val width = textSize.x + padding * 2
        val height = textSize.y + padding * 2

        val originX = when (hAnchor) {
            Text.HorizontalAnchor.LEFT -> location.x
            Text.HorizontalAnchor.RIGHT -> location.x - width
            Text.HorizontalAnchor.MIDDLE -> location.x - width / 2
        }
        val originY = when (vAnchor) {
            Text.VerticalAnchor.TOP -> location.y
            Text.VerticalAnchor.BOTTOM -> location.y - height
            Text.VerticalAnchor.CENTER -> location.y - height / 2
        }
        return DoubleRectangle(originX, originY, width, height)
    }

    companion object {
        private fun roundedRectangle(rect: DoubleRectangle, radius: Double): SvgPathDataBuilder {
            return SvgPathDataBuilder().apply {
                with(rect) {
                    // Ensure normal radius
                    val r = minOf(radius, width / 2, height / 2)

                    moveTo(right - r, bottom)
                    curveTo(
                        right - r, bottom,
                        right, bottom,
                        right, bottom - r
                    )

                    lineTo(right, top + r)
                    curveTo(
                        right, top + r,
                        right, top,
                        right - r, top
                    )

                    lineTo(left + r, top)
                    curveTo(
                        left + r, top,
                        left, top,
                        left, top + r
                    )

                    lineTo(left, bottom - r)
                    curveTo(
                        left, bottom - r,
                        left, bottom,
                        left + r, bottom
                    )

                    closePath()
                }
            }
        }
    }
}