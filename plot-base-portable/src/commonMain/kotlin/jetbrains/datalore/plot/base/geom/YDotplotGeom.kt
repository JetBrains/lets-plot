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
import jetbrains.datalore.plot.base.interact.GeomTargetCollector.TooltipParams.Companion.tooltip
import jetbrains.datalore.plot.base.interact.TipLayoutHint
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.render.SvgRoot
import kotlin.math.max

class YDotplotGeom : DotplotGeom() {
    var yStackDir: YStackdir = DEF_YSTACKDIR

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
        val dotHelper = DotHelper(pos, coord, ctx)
        val geomHelper = GeomHelper(pos, coord, ctx)
        val fullStackSize = dataPoints.map { it.stacksize()!! }.sum().toInt()
        val stackSize = boundedStackSize(fullStackSize, ctx, binWidthPx, !ctx.flipped)
        var builtStackSize = 0
        for (p in dataPoints) {
            val groupStackSize = boundedStackSize(
                builtStackSize + p.stacksize()!!.toInt(),
                ctx,
                binWidthPx,
                !ctx.flipped
            ) - builtStackSize
            val currentStackSize = if (stackDotsAcrossGroups()) stackSize else groupStackSize
            var dotId = -1
            for (i in 0 until groupStackSize) {
                dotId = if (stackDotsAcrossGroups()) builtStackSize + i else i
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
        val currentStackSize = p.stacksize()!!.toInt()
        if (currentStackSize == 0) return

        val center = getDotCenter(p, dotId, stackSize, binWidthPx, ctx.flipped, geomHelper)
        val radius = dotSize * binWidthPx / 2.0
        val width = 2.0 * radius
        val height = 2.0 * radius * (currentStackSize * stackRatio - (stackRatio - 1))
        val stackShift = if (yStackDir == YStackdir.LEFT) -radius else -height + radius
        val rect = if (ctx.flipped) {
            DoubleRectangle(
                DoubleVector(center.x - radius, center.y + stackShift),
                DoubleVector(width, height)
            )
        } else {
            DoubleRectangle(
                DoubleVector(center.x + stackShift, center.y - radius),
                DoubleVector(height, width)
            )
        }
        val colorMarkerMapper = HintColorUtil.createColorMarkerMapper(GeomKind.Y_DOT_PLOT, ctx)

        ctx.targetCollector.addRectangle(
            p.index(),
            rect,
            tooltip {
                markerColors = colorMarkerMapper(p)
            },
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
        val shiftedDotId = when (yStackDir) {
            YStackdir.LEFT -> -dotId - 1.0 / (2.0 * stackRatio)
            YStackdir.RIGHT -> dotId + 1.0 / (2.0 * stackRatio)
            YStackdir.CENTER -> dotId + 0.5 - stackSize / 2.0
            YStackdir.CENTERWHOLE -> {
                val parityShift = if (stackSize % 2 == 0) 0.0 else 0.5
                dotId + parityShift - stackSize / 2.0 + 1.0 / (2.0 * stackRatio)
            }
        }
        val shift = DoubleVector(shiftedDotId * dotSize * stackRatio * binWidthPx, 0.0)

        return geomHelper.toClient(DoubleVector(x, y), p).add(if (flip) shift.flip() else shift)
    }

    enum class YStackdir {
        LEFT, RIGHT, CENTER, CENTERWHOLE;

        companion object {

            private val ENUM_INFO = EnumInfoFactory.createEnumInfo<YStackdir>()

            fun safeValueOf(v: String): YStackdir {
                return ENUM_INFO.safeValueOf(v) ?:
                throw IllegalArgumentException(
                    "Unsupported stackdir: '$v'\n" +
                    "Use one of: left, right, center, centerwhole."
                )
            }
        }
    }

    companion object {
        val DEF_YSTACKDIR = YStackdir.CENTER

        const val HANDLES_GROUPS = true
    }
}