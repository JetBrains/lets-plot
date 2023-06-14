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
    val aestheticsDefaults: AestheticsDefaults,
    val handlesGroups: Boolean,
    private val geomSupplier: (ctx: Context) -> Geom
) {

    fun createGeom(ctx: Context): Geom {
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
                    AestheticsDefaults(ctx.geomTheme(GeomKind.POINT)),
                    PointGeom.HANDLES_GROUPS,
                    supplier
                )
            }
        }

        fun path(supplier: (Context) -> Geom): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.PATH,
                    AestheticsDefaults(ctx.geomTheme(GeomKind.PATH)),
                    PathGeom.HANDLES_GROUPS,
                    supplier
                )
            }
        }

        fun line(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.LINE,
                    AestheticsDefaults(ctx.geomTheme(GeomKind.LINE)),
                    LineGeom.HANDLES_GROUPS
                ) { LineGeom() }
            }
        }

        fun smooth(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.SMOOTH,
                    AestheticsDefaults(ctx.geomTheme(GeomKind.SMOOTH)),
                    SmoothGeom.HANDLES_GROUPS
                ) { SmoothGeom() }
            }
        }

        fun bar(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.BAR,
                    AestheticsDefaults(ctx.geomTheme(GeomKind.BAR)),
                    BarGeom.HANDLES_GROUPS
                ) { BarGeom() }
            }
        }

        fun histogram(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.HISTOGRAM,
                    AestheticsDefaults(ctx.geomTheme(GeomKind.HISTOGRAM)),
                    HistogramGeom.HANDLES_GROUPS
                ) { HistogramGeom() }
            }
        }

        fun dotplot(supplier: (Context) -> Geom): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.DOT_PLOT,
                    AestheticsDefaults(ctx.geomTheme(GeomKind.DOT_PLOT)),
                    DotplotGeom.HANDLES_GROUPS,
                    supplier
                )
            }
        }

        fun tile(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.TILE,
                    AestheticsDefaults(ctx.geomTheme(GeomKind.TILE)),
                    TileGeom.HANDLES_GROUPS
                ) { TileGeom() }
            }
        }

        fun bin2d(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.BIN_2D,
                    AestheticsDefaults(ctx.geomTheme(GeomKind.BIN_2D)),
                    Bin2dGeom.HANDLES_GROUPS
                ) { Bin2dGeom() }
            }
        }

        fun errorBar(supplier: (Context) -> Geom): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.ERROR_BAR,
                    AestheticsDefaults(ctx.geomTheme(GeomKind.ERROR_BAR)),
                    ErrorBarGeom.HANDLES_GROUPS,
                    supplier
                )
            }
        }

        fun crossBar(supplier: (Context) -> Geom): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.CROSS_BAR,
                    AestheticsDefaults(ctx.geomTheme(GeomKind.CROSS_BAR)),
                    CrossBarGeom.HANDLES_GROUPS,
                    supplier
                )
            }
        }

        fun lineRange(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.LINE_RANGE,
                    AestheticsDefaults(ctx.geomTheme(GeomKind.LINE_RANGE)),
                    LineRangeGeom.HANDLES_GROUPS
                ) { LineRangeGeom() }
            }
        }

        fun pointRange(supplier: (Context) -> Geom): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.POINT_RANGE,
                    AestheticsDefaults(ctx.geomTheme(GeomKind.POINT_RANGE)),
                    PointRangeGeom.HANDLES_GROUPS,
                    supplier
                )
            }
        }

        fun contour(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.CONTOUR,
                    AestheticsDefaults(ctx.geomTheme(GeomKind.CONTOUR)),
                    ContourGeom.HANDLES_GROUPS
                ) { ContourGeom() }
            }
        }

        fun contourf(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.CONTOURF,
                    AestheticsDefaults(ctx.geomTheme(GeomKind.CONTOURF)),
                    ContourfGeom.HANDLES_GROUPS
                ) { ContourfGeom() }
            }
        }

        fun polygon(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.POLYGON,
                    AestheticsDefaults(ctx.geomTheme(GeomKind.POLYGON)),
                    PolygonGeom.HANDLES_GROUPS
                ) { PolygonGeom() }
            }
        }

        fun map(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.MAP,
                    AestheticsDefaults(ctx.geomTheme(GeomKind.MAP)),
                    MapGeom.HANDLES_GROUPS
                ) { MapGeom() }
            }
        }

        fun abline(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.AB_LINE,
                    AestheticsDefaults(ctx.geomTheme(GeomKind.AB_LINE)),
                    ABLineGeom.HANDLES_GROUPS
                ) { ABLineGeom() }
            }
        }

        fun hline(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.H_LINE,
                    AestheticsDefaults(ctx.geomTheme(GeomKind.H_LINE)),
                    HLineGeom.HANDLES_GROUPS
                ) { HLineGeom() }
            }
        }

        fun vline(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.V_LINE,
                    AestheticsDefaults(ctx.geomTheme(GeomKind.V_LINE)),
                    VLineGeom.HANDLES_GROUPS
                ) { VLineGeom() }
            }
        }

        fun boxplot(supplier: (Context) -> Geom): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.BOX_PLOT,
                    AestheticsDefaults(ctx.geomTheme(GeomKind.BOX_PLOT)),
                    BoxplotGeom.HANDLES_GROUPS,
                    supplier
                )
            }
        }

        fun arearidges(supplier: (Context) -> Geom): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.AREA_RIDGES,
                    AestheticsDefaults(ctx.geomTheme(GeomKind.AREA_RIDGES)),
                    AreaRidgesGeom.HANDLES_GROUPS,
                    supplier
                )
            }
        }

        fun violin(supplier: (Context) -> Geom): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.VIOLIN,
                    AestheticsDefaults(ctx.geomTheme(GeomKind.VIOLIN)),
                    ViolinGeom.HANDLES_GROUPS,
                    supplier
                )
            }
        }

        fun ydotplot(supplier: (Context) -> Geom): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.Y_DOT_PLOT,
                    AestheticsDefaults(ctx.geomTheme(GeomKind.Y_DOT_PLOT)),
                    YDotplotGeom.HANDLES_GROUPS,
                    supplier
                )
            }
        }

        fun livemap(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.LIVE_MAP,
                    AestheticsDefaults(ctx.geomTheme(GeomKind.LIVE_MAP)),
                    LiveMapGeom.HANDLES_GROUPS,
                ) { LiveMapGeom() }
            }
        }

        fun ribbon(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.RIBBON,
                    AestheticsDefaults(ctx.geomTheme(GeomKind.RIBBON)),
                    RibbonGeom.HANDLES_GROUPS
                ) { RibbonGeom() }
            }
        }

        fun area(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.AREA,
                    AestheticsDefaults(ctx.geomTheme(GeomKind.AREA)),
                    AreaGeom.HANDLES_GROUPS
                ) { AreaGeom() }
            }
        }

        fun density(supplier: (Context) -> Geom): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.DENSITY,
                    AestheticsDefaults(ctx.geomTheme(GeomKind.DENSITY)),
                    DensityGeom.HANDLES_GROUPS,
                    supplier
                )
            }
        }

        fun density2d(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.DENSITY2D,
                    AestheticsDefaults(ctx.geomTheme(GeomKind.DENSITY2D)),
                    Density2dGeom.HANDLES_GROUPS
                ) { Density2dGeom() }
            }
        }

        fun density2df(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.DENSITY2DF,
                    AestheticsDefaults(ctx.geomTheme(GeomKind.DENSITY2DF)),
                    Density2dfGeom.HANDLES_GROUPS
                ) { Density2dfGeom() }
            }
        }

        fun jitter(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.JITTER,
                    AestheticsDefaults(ctx.geomTheme(GeomKind.JITTER)),
                    JitterGeom.HANDLES_GROUPS
                ) { JitterGeom() }
            }
        }

        fun qq(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.Q_Q,
                    AestheticsDefaults(ctx.geomTheme(GeomKind.Q_Q)),
                    QQGeom.HANDLES_GROUPS
                ) { QQGeom() }
            }
        }

        fun qq2(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.Q_Q_2,
                    AestheticsDefaults(ctx.geomTheme(GeomKind.Q_Q_2)),
                    QQ2Geom.HANDLES_GROUPS
                ) { QQ2Geom() }
            }
        }

        fun qqline(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.Q_Q_LINE,
                    AestheticsDefaults(ctx.geomTheme(GeomKind.Q_Q_LINE)),
                    QQLineGeom.HANDLES_GROUPS
                ) { QQLineGeom() }
            }
        }

        fun qq2line(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.Q_Q_2_LINE,
                    AestheticsDefaults(ctx.geomTheme(GeomKind.Q_Q_2_LINE)),
                    QQ2LineGeom.HANDLES_GROUPS
                ) { QQ2LineGeom() }
            }
        }

        fun freqpoly(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.FREQPOLY,
                    AestheticsDefaults(ctx.geomTheme(GeomKind.FREQPOLY)),
                    FreqpolyGeom.HANDLES_GROUPS
                ) { FreqpolyGeom() }
            }
        }

        fun step(supplier: (Context) -> Geom): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.STEP,
                    AestheticsDefaults(ctx.geomTheme(GeomKind.STEP)),
                    StepGeom.HANDLES_GROUPS,
                    supplier
                )
            }
        }

        fun rect(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.RECT,
                    AestheticsDefaults(ctx.geomTheme(GeomKind.RECT)),
                    RectGeom.HANDLES_GROUPS
                ) { RectGeom() }
            }
        }

        fun segment(supplier: (Context) -> Geom): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.SEGMENT,
                    AestheticsDefaults(ctx.geomTheme(GeomKind.SEGMENT)),
                    SegmentGeom.HANDLES_GROUPS,
                    supplier
                )
            }
        }

        fun text(supplier: (Context) -> Geom): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.TEXT,
                    AestheticsDefaults(ctx.geomTheme(GeomKind.TEXT)),
                    TextGeom.HANDLES_GROUPS,
                    supplier
                )
            }
        }

        fun label(supplier: (Context) -> Geom): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.LABEL,
                    AestheticsDefaults(ctx.geomTheme(GeomKind.LABEL)),
                    TextGeom.HANDLES_GROUPS,
                    supplier
                )
            }
        }

        fun raster(): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.RASTER,
                    AestheticsDefaults(ctx.geomTheme(GeomKind.RASTER)),
                    RasterGeom.HANDLES_GROUPS
                ) { RasterGeom() }
            }
        }

        fun image(supplier: (Context) -> Geom): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.IMAGE,
                    AestheticsDefaults(ctx.geomTheme(GeomKind.IMAGE)),
                    ImageGeom.HANDLES_GROUPS,
                    supplier
                )
            }
        }

        fun pie(supplier: (Context) -> Geom): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.PIE,
                    AestheticsDefaults(ctx.geomTheme(GeomKind.PIE)),
                    PieGeom.HANDLES_GROUPS,
                    supplier
                )
            }
        }

        fun lollipop(supplier: (Context) -> Geom): (Context) -> GeomProvider {
            return { ctx ->
                GeomProvider(
                    GeomKind.LOLLIPOP,
                    AestheticsDefaults(ctx.geomTheme(GeomKind.LOLLIPOP)),
                    LollipopGeom.HANDLES_GROUPS,
                    supplier
                )
            }
        }
    }
}
