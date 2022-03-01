/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.enums.EnumInfoFactory
import jetbrains.datalore.base.gcommon.collect.DoubleSpan
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.geom.util.GeomHelper
import jetbrains.datalore.plot.base.geom.util.GeomUtil
import jetbrains.datalore.plot.base.geom.util.HintColorUtil
import jetbrains.datalore.plot.base.geom.util.LinesHelper
import jetbrains.datalore.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.plot.base.interact.TipLayoutHint
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.plot.base.render.svg.LinePath
import jetbrains.datalore.plot.base.stat.DotplotStat.Method
import jetbrains.datalore.plot.common.data.SeriesUtil.isFinite
import jetbrains.datalore.vis.svg.SvgPathDataBuilder
import kotlin.math.ceil
import kotlin.math.max

class DotplotGeom : GeomBase() {
    var dotSize: Double = DEF_DOTSIZE
    var stackRatio: Double = DEF_STACKRATIO
    var stackGroups: Boolean = DEF_STACKGROUPS
    var stackDir: Stackdir = DEF_STACKDIR
    var method: Method = DEF_METHOD

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = FilledCircleLegendKeyElementFactory()

    override fun preferableNullDomain(aes: Aes<*>): DoubleSpan {
        return if (aes == Aes.Y)
            when (stackDir) {
                Stackdir.UP -> DoubleSpan(0.0, 1.0)
                Stackdir.DOWN -> DoubleSpan(-1.0, 0.0)
                Stackdir.CENTER,
                Stackdir.CENTERWHOLE -> DoubleSpan(-0.5, 0.5)
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

        val binWidthPx = max(pointsWithBinWidth.first().binwidth()!! * ctx.getUnitResolution(Aes.X), 2.0)
        val stackCapacity = (
            if (ctx.flipped) ctx.getAesBounds().width else ctx.getAesBounds().height
        ).let {
            ceil(it / (dotSize * binWidthPx)).toInt()
        }
        GeomUtil.withDefined(pointsWithBinWidth, Aes.X, Aes.STACKSIZE)
            .groupBy { p -> p.x()!! }
            .forEach { (_, dataPointStack) ->
                buildStack(root, dataPointStack, pos, coord, ctx, binWidthPx, stackCapacity)
            }
    }

    private fun buildStack(
        root: SvgRoot,
        dataPoints: Iterable<DataPointAesthetics>,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext,
        binWidthPx: Double,
        stackCapacity: Int
    ) {
        val dotHelper = DotHelper(pos, coord, ctx)
        val geomHelper = GeomHelper(pos, coord, ctx)
        var stackSize = 0
        for (p in dataPoints) {
            val groupStackSize = p.stacksize()!!.toInt()
            var dotId = -1
            for (i in 0 until groupStackSize) {
                dotId = if (stackGroups && method == Method.HISTODOT) stackSize + i else i
                // Break only current loop because of hints for empty groups
                if (dotId > stackCapacity) break
                val center = getDotCenter(p, dotId, binWidthPx)
                val path = dotHelper.createDot(
                    p,
                    geomHelper.toClient(center, p),
                    dotSize * binWidthPx / 2
                )
                root.add(path.rootGroup)
            }
            buildHint(p, dotId, ctx, geomHelper, binWidthPx)
            stackSize += groupStackSize
        }
    }

    private fun buildHint(
        p: DataPointAesthetics,
        dotId: Int,
        ctx: GeomContext,
        geomHelper: GeomHelper,
        binWidthPx: Double
    ) {
        if (!isFinite(p.x()) || !isFinite(p.stacksize()))
            return

        val dotRadius = dotSize * binWidthPx / 2
        val yShiftSign = if (stackDir == Stackdir.DOWN) -1 else 1
        val origin = getDotCenter(p, dotId, binWidthPx)
            .add(DoubleVector(-dotRadius, yShiftSign * dotRadius))
        val dimension = DoubleVector(2 * dotRadius, 0.0)
        val rect = DoubleRectangle(origin, dimension)

        ctx.targetCollector.addRectangle(
            p.index(),
            geomHelper.toClient(rect, p),
            GeomTargetCollector.TooltipParams.params()
                .setMainColor(HintColorUtil.fromFill(p))
                .setColors(HintColorUtil.fromMappedColors(ctx)(p)),
            tooltipKind = if (ctx.flipped) {
                TipLayoutHint.Kind.VERTICAL_TOOLTIP
            } else {
                TipLayoutHint.Kind.HORIZONTAL_TOOLTIP
            }
        )
    }

    private fun getDotCenter(
        p: DataPointAesthetics,
        dotId: Int,
        binWidthPx: Double
    ) : DoubleVector {
        val x = p.x()!!
        val shiftedDotId = when (stackDir) {
            Stackdir.UP -> dotId + 1 / (2 * stackRatio)
            Stackdir.DOWN -> -dotId - 1 / (2 * stackRatio)
            Stackdir.CENTER -> dotId + 0.5 - p.stacksize()!! / 2
            Stackdir.CENTERWHOLE -> {
                val parityShift = if (p.stacksize()!!.toInt() % 2 == 0) 0.0 else 0.5
                dotId + parityShift - p.stacksize()!! / 2 + 1 / (2 * stackRatio)
            }
        }

        return DoubleVector(x, shiftedDotId * dotSize * stackRatio * binWidthPx)
    }

    class DotHelper constructor(pos: PositionAdjustment, coord: CoordinateSystem, ctx: GeomContext) :
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
        UP, DOWN, CENTER, CENTERWHOLE;

        companion object {

            private val ENUM_INFO = EnumInfoFactory.createEnumInfo<Stackdir>()

            fun safeValueOf(v: String): Stackdir {
                return ENUM_INFO.safeValueOf(v) ?:
                throw IllegalArgumentException(
                    "Unsupported stackdir: '$v'\n" +
                    "Use one of: up, down, center, centerwhole."
                )
            }
        }
    }

    companion object {
        val DEF_DOTSIZE = 1.0
        val DEF_STACKRATIO = 1.0
        val DEF_STACKGROUPS = false
        val DEF_STACKDIR = Stackdir.UP
        val DEF_METHOD = Method.DOTDENSITY

        const val HANDLES_GROUPS = false
    }
}