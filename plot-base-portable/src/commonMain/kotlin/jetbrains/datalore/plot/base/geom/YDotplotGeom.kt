/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.enums.EnumInfoFactory
import jetbrains.datalore.base.geometry.DoubleRectangle
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
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

class YDotplotGeom : GeomBase() {
    var dotSize: Double = DotplotGeom.DEF_DOTSIZE
    var stackRatio: Double = DotplotGeom.DEF_STACKRATIO
    var stackGroups: Boolean = DotplotGeom.DEF_STACKGROUPS
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
            if (ctx.flipped) ctx.getAesBounds().height else ctx.getAesBounds().width
        ).let { boundPx ->
            ceil(boundPx / (dotSize * stackRatio * binWidthPx)).toInt() + 1
        }
        GeomUtil.withDefined(pointsWithBinWidth, Aes.X, Aes.Y, Aes.STACKSIZE)
            .groupBy(DataPointAesthetics::x)
            .forEach { (_, dataPointGroup) ->
                dataPointGroup
                    .groupBy(DataPointAesthetics::y)
                    .forEach { (_, dataPointStack) ->
                        buildStack(root, dataPointStack, pos, coord, ctx, binWidthPx)
                    }
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
        val stackSize = boundedStackSize(dataPoints.map { it.stacksize()!! }.sum().toInt())
        var builtStackSize = 0
        for (p in dataPoints) {
            val groupStackSize = boundedStackSize(builtStackSize + p.stacksize()!!.toInt()) - builtStackSize
            val currentStackSize = if (stackGroups()) stackSize else groupStackSize
            var dotId = -1
            for (i in 0 until groupStackSize) {
                dotId = if (stackGroups()) builtStackSize + i else i
                val center = getDotCenter(p, dotId, currentStackSize, binWidthPx, ctx.flipped, geomHelper)
                val path = dotHelper.createDot(p, center, dotSize * binWidthPx / 2)
                root.add(path.rootGroup)
            }
            buildHint(p, dotId, currentStackSize, ctx, geomHelper, binWidthPx)
            builtStackSize += groupStackSize
        }
    }

    private fun buildHint(
        p: DataPointAesthetics,
        dotId: Int,
        stackSize: Int,
        ctx: GeomContext,
        geomHelper: GeomHelper,
        binWidthPx: Double
    ) {
        val dotRadius = dotSize * binWidthPx / 2.0
        val currentStackSize = p.stacksize()!!.toInt()
        val rect: DoubleRectangle = (if (currentStackSize > 0) {
            getDotCenter(p, dotId, stackSize, binWidthPx, ctx.flipped, geomHelper)
        } else null)?.let { origin ->
            val width = 2.0 * dotRadius
            val height = 2.0 * dotRadius * (currentStackSize * stackRatio - (stackRatio - 1))
            val stackShift = if (stackDir == Stackdir.LEFT) -dotRadius else -height + dotRadius
            if (ctx.flipped) {
                DoubleRectangle(
                    DoubleVector(origin.x - dotRadius, origin.y + stackShift),
                    DoubleVector(width, height)
                )
            } else {
                DoubleRectangle(
                    DoubleVector(origin.x + stackShift, origin.y - dotRadius),
                    DoubleVector(height, width)
                )
            }
        } ?: return

        ctx.targetCollector.addRectangle(
            p.index(),
            rect,
            GeomTargetCollector.TooltipParams.params()
                .setMainColor(HintColorUtil.fromFill(p))
                .setColors(HintColorUtil.fromMappedColors(ctx)(p)),
            TipLayoutHint.Kind.CURSOR_TOOLTIP
        )
    }

    private fun getDotCenter(
        p: DataPointAesthetics,
        dotId: Int,
        stackSize: Int,
        binWidthPx: Double,
        flip: Boolean,
        geomHelper: GeomHelper
    ) : DoubleVector {
        val x = p.x()!!
        val y = p.y()!!
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

    private fun stackGroups(): Boolean {
        return stackGroups && method == DotplotStat.Method.HISTODOT
    }

    private fun boundedStackSize(stackSize: Int): Int {
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