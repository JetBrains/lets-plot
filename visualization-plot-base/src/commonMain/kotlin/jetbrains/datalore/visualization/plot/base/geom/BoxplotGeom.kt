package jetbrains.datalore.visualization.plot.base.geom

import jetbrains.datalore.base.function.Function
import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.typedKey.TypedKeyHashMap
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.visualization.base.svg.SvgGElement
import jetbrains.datalore.visualization.base.svg.SvgLineElement
import jetbrains.datalore.visualization.base.svg.SvgRectElement
import jetbrains.datalore.visualization.plot.base.*
import jetbrains.datalore.visualization.plot.base.aes.AesScaling
import jetbrains.datalore.visualization.plot.base.aes.AestheticsDefaults
import jetbrains.datalore.visualization.plot.base.geom.util.*
import jetbrains.datalore.visualization.plot.base.geom.util.HintColorUtil.fromColor
import jetbrains.datalore.visualization.plot.base.geom.util.HintsCollection.HintConfigFactory
import jetbrains.datalore.visualization.plot.base.interact.GeomTargetCollector.TooltipParams.Companion.params
import jetbrains.datalore.visualization.plot.base.interact.NullGeomTargetCollector
import jetbrains.datalore.visualization.plot.base.interact.TipLayoutHint.Kind.HORIZONTAL_TOOLTIP
import jetbrains.datalore.visualization.plot.base.render.LegendKeyElementFactory
import jetbrains.datalore.visualization.plot.base.render.SvgRoot
import jetbrains.datalore.visualization.plot.base.render.point.PointShape

class BoxplotGeom : GeomBase() {

    private var myOutlierColor: Color? = null
    private var myOutlierFill: Color? = null
    private var myOutlierShape: PointShape? = null
    private var myOutlierSize: Double? = null

    override val legendKeyElementFactory: LegendKeyElementFactory
        get() = MyLegendKeyElementFactory()

    fun setOutlierColor(outlierColor: Color) {
        myOutlierColor = outlierColor
    }

    fun setOutlierFill(outlierFill: Color) {
        myOutlierFill = outlierFill
    }

    fun setOutlierShape(outlierShape: PointShape?) {
        myOutlierShape = outlierShape
    }

    fun setOutlierSize(outlierSize: Double?) {
        myOutlierSize = outlierSize
    }

    override fun buildIntern(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        buildBoxes(root, aesthetics, pos, coord, ctx)
        buildLines(root, aesthetics, pos, coord, ctx)
        buildOutliers(root, aesthetics, pos, coord, ctx)
        buildHints(aesthetics, pos, coord, ctx)
    }

    private fun buildHints(aesthetics: Aesthetics, pos: PositionAdjustment, coord: CoordinateSystem, ctx: GeomContext) {
        val helper = GeomHelper(pos, coord, ctx)

        for (p in aesthetics.dataPoints()) {
            val rect = rectangleByDataPoint(ctx)(p) ?: continue

            val xCoord = rect.center.x
            val width = GeomUtil.widthPx(p, ctx, 2.0)
            val clientRect = helper.toClient(DoubleRectangle(0.0, 0.0, width, 0.0), p)
            val objectRadius = clientRect.width / 2.0

            val hint = HintConfigFactory()
                .defaultObjectRadius(objectRadius)
                .defaultX(xCoord)
                .defaultKind(HORIZONTAL_TOOLTIP)

            val hints = HintsCollection(p, helper)
                .addHint(hint.create(Aes.YMAX))
                .addHint(hint.create(Aes.UPPER))
                .addHint(hint.create(Aes.MIDDLE))
                .addHint(hint.create(Aes.LOWER))
                .addHint(hint.create(Aes.YMIN))
                .hints

            ctx.targetCollector.addRectangle(
                p.index(),
                helper.toClient(rect, p),
                params()
                    .setTipLayoutHints(hints)
                    .setColor(fromColor(p))
            )

        }
    }

    private fun buildBoxes(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        // rectangles
        val helper = RectanglesHelper(aesthetics, pos, coord, ctx)
        val rectangles = helper.createRectangles(rectangleByDataPoint(ctx))
        rectangles.forEach { root.add(it) }
    }

    private fun buildLines(
        root: SvgRoot,
        aesthetics: Aesthetics,
        pos: PositionAdjustment,
        coord: CoordinateSystem,
        ctx: GeomContext
    ) {
        val helper = GeomHelper(pos, coord, ctx)
        val elementHelper = helper.createSvgElementHelper()

        for (p in GeomUtil.withDefined(aesthetics.dataPoints(), Aes.X)) {
            val x = p.x()!!
            val hasWidth = p.defined(Aes.WIDTH)
            val width = if (hasWidth)
                GeomUtil.widthPx(p, ctx, 2.0)
            else
                Double.NaN

            val lines = ArrayList<SvgLineElement>()

            // middle
            if (hasWidth && p.defined(Aes.MIDDLE)) {
                val middle = p.middle()!!
                lines.add(
                    elementHelper.createLine(
                        DoubleVector(x - width / 2, middle),
                        DoubleVector(x + width / 2, middle),
                        p
                    )
                )
            }

            /*
      double halfFenceWidth = hasWidth
          ? width * .1 / 2
          : 0;
      */

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
            myOutlierColor, myOutlierFill, myOutlierShape, myOutlierSize
        )

        return MappedAesthetics(aesthetics) { outlierAesMapper.apply(it) }
    }

    private class OutlierAestheticsMapper(color: Color?, fill: Color?, shape: PointShape?, size: Double?) :
        Function<DataPointAesthetics, DataPointAesthetics> {

        //private final Map<Aes, Object> myValueByAes = new HashMap<>();
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
                    return getIntern(Aes.SIZE, DEF_SIZE)
                }

                private fun <T> getIntern(aes: Aes<T>, naValue: T?): T? {
                    return if (myValueByAes.containsKey(aes)) {
                        myValueByAes[aes]
                    } else naValue
                }
            }
        }

        companion object {
            private val DEF_SIZE = AestheticsDefaults.point().defaultValue(Aes.SIZE)
        }
    }

    private class MyLegendKeyElementFactory : LegendKeyElementFactory {

        override fun createKeyElement(p: DataPointAesthetics, size: DoubleVector): SvgGElement {
            val whiskerSize = .2

            val strokeWidth = AesScaling.strokeWidth(p)
            val width = (size.x - strokeWidth) * .8 // a bit narrower
            val height = size.y - strokeWidth
            val x = (size.x - width) / 2
            val y = strokeWidth / 2


            // box
            val rect = SvgRectElement(x, y + height * whiskerSize, width, height * (1 - 2 * whiskerSize))
            GeomHelper.decorate(rect, p)

            // lines
            val middleY = y + height * .5
            val middle = SvgLineElement(x, middleY, x + width, middleY)
            GeomHelper.decorate(middle, p)
            val middleX = x + width * .5
            val lowerWhisker = SvgLineElement(middleX, y + height * (1 - whiskerSize), middleX, y + height)
            GeomHelper.decorate(lowerWhisker, p)
            val upperWhisker = SvgLineElement(middleX, y, middleX, y + height * whiskerSize)
            GeomHelper.decorate(upperWhisker, p)

            val g = SvgGElement()
            g.children().add(rect)
            g.children().add(middle)
            g.children().add(lowerWhisker)
            g.children().add(upperWhisker)
            return g
        }
    }

    companion object {
//        val RENDERS = listOf(
//                Aes.LOWER, // NaN for 'outlier' data-point
//                Aes.MIDDLE, // NaN for 'outlier' data-point
//                Aes.UPPER, // NaN for 'outlier' data-point
//
//                Aes.X,
//                Aes.Y, // NaN for 'box' data-point
//                Aes.YMAX,
//                Aes.YMIN,
//
//                Aes.ALPHA,
//                Aes.COLOR,
//                Aes.FILL,
//                Aes.LINETYPE,
//                Aes.SHAPE,
//                Aes.SIZE, // path width
//                Aes.WIDTH
//        )

        const val HANDLES_GROUPS = false

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
