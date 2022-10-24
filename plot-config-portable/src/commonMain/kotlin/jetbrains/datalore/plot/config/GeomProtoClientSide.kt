/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.geometry.DoubleRectangle
import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.stringFormat.StringFormat
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.geom.*
import jetbrains.datalore.plot.base.livemap.LivemapConstants.DisplayMode
import jetbrains.datalore.plot.base.stat.DotplotStat
import jetbrains.datalore.plot.builder.assemble.geom.GeomProvider
import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.coord.CoordProviders
import jetbrains.datalore.plot.config.Option.Geom.Boxplot
import jetbrains.datalore.plot.config.Option.Geom.BoxplotOutlier
import jetbrains.datalore.plot.config.Option.Geom.CrossBar
import jetbrains.datalore.plot.config.Option.Geom.Dotplot
import jetbrains.datalore.plot.config.Option.Geom.Image
import jetbrains.datalore.plot.config.Option.Geom.Label
import jetbrains.datalore.plot.config.Option.Geom.LiveMap.DISPLAY_MODE
import jetbrains.datalore.plot.config.Option.Geom.Path
import jetbrains.datalore.plot.config.Option.Geom.Point
import jetbrains.datalore.plot.config.Option.Geom.PointRange
import jetbrains.datalore.plot.config.Option.Geom.Segment
import jetbrains.datalore.plot.config.Option.Geom.Step
import jetbrains.datalore.plot.config.Option.Geom.Text
import jetbrains.datalore.plot.config.Option.Geom.Violin
import jetbrains.datalore.plot.config.Option.Geom.YDotplot


class GeomProtoClientSide(geomKind: GeomKind) : GeomProto(geomKind) {
    private val preferredCoordinateSystem: CoordProvider? = when (geomKind) {
        GeomKind.TILE,
        GeomKind.BIN_2D,
        GeomKind.CONTOUR,
        GeomKind.CONTOURF,
        GeomKind.DENSITY2D,
        GeomKind.DENSITY2DF,
        GeomKind.RASTER,
        GeomKind.IMAGE -> CoordProviders.fixed(1.0)

        GeomKind.MAP -> CoordProviders.map()

        else -> null
    }

    fun hasPreferredCoordinateSystem(): Boolean {
        return preferredCoordinateSystem != null
    }

    fun preferredCoordinateSystem(): CoordProvider {
        return preferredCoordinateSystem!!
    }

    fun geomProvider(opts: OptionsAccessor): GeomProvider {
        when (geomKind) {
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
                geom
            }

            GeomKind.VIOLIN -> return GeomProvider.violin {
                val geom = ViolinGeom()
                if (opts.hasOwn(Violin.DRAW_QUANTILES)) {
                    geom.setDrawQuantiles(opts.getBoundedDoubleList(Violin.DRAW_QUANTILES, 0.0, 1.0))
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

            GeomKind.LIVE_MAP -> {
                return GeomProvider.livemap(opts.mergedOptions.getEnum<DisplayMode>(DISPLAY_MODE))
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
                geom
            }

            GeomKind.PATH -> return GeomProvider.path {
                val geom = PathGeom()
                if (opts.has(Path.ANIMATION)) {
                    geom.animation = opts[Path.ANIMATION]
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
                withTextOptions(opts, TextGeom())
            }

            GeomKind.LABEL -> return GeomProvider.label {
                withTextOptions(opts, LabelGeom()).also {
                    it as LabelGeom

                    if (opts.has(Label.LABEL_PADDING)) {
                        it.paddingFactor = opts.getDoubleSafe(Label.LABEL_PADDING)!!
                    }
                    if (opts.has(Label.LABEL_R)) {
                        it.radiusFactor = opts.getDoubleSafe(Label.LABEL_R)!!
                    }
                    if (opts.has(Label.LABEL_SIZE)) {
                        it.borderWidth = opts.getDoubleSafe(Label.LABEL_SIZE)!!
                    }
                }
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
            // violin - special case
            PROVIDER[GeomKind.RIBBON] = GeomProvider.ribbon()
            PROVIDER[GeomKind.AREA] = GeomProvider.area()
            PROVIDER[GeomKind.DENSITY] = GeomProvider.density()
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
        }

        private fun withTextOptions(opts: OptionsAccessor, geom: TextGeom): TextGeom {
            if (opts.has(Text.LABEL_FORMAT)) {
                val labelFormat = opts.getString(Text.LABEL_FORMAT)!!
                geom.formatter = StringFormat.forOneArg(labelFormat)::format
            }
            if (opts.has(Text.NA_TEXT)) {
                val naValue = opts.getString(Text.NA_TEXT)!!
                geom.naValue = naValue
            }

            geom.sizeUnit = opts.getString(Text.SIZE_UNIT)?.lowercase()

            return geom
        }
    }
}