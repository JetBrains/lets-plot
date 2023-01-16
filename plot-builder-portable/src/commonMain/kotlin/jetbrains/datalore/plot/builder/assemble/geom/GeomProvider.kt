/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble.geom

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Geom
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.GeomMeta
import jetbrains.datalore.plot.base.aes.AestheticsDefaults
import jetbrains.datalore.plot.base.geom.*
import jetbrains.datalore.plot.base.livemap.LivemapConstants
import jetbrains.datalore.plot.builder.coord.CoordProvider

abstract class GeomProvider private constructor(val geomKind: GeomKind) {

    open val preferredCoordinateSystem: CoordProvider
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

        fun dotplot(supplier: () -> Geom): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.DOT_PLOT,
                AestheticsDefaults.dotplot(),
                DotplotGeom.HANDLES_GROUPS,
                supplier
            ).build()
        }

        fun tile(): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.TILE,
                AestheticsDefaults.tile(),
                TileGeom.HANDLES_GROUPS
            ) { TileGeom() }.build()
        }

        fun bin2d(): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.BIN_2D,
                AestheticsDefaults.bin2d(),
                Bin2dGeom.HANDLES_GROUPS
            ) { Bin2dGeom() }.build()
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
            ) { LineRangeGeom() }.build()
        }

        fun pointRange(supplier: () -> Geom): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.POINT_RANGE,
                AestheticsDefaults.pointRange(),
                PointRangeGeom.HANDLES_GROUPS,
                supplier
            ).build()
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

        fun arearidges(supplier: () -> Geom): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.AREA_RIDGES,
                AestheticsDefaults.areaRidges(),
                AreaRidgesGeom.HANDLES_GROUPS,
                supplier
            ).build()
        }

        fun violin(supplier: () -> Geom): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.VIOLIN,
                AestheticsDefaults.violin(),
                ViolinGeom.HANDLES_GROUPS,
                supplier
            ).build()
        }

        fun ydotplot(supplier: () -> Geom): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.Y_DOT_PLOT,
                AestheticsDefaults.ydotplot(),
                YDotplotGeom.HANDLES_GROUPS,
                supplier
            ).build()
        }

        fun livemap(): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.LIVE_MAP,
                AestheticsDefaults.livemap(),
                LiveMapGeom.HANDLES_GROUPS,
            ) { LiveMapGeom() }.build()
        }

        fun ribbon(): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.RIBBON,
                AestheticsDefaults.ribbon(),
                RibbonGeom.HANDLES_GROUPS
            ) { RibbonGeom() }.build()
        }

        fun area(supplier: () -> Geom): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.AREA,
                AestheticsDefaults.area(),
                AreaGeom.HANDLES_GROUPS,
                supplier
            ).build()
        }

        fun density(supplier: () -> Geom): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.DENSITY,
                AestheticsDefaults.density(),
                DensityGeom.HANDLES_GROUPS,
                supplier
            ).build()
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

        fun qq(): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.Q_Q,
                AestheticsDefaults.qq(),
                QQGeom.HANDLES_GROUPS
            ) { QQGeom() }.build()
        }

        fun qq2(): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.Q_Q_2,
                AestheticsDefaults.qq2(),
                QQ2Geom.HANDLES_GROUPS
            ) { QQ2Geom() }.build()
        }

        fun qqline(): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.Q_Q_LINE,
                AestheticsDefaults.qq_line(),
                QQLineGeom.HANDLES_GROUPS
            ) { QQLineGeom() }.build()
        }

        fun qq2line(): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.Q_Q_2_LINE,
                AestheticsDefaults.qq2_line(),
                QQ2LineGeom.HANDLES_GROUPS
            ) { QQ2LineGeom() }.build()
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

        fun label(supplier: () -> Geom): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.LABEL,
                AestheticsDefaults.label(),
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

        fun pie(supplier: () -> Geom): GeomProvider {
            return GeomProviderBuilder(
                GeomKind.PIE,
                AestheticsDefaults.pie(),
                PieGeom.HANDLES_GROUPS,
                supplier
            ).build()
        }
    }
}
