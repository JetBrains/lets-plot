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
import jetbrains.datalore.plot.base.render.svg.MultilineLabel
import jetbrains.datalore.plot.base.render.svg.Text
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
        // text size estimation
        val fontSize = GeomHelper.fontSize(p, sizeUnitRatio)
        val textSize = textSize(text, GeomHelper.fontFamily(p), fontSize, FontFace.fromString(p.fontface()), ctx)

        // Background rectangle
        val padding = fontSize * paddingFactor
        val rectangle = rectangleForText(p, location, textSize, padding)
        val backgroundRect = SvgPathElement().apply {
            d().set(
                roundedRectangle(rectangle, radiusFactor * rectangle.height).build()
            )
        }
        GeomHelper.decorate(backgroundRect, p)
        backgroundRect.strokeWidth().set(borderWidth)

        // Text element
        val label = MultilineLabel(text)
        GeomHelper.decorate(label, p, sizeUnitRatio, applyAlpha = false)
        // move text to the rectangle's center and top-align the first line
        val textPosition = with(rectangle) {
            origin.add(DoubleVector(width / 2, padding + fontSize * 0.8))
        }
        label.setHorizontalAnchor(Text.HorizontalAnchor.MIDDLE)
        label.moveTo(textPosition)

        // group elements and apply rotation
        val g = SvgGElement()
        g.children().add(backgroundRect)
        g.children().add(label.rootGroup)

        // rotate all
        SvgUtils.transformRotate(g, GeomHelper.angle(p), location.x, location.y)

        root.add(g)
    }

    private fun rectangleForText(
        p: DataPointAesthetics,
        location: DoubleVector,
        textSize: DoubleVector,
        padding: Double
    ): DoubleRectangle {
        val width = textSize.x + padding * 2
        val height = textSize.y + padding * 2

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
        private fun textSize(
            text: String,
            fontFamily: String,
            fontSize: Double,
            fontFace: FontFace,
            ctx: GeomContext
        ): DoubleVector {
            return text.split('\n').map(String::trim).map { line ->
                ctx.estimateTextSize(line, fontFamily, fontSize, fontFace.bold, fontFace.italic)
            }.fold(DoubleVector.ZERO) { acc, sz ->
                DoubleVector(
                    x = kotlin.math.max(acc.x, sz.x),
                    y = acc.y + sz.y
                )
            }
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