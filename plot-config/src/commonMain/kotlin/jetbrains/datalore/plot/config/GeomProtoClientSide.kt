package jetbrains.datalore.plot.config

import jetbrains.datalore.base.gcommon.base.Preconditions
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
    fun geomProvider(opts: OptionsAccessor): jetbrains.datalore.plot.builder.assemble.geom.GeomProvider {
        when (geomKind) {
            GeomKind.BOX_PLOT -> return jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.boxplot {
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
                val cfg = LiveMapConfig.create(opts.mergedOptions)
                val livemapOptions = cfg.createLivemapOptions()

                return jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.livemap(
                    { LiveMapGeom(livemapOptions.displayMode) },
                    livemapOptions.displayMode,
                    livemapOptions.scaled
                )
            }
//            GeomKind.JITTER -> return GeomProvider.jitter(
//                PosProvider.jitter(
//                    opts.getDouble(Option.Geom.Jitter.WIDTH),
//                    opts.getDouble(Option.Geom.Jitter.HEIGHT)
//                )
//            )
            GeomKind.JITTER -> return jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.jitter()

            GeomKind.STEP -> return jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.step {
                val geom = StepGeom()
                if (opts.hasOwn(Option.Geom.Step.DIRECTION)) {
                    geom.setDirection(opts.getString(Option.Geom.Step.DIRECTION)!!)
                }
                geom
            }
            GeomKind.SEGMENT -> return jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.segment {
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

            GeomKind.PATH -> return jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.path {
                val geom = PathGeom()
                if (opts.has(Option.Geom.Path.ANIMATION)) {
                    geom.animation = opts[Option.Geom.Path.ANIMATION]
                }
                geom
            }

            GeomKind.POINT -> return jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.point {
                val geom = PointGeom()
                if (opts.has(Option.Geom.Point.ANIMATION)) {
                    geom.animation = opts[Option.Geom.Point.ANIMATION]
                }
                geom
            }

            GeomKind.TEXT -> return jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.text {
                val geom = TextGeom()
                // ToDo: geom_text options here
                geom
            }

            GeomKind.IMAGE -> return jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.image {
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
        private val PROVIDER = HashMap<GeomKind, jetbrains.datalore.plot.builder.assemble.geom.GeomProvider>()

        init {
            PROVIDER[GeomKind.POINT] = jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.point()
            PROVIDER[GeomKind.PATH] = jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.path()
            PROVIDER[GeomKind.LINE] = jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.line()
            PROVIDER[GeomKind.SMOOTH] = jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.smooth()
            PROVIDER[GeomKind.BAR] = jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.bar()
            PROVIDER[GeomKind.HISTOGRAM] = jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.histogram()
            PROVIDER[GeomKind.TILE] = jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.tile()
            PROVIDER[GeomKind.ERROR_BAR] = jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.errorBar()
            PROVIDER[GeomKind.CONTOUR] = jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.contour()
            PROVIDER[GeomKind.CONTOURF] = jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.contourf()
            PROVIDER[GeomKind.POLYGON] = jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.polygon()
            PROVIDER[GeomKind.MAP] = jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.map()
            PROVIDER[GeomKind.AB_LINE] = jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.abline()
            PROVIDER[GeomKind.H_LINE] = jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.hline()
            PROVIDER[GeomKind.V_LINE] = jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.vline()
            // boxplot - special case
            PROVIDER[GeomKind.RIBBON] = jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.ribbon()
            PROVIDER[GeomKind.AREA] = jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.area()
            PROVIDER[GeomKind.DENSITY] = jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.density()
            PROVIDER[GeomKind.DENSITY2D] = jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.density2d()
            PROVIDER[GeomKind.DENSITY2DF] = jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.density2df()
            // jitter - special case
            PROVIDER[GeomKind.FREQPOLY] = jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.freqpoly()
            // step - special case
            PROVIDER[GeomKind.RECT] = jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.rect()
            // segment - special case

            // text - special case
            PROVIDER[GeomKind.RASTER] = jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.raster()
            // image - special case
        }
    }
}