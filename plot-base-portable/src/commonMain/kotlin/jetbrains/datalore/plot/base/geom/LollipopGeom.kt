/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.enums.EnumInfoFactory
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
    var orientation: Orientation = DEF_ORIENTATION

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

        for (p in GeomUtil.withDefined(aesthetics.dataPoints(), Aes.X, Aes.Y)) {
            val x = p.x()!!
            val y = p.y()!!
            val head = DoubleVector(x, y)
            val base = getBase(x, y, true)
            val stick = createStick(base, head, p, helper)
            if (stick != null) {
                root.add(stick)
            }
            root.add(createCandy(head, p, helper))
            buildHint(head, p, helper, targetCollector, colorsByDataPoint)
        }
    }

    private fun createCandy(
        place: DoubleVector,
        p: DataPointAesthetics,
        helper: GeomHelper
    ): SvgGElement {
        val location = helper.toClient(place, p)!!
        val shape = p.shape()!!
        val o = PointShapeSvg.create(shape, location, p, fatten)
        return wrap(o)
    }

    private fun createStick(
        originalBase: DoubleVector,
        originalHead: DoubleVector,
        p: DataPointAesthetics,
        helper: GeomHelper
    ): SvgLineElement? {
        val base = helper.toClient(originalBase, p) ?: return null // base of the lollipop stick
        val head = helper.toClient(originalHead, p) ?: return null // center of the lollipop candy
        val stickLength = sqrt((head.x - base.x).pow(2) + (head.y - base.y).pow(2))
        val candyRadius = candyRadius(p)
        if (candyRadius > stickLength) {
            return null
        }
        val neck = shiftHeadToBase(base, head, candyRadius) // meeting point of candy and stick
        val line = SvgLineElement(base.x, base.y, neck.x, neck.y)
        GeomHelper.decorate(line, p, applyAlphaToAll = true, strokeScaler = AesScaling::lineWidth)

        return line
    }

    private fun buildHint(
        place: DoubleVector,
        p: DataPointAesthetics,
        helper: GeomHelper,
        targetCollector: GeomTargetCollector,
        colorsByDataPoint: (DataPointAesthetics) -> List<Color>
    ) {
        targetCollector.addPoint(
            p.index(),
            helper.toClient(place, p)!!,
            candyRadius(p),
            GeomTargetCollector.TooltipParams(
                markerColors = colorsByDataPoint(p)
            )
        )
    }

    private fun candyRadius(p: DataPointAesthetics): Double {
        val shape = p.shape()!!
        val shapeCoeff = when (shape) {
            NamedShape.STICK_PLUS,
            NamedShape.STICK_STAR,
            NamedShape.STICK_CROSS -> 0.0
            else -> 1.0
        }
        return (shape.size(p, fatten) + shapeCoeff * shape.strokeWidth(p)) / 2.0
    }

    private fun shiftHeadToBase(
        base: DoubleVector,
        head: DoubleVector,
        shiftLength: Double
    ): DoubleVector {
        val x0 = base.x
        val x1 = head.x
        val y0 = base.y
        val y1 = head.y

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

    override fun widthSpan(
        p: DataPointAesthetics,
        coordAes: Aes<Double>,
        resolution: Double,
        isDiscrete: Boolean
    ): DoubleSpan? {
        return span(p, coordAes)
    }

    override fun heightSpan(
        p: DataPointAesthetics,
        coordAes: Aes<Double>,
        resolution: Double,
        isDiscrete: Boolean
    ): DoubleSpan? {
        return span(p, coordAes)
    }

    private fun span(
        p: DataPointAesthetics,
        coordAes: Aes<Double>
    ): DoubleSpan? {
        val x = p.x()
        val y = p.y()
        if (!SeriesUtil.allFinite(x, y)) {
            return null
        }
        val base = getBase(x!!, y!!, false)

        return when (coordAes) {
            Aes.X -> DoubleSpan(base.x, x)
            Aes.Y -> DoubleSpan(base.y, y)
            else -> throw IllegalArgumentException("Aesthetic ${coordAes.name} is not consumed by spanning function")
        }
    }

    private fun getBase(x: Double, y: Double, orientationHasBeenApplied: Boolean): DoubleVector {
        return when {
            orientation == Orientation.UNSPECIFIED -> {
                val baseX = (x + slope * (y - intercept)) / (1 + slope.pow(2))
                val baseY = slope * baseX + intercept
                DoubleVector(baseX, baseY)
            }
            orientation == Orientation.X -> DoubleVector(x, slope * x + intercept)
            orientation == Orientation.Y && orientationHasBeenApplied -> DoubleVector(x, slope * x + intercept)
            else -> DoubleVector(slope * y + intercept, y)
        }
    }

    enum class Orientation {
        UNSPECIFIED, X, Y;

        companion object {
            private val ENUM_INFO = EnumInfoFactory.createEnumInfo<Orientation>()

            fun safeValueOf(v: String): Orientation {
                return ENUM_INFO.safeValueOf(v) ?: throw IllegalArgumentException("orientation expected x|y but was $v")
            }
        }
    }

    companion object {
        const val DEF_FATTEN = 2.5
        const val DEF_SLOPE = 0.0
        const val DEF_INTERCEPT = 0.0
        val DEF_ORIENTATION = Orientation.UNSPECIFIED

        const val HANDLES_GROUPS = PointGeom.HANDLES_GROUPS
    }
}