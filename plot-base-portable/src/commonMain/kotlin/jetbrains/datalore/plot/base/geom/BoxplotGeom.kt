/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.aes.AestheticsDefaults
import jetbrains.datalore.plot.base.geom.util.*
import jetbrains.datalore.plot.base.geom.util.HintColorUtil.colorWithAlpha
import jetbrains.datalore.plot.base.interact.NullGeomTargetCollector
import jetbrains.datalore.plot.base.interact.TipLayoutHint
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.plot.base.render.point.PointShape
import jetbrains.datalore.vis.svg.SvgLineElement

class BoxplotGeom : GeomBase() {

    var fattenMidline: Double = 1.0

    var outlierColor: Color? = null
    var outlierFill: Color? = null
    var outlierShape: PointShape? = null
    var outlierSize: Double? = null

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = LEGEND_FACTORY

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        CrossBarHelper.buildBoxes(
            root, aesthetics, pos, coord, ctx,
            rectangleByDataPoint(ctx)
        )
        buildLines(root, aesthetics, pos, coord, ctx)
        buildOutliers(root, aesthetics, pos, coord, ctx)
        BarTooltipHelper.collectRectangleTargets(
            listOf(Aes.YMAX, Aes.UPPER, Aes.MIDDLE, Aes.LOWER, Aes.YMIN),
            aesthetics, pos, coord, ctx,
            rectangleByDataPoint(ctx),
            { colorWithAlpha(it) },
            defaultTooltipKind = if (ctx.flipped) TipLayoutHint.Kind.CURSOR_TOOLTIP else null
        )
    }

    private fun buildLines(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        CrossBarHelper.buildMidlines(root, aesthetics, pos, coord, ctx, fattenMidline)

        val helper = GeomHelper(pos, coord, ctx)
        val elementHelper = helper.createSvgElementHelper()
        for (p in GeomUtil.withDefined(aesthetics.dataPoints(), Aes.X)) {
            val x = p.x()!!
            val lines = ArrayList<SvgLineElement>()

            // lower whisker
            if (p.defined(Aes.LOWER) && p.defined(Aes.YMIN)) {
                val hinge = p.lower()!!
                val fence = p.ymin()!!
                // whisker line
                lines.add(
                    elementHelper.createLine(
                        DoubleVector(x, hinge),
                        DoubleVector(x, fence),
                        p
                    )
                )
                // fence line
                /*
        lines.add(elementHelper.createLine(
            new DoubleVector(x - halfFenceWidth, fence),
            new DoubleVector(x + halfFenceWidth, fence),
            p));
        */
            }

            // upper whisker
            if (p.defined(Aes.UPPER) && p.defined(Aes.YMAX)) {
                val hinge = p.upper()!!
                val fence = p.ymax()!!
                // whisker line
                lines.add(
                    elementHelper.createLine(
                        DoubleVector(x, hinge),
                        DoubleVector(x, fence),
                        p
                    )
                )
                // fence line
                /*
        lines.add(elementHelper.createLine(
            new DoubleVector(x - halfFenceWidth, fence),
            new DoubleVector(x + halfFenceWidth, fence),
            p));
        */

                lines.forEach { root.add(it) }
            }
        }
    }

    private fun buildOutliers(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val outlierAesthetics = getOutliersAesthetics(aesthetics)
        PointGeom()
            .buildIntern(root, outlierAesthetics, pos, coord, ctx.withTargetCollector(NullGeomTargetCollector()))
    }

    private fun getOutliersAesthetics(aesthetics: Aesthetics): Aesthetics {
        return MappedAesthetics(aesthetics) { p ->
            toOutlierDataPointAesthetics(p)
        }
    }

    /**
     * The geom `Aesthetics` contains both: reqular data-points and "outlier" data-points.
     * Regular data-point do not yave Y defined. We use this feature to feature to
     * detect regular data-points and ignore them.
     */
    private fun toOutlierDataPointAesthetics(p: DataPointAesthetics): DataPointAesthetics {
        if (!p.defined(Aes.Y)) {
            // not an "outlier" data-point
            return p
        }

        return object : DataPointAestheticsDelegate(p) {
            override operator fun <T> get(aes: Aes<T>): T? = getIntern(aes)

            override fun color(): Color? = getIntern(Aes.COLOR)
            override fun fill(): Color? = getIntern(Aes.FILL)
            override fun shape(): PointShape? = getIntern(Aes.SHAPE)
            override fun size(): Double? = getIntern(Aes.SIZE)

            private fun <T> getIntern(aes: Aes<T>): T? {
                val value: Any? = when (aes) {
                    Aes.COLOR -> outlierColor ?: super.color()
                    Aes.FILL -> outlierFill ?: super.fill()
                    Aes.SHAPE -> outlierShape ?: super.shape()
                    Aes.SIZE -> outlierSize ?: OUTLIER_DEF_SIZE  // 'size' of 'super' is line thickness on box-plot
                    else -> super.get(aes)
                }
                @Suppress("UNCHECKED_CAST")
                return value as T?
            }
        }
    }


    companion object {
        const val HANDLES_GROUPS = false

        private val LEGEND_FACTORY = CrossBarHelper.legendFactory(true)
        private val OUTLIER_DEF_SIZE = AestheticsDefaults.point().defaultValue(Aes.SIZE)

        private fun rectangleByDataPoint(ctx: GeomContext): (DataPointAesthetics) -> DoubleRectangle? {
            return { p ->
                if (p.defined(Aes.X) &&
                    p.defined(Aes.LOWER) &&
                    p.defined(Aes.UPPER) &&
                    p.defined(Aes.WIDTH)
                ) {
                    val x = p.x()!!
                    val lower = p.lower()!!
                    val upper = p.upper()!!
                    val width = GeomUtil.widthPx(p, ctx, 2.0)

                    val origin = DoubleVector(x - width / 2, lower)
                    val dimensions = DoubleVector(width, upper - lower)
                    DoubleRectangle(origin, dimensions)
                } else {
                    null
                }
            }
        }
    }
}
