/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.geom.util.*
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.plot.base.render.svg.LinePath
import jetbrains.datalore.plot.common.data.SeriesUtil.isFinite
import jetbrains.datalore.vis.svg.SvgPathDataBuilder
import kotlin.math.max

class DotplotGeom : GeomBase() {
    var stackratio: Double = DEF_STACKRATIO
    private var stackdir: Stackdir = DEF_STACKDIR

    fun setStackdir(v: String?) {
        stackdir = when (v?.lowercase()) {
            "up" -> Stackdir.UP
            "down" -> Stackdir.DOWN
            "center" -> Stackdir.CENTER
            "centerwhole" -> Stackdir.CENTERWHOLE
            else -> throw IllegalArgumentException(
                "Unsupported stackdir: '$v'\n" +
                "Use one of: up, down, center, centerwhole."
            )
        }
    }

    override fun preferableNullDomain(aes: Aes<*>): ClosedRange<Double> {
        return if (aes == Aes.Y)
            when (stackdir) {
                Stackdir.UP -> ClosedRange(0.0, 1.0)
                Stackdir.DOWN -> ClosedRange(-1.0, 0.0)
                Stackdir.CENTER,
                Stackdir.CENTERWHOLE -> ClosedRange(-0.5, 0.5)
            }
        else
            super.preferableNullDomain(aes)
    }

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val pointsWithBinWidth = GeomUtil.withDefined(aesthetics.dataPoints(), Aes.BINWIDTH)
        if (!pointsWithBinWidth.any()) return

        val dotHelper = DotHelper(pos, coord, ctx)
        val geomHelper = GeomHelper(pos, coord, ctx)
        val binWidthPx = max(pointsWithBinWidth.first().binwidth()!! * ctx.getUnitResolution(Aes.X), 2.0)
        for (p in GeomUtil.withDefined(pointsWithBinWidth, Aes.X, Aes.STACKSIZE)) {
            for (i in 0 until p.stacksize()!!.toInt()) {
                val center = getDotCenter(p, i, binWidthPx)
                val path = dotHelper.createDot(
                    p,
                    geomHelper.toClient(center, p),
                    binWidthPx / 2
                )
                root.add(path.rootGroup)
            }
        }
        buildHints(aesthetics, pos, coord, ctx, binWidthPx)
    }

    private fun buildHints(
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext,
        binWidthPx: Double,
    ) {
        val rectFactory = fun (p: DataPointAesthetics) : DoubleRectangle? {
            if (!isFinite(p.x()) || !isFinite(p.stacksize()))
                return null

            val origin = getDotCenter(p, p.stacksize()!!.toInt() - 1, binWidthPx)
                .add(DoubleVector(-binWidthPx / 2, binWidthPx / 2))
            val dimension = DoubleVector(binWidthPx, 0.0)

            return DoubleRectangle(origin, dimension)
        }

        BarTooltipHelper.collectRectangleTargets(
            emptyList(),
            aesthetics, pos, coord, ctx,
            rectFactory,
            { HintColorUtil.fromFill(it) }
        )
    }

    private fun getDotCenter(
        p: DataPointAesthetics,
        dotId: Int,
        binWidthPx: Double
    ) : DoubleVector {
        val x = p.x()!!
        val shiftedDotId = when (stackdir) {
            Stackdir.UP -> dotId + 1 / (2 * stackratio)
            Stackdir.DOWN -> -dotId - 1 / (2 * stackratio)
            Stackdir.CENTER -> dotId + 0.5 - p.stacksize()!! / 2
            Stackdir.CENTERWHOLE -> {
                val parityShift = if (p.stacksize()!!.toInt() % 2 == 0) 0.0 else 0.5
                dotId + parityShift - p.stacksize()!! / 2 + 1 / (2 * stackratio)
            }
        }

        return DoubleVector(x, shiftedDotId * stackratio * binWidthPx)
    }

    private class DotHelper constructor(pos: PositionAdjustment, coord: CoordinateSystem, ctx: GeomContext) :
        LinesHelper(pos, coord, ctx) {
            fun createDot(
                p: DataPointAesthetics,
                center: DoubleVector,
                r: Double
            ): LinePath {
                val leftBound = center.add(DoubleVector(-r, 0.0))
                val rightBound = center.add(DoubleVector(r, 0.0))

                val builder = SvgPathDataBuilder(true)
                builder.moveTo(leftBound)
                builder.ellipticalArc(r, r, 0.0, false, false, rightBound)
                builder.ellipticalArc(r, r, 0.0, false, false, leftBound)
                builder.closePath()

                val path = LinePath(builder)
                decorate(path, p, true)

                return path
            }
    }

    enum class Stackdir {
        UP, DOWN, CENTER, CENTERWHOLE
    }

    companion object {
        val DEF_STACKDIR = Stackdir.UP
        val DEF_STACKRATIO = 1.0

        const val HANDLES_GROUPS = false
    }
}