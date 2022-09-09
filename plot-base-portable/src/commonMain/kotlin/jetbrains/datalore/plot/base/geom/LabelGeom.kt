/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.FontFace
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.geom.util.GeomHelper
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.plot.base.render.svg.Text
import jetbrains.datalore.plot.base.render.svg.TextLabel
import jetbrains.datalore.vis.svg.SvgGElement
import jetbrains.datalore.vis.svg.SvgPathDataBuilder
import jetbrains.datalore.vis.svg.SvgPathElement
import jetbrains.datalore.vis.svg.SvgUtils


class LabelGeom : TextGeom() {

    private val myPadding: Double = 0.25     //  Amount of padding around label, 0.25 lines ('label.padding')
    private val myRadius: Double = 0.15      //  Radius of rounded corners, 0.15 lines  ('label.r')
    private val myBorderWidth: Double = 1.0  //  Size of label border ('label.size')

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = TextLegendKeyElementFactory(withBackgroundRect = true)

    override fun buildTextComponent(
        root: SvgRoot,
        p: DataPointAesthetics,
        location: DoubleVector,
        text: String,
        sizeUnitRatio: Double
    ) {
        val fontSize = GeomHelper.fontSize(p, sizeUnitRatio)

        // background rectangle
        val rectangle = rectangleForText(p, location, text, fontSize)
        val backgroundRect = SvgPathElement().apply {
            d().set(
                roundedRectangle(rectangle, fontSize * myRadius).build()
            )
        }
        GeomHelper.decorate(backgroundRect, p)
        backgroundRect.strokeWidth().set(myBorderWidth)

        // text element
        val label = TextLabel(text)
        GeomHelper.decorate(label, p, sizeUnitRatio, applyAlpha = false)
        // move to the rectangle's center
        label.setHorizontalAnchor(Text.HorizontalAnchor.MIDDLE)
        label.setVerticalAnchor(Text.VerticalAnchor.CENTER)
        label.moveTo(rectangle.center)
        label.rotate(0.0)

        // group elements and apply rotation
        val g = SvgGElement()
        g.children().add(backgroundRect)
        g.children().add(label.rootGroup)
        SvgUtils.transformRotate(g, GeomHelper.angle(p), location.x, location.y)

        root.add(g)
    }

    private fun rectangleForText(
        p: DataPointAesthetics,
        location: DoubleVector,
        text: String,
        fontSize: Double
    ): DoubleRectangle {
        val fontFace = FontFace.fromString(p.fontface())
        val textSize = textSize(text, fontSize, fontFace)

        val width = textSize.x + fontSize * myPadding * 2
        val height = textSize.y + fontSize * myPadding * 2

        val originX = when (GeomHelper.hAnchor(p)) {
            Text.HorizontalAnchor.LEFT -> location.x
            Text.HorizontalAnchor.RIGHT -> location.x - width
            Text.HorizontalAnchor.MIDDLE -> location.x - width / 2
        }
        val originY = when (GeomHelper.vAnchor(p)) {
            Text.VerticalAnchor.TOP -> location.y
            Text.VerticalAnchor.BOTTOM -> location.y - height
            Text.VerticalAnchor.CENTER -> location.y - height / 2
        }
        return DoubleRectangle(originX, originY, width, height)
    }

    companion object {
        private fun textSize(text: String, fontSize: Double, fontFace: FontFace): DoubleVector {
            // todo size estimation
            // val textSize = labelSpec.textDimension(text)

            val width = run {
                val FONT_SIZE_TO_GLYPH_WIDTH_RATIO = 0.67
                val FONT_WEIGHT_BOLD_TO_NORMAL_WIDTH_RATIO = 1.075
                var w = fontSize * text.length * FONT_SIZE_TO_GLYPH_WIDTH_RATIO
                if (fontFace.bold) w *= FONT_WEIGHT_BOLD_TO_NORMAL_WIDTH_RATIO
                w
            }
            return DoubleVector(width, fontSize)
        }

        private fun roundedRectangle(rect: DoubleRectangle, radius: Double): SvgPathDataBuilder {
            return SvgPathDataBuilder().apply {
                with(rect) {
                    moveTo(right - radius, bottom)
                    curveTo(
                        right - radius, bottom,
                        right, bottom,
                        right, bottom - radius
                    )

                    lineTo(right, top + radius)
                    curveTo(
                        right, top + radius,
                        right, top,
                        right - radius, top
                    )

                    lineTo(left + radius, top)
                    curveTo(
                        left + radius, top,
                        left, top,
                        left, top + radius
                    )

                    lineTo(left, bottom - radius)
                    curveTo(
                        left, bottom - radius,
                        left, bottom,
                        left + radius, bottom
                    )

                    closePath()
                }
            }
        }
    }
}