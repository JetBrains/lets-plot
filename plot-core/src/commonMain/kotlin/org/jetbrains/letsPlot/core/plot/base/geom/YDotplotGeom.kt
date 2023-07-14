/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.geom

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomHelper
import org.jetbrains.letsPlot.core.plot.base.geom.util.GeomUtil
import org.jetbrains.letsPlot.core.plot.base.geom.util.HintColorUtil
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetCollector
import org.jetbrains.letsPlot.core.plot.base.tooltip.TipLayoutHint
import org.jetbrains.letsPlot.core.plot.base.render.LegendKeyElementFactory
import org.jetbrains.letsPlot.core.plot.base.render.SvgRoot
import kotlin.math.abs

class YDotplotGeom : DotplotGeom(), WithHeight {
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
        val pointsWithBinWidth = GeomUtil.withDefined(
            aesthetics.dataPoints(),
            Aes.BINWIDTH, Aes.X, Aes.Y
        )
        if (!pointsWithBinWidth.any()) return

//        val binWidthPx = pointsWithBinWidth.first().binwidth()!! * ctx.getUnitResolution(Aes.Y)
        val binWidthPx = pointsWithBinWidth.first().let {
            val x = it.x()!!
            val y = it.y()!!
            val bw = it.binwidth()!!
            val p0 = coord.toClient(DoubleVector(x, y))!!
            val p1 = coord.toClient(DoubleVector(x, y + bw))!!
            when (ctx.flipped) {
                false -> abs(p0.y - p1.y)
                true -> abs(p0.x - p1.x)
            }
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
        val dotHelper = DotHelper(pos, coord, ctx)
        val geomHelper = GeomHelper(pos, coord, ctx)
        val fullStackSize = dataPoints.sumOf { it.stacksize()!! }.toInt()
        val stackSize = boundedStackSize(fullStackSize, coord, ctx, binWidthPx, !ctx.flipped)
        var builtStackSize = 0
        for (p in dataPoints) {
            val groupStackSize = boundedStackSize(
                builtStackSize + p.stacksize()!!.toInt(),
                coord,
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
            GeomTargetCollector.TooltipParams(
                markerColors = colorMarkerMapper(p)
            ),
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
    ): DoubleVector {
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

        return geomHelper.toClient(x, y, p)!!.add(if (flip) shift.flip() else shift)
    }

    enum class YStackdir {
        LEFT, RIGHT, CENTER, CENTERWHOLE
    }

    override fun heightSpan(p: DataPointAesthetics, coordAes: Aes<Double>, resolution: Double, isDiscrete: Boolean): DoubleSpan? {
        return PointDimensionsUtil.dimensionSpan(
            p,
            coordAes,
            sizeAes = Aes.BINWIDTH,
            resolution
        )
    }

    companion object {
        val DEF_YSTACKDIR = YStackdir.CENTER

        const val HANDLES_GROUPS = true
    }
}