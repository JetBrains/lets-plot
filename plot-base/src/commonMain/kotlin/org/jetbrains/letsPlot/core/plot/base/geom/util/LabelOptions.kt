/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.util

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.DataPointAesthetics
import org.jetbrains.letsPlot.core.plot.base.GeomContext
import org.jetbrains.letsPlot.core.plot.base.render.svg.MultilineLabel
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathDataBuilder
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgUtils

class LabelOptions {
    var paddingFactor: Double = 0.25    //  Amount of padding around label
    var radiusFactor: Double = 0.15     //  Radius of rounded corners
    var borderWidth: Double = 1.0       //  Size of a label border
    var alphaStroke: Boolean = false    //  Apply alpha to text and border

    companion object {
        fun buildLabelComponent(
            p: DataPointAesthetics,
            location: DoubleVector,
            text: String,
            sizeUnitRatio: Double,
            ctx: GeomContext,
            boundsCenter: DoubleVector?,
            labelOptions: LabelOptions
        ): SvgGElement {
            // text size estimation
            val textSize = TextUtil.measure(text, p, ctx, sizeUnitRatio)

            val hAnchor = TextUtil.hAnchor(p, location, boundsCenter)
            val vAnchor = TextUtil.vAnchor(p, location, boundsCenter)

            // Background rectangle
            val fontSize = TextUtil.fontSize(p, sizeUnitRatio)
            val rectangle = labelRectangle(location, textSize, fontSize, hAnchor, vAnchor, labelOptions)
            val backgroundRect = SvgPathElement().apply {
                d().set(
                    roundedRectangle(rectangle, labelOptions.radiusFactor * rectangle.height).build()
                )
            }
            GeomHelper.decorate(backgroundRect, p, applyAlphaToAll = labelOptions.alphaStroke)
            backgroundRect.strokeWidth().set(labelOptions.borderWidth)

            // Text element
            val label = MultilineLabel(text)
            TextUtil.decorate(label, p, sizeUnitRatio, applyAlpha = labelOptions.alphaStroke)

            val padding = fontSize * labelOptions.paddingFactor
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

        fun labelRectangle(
            location: DoubleVector,
            textSize: DoubleVector,
            fontSize: Double,
            hAnchor: Text.HorizontalAnchor,
            vAnchor: Text.VerticalAnchor,
            labelOptions: LabelOptions
        ): DoubleRectangle {
            val padding = fontSize * labelOptions.paddingFactor
            return TextUtil.rectangleForText(location, textSize, padding, hAnchor, vAnchor)
        }

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