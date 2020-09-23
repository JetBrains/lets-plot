/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import jetbrains.datalore.base.gcommon.base.Preconditions
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.geom.*
import jetbrains.datalore.plot.base.util.StringFormat
import jetbrains.datalore.plot.builder.assemble.geom.GeomProvider
import jetbrains.datalore.plot.builder.coord.CoordProvider
import jetbrains.datalore.plot.builder.coord.CoordProviders
import jetbrains.datalore.plot.config.LiveMapOptionsParser.Companion.parseFromLayerOptions
import jetbrains.datalore.plot.config.Option.Geom.Boxplot
import jetbrains.datalore.plot.config.Option.Geom.BoxplotOutlier
import jetbrains.datalore.plot.config.Option.Geom.CrossBar
import jetbrains.datalore.plot.config.Option.Geom.Image
import jetbrains.datalore.plot.config.Option.Geom.Path
import jetbrains.datalore.plot.config.Option.Geom.Point
import jetbrains.datalore.plot.config.Option.Geom.Text
import jetbrains.datalore.plot.config.Option.Geom.PointRange
import jetbrains.datalore.plot.config.Option.Geom.Segment
import jetbrains.datalore.plot.config.Option.Geom.Step

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
            GeomKind.CROSS_BAR -> return GeomProvider.crossBar() {
                val geom = CrossBarGeom()
                if (opts.hasOwn(CrossBar.FATTEN)) {
                    geom.fattenMidline = opts.getDouble(CrossBar.FATTEN)!!
                }
                geom
            }

            GeomKind.POINT_RANGE -> return GeomProvider.pointRange() {
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

            GeomKind.LIVE_MAP -> {
                return GeomProvider.livemap(parseFromLayerOptions(opts))
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
                geom
            }

            GeomKind.TEXT -> return GeomProvider.text {
                val geom = TextGeom()

                if (opts.has(Text.LABEL_FORMAT)) {
                    val labelFormat = opts[Text.LABEL_FORMAT] as? String

                    if (labelFormat != null) {
                        geom.formatter = StringFormat(labelFormat)
                    } else {
                        throw IllegalArgumentException("Expected: label_format = 'format string'")
                    }
                }

                if (opts.has(Text.NA_VALUE)) {
                    val naValue = opts[Text.NA_VALUE] as? String

                    if (naValue != null) {
                        geom.naValue = naValue
                    }else {
                        throw IllegalArgumentException("Expected: na_value = 'some string'")
                    }
                }

                geom
            }

            GeomKind.IMAGE -> return GeomProvider.image {
                Preconditions.checkArgument(
                    opts.hasOwn(Image.HREF),
                    "Image reference URL (href) is not specified"
                )
                ImageGeom(
                    opts.getString(Image.HREF)!!
                )
            }


            else -> {
                Preconditions.checkArgument(
                    PROVIDER.containsKey(geomKind),
                    "Provider doesn't support geom kind: '$geomKind'"
                )
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
            PROVIDER[GeomKind.RIBBON] = GeomProvider.ribbon()
            PROVIDER[GeomKind.AREA] = GeomProvider.area()
            PROVIDER[GeomKind.DENSITY] = GeomProvider.density()
            PROVIDER[GeomKind.DENSITY2D] = GeomProvider.density2d()
            PROVIDER[GeomKind.DENSITY2DF] = GeomProvider.density2df()
            PROVIDER[GeomKind.JITTER] = GeomProvider.jitter()
            PROVIDER[GeomKind.FREQPOLY] = GeomProvider.freqpoly()
            // step - special case
            PROVIDER[GeomKind.RECT] = GeomProvider.rect()
            // segment - special case

            // text - special case
            PROVIDER[GeomKind.RASTER] = GeomProvider.raster()
            // image - special case
        }
    }
}