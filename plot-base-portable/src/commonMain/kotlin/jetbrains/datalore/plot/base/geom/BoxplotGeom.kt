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
import jetbrains.datalore.plot.base.geom.util.GeomUtil.extendTrueHeight
import jetbrains.datalore.plot.base.geom.util.HintColorUtil.colorWithAlpha
import jetbrains.datalore.plot.base.interact.NullGeomTargetCollector
import jetbrains.datalore.plot.base.interact.TipLayoutHint
import jetbrains.datalore.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.plot.base.render.SvgRoot
import jetbrains.datalore.plot.base.render.point.PointShape
import jetbrains.datalore.vis.svg.SvgLineElement

class BoxplotGeom : GeomBase() {

    var fattenMidline: Double = 1.0
    var whiskerWidth: Double = 0.5

    var outlierColor: Color? = null
    var outlierFill: Color? = null
    var outlierShape: PointShape? = null
    var outlierSize: Double? = null
    var outlierStroke: Double? = null

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = LEGEND_FACTORY

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val geomHelper = GeomHelper(pos, coord, ctx)
        CrossBarHelper.buildBoxes(
            root, aesthetics, pos, coord, ctx,
            clientRectByDataPoint(ctx, geomHelper, isHintRect = false)
        )
        buildLines(root, aesthetics, ctx, geomHelper)
        buildOutliers(root, aesthetics, pos, coord, ctx)
        BarTooltipHelper.collectRectangleTargets(
            listOf(Aes.YMAX, Aes.UPPER, Aes.MIDDLE, Aes.LOWER, Aes.YMIN),
            aesthetics, pos, coord, ctx,
            clientRectByDataPoint(ctx, geomHelper, isHintRect = true),
            { colorWithAlpha(it) },
            defaultTooltipKind = TipLayoutHint.Kind.CURSOR_TOOLTIP
        )
    }

    private fun buildLines(
        root: SvgRoot,
        aesthetics: Aesthetics,
        ctx: GeomContext,
        geomHelper: GeomHelper
    ) {
        CrossBarHelper.buildMidlines(root, aesthetics, ctx, geomHelper, fattenMidline)

        val elementHelper = geomHelper.createSvgElementHelper()
        for (p in GeomUtil.withDefined(aesthetics.dataPoints(), Aes.X)) {
            val x = p.x()!!
            val halfWidth = p.width()?.let { it * ctx.getResolution(Aes.X) / 2 } ?: 0.0
            val halfFenceWidth = halfWidth * whiskerWidth

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
                    )!!
                )
                // fence line
                lines.add(
                    elementHelper.createLine(
                        DoubleVector(x - halfFenceWidth, fence),
                        DoubleVector(x + halfFenceWidth, fence),
                        p
                    )!!
                )
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
                    )!!
                )
                // fence line
                lines.add(
                    elementHelper.createLine(
                        DoubleVector(x - halfFenceWidth, fence),
                        DoubleVector(x + halfFenceWidth, fence),
                        p
                    )!!
                )

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
            override operator fun <T> get(aes: Aes<T>): T? {
                val value: Any? = when (aes) {
                    Aes.COLOR -> outlierColor ?: super.get(aes)
                    Aes.FILL -> outlierFill ?: super.get(aes)
                    Aes.SHAPE -> outlierShape ?: super.get(aes)
                    Aes.SIZE -> outlierSize ?: OUTLIER_DEF_SIZE  // 'size' of 'super' is line thickness on box-plot
                    Aes.STROKE -> outlierStroke ?: OUTLIER_DEF_STROKE // other elements of boxplot has no 'stroke' aes
                    Aes.ALPHA -> 1.0 // Don't apply boxplot' alpha to outlier points.
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
        private val OUTLIER_DEF_STROKE = AestheticsDefaults.point().defaultValue(Aes.STROKE)

        private fun clientRectByDataPoint(
            ctx: GeomContext,
            geomHelper: GeomHelper,
            isHintRect: Boolean
        ): (DataPointAesthetics) -> DoubleRectangle? {
            return { p ->
                val clientRect = if (p.defined(Aes.X) &&
                    p.defined(Aes.LOWER) &&
                    p.defined(Aes.UPPER) &&
                    p.defined(Aes.WIDTH)
                ) {
                    val x = p.x()!!
                    val lower = p.lower()!!
                    val upper = p.upper()!!
                    val width = p.width()!! * ctx.getResolution(Aes.X)
                    geomHelper.toClient(
                        DoubleRectangle.XYWH(x - width / 2, lower, width, upper - lower),
                        p
                    )?.let {
                        if (isHintRect && upper == lower) {
                            // Add tooltips for geom_boxplot with zero height (issue #563)
                            extendTrueHeight(it, 2.0, ctx)
                        } else {
                            it
                        }
                    }
                } else {
                    null
                }
                clientRect
            }
        }
    }
}
