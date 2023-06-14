/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.assemble.geom

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.Geom
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.base.aes.AestheticsDefaults
import jetbrains.datalore.plot.base.aes.GeomTheme
import jetbrains.datalore.plot.base.geom.*

class GeomProvider internal constructor(
    val geomKind: GeomKind,
    private val ctx: Context,
    val aestheticsDefaults: AestheticsDefaults,
    val handlesGroups: Boolean,
    private val geomSupplier: (ctx: Context) -> Geom
) {

    fun createGeom(): Geom {
        return geomSupplier(ctx)
    }

    abstract class Context(
        // private val colorByAes: Aes<Color>,
        // private val fillByAes: Aes<Color>,
    ) {
        abstract fun hasBinding(aes: Aes<*>): Boolean
        abstract fun hasConstant(aes: Aes<*>): Boolean
        abstract fun geomTheme(geomKind: GeomKind): GeomTheme
    }

    companion object {
        fun point(supplier: (Context) -> Geom): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.POINT,
                    ctx,
                    AestheticsDefaults.point(ctx.geomTheme(GeomKind.POINT)),
                    PointGeom.HANDLES_GROUPS,
                    supplier
                )
            }
        }

        fun path(supplier: (Context) -> Geom): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.PATH,
                    ctx,
                    AestheticsDefaults.path(ctx.geomTheme(GeomKind.PATH)),
                    PathGeom.HANDLES_GROUPS,
                    supplier
                )
            }
        }

        fun line(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.LINE,
                    ctx,
                    AestheticsDefaults.line(ctx.geomTheme(GeomKind.LINE)),
                    LineGeom.HANDLES_GROUPS
                ) { LineGeom() }
            }
        }

        fun smooth(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.SMOOTH,
                    ctx,
                    AestheticsDefaults.smooth(ctx.geomTheme(GeomKind.SMOOTH)),
                    SmoothGeom.HANDLES_GROUPS
                ) { SmoothGeom() }
            }
        }

        fun bar(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.BAR,
                    ctx,
                    AestheticsDefaults.bar(ctx.geomTheme(GeomKind.BAR)),
                    BarGeom.HANDLES_GROUPS
                ) { BarGeom() }
            }
        }

        fun histogram(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.HISTOGRAM,
                    ctx,
                    AestheticsDefaults.histogram(ctx.geomTheme(GeomKind.HISTOGRAM)),
                    HistogramGeom.HANDLES_GROUPS
                ) { HistogramGeom() }
            }
        }

        fun dotplot(supplier: (Context) -> Geom): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.DOT_PLOT,
                    ctx,
                    AestheticsDefaults.dotplot(ctx.geomTheme(GeomKind.DOT_PLOT)),
                    DotplotGeom.HANDLES_GROUPS,
                    supplier
                )
            }
        }

        fun tile(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.TILE,
                    ctx,
                    AestheticsDefaults.tile(ctx.geomTheme(GeomKind.TILE)),
                    TileGeom.HANDLES_GROUPS
                ) { TileGeom() }
            }
        }

        fun bin2d(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.BIN_2D,
                    ctx,
                    AestheticsDefaults.bin2d(ctx.geomTheme(GeomKind.BIN_2D)),
                    Bin2dGeom.HANDLES_GROUPS
                ) { Bin2dGeom() }
            }
        }

        fun errorBar(supplier: (Context) -> Geom): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.ERROR_BAR,
                    ctx,
                    AestheticsDefaults.errorBar(ctx.geomTheme(GeomKind.ERROR_BAR)),
                    ErrorBarGeom.HANDLES_GROUPS,
                    supplier
                )
            }
        }

        fun crossBar(supplier: (Context) -> Geom): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.CROSS_BAR,
                    ctx,
                    AestheticsDefaults.crossBar(ctx.geomTheme(GeomKind.CROSS_BAR)),
                    CrossBarGeom.HANDLES_GROUPS,
                    supplier
                )
            }
        }

        fun lineRange(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.LINE_RANGE,
                    ctx,
                    AestheticsDefaults.lineRange(ctx.geomTheme(GeomKind.LINE_RANGE)),
                    LineRangeGeom.HANDLES_GROUPS
                ) { LineRangeGeom() }
            }
        }

        fun pointRange(supplier: (Context) -> Geom): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.POINT_RANGE,
                    ctx,
                    AestheticsDefaults.pointRange(ctx.geomTheme(GeomKind.POINT_RANGE)),
                    PointRangeGeom.HANDLES_GROUPS,
                    supplier
                )
            }
        }

        fun contour(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.CONTOUR,
                    ctx,
                    AestheticsDefaults.contour(ctx.geomTheme(GeomKind.CONTOUR)),
                    ContourGeom.HANDLES_GROUPS
                ) { ContourGeom() }
            }
        }

        fun contourf(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.CONTOURF,
                    ctx,
                    AestheticsDefaults.contourf(ctx.geomTheme(GeomKind.CONTOURF)),
                    ContourfGeom.HANDLES_GROUPS
                ) { ContourfGeom() }
            }
        }

        fun polygon(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.POLYGON,
                    ctx,
                    AestheticsDefaults.polygon(ctx.geomTheme(GeomKind.POLYGON)),
                    PolygonGeom.HANDLES_GROUPS
                ) { PolygonGeom() }
            }
        }

        fun map(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.MAP,
                    ctx,
                    AestheticsDefaults.map(ctx.geomTheme(GeomKind.MAP)),
                    MapGeom.HANDLES_GROUPS
                ) { MapGeom() }
            }
        }

        fun abline(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.AB_LINE,
                    ctx,
                    AestheticsDefaults.abline(ctx.geomTheme(GeomKind.AB_LINE)),
                    ABLineGeom.HANDLES_GROUPS
                ) { ABLineGeom() }
            }
        }

        fun hline(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.H_LINE,
                    ctx,
                    AestheticsDefaults.hline(ctx.geomTheme(GeomKind.H_LINE)),
                    HLineGeom.HANDLES_GROUPS
                ) { HLineGeom() }
            }
        }

        fun vline(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.V_LINE,
                    ctx,
                    AestheticsDefaults.vline(ctx.geomTheme(GeomKind.V_LINE)),
                    VLineGeom.HANDLES_GROUPS
                ) { VLineGeom() }
            }
        }

        fun boxplot(supplier: (Context) -> Geom): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.BOX_PLOT,
                    ctx,
                    AestheticsDefaults.boxplot(ctx.geomTheme(GeomKind.BOX_PLOT)),
                    BoxplotGeom.HANDLES_GROUPS,
                    supplier
                )
            }
        }

        fun arearidges(supplier: (Context) -> Geom): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.AREA_RIDGES,
                    ctx,
                    AestheticsDefaults.areaRidges(ctx.geomTheme(GeomKind.AREA_RIDGES)),
                    AreaRidgesGeom.HANDLES_GROUPS,
                    supplier
                )
            }
        }

        fun violin(supplier: (Context) -> Geom): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.VIOLIN,
                    ctx,
                    AestheticsDefaults.violin(ctx.geomTheme(GeomKind.VIOLIN)),
                    ViolinGeom.HANDLES_GROUPS,
                    supplier
                )
            }
        }

        fun ydotplot(supplier: (Context) -> Geom): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.Y_DOT_PLOT,
                    ctx,
                    AestheticsDefaults.ydotplot(ctx.geomTheme(GeomKind.Y_DOT_PLOT)),
                    YDotplotGeom.HANDLES_GROUPS,
                    supplier
                )
            }
        }

        fun livemap(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.LIVE_MAP,
                    ctx,
                    AestheticsDefaults.livemap(ctx.geomTheme(GeomKind.LIVE_MAP)),
                    LiveMapGeom.HANDLES_GROUPS
                ) { LiveMapGeom() }
            }
        }

        fun ribbon(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.RIBBON,
                    ctx,
                    AestheticsDefaults.ribbon(ctx.geomTheme(GeomKind.RIBBON)),
                    RibbonGeom.HANDLES_GROUPS
                ) { RibbonGeom() }
            }
        }

        fun area(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.AREA,
                    ctx,
                    AestheticsDefaults.area(ctx.geomTheme(GeomKind.AREA)),
                    AreaGeom.HANDLES_GROUPS
                ) { AreaGeom() }
            }
        }

        fun density(supplier: (Context) -> Geom): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.DENSITY,
                    ctx,
                    AestheticsDefaults.density(ctx.geomTheme(GeomKind.DENSITY)),
                    DensityGeom.HANDLES_GROUPS,
                    supplier
                )
            }
        }

        fun density2d(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.DENSITY2D,
                    ctx,
                    AestheticsDefaults.density2d(ctx.geomTheme(GeomKind.DENSITY2D)),
                    Density2dGeom.HANDLES_GROUPS
                ) { Density2dGeom() }
            }
        }

        fun density2df(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.DENSITY2DF,
                    ctx,
                    AestheticsDefaults.density2df(ctx.geomTheme(GeomKind.DENSITY2DF)),
                    Density2dfGeom.HANDLES_GROUPS
                ) { Density2dfGeom() }
            }
        }

        fun jitter(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.JITTER,
                    ctx,
                    AestheticsDefaults.jitter(ctx.geomTheme(GeomKind.JITTER)),
                    JitterGeom.HANDLES_GROUPS
                ) { JitterGeom() }
            }
        }

        fun qq(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.Q_Q,
                    ctx,
                    AestheticsDefaults.qq(ctx.geomTheme(GeomKind.Q_Q)),
                    QQGeom.HANDLES_GROUPS
                ) { QQGeom() }
            }
        }

        fun qq2(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.Q_Q_2,
                    ctx,
                    AestheticsDefaults.qq2(ctx.geomTheme(GeomKind.Q_Q_2)),
                    QQ2Geom.HANDLES_GROUPS
                ) { QQ2Geom() }
            }
        }

        fun qqline(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.Q_Q_LINE,
                    ctx,
                    AestheticsDefaults.qq_line(ctx.geomTheme(GeomKind.Q_Q_LINE)),
                    QQLineGeom.HANDLES_GROUPS
                ) { QQLineGeom() }
            }
        }

        fun qq2line(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.Q_Q_2_LINE,
                    ctx,
                    AestheticsDefaults.qq2_line(ctx.geomTheme(GeomKind.Q_Q_2_LINE)),
                    QQ2LineGeom.HANDLES_GROUPS
                ) { QQ2LineGeom() }
            }
        }

        fun freqpoly(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.FREQPOLY,
                    ctx,
                    AestheticsDefaults.freqpoly(ctx.geomTheme(GeomKind.FREQPOLY)),
                    FreqpolyGeom.HANDLES_GROUPS
                ) { FreqpolyGeom() }
            }
        }

        fun step(supplier: (Context) -> Geom): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.STEP,
                    ctx,
                    AestheticsDefaults.step(ctx.geomTheme(GeomKind.STEP)),
                    StepGeom.HANDLES_GROUPS,
                    supplier
                )
            }
        }

        fun rect(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.RECT,
                    ctx,
                    AestheticsDefaults.rect(ctx.geomTheme(GeomKind.RECT)),
                    RectGeom.HANDLES_GROUPS
                ) { RectGeom() }
            }
        }

        fun segment(supplier: (Context) -> Geom): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.SEGMENT,
                    ctx,
                    AestheticsDefaults.segment(ctx.geomTheme(GeomKind.SEGMENT)),
                    SegmentGeom.HANDLES_GROUPS,
                    supplier
                )
            }
        }

        fun text(supplier: (Context) -> Geom): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.TEXT,
                    ctx,
                    AestheticsDefaults.text(ctx.geomTheme(GeomKind.TEXT)),
                    TextGeom.HANDLES_GROUPS,
                    supplier
                )
            }
        }

        fun label(supplier: (Context) -> Geom): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.LABEL,
                    ctx,
                    AestheticsDefaults.label(ctx.geomTheme(GeomKind.LABEL)),
                    TextGeom.HANDLES_GROUPS,
                    supplier
                )
            }
        }

        fun raster(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.RASTER,
                    ctx,
                    AestheticsDefaults.raster(ctx.geomTheme(GeomKind.RASTER)),
                    RasterGeom.HANDLES_GROUPS
                ) { RasterGeom() }
            }
        }

        fun image(supplier: (Context) -> Geom): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.IMAGE,
                    ctx,
                    AestheticsDefaults.image(ctx.geomTheme(GeomKind.IMAGE)),
                    ImageGeom.HANDLES_GROUPS,
                    supplier
                )
            }
        }

        fun pie(supplier: (Context) -> Geom): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.PIE,
                    ctx,
                    AestheticsDefaults.pie(ctx.geomTheme(GeomKind.PIE)),
                    PieGeom.HANDLES_GROUPS,
                    supplier
                )
            }
        }

        fun lollipop(supplier: (Context) -> Geom): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.LOLLIPOP,
                    ctx,
                    AestheticsDefaults.lollipop(ctx.geomTheme(GeomKind.LOLLIPOP)),
                    LollipopGeom.HANDLES_GROUPS,
                    supplier
                )
            }
        }
    }
}
