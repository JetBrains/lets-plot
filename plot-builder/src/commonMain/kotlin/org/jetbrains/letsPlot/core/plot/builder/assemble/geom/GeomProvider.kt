/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble.geom

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.Geom
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.plot.base.geom.*

class GeomProvider internal constructor(
    val geomKind: GeomKind,
    val handlesGroups: Boolean,
    private val geomSupplier: (ctx: Context) -> Geom
) {

    fun createGeom(ctx: Context): Geom {
        return geomSupplier(ctx)
    }

    abstract class Context {
        abstract fun hasBinding(aes: Aes<*>): Boolean
        abstract fun hasConstant(aes: Aes<*>): Boolean
    }

    companion object {
        fun point(supplier: (Context) -> Geom): GeomProvider {
            return GeomProvider(
                GeomKind.POINT,
                PointGeom.HANDLES_GROUPS,
                supplier
            )
        }

        fun path(supplier: (Context) -> Geom): GeomProvider {
            return GeomProvider(
                GeomKind.PATH,
                PathGeom.HANDLES_GROUPS,
                supplier
            )
        }

        fun line(): GeomProvider {
            return GeomProvider(
                GeomKind.LINE,
                LineGeom.HANDLES_GROUPS
            ) { LineGeom() }
        }

        fun smooth(): GeomProvider {
            return GeomProvider(
                GeomKind.SMOOTH,
                SmoothGeom.HANDLES_GROUPS
            ) { SmoothGeom() }
        }

        fun bar(): GeomProvider {
            return GeomProvider(
                GeomKind.BAR,
                BarGeom.HANDLES_GROUPS
            ) { BarGeom() }
        }

        fun histogram(): GeomProvider {
            return GeomProvider(
                GeomKind.HISTOGRAM,
                HistogramGeom.HANDLES_GROUPS
            ) { HistogramGeom() }
        }

        fun dotplot(supplier: (Context) -> Geom): GeomProvider {
            return GeomProvider(
                GeomKind.DOT_PLOT,
                DotplotGeom.HANDLES_GROUPS,
                supplier
            )
        }

        fun tile(supplier: (Context) -> Geom): GeomProvider {
            return GeomProvider(
                GeomKind.TILE,
                TileGeom.HANDLES_GROUPS,
                supplier
            )
        }

        fun bin2d(): GeomProvider {
            return GeomProvider(
                GeomKind.BIN_2D,
                Bin2dGeom.HANDLES_GROUPS
            ) { Bin2dGeom() }
        }

        fun hex(supplier: (Context) -> Geom): GeomProvider {
            return GeomProvider(
                GeomKind.HEX,
                HexGeom.HANDLES_GROUPS,
                supplier
            )
        }

        fun errorBar(supplier: (Context) -> Geom): GeomProvider {
            return GeomProvider(
                GeomKind.ERROR_BAR,
                ErrorBarGeom.HANDLES_GROUPS,
                supplier
            )
        }

        fun crossBar(supplier: (Context) -> Geom): GeomProvider {
            return GeomProvider(
                GeomKind.CROSS_BAR,
                CrossBarGeom.HANDLES_GROUPS,
                supplier
            )
        }

        fun lineRange(): GeomProvider {
            return GeomProvider(
                GeomKind.LINE_RANGE,
                LineRangeGeom.HANDLES_GROUPS
            ) { LineRangeGeom() }
        }

        fun pointRange(supplier: (Context) -> Geom): GeomProvider {
            return GeomProvider(
                GeomKind.POINT_RANGE,
                PointRangeGeom.HANDLES_GROUPS,
                supplier
            )
        }

        fun contour(): GeomProvider {
            return GeomProvider(
                GeomKind.CONTOUR,
                ContourGeom.HANDLES_GROUPS
            ) { ContourGeom() }
        }

        fun contourf(): GeomProvider {
            return GeomProvider(
                GeomKind.CONTOURF,
                ContourfGeom.HANDLES_GROUPS
            ) { ContourfGeom() }
        }

        fun polygon(): GeomProvider {
            return GeomProvider(
                GeomKind.POLYGON,
                PolygonGeom.HANDLES_GROUPS
            ) { PolygonGeom() }
        }

        fun map(): GeomProvider {
            return GeomProvider(
                GeomKind.MAP,
                MapGeom.HANDLES_GROUPS
            ) { MapGeom() }
        }

        fun abline(): GeomProvider {
            return GeomProvider(
                GeomKind.AB_LINE,
                ABLineGeom.HANDLES_GROUPS
            ) { ABLineGeom() }
        }

        fun hline(): GeomProvider {
            return GeomProvider(
                GeomKind.H_LINE,
                HLineGeom.HANDLES_GROUPS
            ) { HLineGeom() }
        }

        fun vline(): GeomProvider {
            return GeomProvider(
                GeomKind.V_LINE,
                VLineGeom.HANDLES_GROUPS
            ) { VLineGeom() }
        }

        fun band(supplier: (Context) -> Geom): GeomProvider {
            return GeomProvider(
                GeomKind.BAND,
                BandGeom.HANDLES_GROUPS,
                supplier
            )
        }

        fun boxplot(supplier: (Context) -> Geom): GeomProvider {
            return GeomProvider(
                GeomKind.BOX_PLOT,
                BoxplotGeom.HANDLES_GROUPS,
                supplier
            )
        }

        fun arearidges(supplier: (Context) -> Geom): GeomProvider {
            return GeomProvider(
                GeomKind.AREA_RIDGES,
                AreaRidgesGeom.HANDLES_GROUPS,
                supplier
            )
        }

        fun violin(supplier: (Context) -> Geom): GeomProvider {
            return GeomProvider(
                GeomKind.VIOLIN,
                ViolinGeom.HANDLES_GROUPS,
                supplier
            )
        }

        fun ydotplot(supplier: (Context) -> Geom): GeomProvider {
            return GeomProvider(
                GeomKind.Y_DOT_PLOT,
                YDotplotGeom.HANDLES_GROUPS,
                supplier
            )
        }

        fun livemap(): GeomProvider {
            return GeomProvider(
                GeomKind.LIVE_MAP,
                LiveMapGeom.HANDLES_GROUPS,
            ) { LiveMapGeom() }
        }

        fun ribbon(): GeomProvider {
            return GeomProvider(
                GeomKind.RIBBON,
                RibbonGeom.HANDLES_GROUPS
            ) { RibbonGeom() }
        }

        fun area(supplier: (Context) -> Geom): GeomProvider {
            return GeomProvider(
                GeomKind.AREA,
                AreaGeom.HANDLES_GROUPS,
                supplier
            )
        }

        fun density(supplier: (Context) -> Geom): GeomProvider {
            return GeomProvider(
                GeomKind.DENSITY,
                DensityGeom.HANDLES_GROUPS,
                supplier
            )
        }

        fun density2d(): GeomProvider {
            return GeomProvider(
                GeomKind.DENSITY2D,
                Density2dGeom.HANDLES_GROUPS
            ) { Density2dGeom() }
        }

        fun density2df(): GeomProvider {
            return GeomProvider(
                GeomKind.DENSITY2DF,
                Density2dfGeom.HANDLES_GROUPS
            ) { Density2dfGeom() }
        }

        fun jitter(): GeomProvider {
            return GeomProvider(
                GeomKind.JITTER,
                JitterGeom.HANDLES_GROUPS
            ) { JitterGeom() }
        }

        fun qq(): GeomProvider {
            return GeomProvider(
                GeomKind.Q_Q,
                QQGeom.HANDLES_GROUPS
            ) { QQGeom() }
        }

        fun qq2(): GeomProvider {
            return GeomProvider(
                GeomKind.Q_Q_2,
                QQ2Geom.HANDLES_GROUPS
            ) { QQ2Geom() }
        }

        fun qqline(): GeomProvider {
            return GeomProvider(
                GeomKind.Q_Q_LINE,
                QQLineGeom.HANDLES_GROUPS
            ) { QQLineGeom() }
        }

        fun qq2line(): GeomProvider {
            return GeomProvider(
                GeomKind.Q_Q_2_LINE,
                QQ2LineGeom.HANDLES_GROUPS
            ) { QQ2LineGeom() }
        }

        fun freqpoly(): GeomProvider {
            return GeomProvider(
                GeomKind.FREQPOLY,
                FreqpolyGeom.HANDLES_GROUPS
            ) { FreqpolyGeom() }
        }

        fun step(supplier: (Context) -> Geom): GeomProvider {
            return GeomProvider(
                GeomKind.STEP,
                StepGeom.HANDLES_GROUPS,
                supplier
            )
        }

        fun rect(): GeomProvider {
            return GeomProvider(
                GeomKind.RECT,
                RectGeom.HANDLES_GROUPS
            ) { RectGeom() }
        }

        fun segment(supplier: (Context) -> Geom): GeomProvider {
            return GeomProvider(
                GeomKind.SEGMENT,
                SegmentGeom.HANDLES_GROUPS,
                supplier
            )
        }

        fun curve(supplier: (Context) -> Geom): GeomProvider {
            return GeomProvider(
                GeomKind.CURVE,
                SegmentGeom.HANDLES_GROUPS,
                supplier
            )
        }

        fun spoke(supplier: (Context) -> Geom): GeomProvider {
            return GeomProvider(
                GeomKind.SPOKE,
                LollipopGeom.HANDLES_GROUPS,
                supplier
            )
        }

        fun text(supplier: (Context) -> Geom): GeomProvider {
            return GeomProvider(
                GeomKind.TEXT,
                TextGeom.HANDLES_GROUPS,
                supplier
            )
        }

        fun label(supplier: (Context) -> Geom): GeomProvider {
            return GeomProvider(
                GeomKind.LABEL,
                TextGeom.HANDLES_GROUPS,
                supplier
            )
        }

        fun raster(): GeomProvider {
            return GeomProvider(
                GeomKind.RASTER,
                RasterGeom.HANDLES_GROUPS
            ) { RasterGeom() }
        }

        fun image(supplier: (Context) -> Geom): GeomProvider {
            return GeomProvider(
                GeomKind.IMAGE,
                ImageGeom.HANDLES_GROUPS,
                supplier
            )
        }

        fun pie(supplier: (Context) -> Geom): GeomProvider {
            return GeomProvider(
                GeomKind.PIE,
                PieGeom.HANDLES_GROUPS,
                supplier
            )
        }

        fun lollipop(supplier: (Context) -> Geom): GeomProvider {
            return GeomProvider(
                GeomKind.LOLLIPOP,
                LollipopGeom.HANDLES_GROUPS,
                supplier
            )
        }

        fun blank(): GeomProvider {
            return GeomProvider(
                GeomKind.BLANK,
                BlankGeom.HANDLES_GROUPS
            ) { BlankGeom() }
        }

    }
}
