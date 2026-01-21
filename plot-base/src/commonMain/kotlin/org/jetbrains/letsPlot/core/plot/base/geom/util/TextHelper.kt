/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom.util

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.geometry.GeometryUtils
import org.jetbrains.letsPlot.commons.intern.math.toRadians
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.geom.TextGeom.Companion.BASELINE_TEXT_WIDTH
import org.jetbrains.letsPlot.core.plot.base.render.svg.Label
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathDataBuilder
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgUtils

class TextHelper(
    private val myAesthetics: Aesthetics,
    pos: PositionAdjustment,
    coord: CoordinateSystem,
    ctx: GeomContext,
    private val formatter: ((Any) -> String)?,
    private val naValue: String,
    private val sizeUnit: String?,
    private val checkOverlap: Boolean,
    private val coordOrNull: (DataPointAesthetics) -> DoubleVector?,
    private val objectRectangle: (DoubleVector, DoubleVector, Double, Text.HorizontalAnchor, Text.VerticalAnchor) -> DoubleRectangle,
    private val componentFactory: (DataPointAesthetics, DoubleVector, String, Double, GeomContext, DoubleVector?) -> SvgGElement
) : GeomHelper(pos, coord, ctx) {

    internal fun createSvgComponents(): List<SvgGElement> {
        val restrictions = mutableListOf<List<DoubleVector>>()
        val aesBoundsCenter = coord.toClient(ctx.getAesBounds())?.center
        return myAesthetics.dataPoints().mapNotNull { p ->
            val text = toString(p.label())
            if (text.isEmpty()) return@mapNotNull null
            val point = coordOrNull(p) ?: return@mapNotNull null
            val location = toClient(point, p) ?: return@mapNotNull null

            // Adapt point size to plot 'grid step' if necessary (i.e. in correlation matrix).
            val sizeUnitRatio = AesScaling.sizeUnitRatio(point, coord, sizeUnit, BASELINE_TEXT_WIDTH)

            val rectangle = getRect(p, location, text, sizeUnitRatio, ctx, aesBoundsCenter)
            if (checkOverlap) {
                if (restrictions.any { GeometryUtils.arePolygonsIntersected(rectangle, it) }) {
                    return@mapNotNull null
                }
                restrictions.add(rectangle)
            }

            componentFactory(p, location, text, sizeUnitRatio, ctx, aesBoundsCenter)
        }
    }

    internal fun buildHints(targetCollector: GeomTargetCollector) {
        val colorsByDataPoint = HintColorUtil.createColorMarkerMapper(GeomKind.TEXT, this.ctx)

        myAesthetics.dataPoints().forEach { p ->
            val point = coordOrNull(p) ?: return
            val location = toClient(point, p) ?: return
            val sizeUnitRatio = AesScaling.sizeUnitRatio(point, coord, sizeUnit, BASELINE_TEXT_WIDTH)
            targetCollector.addPoint(
                p.index(),
                location,
                sizeUnitRatio * AesScaling.textSize(p) / 2,
                GeomTargetCollector.TooltipParams(
                    markerColors = colorsByDataPoint(p)
                ),
                TipLayoutHint.Kind.CURSOR_TOOLTIP
            )
        }
    }

    internal fun toString(label: Any?): String {
        if (label == null) return naValue

        val formatter = formatter ?: ctx.getDefaultFormatter(Aes.LABEL)
        return formatter(label)
    }

    internal fun getRect(
        p: DataPointAesthetics,
        location: DoubleVector,
        text: String,
        sizeUnitRatio: Double,
        ctx: GeomContext,
        boundsCenter: DoubleVector?
    ): List<DoubleVector> {
        val textSize = TextUtil.measure(text, p, ctx, sizeUnitRatio)
        val hAnchor = TextUtil.hAnchor(p, location, boundsCenter)
        val vAnchor = TextUtil.vAnchor(p, location, boundsCenter)
        val fontSize = TextUtil.fontSize(p, sizeUnitRatio)
        val angle = toRadians(TextUtil.angle(p))

        return objectRectangle(location, textSize, fontSize, hAnchor, vAnchor)
            .rotate(angle, location)
    }

    companion object {
        internal fun textComponentFactory(
            p: DataPointAesthetics,
            location: DoubleVector,
            text: String,
            sizeUnitRatio: Double,
            ctx: GeomContext,
            boundsCenter: DoubleVector?
        ): SvgGElement {
            val label = Label(text)
            TextUtil.decorate(label, p, sizeUnitRatio, applyAlpha = true)
            val hAnchor = TextUtil.hAnchor(p, location, boundsCenter)
            label.setHorizontalAnchor(hAnchor)

            val fontSize = TextUtil.fontSize(p, sizeUnitRatio)
            val textHeight = TextUtil.measure(text, p, ctx, sizeUnitRatio).y
            //val textHeight = TextHelper.lineheight(p, sizeUnitRatio) * (label.linesCount() - 1) + fontSize

            val yPosition = when (TextUtil.vAnchor(p, location, boundsCenter)) {
                Text.VerticalAnchor.TOP -> location.y + fontSize * 0.7
                Text.VerticalAnchor.BOTTOM -> location.y - textHeight + fontSize
                Text.VerticalAnchor.CENTER -> location.y - textHeight / 2 + fontSize * 0.8
            }

            val textLocation = DoubleVector(location.x, yPosition)
            label.moveTo(textLocation)

            val g = SvgGElement()
            g.children().add(label.rootGroup)
            SvgUtils.transformRotate(g, TextUtil.angle(p), location.x, location.y)
            return g
        }

        internal fun labelComponentFactory(
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
            val label = Label(text)
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

        internal fun textRectangle(
            location: DoubleVector,
            textSize: DoubleVector,
            hAnchor: Text.HorizontalAnchor,
            vAnchor: Text.VerticalAnchor,
        ) = TextUtil.rectangleForText(location, textSize, padding = 0.0, hAnchor, vAnchor)

        internal fun labelRectangle(
            location: DoubleVector,
            textSize: DoubleVector,
            fontSize: Double,
            hAnchor: Text.HorizontalAnchor,
            vAnchor: Text.VerticalAnchor,
            labelOptions: LabelOptions
        ) = TextUtil.rectangleForText(location, textSize, padding = fontSize * labelOptions.paddingFactor, hAnchor, vAnchor)

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