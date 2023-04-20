/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.aes.AesScaling
import jetbrains.datalore.plot.base.geom.util.*
import jetbrains.datalore.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.plot.base.render.point.NamedShape
import jetbrains.datalore.plot.base.render.point.PointShapeSvg
import jetbrains.datalore.plot.common.data.SeriesUtil
import jetbrains.datalore.vis.svg.SvgGElement
import jetbrains.datalore.vis.svg.SvgLineElement
import kotlin.math.*

class LollipopGeom : GeomBase(), WithWidth, WithHeight {
    var fatten: Double = DEF_FATTEN
    var slope: Double = DEF_SLOPE
    var intercept: Double = DEF_INTERCEPT
    var direction: Direction = DEF_DIRECTION

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = PointLegendKeyElementFactory()

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
        for (p in GeomUtil.withDefined(aesthetics.dataPoints(), Aes.X, Aes.Y)) {
            val x = p.x()!!
            val y = p.y()!!
            val head = DoubleVector(x, y)
            val base = getBase(x, y, isYOrientation = false, orientationHasBeenApplied = true)
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
            lollipop.candyRadius(),
            GeomTargetCollector.TooltipParams(
                markerColors = colorsByDataPoint(lollipop.point)
            )
        )
    }

    override fun widthSpan(
        p: DataPointAesthetics,
        coordAes: Aes<Double>,
        resolution: Double,
        isDiscrete: Boolean
    ): DoubleSpan? {
        return span(p, coordAes, coordAes == Aes.Y)
    }

    override fun heightSpan(
        p: DataPointAesthetics,
        coordAes: Aes<Double>,
        resolution: Double,
        isDiscrete: Boolean
    ): DoubleSpan? {
        return span(p, coordAes, coordAes == Aes.X)
    }

    private fun span(p: DataPointAesthetics, coordAes: Aes<Double>, isYOrientation: Boolean): DoubleSpan? {
        val x = p.x()
        val y = p.y()
        if (!SeriesUtil.allFinite(x, y)) {
            return null
        }
        val base = getBase(x!!, y!!, isYOrientation, orientationHasBeenApplied = false)

        return when (coordAes) {
            Aes.X -> DoubleSpan(base.x, x)
            Aes.Y -> DoubleSpan(base.y, y)
            else -> throw IllegalArgumentException("Aesthetic ${coordAes.name} is not consumed by spanning function")
        }
    }

    private fun getBase(x: Double, y: Double, isYOrientation: Boolean, orientationHasBeenApplied: Boolean): DoubleVector {
        return when (direction) {
            Direction.ORTHOGONAL_TO_AXIS -> when (isYOrientation) {
                false -> getYByX(x)
                true -> if (orientationHasBeenApplied) {
                    getYByX(x)
                } else {
                    getYByX(y).flip()
                }
            }
            Direction.ALONG_AXIS -> when (isYOrientation) {
                false -> getXByY(y)
                true -> if (orientationHasBeenApplied) {
                    getYByX(x)
                } else {
                    getXByY(x).flip()
                }
            }
            Direction.SLOPE -> when (isYOrientation) {
                false -> getBaseForOrthogonalStick(x, y)
                true -> if (orientationHasBeenApplied) {
                    getBaseForOrthogonalStick(x, y)
                } else {
                    getBaseForOrthogonalStick(y, x).flip()
                }
            }
        }
    }

    private fun getBaseForOrthogonalStick(x: Double, y: Double): DoubleVector {
        val baseX = (x + slope * (y - intercept)) / (1 + slope.pow(2))
        val baseY = slope * baseX + intercept
        return DoubleVector(baseX, baseY)
    }

    private fun getYByX(x: Double): DoubleVector {
        return DoubleVector(x, slope * x + intercept)
    }

    private fun getXByY(y: Double): DoubleVector {
        require(slope != 0.0) { "For current combination of parameters lollipop sticks are parallel to the baseline" }
        return DoubleVector((y - intercept) / slope, y)
    }

    private inner class Lollipop(
        val point: DataPointAesthetics,
        val head: DoubleVector,
        val base: DoubleVector,
        val length: Double
    ) {
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
            val candyRadius = candyRadius()
            if (candyRadius > stickLength) {
                return null
            }
            val neck = shiftHeadToBase(clientBase, clientHead, candyRadius) // meeting point of candy and stick
            val line = SvgLineElement(clientBase.x, clientBase.y, neck.x, neck.y)
            GeomHelper.decorate(line, point, applyAlphaToAll = true, strokeScaler = AesScaling::lineWidth)

            return line
        }

        fun candyRadius(): Double {
            val shape = point.shape()!!
            val shapeCoeff = when (shape) {
                NamedShape.STICK_PLUS,
                NamedShape.STICK_STAR,
                NamedShape.STICK_CROSS -> 0.0
                else -> 1.0
            }
            return (shape.size(point, fatten) + shapeCoeff * shape.strokeWidth(point)) / 2.0
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