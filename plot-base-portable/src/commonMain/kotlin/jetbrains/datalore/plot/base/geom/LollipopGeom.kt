/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.aes.AesScaling
import jetbrains.datalore.plot.base.geom.util.GeomHelper
import jetbrains.datalore.plot.base.geom.util.GeomUtil
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.plot.base.render.point.NamedShape
import jetbrains.datalore.plot.common.data.SeriesUtil
import jetbrains.datalore.vis.svg.SvgLineElement
import kotlin.math.*

class LollipopGeom : PointGeom(), WithWidth, WithHeight {
    var slope: Double = DEF_SLOPE
    var intercept: Double = DEF_INTERCEPT

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        buildSticks(root, aesthetics, pos, coord, ctx)
        super.buildIntern(root, aesthetics, pos, coord, ctx)
    }

    private fun buildSticks(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val helper = GeomHelper(pos, coord, ctx)

        for (p in GeomUtil.withDefined(aesthetics.dataPoints(), Aes.X, Aes.Y)) {
            val x = p.x()!!
            val y = p.y()!!
            val head = DoubleVector(x, y)
            val base = getBase(x, y)
            val stick = createStick(base, head, p, helper) ?: continue
            root.add(stick)
        }
    }

    private fun createStick(
        originalBase: DoubleVector,
        originalHead: DoubleVector,
        p: DataPointAesthetics,
        helper: GeomHelper
    ): SvgLineElement? {
        val base = helper.toClient(originalBase, p) ?: return null // base of the lollipop stick
        val head = helper.toClient(originalHead, p) ?: return null // center of the lollipop candy
        val neck = shiftHeadToBase(base, head, neckLength(p)) // meeting point of candy and stick
        val line = SvgLineElement(base.x, base.y, neck.x, neck.y)
        GeomHelper.decorate(line, p, strokeScaler = AesScaling::pointStrokeWidth)

        return line
    }

    private fun neckLength(p: DataPointAesthetics): Double {
        val shape = p.shape()!!
        val shapeCoeff = when (shape) {
            NamedShape.STICK_PLUS,
            NamedShape.STICK_STAR,
            NamedShape.STICK_CROSS -> 0.0
            else -> 1.0
        }
        return (shape.size(p) + shapeCoeff * shape.strokeWidth(p)) / 2.0
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
        val base = getBase(x!!, y!!)

        return when (coordAes) {
            Aes.X -> DoubleSpan(base.x, x)
            Aes.Y -> DoubleSpan(base.y, y)
            else -> throw IllegalArgumentException("Aesthetic ${coordAes.name} is not consumed by spanning function")
        }
    }

    private fun getBase(x: Double, y: Double): DoubleVector {
        val baseX = (x + slope * (y - intercept)) / (1 + slope.pow(2))
        val baseY = slope * baseX + intercept

        return DoubleVector(baseX, baseY)
    }

    companion object {
        const val DEF_SLOPE = 0.0
        const val DEF_INTERCEPT = 0.0

        const val HANDLES_GROUPS = PointGeom.HANDLES_GROUPS
    }
}