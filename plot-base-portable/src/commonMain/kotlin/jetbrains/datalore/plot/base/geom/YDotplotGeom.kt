/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.enums.EnumInfoFactory
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.geom.util.GeomHelper
import jetbrains.datalore.plot.base.geom.util.GeomUtil
import jetbrains.datalore.plot.base.geom.util.HintColorUtil
import jetbrains.datalore.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.plot.base.interact.TipLayoutHint
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.plot.base.stat.DotplotStat
import jetbrains.datalore.plot.common.data.SeriesUtil
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

class YDotplotGeom : GeomBase() {
    var dotSize: Double = DotplotGeom.DEF_DOTSIZE
    var stackRatio: Double = DotplotGeom.DEF_STACKRATIO
    var stackDir: Stackdir = DEF_STACKDIR
    var method: DotplotStat.Method = DotplotGeom.DEF_METHOD
    var stackCapacity: Int = 0

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = FilledCircleLegendKeyElementFactory()

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val pointsWithBinWidth = GeomUtil.withDefined(aesthetics.dataPoints(), Aes.BINWIDTH)
        if (!pointsWithBinWidth.any()) return

        val binWidthPx = max(pointsWithBinWidth.first().binwidth()!! * ctx.getUnitResolution(Aes.Y), 2.0)
        stackCapacity = (
            if (ctx.flipped) ctx.getAesBounds().width else ctx.getAesBounds().height
        ).let {
            ceil(it / (dotSize * binWidthPx)).toInt() + 1
        }
        GeomUtil.withDefined(pointsWithBinWidth, Aes.X, Aes.Y, Aes.STACKSIZE)
            .groupBy(DataPointAesthetics::x)
            .forEach { (_, dataPointGroup) ->
                buildStack(root, dataPointGroup, pos, coord, ctx, binWidthPx)
            }
    }

    private fun buildStack(
        root: SvgRoot,
        dataPoints: Iterable<DataPointAesthetics>,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext,
        binWidthPx: Double
    ) {
        val dotHelper = DotplotGeom.DotHelper(pos, coord, ctx)
        val geomHelper = GeomHelper(pos, coord, ctx)
        for (p in dataPoints) {
            for (dotId in 0 until boundedStackSize(p)) {
                val center = getDotCenter(p, dotId, binWidthPx, ctx.flipped, geomHelper)
                val path = dotHelper.createDot(p, center, dotSize * binWidthPx / 2)
                root.add(path.rootGroup)
            }
            buildHint(p, ctx, geomHelper, binWidthPx)
        }
    }

    private fun buildHint(
        p: DataPointAesthetics,
        ctx: GeomContext,
        geomHelper: GeomHelper,
        binWidthPx: Double
    ) {
        if (!SeriesUtil.isFinite(p.x()) || !SeriesUtil.isFinite(p.stacksize()))
            return

        val dotRadius = dotSize * binWidthPx / 2
        val stackSize = boundedStackSize(p)
        val origin = if (stackSize > 0) {
            getDotCenter(p, stackSize - 1, binWidthPx, ctx.flipped, geomHelper)
        } else {
            geomHelper.toClient(DoubleVector(p.x()!!, p.y()!!), p)
        }

        ctx.targetCollector.addPoint(
            p.index(),
            origin,
            dotRadius,
            GeomTargetCollector.TooltipParams.params()
                .setMainColor(HintColorUtil.fromFill(p))
                .setColors(HintColorUtil.fromMappedColors(ctx)(p)),
            TipLayoutHint.Kind.VERTICAL_TOOLTIP
        )
    }

    private fun getDotCenter(
        p: DataPointAesthetics,
        dotId: Int,
        binWidthPx: Double,
        flip: Boolean,
        geomHelper: GeomHelper
    ) : DoubleVector {
        val x = p.x()!!
        val y = p.y()!!
        val stackSize = boundedStackSize(p)
        val shiftedDotId = when (stackDir) {
            Stackdir.LEFT -> -dotId - 1.0 / (2.0 * stackRatio)
            Stackdir.RIGHT -> dotId + 1.0 / (2.0 * stackRatio)
            Stackdir.CENTER -> dotId + 0.5 - stackSize / 2.0
            Stackdir.CENTERWHOLE -> {
                val parityShift = if (stackSize % 2 == 0) 0.0 else 0.5
                dotId + parityShift - stackSize / 2.0 + 1.0 / (2.0 * stackRatio)
            }
        }
        val shift = DoubleVector(shiftedDotId * dotSize * stackRatio * binWidthPx, 0.0)

        return geomHelper.toClient(DoubleVector(x, y), p).add(if (flip) shift.flip() else shift)
    }

    private fun boundedStackSize(p: DataPointAesthetics): Int {
        val stackSize = p.stacksize()!!.toInt()
        val parityCorrectionTerm = if (stackSize % 2 == stackCapacity % 2) 0 else 1

        return min(stackSize, stackCapacity + parityCorrectionTerm)
    }

    enum class Stackdir {
        LEFT, RIGHT, CENTER, CENTERWHOLE;

        companion object {

            private val ENUM_INFO = EnumInfoFactory.createEnumInfo<Stackdir>()

            fun safeValueOf(v: String): Stackdir {
                return ENUM_INFO.safeValueOf(v) ?:
                throw IllegalArgumentException(
                    "Unsupported stackdir: '$v'\n" +
                    "Use one of: left, right, center, centerwhole."
                )
            }
        }
    }

    companion object {
        val DEF_STACKDIR = Stackdir.CENTER

        const val HANDLES_GROUPS = true
    }
}