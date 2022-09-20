/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.FontFace
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.GeomContext
import jetbrains.datalore.plot.base.geom.util.GeomHelper
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.plot.base.render.svg.Text
import jetbrains.datalore.plot.base.render.svg.TextLabel
import jetbrains.datalore.vis.svg.SvgGElement
import jetbrains.datalore.vis.svg.SvgPathDataBuilder
import jetbrains.datalore.vis.svg.SvgPathElement
import jetbrains.datalore.vis.svg.SvgUtils


class LabelGeom : TextGeom() {

    var paddingFactor: Double = 0.25    //  Amount of padding around label
    var radiusFactor: Double = 0.15     //  Radius of rounded corners
    var borderWidth: Double = 1.0       //  Size of label border

    override fun buildTextComponent(
        root: SvgRoot,
        p: DataPointAesthetics,
        location: DoubleVector,
        text: String,
        sizeUnitRatio: Double,
        ctx: GeomContext
    ) {
        // background rectangle
        val rectangle = rectangleForText(p, location, text, sizeUnitRatio, ctx)
        val backgroundRect = SvgPathElement().apply {
            d().set(
                roundedRectangle(rectangle, radiusFactor * rectangle.height).build()
            )
        }
        GeomHelper.decorate(backgroundRect, p)
        backgroundRect.strokeWidth().set(borderWidth)

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
        sizeUnitRatio: Double,
        ctx: GeomContext
    ): DoubleRectangle {
        val fontSize = GeomHelper.fontSize(p, sizeUnitRatio)
        val fontFace = FontFace.fromString(p.fontface())
        val fontFamily = GeomHelper.fontFamily(p)

        val textSize = ctx.estimateTextSize(
            text, fontFamily, fontSize,
            isBold = fontFace.bold,
            isItalic = fontFace.italic
        )

        val width = textSize.x + fontSize * paddingFactor * 2
        val height = textSize.y + fontSize * paddingFactor * 2

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