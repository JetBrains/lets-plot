/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.geom

import jetbrains.datalore.base.function.Function
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.typedKey.TypedKeyHashMap
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.*
import jetbrains.datalore.plot.base.aes.AestheticsDefaults
import jetbrains.datalore.plot.base.geom.util.*
import jetbrains.datalore.plot.base.interact.NullGeomTargetCollector
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
        CrossBarHelper.buildBoxes(root, aesthetics, pos, coord, ctx, rectangleByDataPoint(ctx))
        buildLines(root, aesthetics, pos, coord, ctx)
        buildOutliers(root, aesthetics, pos, coord, ctx)
        CrossBarHelper.buildTooltips(
            listOf(Aes.YMAX, Aes.UPPER, Aes.MIDDLE, Aes.LOWER, Aes.YMIN),
            aesthetics, pos, coord, ctx, rectangleByDataPoint(ctx)
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
        PointGeom().buildIntern(root, outlierAesthetics, pos, coord, ctx.withTargetCollector(NullGeomTargetCollector()))
    }

    private fun getOutliersAesthetics(aesthetics: Aesthetics): Aesthetics {
        val outlierAesMapper = OutlierAestheticsMapper(
            outlierColor, outlierFill, outlierShape, outlierSize
        )

        return MappedAesthetics(aesthetics) { outlierAesMapper.apply(it) }
    }

    private class OutlierAestheticsMapper(color: Color?, fill: Color?, shape: PointShape?, size: Double?) :
        Function<DataPointAesthetics, DataPointAesthetics> {

        private val myValueByAes = TypedKeyHashMap()

        init {
            if (color != null) {
                myValueByAes.put(Aes.COLOR, color)
            }
            if (fill != null) {
                myValueByAes.put(Aes.FILL, fill)
            }
            if (shape != null) {
                myValueByAes.put(Aes.SHAPE, shape)
            }
            if (size != null) {
                myValueByAes.put(Aes.SIZE, size)
            }
        }

        override fun apply(value: DataPointAesthetics): DataPointAesthetics {
            return if (!value.defined(Aes.Y)) {
                // not an outlier data-point
                value
            } else object : DataPointAestheticsDelegate(value) {
                override operator fun <T> get(aes: Aes<T>): T? {
                    return getIntern(aes, super.get(aes))
                }

                override fun color(): Color? {
                    return getIntern(Aes.COLOR, super.color())
                }

                override fun fill(): Color? {
                    return getIntern(Aes.FILL, super.fill())
                }

                override fun shape(): PointShape? {
                    return getIntern(Aes.SHAPE, super.shape())
                }

                override fun size(): Double? {
                    // overwrite 'size' of 'super'
                    return getIntern(
                        Aes.SIZE,
                        OUTLIER_DEF_SIZE
                    )
                }

                private fun <T> getIntern(aes: Aes<T>, naValue: T?): T? {
                    return if (myValueByAes.containsKey(aes)) {
                        myValueByAes[aes]
                    } else naValue
                }
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
