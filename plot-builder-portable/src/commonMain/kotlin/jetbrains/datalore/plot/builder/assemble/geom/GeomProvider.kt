/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble.geom

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Geom
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.aes.AestheticsDefaults
import jetbrains.datalore.plot.base.aes.GeomTheme
import jetbrains.datalore.plot.base.geom.*

class GeomProvider internal constructor(
    val geomKind: GeomKind,
    private val aestheticsDefaults: AestheticsDefaults,
    val handlesGroups: Boolean,
    private val geomSupplier: (ctx: Context) -> Geom
) {

    fun createGeom(ctx: Context): Geom {
        return geomSupplier(ctx)
    }

    fun getAestheticsDefaults(geomTheme: GeomTheme?): AestheticsDefaults {
        return aestheticsDefaults.apply { geomTheme?.let(this::updateWith) }
    }

    abstract class Context(
        val colorByAes: Aes<Color>,
        val fillByAes: Aes<Color>,
    ) {
        abstract fun hasBinding(aes: Aes<*>): Boolean
        abstract fun hasConstant(aes: Aes<*>): Boolean
    }

    companion object {
        fun point(supplier: (Context) -> Geom): GeomProvider {
            return GeomProvider(
                GeomKind.POINT,
                AestheticsDefaults.point(),
                PointGeom.HANDLES_GROUPS,
                supplier
            )
        }

        fun path(supplier: (Context) -> Geom): GeomProvider {
            return GeomProvider(
                GeomKind.PATH,
                AestheticsDefaults.path(),
                PathGeom.HANDLES_GROUPS,
                supplier
            )
        }

        fun line(): GeomProvider {
            return GeomProvider(
                GeomKind.LINE,
                AestheticsDefaults.line(),
                LineGeom.HANDLES_GROUPS
            ) { LineGeom() }
        }

        fun smooth(): GeomProvider {
            return GeomProvider(
                GeomKind.SMOOTH,
                AestheticsDefaults.smooth(),
                SmoothGeom.HANDLES_GROUPS
            ) { SmoothGeom() }
        }

        fun bar(): GeomProvider {
            return GeomProvider(
                GeomKind.BAR,
                AestheticsDefaults.bar(),
                BarGeom.HANDLES_GROUPS
            ) { BarGeom() }
        }

        fun histogram(): GeomProvider {
            return GeomProvider(
                GeomKind.HISTOGRAM,
                AestheticsDefaults.histogram(),
                HistogramGeom.HANDLES_GROUPS
            ) { HistogramGeom() }
        }

        fun dotplot(supplier: (Context) -> Geom): GeomProvider {
            return GeomProvider(
                GeomKind.DOT_PLOT,
                AestheticsDefaults.dotplot(),
                DotplotGeom.HANDLES_GROUPS,
                supplier
            )
        }

        fun tile(): GeomProvider {
            return GeomProvider(
                GeomKind.TILE,
                AestheticsDefaults.tile(),
                TileGeom.HANDLES_GROUPS
            ) { TileGeom() }
        }

        fun bin2d(): GeomProvider {
            return GeomProvider(
                GeomKind.BIN_2D,
                AestheticsDefaults.bin2d(),
                Bin2dGeom.HANDLES_GROUPS
            ) { Bin2dGeom() }
        }

        fun errorBar(supplier: (Context) -> Geom): GeomProvider {
            return GeomProvider(
                GeomKind.ERROR_BAR,
                AestheticsDefaults.errorBar(),
                ErrorBarGeom.HANDLES_GROUPS,
                supplier
            )
        }

        fun crossBar(supplier: (Context) -> Geom): GeomProvider {
            return GeomProvider(
                GeomKind.CROSS_BAR,
                AestheticsDefaults.crossBar(),
                CrossBarGeom.HANDLES_GROUPS,
                supplier
            )
        }

        fun lineRange(): GeomProvider {
            return GeomProvider(
                GeomKind.LINE_RANGE,
                AestheticsDefaults.lineRange(),
                LineRangeGeom.HANDLES_GROUPS
            ) { LineRangeGeom() }
        }

        fun pointRange(supplier: (Context) -> Geom): GeomProvider {
            return GeomProvider(
                GeomKind.POINT_RANGE,
                AestheticsDefaults.pointRange(),
                PointRangeGeom.HANDLES_GROUPS,
                supplier
            )
        }

        fun contour(): GeomProvider {
            return GeomProvider(
                GeomKind.CONTOUR,
                AestheticsDefaults.contour(),
                ContourGeom.HANDLES_GROUPS
            ) { ContourGeom() }
        }

        fun contourf(): GeomProvider {
            return GeomProvider(
                GeomKind.CONTOURF,
                AestheticsDefaults.contourf(),
                ContourfGeom.HANDLES_GROUPS
            ) { ContourfGeom() }
        }

        fun polygon(): GeomProvider {
            return GeomProvider(
                GeomKind.POLYGON,
                AestheticsDefaults.polygon(),
                PolygonGeom.HANDLES_GROUPS
            ) { PolygonGeom() }
        }

        fun map(): GeomProvider {
            return GeomProvider(
                GeomKind.MAP,
                AestheticsDefaults.map(),
                MapGeom.HANDLES_GROUPS
            ) { MapGeom() }
        }

        fun abline(): GeomProvider {
            return GeomProvider(
                GeomKind.AB_LINE,
                AestheticsDefaults.abline(),
                ABLineGeom.HANDLES_GROUPS
            ) { ABLineGeom() }
        }

        fun hline(): GeomProvider {
            return GeomProvider(
                GeomKind.H_LINE,
                AestheticsDefaults.hline(),
                HLineGeom.HANDLES_GROUPS
            ) { HLineGeom() }
        }

        fun vline(): GeomProvider {
            return GeomProvider(
                GeomKind.V_LINE,
                AestheticsDefaults.vline(),
                VLineGeom.HANDLES_GROUPS
            ) { VLineGeom() }
        }

        fun boxplot(supplier: (Context) -> Geom): GeomProvider {
            return GeomProvider(
                GeomKind.BOX_PLOT,
                AestheticsDefaults.boxplot(),
                BoxplotGeom.HANDLES_GROUPS,
                supplier
            )
        }

        fun arearidges(supplier: (Context) -> Geom): GeomProvider {
            return GeomProvider(
                GeomKind.AREA_RIDGES,
                AestheticsDefaults.areaRidges(),
                AreaRidgesGeom.HANDLES_GROUPS,
                supplier
            )
        }

        fun violin(supplier: (Context) -> Geom): GeomProvider {
            return GeomProvider(
                GeomKind.VIOLIN,
                AestheticsDefaults.violin(),
                ViolinGeom.HANDLES_GROUPS,
                supplier
            )
        }

        fun ydotplot(supplier: (Context) -> Geom): GeomProvider {
            return GeomProvider(
                GeomKind.Y_DOT_PLOT,
                AestheticsDefaults.ydotplot(),
                YDotplotGeom.HANDLES_GROUPS,
                supplier
            )
        }

        fun livemap(): GeomProvider {
            return GeomProvider(
                GeomKind.LIVE_MAP,
                AestheticsDefaults.livemap(),
                LiveMapGeom.HANDLES_GROUPS,
            ) { LiveMapGeom() }
        }

        fun ribbon(): GeomProvider {
            return GeomProvider(
                GeomKind.RIBBON,
                AestheticsDefaults.ribbon(),
                RibbonGeom.HANDLES_GROUPS
            ) { RibbonGeom() }
        }

        fun area(): GeomProvider {
            return GeomProvider(
                GeomKind.AREA,
                AestheticsDefaults.area(),
                AreaGeom.HANDLES_GROUPS
            ) { AreaGeom() }
        }

        fun density(supplier: (Context) -> Geom): GeomProvider {
            return GeomProvider(
                GeomKind.DENSITY,
                AestheticsDefaults.density(),
                DensityGeom.HANDLES_GROUPS,
                supplier
            )
        }

        fun density2d(): GeomProvider {
            return GeomProvider(
                GeomKind.DENSITY2D,
                AestheticsDefaults.density2d(),
                Density2dGeom.HANDLES_GROUPS
            ) { Density2dGeom() }
        }

        fun density2df(): GeomProvider {
            return GeomProvider(
                GeomKind.DENSITY2DF,
                AestheticsDefaults.density2df(),
                Density2dfGeom.HANDLES_GROUPS
            ) { Density2dfGeom() }
        }

        fun jitter(): GeomProvider {
            return GeomProvider(
                GeomKind.JITTER,
                AestheticsDefaults.jitter(),
                JitterGeom.HANDLES_GROUPS
            ) { JitterGeom() }
        }

        fun qq(): GeomProvider {
            return GeomProvider(
                GeomKind.Q_Q,
                AestheticsDefaults.qq(),
                QQGeom.HANDLES_GROUPS
            ) { QQGeom() }
        }

        fun qq2(): GeomProvider {
            return GeomProvider(
                GeomKind.Q_Q_2,
                AestheticsDefaults.qq2(),
                QQ2Geom.HANDLES_GROUPS
            ) { QQ2Geom() }
        }

        fun qqline(): GeomProvider {
            return GeomProvider(
                GeomKind.Q_Q_LINE,
                AestheticsDefaults.qq_line(),
                QQLineGeom.HANDLES_GROUPS
            ) { QQLineGeom() }
        }

        fun qq2line(): GeomProvider {
            return GeomProvider(
                GeomKind.Q_Q_2_LINE,
                AestheticsDefaults.qq2_line(),
                QQ2LineGeom.HANDLES_GROUPS
            ) { QQ2LineGeom() }
        }

        fun freqpoly(): GeomProvider {
            return GeomProvider(
                GeomKind.FREQPOLY,
                AestheticsDefaults.freqpoly(),
                FreqpolyGeom.HANDLES_GROUPS
            ) { FreqpolyGeom() }
        }

        fun step(supplier: (Context) -> Geom): GeomProvider {
            return GeomProvider(
                GeomKind.STEP,
                AestheticsDefaults.step(),
                StepGeom.HANDLES_GROUPS,
                supplier
            )
        }

        fun rect(): GeomProvider {
            return GeomProvider(
                GeomKind.RECT,
                AestheticsDefaults.rect(),
                RectGeom.HANDLES_GROUPS
            ) { RectGeom() }
        }

        fun segment(supplier: (Context) -> Geom): GeomProvider {
            return GeomProvider(
                GeomKind.SEGMENT,
                AestheticsDefaults.segment(),
                SegmentGeom.HANDLES_GROUPS,
                supplier
            )
        }

        fun text(supplier: (Context) -> Geom): GeomProvider {
            return GeomProvider(
                GeomKind.TEXT,
                AestheticsDefaults.text(),
                TextGeom.HANDLES_GROUPS,
                supplier
            )
        }

        fun label(supplier: (Context) -> Geom): GeomProvider {
            return GeomProvider(
                GeomKind.LABEL,
                AestheticsDefaults.label(),
                TextGeom.HANDLES_GROUPS,
                supplier
            )
        }

        fun raster(): GeomProvider {
            return GeomProvider(
                GeomKind.RASTER,
                AestheticsDefaults.raster(),
                RasterGeom.HANDLES_GROUPS
            ) { RasterGeom() }
        }

        fun image(supplier: (Context) -> Geom): GeomProvider {
            return GeomProvider(
                GeomKind.IMAGE,
                AestheticsDefaults.image(),
                ImageGeom.HANDLES_GROUPS,
                supplier
            )
        }

        fun pie(supplier: (Context) -> Geom): GeomProvider {
            return GeomProvider(
                GeomKind.PIE,
                AestheticsDefaults.pie(),
                PieGeom.HANDLES_GROUPS,
                supplier
            )
        }

        fun lollipop(supplier: (Context) -> Geom): GeomProvider {
            return GeomProvider(
                GeomKind.LOLLIPOP,
                AestheticsDefaults.lollipop(),
                LollipopGeom.HANDLES_GROUPS,
                supplier
            )
        }
    }
}
