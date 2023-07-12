/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.aes.AesScaling
import org.jetbrains.letsPlot.core.plot.base.geom.legend.LollipopLegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil
import org.jetbrains.letsPlot.core.plot.base.geom.util.HintColorUtil
import org.jetbrains.letsPlot.core.plot.base.interact.GeomTargetCollector
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import org.jetbrains.letsPlot.core.plot.base.render.point.NamedShape
import org.jetbrains.letsPlot.core.plot.base.render.point.PointShapeSvg
import org.jetbrains.letsPlot.core.commons.data.SeriesUtil
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgGElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgLineElement
import kotlin.math.pow
import kotlin.math.sqrt

class LollipopGeom : GeomBase(), WithWidth, WithHeight {
    var fatten: Double = DEF_FATTEN
    var slope: Double = DEF_SLOPE
    var intercept: Double = DEF_INTERCEPT
    var direction: Direction = DEF_DIRECTION

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = LollipopLegendKeyElementFactory(fatten)

    override fun rangeIncludesZero(aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>): Boolean {
        // Pin the lollipops to an axis when baseline coincides with this axis and sticks are perpendicular to it
        return aes == org.jetbrains.letsPlot.core.plot.base.Aes.Y
                && slope == 0.0
                && intercept == 0.0
                && direction != Direction.ALONG_AXIS
    }

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val helper = GeomHelper(pos, coord, ctx)
        val targetCollector = getGeomTargetCollector(ctx)
        val colorsByDataPoint = HintColorUtil.createColorMarkerMapper(GeomKind.LOLLIPOP, ctx)

        val lollipops = mutableListOf<Lollipop>()
        for (p in GeomUtil.withDefined(aesthetics.dataPoints(), org.jetbrains.letsPlot.core.plot.base.Aes.X, org.jetbrains.letsPlot.core.plot.base.Aes.Y)) {
            val x = p.x()!!
            val y = p.y()!!
            val head = DoubleVector(x, y)
            val base = getBase(head)
            val stickLength = sqrt((head.x - base.x).pow(2) + (head.y - base.y).pow(2))
            lollipops.add(Lollipop(p, head, base, stickLength))
        }
        // Sort lollipops to better displaying when they are intersects
        for (lollipop in lollipops.sortedByDescending { it.length }) {
            val stick = lollipop.createStick(helper)
            if (stick != null) {
                root.add(stick)
            }
            root.add(lollipop.createCandy(helper))
            buildHint(lollipop, helper, targetCollector, colorsByDataPoint)
        }
    }

    private fun buildHint(
        lollipop: Lollipop,
        helper: GeomHelper,
        targetCollector: GeomTargetCollector,
        colorsByDataPoint: (DataPointAesthetics) -> List<Color>
    ) {
        targetCollector.addPoint(
            lollipop.point.index(),
            helper.toClient(lollipop.head, lollipop.point)!!,
            lollipop.candyRadius,
            GeomTargetCollector.TooltipParams(
                markerColors = colorsByDataPoint(lollipop.point)
            )
        )
    }

    override fun widthSpan(
        p: DataPointAesthetics,
        coordAes: org.jetbrains.letsPlot.core.plot.base.Aes<Double>,
        resolution: Double,
        isDiscrete: Boolean
    ): DoubleSpan? {
        val flip = coordAes == org.jetbrains.letsPlot.core.plot.base.Aes.Y
        val head = xyVec(p, flip) ?: return null
        return DoubleSpan(getBase(head).x, head.x)
    }

    override fun heightSpan(
        p: DataPointAesthetics,
        coordAes: org.jetbrains.letsPlot.core.plot.base.Aes<Double>,
        resolution: Double,
        isDiscrete: Boolean
    ): DoubleSpan? {
        val flip = coordAes == org.jetbrains.letsPlot.core.plot.base.Aes.X
        val head = xyVec(p, flip) ?: return null
        return DoubleSpan(getBase(head).y, head.y)
    }

    private fun xyVec(p: DataPointAesthetics, flip: Boolean): DoubleVector? {
        val x = p.x().takeUnless { flip } ?: p.y()
        val y = p.y().takeUnless { flip } ?: p.x()
        if (!SeriesUtil.allFinite(x, y)) {
            return null
        }

        return DoubleVector(x!!, y!!)
    }

    private fun getBase(head: DoubleVector): DoubleVector {
        return when (direction) {
            Direction.ORTHOGONAL_TO_AXIS -> DoubleVector(head.x, slope * head.x + intercept)
            Direction.ALONG_AXIS -> {
                if (slope == 0.0) {
                    DoubleVector(intercept, head.y)
                } else {
                    DoubleVector((head.y - intercept) / slope, head.y)
                }
            }

            Direction.SLOPE -> {
                val baseX = (head.x + slope * (head.y - intercept)) / (1 + slope.pow(2))
                val baseY = slope * baseX + intercept
                DoubleVector(baseX, baseY)
            }
        }
    }

    private inner class Lollipop(
        val point: DataPointAesthetics,
        val head: DoubleVector,
        val base: DoubleVector,
        val length: Double
    ) {
        val candyRadius: Double
            get() {
                val shape = point.shape()!!
                val shapeCoeff = when (shape) {
                    NamedShape.STICK_PLUS,
                    NamedShape.STICK_STAR,
                    NamedShape.STICK_CROSS -> 0.0

                    else -> 1.0
                }
                return (shape.size(point, fatten) + shapeCoeff * shape.strokeWidth(point)) / 2.0
            }

        fun createCandy(helper: GeomHelper): SvgGElement {
            val location = helper.toClient(head, point)!!
            val shape = point.shape()!!
            val o = PointShapeSvg.create(shape, location, point, fatten)
            return wrap(o)
        }

        fun createStick(helper: GeomHelper): SvgLineElement? {
            val clientBase = helper.toClient(base, point) ?: return null // base of the lollipop stick
            val clientHead = helper.toClient(head, point) ?: return null // center of the lollipop candy
            val stickLength = sqrt((clientHead.x - clientBase.x).pow(2) + (clientHead.y - clientBase.y).pow(2))
            if (candyRadius > stickLength) {
                return null
            }
            val neck = shiftHeadToBase(clientBase, clientHead, candyRadius) // meeting point of candy and stick
            val line = SvgLineElement(clientBase.x, clientBase.y, neck.x, neck.y)
            GeomHelper.decorate(line, point, applyAlphaToAll = true, strokeScaler = AesScaling::lineWidth)

            return line
        }

        private fun shiftHeadToBase(
            clientBase: DoubleVector,
            clientHead: DoubleVector,
            shiftLength: Double
        ): DoubleVector {
            val x0 = clientBase.x
            val x1 = clientHead.x
            val y0 = clientBase.y
            val y1 = clientHead.y

            if (x0 == x1) {
                val dy = if (y0 < y1) -shiftLength else shiftLength
                return DoubleVector(x1, y1 + dy)
            }
            val dx = sqrt(shiftLength.pow(2) / (1.0 + (y0 - y1).pow(2) / (x0 - x1).pow(2)))
            val x = if (x0 < x1) {
                x1 - dx
            } else {
                x1 + dx
            }
            val y = (x - x1) * (y0 - y1) / (x0 - x1) + y1

            return DoubleVector(x, y)
        }
    }

    enum class Direction {
        ORTHOGONAL_TO_AXIS, ALONG_AXIS, SLOPE
    }

    companion object {
        const val DEF_FATTEN = 2.5
        const val DEF_SLOPE = 0.0
        const val DEF_INTERCEPT = 0.0
        val DEF_DIRECTION = Direction.ORTHOGONAL_TO_AXIS

        const val HANDLES_GROUPS = PointGeom.HANDLES_GROUPS
    }
}