package jetbrains.datalore.visualization.plot.gog.config

import jetbrains.datalore.base.gcommon.base.Preconditions.checkArgument
import jetbrains.datalore.visualization.plot.base.GeomKind
import jetbrains.datalore.visualization.plot.base.GeomKind.*
import jetbrains.datalore.visualization.plot.base.render.geom.*
import jetbrains.datalore.visualization.plot.builder.assemble.PosProvider
import jetbrains.datalore.visualization.plot.builder.assemble.geom.GeomProvider
import jetbrains.datalore.visualization.plot.gog.config.Option.Geom.Image
import jetbrains.datalore.visualization.plot.gog.config.Option.GeomName

internal object GeomProto {
    private val DEFAULTS = HashMap<GeomKind, Map<String, Any>>()
    private val PROVIDER = HashMap<GeomKind, GeomProvider>()

    private val COMMON = createCommonDefaults()

    init {
        DEFAULTS[POINT] = COMMON
        DEFAULTS[PATH] = COMMON
        DEFAULTS[LINE] = COMMON
        DEFAULTS[SMOOTH] = createSmoothDefaults()
        DEFAULTS[BAR] = createBarDefaults()
        DEFAULTS[HISTOGRAM] = createHistogramDefaults()
        DEFAULTS[TILE] = COMMON
        DEFAULTS[ERROR_BAR] = COMMON
        DEFAULTS[CONTOUR] = createContourDefaults()
        DEFAULTS[CONTOURF] = createContourfDefaults()
        DEFAULTS[POLYGON] = COMMON
        DEFAULTS[MAP] = COMMON
        DEFAULTS[AB_LINE] = COMMON
        DEFAULTS[H_LINE] = COMMON
        DEFAULTS[V_LINE] = COMMON
        DEFAULTS[BOX_PLOT] = createBoxplotDefaults()
        DEFAULTS[LIVE_MAP] = COMMON
        DEFAULTS[RIBBON] = COMMON
        DEFAULTS[AREA] = createAreaDefaults()
        DEFAULTS[DENSITY] = createDensityDefaults()
        DEFAULTS[DENSITY2D] = createDensity2dDefaults()
        DEFAULTS[DENSITY2DF] = createDensity2dfDefaults()
        DEFAULTS[JITTER] = COMMON
        DEFAULTS[FREQPOLY] = createFreqpolyDefaults()
        DEFAULTS[STEP] = COMMON
        DEFAULTS[RECT] = COMMON
        DEFAULTS[SEGMENT] = COMMON
        DEFAULTS[TEXT] = COMMON
        DEFAULTS[RASTER] = COMMON
        DEFAULTS[IMAGE] = COMMON

        PROVIDER[POINT] = GeomProvider.point()
        PROVIDER[PATH] = GeomProvider.path()
        PROVIDER[LINE] = GeomProvider.line()
        PROVIDER[SMOOTH] = GeomProvider.smooth()
        PROVIDER[BAR] = GeomProvider.bar()
        PROVIDER[HISTOGRAM] = GeomProvider.histogram()
        PROVIDER[TILE] = GeomProvider.tile()
        PROVIDER[ERROR_BAR] = GeomProvider.errorBar()
        PROVIDER[CONTOUR] = GeomProvider.contour()
        PROVIDER[CONTOURF] = GeomProvider.contourf()
        PROVIDER[POLYGON] = GeomProvider.polygon()
        PROVIDER[MAP] = GeomProvider.map()
        PROVIDER[AB_LINE] = GeomProvider.abline()
        PROVIDER[H_LINE] = GeomProvider.hline()
        PROVIDER[V_LINE] = GeomProvider.vline()
        // boxplot - special case
        PROVIDER[RIBBON] = GeomProvider.ribbon()
        PROVIDER[AREA] = GeomProvider.area()
        PROVIDER[DENSITY] = GeomProvider.density()
        PROVIDER[DENSITY2D] = GeomProvider.density2d()
        PROVIDER[DENSITY2DF] = GeomProvider.density2df()
        // jitter - special case
        PROVIDER[FREQPOLY] = GeomProvider.freqpoly()
        // step - special case
        PROVIDER[RECT] = GeomProvider.rect()
        // segment - special case

        // text - special case
        PROVIDER[RASTER] = GeomProvider.raster()
        // image - special case
    }


    fun defaultOptions(geomName: String): Map<String, Any> {
        val geomKind = GeomName.toGeomKind(geomName)
        checkArgument(DEFAULTS.containsKey(geomKind), "Default values doesn't support geom kind: '$geomKind'")
        return DEFAULTS[geomKind]!!
    }

    fun geomProvider(geomName: String, opts: OptionsAccessor): GeomProvider {
        when (val geomKind = GeomName.toGeomKind(geomName)) {
            BOX_PLOT -> return GeomProvider.boxplot {
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
            LIVE_MAP -> {
                val cfg = LivemapConfig.create(opts.mergedOptions)
                val livemapOptions = cfg.createLivemapOptions()

                return GeomProvider.livemap(
                        { LivemapGeom(livemapOptions.displayMode) },
                        livemapOptions.displayMode,
                        livemapOptions.scaled)
            }
            JITTER -> return GeomProvider.jitter(
                    PosProvider.jitter(
                            opts.getDouble(Option.Geom.Jitter.WIDTH),
                            opts.getDouble(Option.Geom.Jitter.HEIGHT)
                    )
            )
            STEP -> return GeomProvider.step {
                val geom = StepGeom()
                if (opts.hasOwn(Option.Geom.Step.DIRECTION)) {
                    geom.setDirection(opts.getString(Option.Geom.Step.DIRECTION)!!)
                }
                geom
            }
            SEGMENT -> return GeomProvider.segment {
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

            PATH -> return GeomProvider.path {
                val geom = PathGeom()
                if (opts.has(Option.Geom.Path.ANIMATION)) {
                    geom.animation = opts[Option.Geom.Path.ANIMATION]
                }
                geom
            }

            POINT -> return GeomProvider.point {
                val geom = PointGeom()
                if (opts.has(Option.Geom.Point.ANIMATION)) {
                    geom.animation = opts[Option.Geom.Point.ANIMATION]
                }
                geom
            }

            TEXT -> return GeomProvider.text {
                val geom = TextGeom()
                // ToDo: geom_text options here
                geom
            }

            IMAGE -> return GeomProvider.image {
                checkArgument(opts.hasOwn(Image.HREF), "Image reference URL (href) is not specified")
                ImageGeom(
                        opts.getString(Image.HREF)!!
                )

            }


            else -> {
                checkArgument(PROVIDER.containsKey(geomKind), "Provider doesn't support geom kind: '$geomKind'")
                return PROVIDER[geomKind]!!
            }
        }
    }

    private fun createCommonDefaults(): Map<String, Any> {
        val defaults = HashMap<String, Any>()
        defaults["stat"] = "identity"
        return defaults
    }

    private fun createSmoothDefaults(): Map<String, Any> {
        val defaults = HashMap<String, Any>()
        defaults["stat"] = "smooth"
        return defaults
    }

    private fun createBarDefaults(): Map<String, Any> {
        val defaults = HashMap<String, Any>()
        defaults["stat"] = "count"
        defaults["position"] = "stack"
        return defaults
    }

    private fun createHistogramDefaults(): Map<String, Any> {
        val defaults = HashMap<String, Any>()
        defaults["stat"] = "bin"
        defaults["position"] = "stack"
        return defaults
    }

    private fun createContourDefaults(): Map<String, Any> {
        val defaults = HashMap<String, Any>()
        defaults["stat"] = "contour"
        return defaults
    }

    private fun createContourfDefaults(): Map<String, Any> {
        val defaults = HashMap<String, Any>()
        defaults["stat"] = "contourf"
        return defaults
    }


    private fun createBoxplotDefaults(): Map<String, Any> {
        val defaults = HashMap<String, Any>()
        defaults["stat"] = "boxplot"
        defaults["position"] = "dodge"
        return defaults
    }

    private fun createAreaDefaults(): Map<String, Any> {
        val defaults = HashMap<String, Any>()
        defaults["stat"] = "identity"
        defaults["position"] = "stack"
        return defaults
    }

    private fun createDensityDefaults(): Map<String, Any> {
        val defaults = HashMap<String, Any>()
        defaults["stat"] = "density"
        return defaults
    }

    private fun createDensity2dDefaults(): Map<String, Any> {
        val defaults = HashMap<String, Any>()
        defaults["stat"] = "density2d"
        return defaults
    }

    private fun createDensity2dfDefaults(): Map<String, Any> {
        val defaults = HashMap<String, Any>()
        defaults["stat"] = "density2df"
        return defaults
    }

    private fun createFreqpolyDefaults(): Map<String, Any> {
        val defaults = HashMap<String, Any>()
        defaults["stat"] = "bin"
        return defaults
    }
}
