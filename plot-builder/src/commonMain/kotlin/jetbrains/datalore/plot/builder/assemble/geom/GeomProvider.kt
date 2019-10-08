package jetbrains.datalore.plot.builder.assemble.geom

import jetbrains.datalore.visualization.plot.base.Aes
import jetbrains.datalore.visualization.plot.base.Geom
import jetbrains.datalore.visualization.plot.base.GeomKind
import jetbrains.datalore.visualization.plot.base.GeomMeta
import jetbrains.datalore.visualization.plot.base.aes.AestheticsDefaults
import jetbrains.datalore.visualization.plot.base.geom.*
import jetbrains.datalore.visualization.plot.base.livemap.LivemapConstants

abstract class GeomProvider private constructor(val geomKind: GeomKind) {

//    protected open val autoMappedAes: List<Aes<*>>
//        get() = emptyList()

    open val preferredCoordinateSystem: jetbrains.datalore.plot.builder.coord.CoordProvider
        get() = throw IllegalStateException("No preferred coordinate system")

//    open val preferredPos: PosProvider
//        get() = PosProvider.wrap(PositionAdjustments.identity())

//    abstract val preferredSampling: Sampling

//    abstract fun renders(): List<Aes<*>>

    fun renders(): List<Aes<*>> {
        return GeomMeta.renders(geomKind)
    }

    abstract fun createGeom(): Geom

    abstract fun aestheticsDefaults(): AestheticsDefaults

    abstract fun handlesGroups(): Boolean

//    fun createAesAutoMapper(): AesAutoMapper {
//        return DefaultAesAutoMapper(autoMappedAes) { this.preferDiscreteForAutoMapping(it) }
//    }

//    protected open fun preferDiscreteForAutoMapping(aes: Aes<*>): Boolean {
//        return false
//    }

//    open fun hasPreferredCoordinateSystem(): Boolean {
//        return false
//    }

    private class GeomProviderBuilder internal constructor(
        private val myKind: GeomKind,
//        private val myRenderedAes: List<Aes<*>>,
        private val myAestheticsDefaults: AestheticsDefaults,
        private val myHandlesGroups: Boolean,
        private val myGeomSupplier: () -> Geom
    ) {
//        private var myPreferredCoordSystemProvider: CoordProvider? = null
        //        private var myAutoMappedAes: List<Aes<*>>? = null
//        private var myPos: PosProvider? = null
//        private var myPreferDiscreteForAutoMapping = false
//        private var myPreferredSampling: Sampling = Samplings.NONE

//        internal fun preferredCoordSystemProvider(preferredCoordSystemProvider: CoordProvider): GeomProviderBuilder {
//            myPreferredCoordSystemProvider = preferredCoordSystemProvider
//            return this
//        }

//        internal fun autoMappedAes(vararg autoMappedAes: Aes<*>): GeomProviderBuilder {
//            myAutoMappedAes = listOf(*autoMappedAes)
//            return this
//        }

//        internal fun preferDiscreteForAutoMapping(preferDiscreteForAutoMapping: Boolean): GeomProviderBuilder {
//            myPreferDiscreteForAutoMapping = preferDiscreteForAutoMapping
//            return this
//        }

//        internal fun posProvider(pos: PosProvider): GeomProviderBuilder {
//            myPos = pos
//            return this
//        }

//        internal fun preferredSampling(sampling: Sampling): GeomProviderBuilder {
//            myPreferredSampling = sampling
//            return this
//        }

        internal fun build(): jetbrains.datalore.plot.builder.assemble.geom.GeomProvider {
            return object : jetbrains.datalore.plot.builder.assemble.geom.GeomProvider(myKind) {

//                override val autoMappedAes: List<Aes<*>>
//                    get() = myAutoMappedAes ?: super.autoMappedAes

//                override val preferredCoordinateSystem: CoordProvider
//                    get() = myPreferredCoordSystemProvider ?: super.preferredCoordinateSystem

//                override val preferredPos: PosProvider
//                    get() = myPos ?: super.preferredPos

                //                override val preferredSampling: Sampling
//                    get() = myPreferredSampling
//
//                override fun renders(): List<Aes<*>> {
//                    return myRenderedAes
//                }

                override fun createGeom(): Geom {
                    return myGeomSupplier()
                }

                override fun aestheticsDefaults(): AestheticsDefaults {
                    return myAestheticsDefaults
                }

                override fun handlesGroups(): Boolean {
                    return myHandlesGroups
                }

//                override fun hasPreferredCoordinateSystem(): Boolean {
//                    return myPreferredCoordSystemProvider != null
//                }

//                override fun preferDiscreteForAutoMapping(aes: Aes<*>): Boolean {
//                    return if (myPreferDiscreteForAutoMapping) {
//                        autoMappedAes.contains(aes)
//                    } else super.preferDiscreteForAutoMapping(aes)
//                }
            }
        }
    }

    companion object {

        fun point(): jetbrains.datalore.plot.builder.assemble.geom.GeomProvider {
            return jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.Companion.point { PointGeom() }
        }

        fun point(supplier: () -> Geom): jetbrains.datalore.plot.builder.assemble.geom.GeomProvider {
            return jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.GeomProviderBuilder(
                GeomKind.POINT,
//                PointGeom.RENDERS,
                AestheticsDefaults.point(),
                PointGeom.HANDLES_GROUPS,
                supplier
            )
//                .autoMappedAes(Aes.X, Aes.Y)
//                    .preferredSampling(POINT)
                .build()
        }

        fun path(): jetbrains.datalore.plot.builder.assemble.geom.GeomProvider {
            return jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.Companion.path { PathGeom() }
        }

        fun path(supplier: () -> Geom): jetbrains.datalore.plot.builder.assemble.geom.GeomProvider {
            return jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.GeomProviderBuilder(
                GeomKind.PATH,
//                PathGeom.RENDERS,
                AestheticsDefaults.path(),
                PathGeom.HANDLES_GROUPS,
                supplier
            )
//                .autoMappedAes(Aes.X, Aes.Y)
//                    .preferredSampling(PATH)
                .build()
        }

        fun line(): jetbrains.datalore.plot.builder.assemble.geom.GeomProvider {
            return jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.GeomProviderBuilder(
                GeomKind.LINE,
//                LineGeom.RENDERS,
                AestheticsDefaults.line(),
                LineGeom.HANDLES_GROUPS
            ) { LineGeom() }
//                .autoMappedAes(Aes.X, Aes.Y)
//                    .preferredSampling(LINE)
                .build()
        }

        fun smooth(): jetbrains.datalore.plot.builder.assemble.geom.GeomProvider {
            return jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.GeomProviderBuilder(
                GeomKind.SMOOTH,
//                SmoothGeom.RENDERS,
                AestheticsDefaults.smooth(),
                SmoothGeom.HANDLES_GROUPS
            ) { SmoothGeom() }
//                .autoMappedAes(Aes.X, Aes.Y)
//                    .preferredSampling(SMOOTH)
                .build()
        }

        fun bar(): jetbrains.datalore.plot.builder.assemble.geom.GeomProvider {
            return jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.GeomProviderBuilder(
                GeomKind.BAR,
//                BarGeom.RENDERS,
                AestheticsDefaults.bar(),
                BarGeom.HANDLES_GROUPS
            ) { BarGeom() }
//                .autoMappedAes(Aes.X)
//                .preferDiscreteForAutoMapping(true)
//                    .preferredSampling(BAR)
                .build()
        }

        fun histogram(): jetbrains.datalore.plot.builder.assemble.geom.GeomProvider {
            return jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.GeomProviderBuilder(
                GeomKind.HISTOGRAM,
//                HistogramGeom.RENDERS,
                AestheticsDefaults.histogram(),
                HistogramGeom.HANDLES_GROUPS
            ) { HistogramGeom() }
//                .autoMappedAes(Aes.X)
//                    .preferredSampling(HISTOGRAM)
                .build()
        }

        fun tile(): jetbrains.datalore.plot.builder.assemble.geom.GeomProvider {
            return jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.GeomProviderBuilder(
                GeomKind.TILE,
//                TileGeom.RENDERS,
                AestheticsDefaults.tile(),
                TileGeom.HANDLES_GROUPS
            ) { TileGeom() }
//                .preferredCoordSystemProvider(CoordProviders.fixed(1.0))
//                .autoMappedAes(Aes.X, Aes.Y)
//                    .preferredSampling(TILE)
                .build()
        }

        fun errorBar(): jetbrains.datalore.plot.builder.assemble.geom.GeomProvider {
            return jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.GeomProviderBuilder(
                GeomKind.ERROR_BAR,
//                ErrorBarGeom.RENDERS,
                AestheticsDefaults.errorBar(),
                ErrorBarGeom.HANDLES_GROUPS
            ) { ErrorBarGeom() }
//                    .preferredSampling(ERROR_BAR)
                .build()
        }

        fun contour(): jetbrains.datalore.plot.builder.assemble.geom.GeomProvider {
            return jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.GeomProviderBuilder(
                GeomKind.CONTOUR,
//                ContourGeom.RENDERS,
                AestheticsDefaults.contour(),
                ContourGeom.HANDLES_GROUPS
            ) { ContourGeom() }
//                .preferredCoordSystemProvider(CoordProviders.fixed(1.0))
//                    .preferredSampling(CONTOUR)
                .build()
        }

        fun contourf(): jetbrains.datalore.plot.builder.assemble.geom.GeomProvider {
            return jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.GeomProviderBuilder(
                GeomKind.CONTOURF,
//                ContourfGeom.RENDERS,
                AestheticsDefaults.contourf(),
                ContourfGeom.HANDLES_GROUPS
            ) { ContourfGeom() }
//                .preferredCoordSystemProvider(CoordProviders.fixed(1.0))
//                    .preferredSampling(CONTOURF)
                .build()
        }

        fun polygon(): jetbrains.datalore.plot.builder.assemble.geom.GeomProvider {
            return jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.GeomProviderBuilder(
                GeomKind.POLYGON,
//                PolygonGeom.RENDERS,
                AestheticsDefaults.polygon(),
                PolygonGeom.HANDLES_GROUPS
            ) { PolygonGeom() }
//                    .preferredSampling(POLYGON)
                .build()
        }

        fun map(): jetbrains.datalore.plot.builder.assemble.geom.GeomProvider {
            return jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.GeomProviderBuilder(
                GeomKind.MAP,
//                MapGeom.RENDERS,
                AestheticsDefaults.map(),
                MapGeom.HANDLES_GROUPS
            ) { MapGeom() }
//                .preferredCoordSystemProvider(CoordProviders.map())
//                    .preferredSampling(MAP)
                .build()
        }

        fun abline(): jetbrains.datalore.plot.builder.assemble.geom.GeomProvider {
            return jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.GeomProviderBuilder(
                GeomKind.AB_LINE,
//                ABLineGeom.RENDERS,
                AestheticsDefaults.abline(),
                ABLineGeom.HANDLES_GROUPS
            ) { ABLineGeom() }
//                    .preferredSampling(AB_LINE)
                .build()
        }

        fun hline(): jetbrains.datalore.plot.builder.assemble.geom.GeomProvider {
            return jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.GeomProviderBuilder(
                GeomKind.H_LINE,
//                HLineGeom.RENDERS,
                AestheticsDefaults.hline(),
                HLineGeom.HANDLES_GROUPS
            ) { HLineGeom() }
//                    .preferredSampling(H_LINE)
                .build()
        }

        fun vline(): jetbrains.datalore.plot.builder.assemble.geom.GeomProvider {
            return jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.GeomProviderBuilder(
                GeomKind.V_LINE,
//                VLineGeom.RENDERS,
                AestheticsDefaults.vline(),
                VLineGeom.HANDLES_GROUPS
            ) { VLineGeom() }
//                    .preferredSampling(V_LINE)
                .build()
        }

        fun boxplot(supplier: () -> Geom): jetbrains.datalore.plot.builder.assemble.geom.GeomProvider {
            return jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.GeomProviderBuilder(
                GeomKind.BOX_PLOT,
//                BoxplotGeom.RENDERS,
                AestheticsDefaults.boxplot(),
                BoxplotGeom.HANDLES_GROUPS,
                supplier
            )
//                    .preferredSampling(BOX_PLOT)
                .build()
        }

        fun livemap(supplier: () -> Geom, displayMode: LivemapConstants.DisplayMode, scaled: Boolean): jetbrains.datalore.plot.builder.assemble.geom.GeomProvider {
            return jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.GeomProviderBuilder(
                GeomKind.LIVE_MAP,
//                LivemapGeom.RENDERS,
                AestheticsDefaults.livemap(displayMode, scaled),
                LiveMapGeom.HANDLES_GROUPS,
                supplier
            )
                .build()
        }

        fun ribbon(): jetbrains.datalore.plot.builder.assemble.geom.GeomProvider {
            return jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.GeomProviderBuilder(
                GeomKind.RIBBON,
//                RibbonGeom.RENDERS,
                AestheticsDefaults.ribbon(),
                RibbonGeom.HANDLES_GROUPS
            ) { RibbonGeom() }
//                    .preferredSampling(RIBBON)
                .build()
        }

        fun area(): jetbrains.datalore.plot.builder.assemble.geom.GeomProvider {
            return jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.GeomProviderBuilder(
                GeomKind.AREA,
//                AreaGeom.RENDERS,
                AestheticsDefaults.area(),
                AreaGeom.HANDLES_GROUPS
            ) { AreaGeom() }
//                    .preferredSampling(AREA)
                .build()
        }

        fun density(): jetbrains.datalore.plot.builder.assemble.geom.GeomProvider {
            return jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.GeomProviderBuilder(
                GeomKind.DENSITY,
//                DensityGeom.RENDERS,
                AestheticsDefaults.density(),
                DensityGeom.HANDLES_GROUPS
            ) { DensityGeom() }
//                    .preferredSampling(DENSITY)
                .build()
        }

        fun density2d(): jetbrains.datalore.plot.builder.assemble.geom.GeomProvider {
            return jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.GeomProviderBuilder(
                GeomKind.DENSITY2D,
//                Density2dGeom.RENDERS,
                AestheticsDefaults.density2d(),
                Density2dGeom.HANDLES_GROUPS
            ) { Density2dGeom() }
//                .preferredCoordSystemProvider(CoordProviders.fixed(1.0))
//                    .preferredSampling(DENSITY2D)
                .build()
        }

        fun density2df(): jetbrains.datalore.plot.builder.assemble.geom.GeomProvider {
            return jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.GeomProviderBuilder(
                GeomKind.DENSITY2DF,
//                Density2dfGeom.RENDERS,
                AestheticsDefaults.density2df(),
                Density2dfGeom.HANDLES_GROUPS
            ) { Density2dfGeom() }
//                .preferredCoordSystemProvider(CoordProviders.fixed(1.0))
//                    .preferredSampling(DENSITY2DF)
                .build()
        }

        //        fun jitter(pos: PosProvider): GeomProvider {
        fun jitter(): jetbrains.datalore.plot.builder.assemble.geom.GeomProvider {
            return jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.GeomProviderBuilder(
                GeomKind.JITTER,
//                JitterGeom.RENDERS,
                AestheticsDefaults.jitter(),
                JitterGeom.HANDLES_GROUPS
            ) { JitterGeom() }
//                .posProvider(pos)
//                    .preferredSampling(JITTER)
                .build()
        }

        fun freqpoly(): jetbrains.datalore.plot.builder.assemble.geom.GeomProvider {
            return jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.GeomProviderBuilder(
                GeomKind.FREQPOLY,
//                FreqpolyGeom.RENDERS,
                AestheticsDefaults.freqpoly(),
                FreqpolyGeom.HANDLES_GROUPS
            ) { FreqpolyGeom() }
//                    .preferredSampling(FREQPOLY)
                .build()
        }

        fun step(supplier: () -> Geom): jetbrains.datalore.plot.builder.assemble.geom.GeomProvider {
            return jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.GeomProviderBuilder(
                GeomKind.STEP,
//                StepGeom.RENDERS,
                AestheticsDefaults.step(),
                StepGeom.HANDLES_GROUPS,
                supplier
            )
//                    .preferredSampling(STEP)
                .build()
        }

        fun rect(): jetbrains.datalore.plot.builder.assemble.geom.GeomProvider {
            return jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.GeomProviderBuilder(
                GeomKind.RECT,
//                RectGeom.RENDERS,
                AestheticsDefaults.rect(),
                RectGeom.HANDLES_GROUPS
            ) { RectGeom() }
//                .autoMappedAes(Aes.XMIN, Aes.YMIN, Aes.XMAX, Aes.YMAX)
//                    .preferredSampling(RECT)
                .build()
        }

        fun segment(supplier: () -> Geom): jetbrains.datalore.plot.builder.assemble.geom.GeomProvider {
            return jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.GeomProviderBuilder(
                GeomKind.SEGMENT,
//                SegmentGeom.RENDERS,
                AestheticsDefaults.segment(),
                SegmentGeom.HANDLES_GROUPS,
                supplier
            )
//                    .preferredSampling(SEGMENT)
                .build()
        }

        fun text(supplier: () -> Geom): jetbrains.datalore.plot.builder.assemble.geom.GeomProvider {
            return jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.GeomProviderBuilder(
                GeomKind.TEXT,
//                TextGeom.RENDERS,
                AestheticsDefaults.text(),
                TextGeom.HANDLES_GROUPS,
                supplier
            )
//                    .preferredSampling(TEXT)
                .build()
        }

        fun raster(): jetbrains.datalore.plot.builder.assemble.geom.GeomProvider {
            return jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.GeomProviderBuilder(
                GeomKind.RASTER,
//                RasterGeom.RENDERS,
                AestheticsDefaults.raster(),
                RasterGeom.HANDLES_GROUPS
            ) { RasterGeom() }
//                .preferredCoordSystemProvider(CoordProviders.fixed(1.0))
                .build()
        }

        fun image(supplier: () -> Geom): jetbrains.datalore.plot.builder.assemble.geom.GeomProvider {
            return jetbrains.datalore.plot.builder.assemble.geom.GeomProvider.GeomProviderBuilder(
                GeomKind.IMAGE,
//                ImageGeom.RENDERS,
                AestheticsDefaults.image(),
                ImageGeom.HANDLES_GROUPS,
                supplier
            )
//                .preferredCoordSystemProvider(CoordProviders.fixed(1.0))
                .build()
        }
    }
}
