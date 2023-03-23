/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.enums.EnumInfoFactory
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.GeomKind.DOT_PLOT
import jetbrains.datalore.plot.base.geom.util.GeomHelper
import jetbrains.datalore.plot.base.geom.util.GeomUtil
import jetbrains.datalore.plot.base.geom.util.HintColorUtil.createColorMarkerMapper
import jetbrains.datalore.plot.base.geom.util.LinesHelper
import jetbrains.datalore.plot.base.interact.GeomTargetCollector
import jetbrains.datalore.plot.base.interact.TipLayoutHint
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.plot.base.render.svg.LinePath
import jetbrains.datalore.plot.base.stat.DotplotStat.Method
import jetbrains.datalore.vis.svg.SvgPathDataBuilder
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.min

open class DotplotGeom : GeomBase(), WithWidth {
    var dotSize: Double = DEF_DOTSIZE
    var stackRatio: Double = DEF_STACKRATIO
    var stackGroups: Boolean = DEF_STACKGROUPS
    open var stackDir: Stackdir = DEF_STACKDIR
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
        val pointsWithBinWidth = GeomUtil.withDefined(
            aesthetics.dataPoints(),
            Aes.BINWIDTH, Aes.X, Aes.Y
        )
        if (!pointsWithBinWidth.any()) return

//        val binWidthPx = pointsWithBinWidth.first().binwidth()!! * ctx.getUnitResolution(Aes.X)
        val binWidthPx = pointsWithBinWidth.first().let {
            val x = it.x()!!
            val y = it.y()!!
            val bw = it.binwidth()!!
            val p0 = coord.toClient(DoubleVector(x, y))!!
            val p1 = coord.toClient(DoubleVector(x + bw, y))!!
            when (ctx.flipped) {
                false -> abs(p0.x - p1.x)
                true -> abs(p0.y - p1.y)
            }
        }

        GeomUtil.withDefined(pointsWithBinWidth, Aes.X, Aes.STACKSIZE)
            .groupBy(DataPointAesthetics::x)
            .forEach { (_, dataPointStack) ->
                buildStack(root, dataPointStack, pos, coord, ctx, binWidthPx)
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
        var builtStackSize = 0
        for (p in dataPoints) {
            val groupStackSize = boundedStackSize(
                builtStackSize + p.stacksize()!!.toInt(),
                coord,
                ctx,
                binWidthPx,
                ctx.flipped
            ) - builtStackSize
            var dotId = -1
            for (i in 0 until groupStackSize) {
                dotId = if (stackDotsAcrossGroups()) builtStackSize + i else i
                val path = dotHelper.createDot(
                    p,
                    getDotCenter(p, dotId, p.stacksize()!!.toInt(), binWidthPx, ctx.flipped, geomHelper),
                    dotSize * binWidthPx / 2
                )
                root.add(path.rootGroup)
            }
            buildHint(p, dotId, ctx, geomHelper, binWidthPx)
            builtStackSize += groupStackSize
        }
    }

    private fun buildHint(
        p: DataPointAesthetics,
        dotId: Int,
        ctx: GeomContext,
        geomHelper: GeomHelper,
        binWidthPx: Double
    ) {
        val dotRadius = dotSize * binWidthPx / 2.0
        val stackDirSign = if (stackDir == Stackdir.DOWN) -1 else 1
        val flipSign = if (ctx.flipped) 1 else -1
        val center = getDotCenter(p, dotId, p.stacksize()!!.toInt(), binWidthPx, ctx.flipped, geomHelper)
        val shiftToOrigin = DoubleVector(-dotRadius, stackDirSign * flipSign * dotRadius)
        val dimension = DoubleVector(2.0 * dotRadius, 0.0)
        val rect = if (ctx.flipped)
            DoubleRectangle(center.add(shiftToOrigin.flip()), dimension.flip())
        else
            DoubleRectangle(center.add(shiftToOrigin), dimension)
        val colorMarkerMapper = createColorMarkerMapper(DOT_PLOT, ctx)

        ctx.targetCollector.addRectangle(
            p.index(),
            rect,
            GeomTargetCollector.TooltipParams(
                markerColors = colorMarkerMapper(p)
            ),
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
        stackSize: Int,
        binWidthPx: Double,
        flip: Boolean,
        geomHelper: GeomHelper
    ): DoubleVector {
        val x = p.x()!!
        val shiftedDotId = when (stackDir) {
            Stackdir.UP -> dotId + 1.0 / (2.0 * stackRatio)
            Stackdir.DOWN -> -dotId - 1.0 / (2.0 * stackRatio)
            Stackdir.CENTER -> dotId + 0.5 - stackSize / 2.0
            Stackdir.CENTERWHOLE -> {
                val parityShift = if (stackSize % 2 == 0) 0.0 else 0.5
                dotId + parityShift - stackSize / 2.0 + 1.0 / (2.0 * stackRatio)
            }
        }
        val shift = DoubleVector(0.0, shiftedDotId * dotSize * stackRatio * binWidthPx)

        return geomHelper.toClient(x, 0.0, p)!!.add(if (flip) shift.flip() else shift.negate())
    }

    protected class DotHelper constructor(pos: PositionAdjustment, coord: CoordinateSystem, ctx: GeomContext) :
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
            builder.ellipticalArc(r, r, 0.0, largeArc = false, sweep = false, to = rightBound)
            builder.ellipticalArc(r, r, 0.0, largeArc = false, sweep = false, to = leftBound)
            builder.closePath()

            val path = LinePath(builder)
            decorate(path, p, filled = true, isLine = false)

            return path
        }
    }

    protected fun stackDotsAcrossGroups(): Boolean {
        return stackGroups && method == Method.HISTODOT
    }

    protected fun boundedStackSize(
        stackSize: Int,
        coord: CoordinateSystem,
        ctx: GeomContext,
        binWidthPx: Double,
        stacksAreVertical: Boolean
    ): Int {
        val bounds = ctx.getAesBounds()
        val boundsPx = coord.toClient(bounds)!!
        val stackCapacityPx = when (stacksAreVertical) {
            true -> boundsPx.width
            false -> boundsPx.height
        }.let {
            ceil(it / (dotSize * stackRatio * binWidthPx)).toInt() + 1
        }
        val parityCorrectionTerm = if (stackSize % 2 == stackCapacityPx % 2) 0 else 1

        return min(stackSize, stackCapacityPx + parityCorrectionTerm)
    }

    enum class Stackdir {
        UP, DOWN, CENTER, CENTERWHOLE;

        companion object {

            private val ENUM_INFO = EnumInfoFactory.createEnumInfo<Stackdir>()

            fun safeValueOf(v: String): Stackdir {
                return ENUM_INFO.safeValueOf(v) ?: throw IllegalArgumentException(
                    "Unsupported stackdir: '$v'\n" +
                            "Use one of: up, down, center, centerwhole."
                )
            }
        }
    }

    override fun widthSpan(p: DataPointAesthetics, coordAes: Aes<Double>, resolution: Double, isDiscrete: Boolean): DoubleSpan? {
        return PointDimensionsUtil.dimensionSpan(
            p,
            coordAes,
            sizeAes = Aes.BINWIDTH,
            resolution
        )
    }

    companion object {
        const val DEF_DOTSIZE = 1.0
        const val DEF_STACKRATIO = 1.0
        const val DEF_STACKGROUPS = false
        val DEF_STACKDIR = Stackdir.UP
        val DEF_METHOD = Method.DOTDENSITY

        const val HANDLES_GROUPS = false
    }
}