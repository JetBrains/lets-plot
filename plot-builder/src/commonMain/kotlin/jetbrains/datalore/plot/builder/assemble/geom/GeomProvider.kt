/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble.geom

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Geom
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.GeomMeta
import jetbrains.datalore.plot.base.aes.AestheticsDefaults
import jetbrains.datalore.plot.base.geom.*
import jetbrains.datalore.plot.base.livemap.LiveMapOptions

abstract class GeomProvider private constructor(val geomKind: GeomKind) {

    open val preferredCoordinateSystem: jetbrains.datalore.plot.builder.coord.CoordProvider
        get() = throw IllegalStateException("No preferred coordinate system")

    fun renders(): List<Aes<*>> {
        return GeomMeta.renders(geomKind)
    }

    abstract fun createGeom(): Geom

    abstract fun aestheticsDefaults(): AestheticsDefaults

    abstract fun handlesGroups(): Boolean

    private class GeomProviderBuilder internal constructor(
        private val myKind: GeomKind,
        private val myAestheticsDefaults: AestheticsDefaults,
        private val myHandlesGroups: Boolean,
        private val myGeomSupplier: () -> Geom
    ) {
        internal fun build(): GeomProvider {
            return object : GeomProvider(myKind) {

                override fun createGeom(): Geom {
                    return myGeomSupplier()
                }

                override fun aestheticsDefaults(): AestheticsDefaults {
                    return myAestheticsDefaults
                }

                override fun handlesGroups(): Boolean {
                    return myHandlesGroups
                }
            }
        }
    }

    companion object {

        fun point(): GeomProvider {
            return point { PointGeom() }
        }

        fun point(supplier: () -> Geom): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.POINT,
                AestheticsDefaults.point(),
                PointGeom.HANDLES_GROUPS,
                supplier
            ).build()
        }

        fun path(): GeomProvider {
            return path { PathGeom() }
        }

        fun path(supplier: () -> Geom): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.PATH,
                AestheticsDefaults.path(),
                PathGeom.HANDLES_GROUPS,
                supplier
            ).build()
        }

        fun line(): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.LINE,
                AestheticsDefaults.line(),
                LineGeom.HANDLES_GROUPS
            ) { LineGeom() }.build()
        }

        fun smooth(): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.SMOOTH,
//                SmoothGeom.RENDERS,
                AestheticsDefaults.smooth(),
                SmoothGeom.HANDLES_GROUPS
            ) { SmoothGeom() }.build()
        }

        fun bar(): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.BAR,
                AestheticsDefaults.bar(),
                BarGeom.HANDLES_GROUPS
            ) { BarGeom() }.build()
        }

        fun histogram(): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.HISTOGRAM,
                AestheticsDefaults.histogram(),
                HistogramGeom.HANDLES_GROUPS
            ) { HistogramGeom() }.build()
        }

        fun tile(): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.TILE,
                AestheticsDefaults.tile(),
                TileGeom.HANDLES_GROUPS
            ) { TileGeom() }.build()
        }

        fun errorBar(): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.ERROR_BAR,
                AestheticsDefaults.errorBar(),
                ErrorBarGeom.HANDLES_GROUPS
            ) { ErrorBarGeom() }.build()
        }

        fun crossBar(supplier: () -> Geom): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.CROSS_BAR,
                AestheticsDefaults.crossBar(),
                CrossBarGeom.HANDLES_GROUPS,
                supplier
            ).build()
        }

        fun lineRange(): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.LINE_RANGE,
                AestheticsDefaults.lineRange(),
                LineRangeGeom.HANDLES_GROUPS
            ) { LineRangeGeom() }
                .build()
        }

        fun contour(): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.CONTOUR,
                AestheticsDefaults.contour(),
                ContourGeom.HANDLES_GROUPS
            ) { ContourGeom() }.build()
        }

        fun contourf(): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.CONTOURF,
                AestheticsDefaults.contourf(),
                ContourfGeom.HANDLES_GROUPS
            ) { ContourfGeom() }.build()
        }

        fun polygon(): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.POLYGON,
                AestheticsDefaults.polygon(),
                PolygonGeom.HANDLES_GROUPS
            ) { PolygonGeom() }.build()
        }

        fun map(): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.MAP,
                AestheticsDefaults.map(),
                MapGeom.HANDLES_GROUPS
            ) { MapGeom() }.build()
        }

        fun abline(): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.AB_LINE,
                AestheticsDefaults.abline(),
                ABLineGeom.HANDLES_GROUPS
            ) { ABLineGeom() }.build()
        }

        fun hline(): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.H_LINE,
                AestheticsDefaults.hline(),
                HLineGeom.HANDLES_GROUPS
            ) { HLineGeom() }.build()
        }

        fun vline(): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.V_LINE,
                AestheticsDefaults.vline(),
                VLineGeom.HANDLES_GROUPS
            ) { VLineGeom() }.build()
        }

        fun boxplot(supplier: () -> Geom): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.BOX_PLOT,
                AestheticsDefaults.boxplot(),
                BoxplotGeom.HANDLES_GROUPS,
                supplier
            ).build()
        }

        fun livemap(
            options: LiveMapOptions
        ): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.LIVE_MAP,
                AestheticsDefaults.livemap(options.displayMode, options.scaled),
                LiveMapGeom.HANDLES_GROUPS,
                myGeomSupplier = { LiveMapGeom(options.displayMode) }
            ).build()
        }

        fun ribbon(): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.RIBBON,
                AestheticsDefaults.ribbon(),
                RibbonGeom.HANDLES_GROUPS
            ) { RibbonGeom() }.build()
        }

        fun area(): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.AREA,
                AestheticsDefaults.area(),
                AreaGeom.HANDLES_GROUPS
            ) { AreaGeom() }.build()
        }

        fun density(): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.DENSITY,
                AestheticsDefaults.density(),
                DensityGeom.HANDLES_GROUPS
            ) { DensityGeom() }.build()
        }

        fun density2d(): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.DENSITY2D,
                AestheticsDefaults.density2d(),
                Density2dGeom.HANDLES_GROUPS
            ) { Density2dGeom() }.build()
        }

        fun density2df(): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.DENSITY2DF,
                AestheticsDefaults.density2df(),
                Density2dfGeom.HANDLES_GROUPS
            ) { Density2dfGeom() }.build()
        }

        fun jitter(): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.JITTER,
                AestheticsDefaults.jitter(),
                JitterGeom.HANDLES_GROUPS
            ) { JitterGeom() }.build()
        }

        fun freqpoly(): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.FREQPOLY,
                AestheticsDefaults.freqpoly(),
                FreqpolyGeom.HANDLES_GROUPS
            ) { FreqpolyGeom() }.build()
        }

        fun step(supplier: () -> Geom): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.STEP,
                AestheticsDefaults.step(),
                StepGeom.HANDLES_GROUPS,
                supplier
            ).build()
        }

        fun rect(): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.RECT,
                AestheticsDefaults.rect(),
                RectGeom.HANDLES_GROUPS
            ) { RectGeom() }.build()
        }

        fun segment(supplier: () -> Geom): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.SEGMENT,
                AestheticsDefaults.segment(),
                SegmentGeom.HANDLES_GROUPS,
                supplier
            ).build()
        }

        fun text(supplier: () -> Geom): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.TEXT,
                AestheticsDefaults.text(),
                TextGeom.HANDLES_GROUPS,
                supplier
            ).build()
        }

        fun raster(): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.RASTER,
                AestheticsDefaults.raster(),
                RasterGeom.HANDLES_GROUPS
            ) { RasterGeom() }.build()
        }

        fun image(supplier: () -> Geom): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.IMAGE,
                AestheticsDefaults.image(),
                ImageGeom.HANDLES_GROUPS,
                supplier
            ).build()
        }
    }
}
