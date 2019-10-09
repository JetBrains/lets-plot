package jetbrains.datalore.plot.config

import jetbrains.datalore.base.gcommon.base.Preconditions
import jetbrains.datalore.plot.builder.assemble.geom.GeomProvider
import jetbrains.datalore.visualization.plot.base.GeomKind
import jetbrains.datalore.visualization.plot.base.geom.*

class GeomProtoClientSide(geomKind: GeomKind) : GeomProto(geomKind) {
    private val preferredCoordinateSystem: jetbrains.datalore.plot.builder.coord.CoordProvider? = when (geomKind) {
        GeomKind.TILE,
        GeomKind.CONTOUR,
        GeomKind.CONTOURF,
        GeomKind.DENSITY2D,
        GeomKind.DENSITY2DF,
        GeomKind.RASTER,
        GeomKind.IMAGE -> jetbrains.datalore.plot.builder.coord.CoordProviders.fixed(1.0)

        GeomKind.MAP -> jetbrains.datalore.plot.builder.coord.CoordProviders.map()

        else -> null
    }

    fun hasPreferredCoordinateSystem(): Boolean {
        return preferredCoordinateSystem != null
    }

    fun preferredCoordinateSystem(): jetbrains.datalore.plot.builder.coord.CoordProvider {
        return preferredCoordinateSystem!!
    }

    //    fun geomProvider(geomName: String, opts: OptionsAccessor): GeomProvider {
//        when (val geomKind = Option.GeomName.toGeomKind(geomName)) {
    fun geomProvider(opts: OptionsAccessor): GeomProvider {
        when (geomKind) {
            GeomKind.BOX_PLOT -> return GeomProvider.boxplot {
                val geom = BoxplotGeom()
                if (opts.hasOwn(Option.Geom.BoxplotOutlier.COLOR)) {
                    geom.setOutlierColor(opts.getColor(Option.Geom.BoxplotOutlier.COLOR)!!)
                }
                if (opts.hasOwn(Option.Geom.BoxplotOutlier.FILL)) {
                    geom.setOutlierFill(opts.getColor(Option.Geom.BoxplotOutlier.FILL)!!)
                }
                geom.setOutlierShape(opts.getShape(Option.Geom.BoxplotOutlier.SHAPE))
                geom.setOutlierSize(opts.getDouble(Option.Geom.BoxplotOutlier.SIZE))
                geom
            }
            GeomKind.LIVE_MAP -> {
                return opts.mergedOptions
                    .run { LiveMapConfig.parseLiveMapFromLayerOptions(this) }
                    .run { GeomProvider.livemap(displayMode, scaled) { LiveMapGeom(displayMode) } }
            }
//            GeomKind.JITTER -> return GeomProvider.jitter(
//                PosProvider.jitter(
//                    opts.getDouble(Option.Geom.Jitter.WIDTH),
//                    opts.getDouble(Option.Geom.Jitter.HEIGHT)
//                )
//            )
            GeomKind.JITTER -> return GeomProvider.jitter()

            GeomKind.STEP -> return GeomProvider.step {
                val geom = StepGeom()
                if (opts.hasOwn(Option.Geom.Step.DIRECTION)) {
                    geom.setDirection(opts.getString(Option.Geom.Step.DIRECTION)!!)
                }
                geom
            }
            GeomKind.SEGMENT -> return GeomProvider.segment {
                val geom = SegmentGeom()
                if (opts.has(Option.Geom.Segment.ARROW)) {
                    val cfg1 = ArrowSpecConfig.create(opts[Option.Geom.Segment.ARROW]!!)
                    geom.arrowSpec = cfg1.createArrowSpec()
                }
                if (opts.has(Option.Geom.Segment.ANIMATION)) {
                    geom.animation = opts[Option.Geom.Segment.ANIMATION]
                }
                geom
            }

            GeomKind.PATH -> return GeomProvider.path {
                val geom = PathGeom()
                if (opts.has(Option.Geom.Path.ANIMATION)) {
                    geom.animation = opts[Option.Geom.Path.ANIMATION]
                }
                geom
            }

            GeomKind.POINT -> return GeomProvider.point {
                val geom = PointGeom()
                if (opts.has(Option.Geom.Point.ANIMATION)) {
                    geom.animation = opts[Option.Geom.Point.ANIMATION]
                }
                geom
            }

            GeomKind.TEXT -> return GeomProvider.text {
                val geom = TextGeom()
                // ToDo: geom_text options here
                geom
            }

            GeomKind.IMAGE -> return GeomProvider.image {
                Preconditions.checkArgument(
                    opts.hasOwn(Option.Geom.Image.HREF),
                    "Image reference URL (href) is not specified"
                )
                ImageGeom(
                    opts.getString(Option.Geom.Image.HREF)!!
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
            PROVIDER[GeomKind.ERROR_BAR] = GeomProvider.errorBar()
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
            // jitter - special case
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