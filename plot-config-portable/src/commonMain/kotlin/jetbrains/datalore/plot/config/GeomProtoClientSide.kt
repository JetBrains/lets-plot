/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.spatial.projections.identity
import jetbrains.datalore.base.spatial.projections.mercator
import jetbrains.datalore.base.stringFormat.StringFormat
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.geom.*
import jetbrains.datalore.plot.base.stat.DotplotStat
import jetbrains.datalore.plot.builder.assemble.geom.GeomProvider
import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.coord.CoordProviders
import jetbrains.datalore.plot.config.Option.Geom.Density
import jetbrains.datalore.plot.config.Option.Geom.Boxplot
import jetbrains.datalore.plot.config.Option.Geom.BoxplotOutlier
import jetbrains.datalore.plot.config.Option.Geom.AreaRidges
import jetbrains.datalore.plot.config.Option.Geom.CrossBar
import jetbrains.datalore.plot.config.Option.Geom.Dotplot
import jetbrains.datalore.plot.config.Option.Geom.Image
import jetbrains.datalore.plot.config.Option.Geom.Label
import jetbrains.datalore.plot.config.Option.Geom.Path
import jetbrains.datalore.plot.config.Option.Geom.Pie
import jetbrains.datalore.plot.config.Option.Geom.Point
import jetbrains.datalore.plot.config.Option.Geom.PointRange
import jetbrains.datalore.plot.config.Option.Geom.Segment
import jetbrains.datalore.plot.config.Option.Geom.Step
import jetbrains.datalore.plot.config.Option.Geom.Text
import jetbrains.datalore.plot.config.Option.Geom.Violin
import jetbrains.datalore.plot.config.Option.Geom.YDotplot
import jetbrains.datalore.plot.config.Option.Geom.Lollipop
import jetbrains.datalore.plot.base.geom.LollipopGeom.Orientation
import jetbrains.datalore.plot.base.geom.LollipopGeom.Direction
import jetbrains.datalore.plot.config.Option.Layer.USE_CRS


class GeomProtoClientSide(geomKind: GeomKind) : GeomProto(geomKind) {

    fun preferredCoordinateSystem(layerConfig: LayerConfig): CoordProvider? {
        return when (geomKind) {
            GeomKind.TILE,
            GeomKind.BIN_2D,
            GeomKind.CONTOUR,
            GeomKind.CONTOURF,
            GeomKind.DENSITY2D,
            GeomKind.DENSITY2DF,
            GeomKind.RASTER,
            GeomKind.IMAGE -> CoordProviders.fixed(1.0)

            GeomKind.MAP -> CoordProviders.map(projection = identity().takeIf { layerConfig.has(USE_CRS) } ?: mercator())

            else -> null
        }
    }

    fun geomProvider(opts: OptionsAccessor): GeomProvider {
        when (geomKind) {
            GeomKind.DENSITY -> return GeomProvider.density {
                val geom = DensityGeom()
                if (opts.hasOwn(Option.Stat.Density.QUANTILES)) {
                    geom.quantiles = opts.getBoundedDoubleList(Option.Stat.Density.QUANTILES, 0.0, 1.0)
                }
                if (opts.hasOwn(Density.QUANTILE_LINES)) {
                    geom.quantileLines = opts.getBoolean(Density.QUANTILE_LINES, DensityGeom.DEF_QUANTILE_LINES)
                }
                geom
            }

            GeomKind.DOT_PLOT -> return GeomProvider.dotplot {
                val geom = DotplotGeom()
                if (opts.hasOwn(Dotplot.DOTSIZE)) {
                    geom.dotSize = opts.getDouble(Dotplot.DOTSIZE)!!
                }
                if (opts.hasOwn(Dotplot.STACKRATIO)) {
                    geom.stackRatio = opts.getDouble(Dotplot.STACKRATIO)!!
                }
                if (opts.hasOwn(Dotplot.STACKGROUPS)) {
                    geom.stackGroups = opts.getBoolean(Dotplot.STACKGROUPS)
                }
                if (opts.hasOwn(Dotplot.STACKDIR)) {
                    geom.stackDir = DotplotGeom.Stackdir.safeValueOf(opts.getString(Dotplot.STACKDIR)!!)
                }
                if (opts.hasOwn(Dotplot.METHOD)) {
                    geom.method = DotplotStat.Method.safeValueOf(opts.getString(Dotplot.METHOD)!!)
                }
                geom
            }

            GeomKind.CROSS_BAR -> return GeomProvider.crossBar {
                val geom = CrossBarGeom()
                if (opts.hasOwn(CrossBar.FATTEN)) {
                    geom.fattenMidline = opts.getDouble(CrossBar.FATTEN)!!
                }
                geom
            }

            GeomKind.POINT_RANGE -> return GeomProvider.pointRange {
                val geom = PointRangeGeom()
                if (opts.hasOwn(PointRange.FATTEN)) {
                    geom.fattenMidPoint = opts.getDouble(PointRange.FATTEN)!!
                }
                geom
            }

            GeomKind.BOX_PLOT -> return GeomProvider.boxplot {
                val geom = BoxplotGeom()
                if (opts.hasOwn(Boxplot.FATTEN)) {
                    geom.fattenMidline = opts.getDouble(Boxplot.FATTEN)!!
                }
                if (opts.hasOwn(Boxplot.WHISKER_WIDTH)) {
                    geom.whiskerWidth = opts.getDouble(Boxplot.WHISKER_WIDTH)!!
                }
                if (opts.hasOwn(BoxplotOutlier.COLOR)) {
                    geom.outlierColor = opts.getColor(BoxplotOutlier.COLOR)!!
                }
                if (opts.hasOwn(BoxplotOutlier.FILL)) {
                    geom.outlierFill = opts.getColor(BoxplotOutlier.FILL)!!
                }
                geom.outlierShape = opts.getShape(BoxplotOutlier.SHAPE)
                geom.outlierSize = opts.getDouble(BoxplotOutlier.SIZE)
                geom.outlierStroke = opts.getDouble(BoxplotOutlier.STROKE)
                geom
            }

            GeomKind.AREA_RIDGES -> return GeomProvider.arearidges {
                val geom = AreaRidgesGeom()
                if (opts.hasOwn(AreaRidges.SCALE)) {
                    geom.scale = opts.getDoubleDef(AreaRidges.SCALE, AreaRidgesGeom.DEF_SCALE)
                }
                if (opts.hasOwn(AreaRidges.MIN_HEIGHT)) {
                    geom.minHeight = opts.getDoubleDef(AreaRidges.MIN_HEIGHT, AreaRidgesGeom.DEF_MIN_HEIGHT)
                }
                if (opts.hasOwn(Option.Stat.DensityRidges.QUANTILES)) {
                    geom.quantiles = opts.getBoundedDoubleList(Option.Stat.DensityRidges.QUANTILES, 0.0, 1.0)
                }
                if (opts.hasOwn(AreaRidges.QUANTILE_LINES)) {
                    geom.quantileLines = opts.getBoolean(AreaRidges.QUANTILE_LINES, AreaRidgesGeom.DEF_QUANTILE_LINES)
                }
                geom
            }

            GeomKind.VIOLIN -> return GeomProvider.violin {
                val geom = ViolinGeom()
                if (opts.hasOwn(Option.Stat.YDensity.QUANTILES)) {
                    geom.quantiles = opts.getBoundedDoubleList(Option.Stat.YDensity.QUANTILES, 0.0, 1.0)
                }
                if (opts.hasOwn(Violin.QUANTILE_LINES)) {
                    geom.quantileLines = opts.getBoolean(Violin.QUANTILE_LINES, ViolinGeom.DEF_QUANTILE_LINES)
                }
                if (opts.hasOwn(Violin.SHOW_HALF)) {
                    geom.showHalf = opts.getDouble(Violin.SHOW_HALF)!!
                }
                geom
            }

            GeomKind.Y_DOT_PLOT -> return GeomProvider.ydotplot {
                val geom = YDotplotGeom()
                if (opts.hasOwn(YDotplot.DOTSIZE)) {
                    geom.dotSize = opts.getDouble(YDotplot.DOTSIZE)!!
                }
                if (opts.hasOwn(YDotplot.STACKRATIO)) {
                    geom.stackRatio = opts.getDouble(YDotplot.STACKRATIO)!!
                }
                if (opts.hasOwn(YDotplot.STACKGROUPS)) {
                    geom.stackGroups = opts.getBoolean(YDotplot.STACKGROUPS)
                }
                if (opts.hasOwn(YDotplot.STACKDIR)) {
                    geom.yStackDir = YDotplotGeom.YStackdir.safeValueOf(opts.getString(YDotplot.STACKDIR)!!)
                }
                if (opts.hasOwn(YDotplot.METHOD)) {
                    geom.method = DotplotStat.Method.safeValueOf(opts.getString(YDotplot.METHOD)!!)
                }
                geom
            }

            GeomKind.STEP -> return GeomProvider.step {
                val geom = StepGeom()
                if (opts.hasOwn(Step.DIRECTION)) {
                    geom.setDirection(opts.getString(Step.DIRECTION)!!)
                }
                geom
            }

            GeomKind.SEGMENT -> return GeomProvider.segment {
                val geom = SegmentGeom()
                if (opts.has(Segment.ARROW)) {
                    val cfg1 = ArrowSpecConfig.create(opts[Segment.ARROW]!!)
                    geom.arrowSpec = cfg1.createArrowSpec()
                }
                if (opts.has(Segment.ANIMATION)) {
                    geom.animation = opts[Segment.ANIMATION]
                }
                if (opts.has(Segment.FLAT)) {
                    geom.flat = opts.getBoolean(Segment.FLAT)
                }
                if (opts.has(Segment.GEODESIC)) {
                    geom.geodesic = opts.getBoolean(Segment.GEODESIC)
                }
                geom
            }

            GeomKind.PATH -> return GeomProvider.path {
                val geom = PathGeom()
                if (opts.has(Path.ANIMATION)) {
                    geom.animation = opts[Path.ANIMATION]
                }
                if (opts.has(Path.FLAT)) {
                    geom.flat = opts.getBoolean(Path.FLAT)
                }
                if (opts.has(Segment.GEODESIC)) {
                    geom.geodesic = opts.getBoolean(Segment.GEODESIC)
                }
                geom
            }

            GeomKind.POINT -> return GeomProvider.point {
                val geom = PointGeom()

                if (opts.has(Point.ANIMATION)) {
                    geom.animation = opts[Point.ANIMATION]
                }

                geom.sizeUnit = opts.getString(Point.SIZE_UNIT)?.lowercase()
                geom
            }

            GeomKind.TEXT -> return GeomProvider.text {
                val geom = TextGeom()

                applyTextOptions(opts, geom)

                geom
            }

            GeomKind.LABEL -> return GeomProvider.label {
                val geom = LabelGeom()

                applyTextOptions(opts, geom)
                opts.getDouble(Label.LABEL_PADDING)?.let { geom.paddingFactor = it }
                opts.getDouble(Label.LABEL_R)?.let { geom.radiusFactor = it }
                opts.getDouble(Label.LABEL_SIZE)?.let { geom.borderWidth = it }

                geom
            }

            GeomKind.IMAGE -> return GeomProvider.image {
                require(opts.hasOwn(Image.HREF)) { "Image reference URL (href) is not specified." }
                for (s in listOf(Image.XMIN, Image.XMAX, Image.YMIN, Image.YMAX)) {
                    require(opts.hasOwn(s)) { "'$s' is not specified." }
                }
                ImageGeom(
                    imageUrl = opts.getString(Image.HREF)!!,
                    bbox = DoubleRectangle.span(
                        DoubleVector(opts.getDoubleSafe(Image.XMIN), opts.getDoubleSafe(Image.YMIN)),
                        DoubleVector(opts.getDoubleSafe(Image.XMAX), opts.getDoubleSafe(Image.YMAX)),
                    )
                )
            }

            GeomKind.PIE -> return GeomProvider.pie {
                val geom = PieGeom()

                opts.getDouble(Pie.HOLE)?.let { geom.holeSize = it }
                opts.getDouble(Pie.STROKE)?.let { geom.strokeWidth = it }
                opts.getColor(Pie.STROKE_COLOR)?.let { geom.strokeColor = it }
                geom
            }

            GeomKind.LOLLIPOP -> return GeomProvider.lollipop {
                val geom = LollipopGeom()
                val orientation = opts.getString(Option.Layer.ORIENTATION)?.let {
                    when (it.lowercase()) {
                        "x" -> Orientation.X
                        "y" -> Orientation.Y
                        else -> error("orientation expected x|y but was $it")
                    }
                } ?: Orientation.X
                val defaultDirection = when (orientation) {
                    Orientation.X -> Direction.VERTICAL
                    Orientation.Y -> Direction.HORIZONTAL
                }
                val direction = opts.getString(Lollipop.DIRECTION)?.let {
                    when (it.lowercase()) {
                        "v", "vertical" -> Direction.VERTICAL
                        "h", "horizontal" -> Direction.HORIZONTAL
                        "s", "slope" -> Direction.SLOPE
                        else -> error(
                            "Unsupported value for ${Lollipop.DIRECTION} parameter: '$it'\n" +
                            "Use one of: v, vertical, h, horizontal, s, slope."
                        )
                    }
                } ?: defaultDirection
                val slope = if (opts.hasOwn(Lollipop.SLOPE)) {
                    opts.getDouble(Lollipop.SLOPE)!!
                } else {
                    0.0
                }
                if (slope == 0.0) {
                    when (orientation to direction) {
                        Orientation.X to Direction.HORIZONTAL,
                        Orientation.Y to Direction.VERTICAL -> {
                            val slopeMessage = if (opts.hasOwn(Lollipop.SLOPE)) {
                                "Set a non-zero value for the slope."
                            } else {
                                "Change slope from 0 (default) to a different value."
                            }
                            error(slopeMessage +
                                  "With this combination of ${Option.Layer.ORIENTATION} and ${Lollipop.DIRECTION}, " +
                                  "the baseline cannot be parallel to the $orientation axis.")
                        }
                    }
                }
                geom.orientation = orientation
                geom.direction = direction
                geom.slope = slope
                if (opts.hasOwn(Lollipop.INTERCEPT)) {
                    geom.intercept = opts.getDouble(Lollipop.INTERCEPT)!!
                }
                if (opts.hasOwn(Lollipop.FATTEN)) {
                    geom.fatten = opts.getDouble(Lollipop.FATTEN)!!
                }
                geom
            }

            else -> {
                require(PROVIDER.containsKey(geomKind)) { "Provider doesn't support geom kind: '$geomKind'" }
                return PROVIDER[geomKind]!!
            }
        }
    }

    private companion object {
        private val PROVIDER = HashMap<GeomKind, GeomProvider>()

        init {
            PROVIDER[GeomKind.POINT] = GeomProvider.point()
            PROVIDER[GeomKind.PATH] = GeomProvider.path()
            PROVIDER[GeomKind.LINE] = GeomProvider.line()
            PROVIDER[GeomKind.SMOOTH] = GeomProvider.smooth()
            PROVIDER[GeomKind.BAR] = GeomProvider.bar()
            PROVIDER[GeomKind.HISTOGRAM] = GeomProvider.histogram()
            // dotplot - special case
            PROVIDER[GeomKind.TILE] = GeomProvider.tile()
            PROVIDER[GeomKind.BIN_2D] = GeomProvider.bin2d()
            PROVIDER[GeomKind.ERROR_BAR] = GeomProvider.errorBar()
            // crossbar - special case
            // pointrange - special case
            PROVIDER[GeomKind.LINE_RANGE] = GeomProvider.lineRange()
            PROVIDER[GeomKind.CONTOUR] = GeomProvider.contour()
            PROVIDER[GeomKind.CONTOURF] = GeomProvider.contourf()
            PROVIDER[GeomKind.POLYGON] = GeomProvider.polygon()
            PROVIDER[GeomKind.MAP] = GeomProvider.map()
            PROVIDER[GeomKind.AB_LINE] = GeomProvider.abline()
            PROVIDER[GeomKind.H_LINE] = GeomProvider.hline()
            PROVIDER[GeomKind.V_LINE] = GeomProvider.vline()
            // boxplot - special case
            // area ridges - special case
            // violin - special case
            PROVIDER[GeomKind.RIBBON] = GeomProvider.ribbon()
            PROVIDER[GeomKind.AREA] = GeomProvider.area()
            // density - special case
            PROVIDER[GeomKind.DENSITY2D] = GeomProvider.density2d()
            PROVIDER[GeomKind.DENSITY2DF] = GeomProvider.density2df()
            PROVIDER[GeomKind.JITTER] = GeomProvider.jitter()
            PROVIDER[GeomKind.Q_Q] = GeomProvider.qq()
            PROVIDER[GeomKind.Q_Q_2] = GeomProvider.qq2()
            PROVIDER[GeomKind.Q_Q_LINE] = GeomProvider.qqline()
            PROVIDER[GeomKind.Q_Q_2_LINE] = GeomProvider.qq2line()
            PROVIDER[GeomKind.FREQPOLY] = GeomProvider.freqpoly()
            // step - special case
            PROVIDER[GeomKind.RECT] = GeomProvider.rect()
            // segment - special case
            // text, label - special case
            PROVIDER[GeomKind.RASTER] = GeomProvider.raster()
            // image - special case
            // pie - special case
            PROVIDER[GeomKind.LIVE_MAP] = GeomProvider.livemap()
            // lollipop - special case
        }

        private fun applyTextOptions(opts: OptionsAccessor, geom: TextGeom) {
            opts.getString(Text.LABEL_FORMAT)?.let { geom.formatter = StringFormat.forOneArg(it)::format }
            opts.getString(Text.NA_TEXT)?.let { geom.naValue = it }
            geom.sizeUnit = opts.getString(Text.SIZE_UNIT)?.lowercase()
        }
    }
}