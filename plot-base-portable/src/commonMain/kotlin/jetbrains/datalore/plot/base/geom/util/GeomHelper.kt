/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom.util

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.aes.AesScaling
import jetbrains.datalore.plot.base.aes.AestheticsUtil
import jetbrains.datalore.plot.base.aes.AestheticsUtil.ALPHA_CONTROLS_BOTH
import jetbrains.datalore.plot.base.render.svg.StrokeDashArraySupport
import jetbrains.datalore.plot.base.render.svg.Text
import jetbrains.datalore.plot.base.render.svg.TextLabel
import jetbrains.datalore.vis.svg.SvgElement
import jetbrains.datalore.vis.svg.SvgLineElement
import jetbrains.datalore.vis.svg.SvgNode
import jetbrains.datalore.vis.svg.SvgShape
import jetbrains.datalore.vis.svg.slim.SvgSlimShape

open class GeomHelper(
    private val myPos: PositionAdjustment,
    coord: CoordinateSystem,
    internal val ctx: GeomContext
) {
    private val myGeomCoord: GeomCoord = GeomCoord(coord)

    fun toClient(location: DoubleVector, p: DataPointAesthetics): DoubleVector {
        return myGeomCoord.toClient(adjust(location, p, myPos, ctx))
    }

    fun toClient(x: Double?, y: Double?, p: DataPointAesthetics): DoubleVector {
        // ToDo: don't create new object (optimization)
        val location = DoubleVector(x!!, y!!)
        return myGeomCoord.toClient(adjust(location, p, myPos, ctx))
    }

    fun toClient(r: DoubleRectangle, p: DataPointAesthetics): DoubleRectangle {
        var clientRect = myGeomCoord.toClient(adjust(r, p, myPos, ctx))
        // do not allow zero height or width (shape becomes invisible)
        if (clientRect.width == 0.0) {
            clientRect = DoubleRectangle(clientRect.origin.x, clientRect.origin.y, 0.1, clientRect.height)
        }
        if (clientRect.height == 0.0) {
            clientRect = DoubleRectangle(clientRect.origin.x, clientRect.origin.y, clientRect.width, 0.1)
        }
        return clientRect
    }

    fun fromClient(location: DoubleVector): DoubleVector {
        return myGeomCoord.fromClient(location)
    }

    private fun adjust(
        location: DoubleVector,
        p: DataPointAesthetics,
        pos: PositionAdjustment,
        ctx: GeomContext
    ): DoubleVector {
        return pos.translate(location, p, ctx)
    }

    internal fun toClientRect(
        p: DataPointAesthetics,
        aesMapper: (DataPointAesthetics) -> DoubleRectangle?
    ): DoubleRectangle? {
        val r = aesMapper(p) ?: return null
        return toClient(r, p)
    }

    private fun adjust(
        r: DoubleRectangle,
        p: DataPointAesthetics,
        pos: PositionAdjustment,
        ctx: GeomContext
    ): DoubleRectangle {
        val leftTop = pos.translate(r.origin, p, ctx)
        val rightBottom = pos.translate(r.origin.add(r.dimension), p, ctx)
        return DoubleRectangle.span(leftTop, rightBottom)
    }

    protected fun project(
        dataPoints: Iterable<DataPointAesthetics>,
        projection: (DataPointAesthetics) -> DoubleVector?
    ): List<DoubleVector> {
        val points = ArrayList<DoubleVector>()
        for (p in dataPoints) {
            val location = projection(p)
            if (location != null) {
                val pp = toClient(location, p)
                points.add(pp)
            }
        }
        return points
    }

    internal fun toClientLocation(aesMapper: (DataPointAesthetics) -> DoubleVector?): (DataPointAesthetics) -> DoubleVector? {
        return { aes ->
            val location = aesMapper(aes)
            if (location != null) {
                toClient(location, aes)
            } else {
                null
            }
        }
    }

    fun createSvgElementHelper(): SvgElementHelper {
        return SvgElementHelper()
    }

    fun toClient(locations: List<DoubleVector>, p: DataPointAesthetics): List<DoubleVector> {
        val localLocations = ArrayList<DoubleVector>()
        for (location in locations) {
            localLocations.add(toClient(location, p))
        }
        return localLocations
    }

    inner class SvgElementHelper {
        private var myStrokeAlphaEnabled = false

        fun setStrokeAlphaEnabled(b: Boolean) {
            myStrokeAlphaEnabled = b
        }

        fun createLine(start: DoubleVector, end: DoubleVector, p: DataPointAesthetics): SvgLineElement {
            @Suppress("NAME_SHADOWING")
            val start = toClient(start, p)

            @Suppress("NAME_SHADOWING")
            val end = toClient(end, p)
            val line = SvgLineElement(
                start.x, start.y,
                end.x, end.y
            )
            decorate(line, p, myStrokeAlphaEnabled)
            return line
        }
    }

    companion object {
        val HJUST_MAP: Map<Any, Text.HorizontalAnchor> = mapOf(
            "right" to Text.HorizontalAnchor.RIGHT,
            "middle" to Text.HorizontalAnchor.MIDDLE,
            "left" to Text.HorizontalAnchor.LEFT,
            0.0 to Text.HorizontalAnchor.RIGHT,
            0.5 to Text.HorizontalAnchor.MIDDLE,
            1.0 to Text.HorizontalAnchor.LEFT
        )
        val VJUST_MAP: Map<Any, Text.VerticalAnchor> = mapOf(
            "bottom" to Text.VerticalAnchor.BOTTOM,
            "center" to Text.VerticalAnchor.CENTER,
            "top" to Text.VerticalAnchor.TOP,
            0.0 to Text.VerticalAnchor.BOTTOM,
            0.5 to Text.VerticalAnchor.CENTER,
            1.0 to Text.VerticalAnchor.TOP
        )
        private val FONT_WEIGHT_SET = setOf(
            "bold", "bolder", "lighter"     // 'normal' is default
        )
        private val FONT_STYLE_SET = setOf(
            "italic", "oblique"                 // 'normal' is default
        )
        private val FONT_FAMILY_MAP = mapOf(
            "sans" to "sans-serif",
            "serif" to "serif",
            "mono" to "monospace"
        )

        fun decorate(label: TextLabel, p: DataPointAesthetics, scale: Double = 1.0) {

            label.textColor().set(p.color())
            label.textOpacity().set(p.alpha())
            label.setFontSize(AesScaling.textSize(p) * scale)

            // family
            var family = p.family()
            if (FONT_FAMILY_MAP.containsKey(family)) {   // otherwise - use value as provided by user
                family = FONT_FAMILY_MAP.get(family)!!
            }
            label.setFontFamily(family)

            // fontface
            // ignore 'plain' / 'normal' as it is default values
            val fontface = p.fontface()
            if (fontface.isNotBlank()) {
                for (s in fontface.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                    if (FONT_WEIGHT_SET.contains(s)) {
                        label.setFontWeight(s)
                    } else if (FONT_STYLE_SET.contains(s)) {
                        label.setFontStyle(s)
                    }
                }
            }

            // text justification
            val hAnchor =
                textLabelAnchor(
                    p.hjust(),
                    HJUST_MAP,
                    Text.HorizontalAnchor.MIDDLE
                )
            val vAnchor =
                textLabelAnchor(
                    p.vjust(),
                    VJUST_MAP,
                    Text.VerticalAnchor.CENTER
                )

            if (hAnchor !== Text.HorizontalAnchor.LEFT) {  // 'left' is default
                label.setHorizontalAnchor(hAnchor)
            }
            if (vAnchor !== Text.VerticalAnchor.BOTTOM) {  // 'bottom' is default
                label.setVerticalAnchor(vAnchor)
            }

            var angle = p.angle()!!
            if (angle != 0.0) {
                // ggplot angle: counter clockwise
                // SVG angle: clockwise
                angle = 360 - angle % 360
                label.rotate(angle)
            }
        }

        fun <T> textLabelAnchor(o: Any, conversionMap: Map<Any, T>, def: T): T {
            return conversionMap.getOrElse(o, { def })
        }

        fun decorate(node: SvgNode, p: DataPointAesthetics, applyAlphaToAll: Boolean = ALPHA_CONTROLS_BOTH) {
            if (node is SvgShape) {
                decorateShape(
                    node as SvgShape,
                    p,
                    applyAlphaToAll
                )
            }

            if (node is SvgElement) {
                val lineType = p.lineType()
                if (!(lineType.isBlank || lineType.isSolid)) {
                    StrokeDashArraySupport.apply(node, AesScaling.strokeWidth(p), lineType.dashArray)
                }
            }
        }

        private fun decorateShape(shape: SvgShape, p: DataPointAesthetics, applyAlphaToAll: Boolean) {
            AestheticsUtil.updateStroke(shape, p, applyAlphaToAll)
            AestheticsUtil.updateFill(shape, p)
            shape.strokeWidth().set(AesScaling.strokeWidth(p))
        }

        internal fun decorateSlimShape(shape: SvgSlimShape, p: DataPointAesthetics) {
            val stroke = p.color()!!
            val strokeAlpha = AestheticsUtil.alpha(stroke, p)

            val fill = p.fill()!!
            val fillAlpha = AestheticsUtil.alpha(fill, p)

            shape.setFill(fill, fillAlpha)
            shape.setStroke(stroke, strokeAlpha)
            shape.setStrokeWidth(AesScaling.strokeWidth(p))
        }

        fun getSizeUnitAes(sizeUnitName: String): Aes<Double> {
            return when (sizeUnitName.lowercase()) {
                "x" -> Aes.X
                "y" -> Aes.Y
                else -> error("Size unit value must be either 'x' or 'y', but was $sizeUnitName.")
            }
        }
    }
}