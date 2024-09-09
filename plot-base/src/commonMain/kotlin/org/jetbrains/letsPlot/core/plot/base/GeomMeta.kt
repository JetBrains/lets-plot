/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base

import org.jetbrains.letsPlot.commons.values.Color
import kotlin.native.concurrent.ThreadLocal

// In Kotlin Native objects a frozen by default. Annotate with `ThreadLocal` to unfreeze.
// See:  https://github.com/JetBrains/kotlin-native/blob/master/IMMUTABILITY.md
// Required mutations:
//      -   `renderedAesByGeom` map
@ThreadLocal
object GeomMeta {
    private val renderedAesByGeom = HashMap<GeomKind, List<Aes<*>>>()

    private fun renders(geomKind: GeomKind): List<Aes<*>> {
        if (!renderedAesByGeom.containsKey(geomKind)) {
            renderedAesByGeom[geomKind] =
                renderedAesList(geomKind)
        }
        return renderedAesByGeom[geomKind]!!
    }

    fun renders(
        geomKind: GeomKind,
        actualColorAes: Aes<Color>,
        actualFillAes: Aes<Color>,
        exclude: List<Aes<*>> = emptyList()
    ): List<Aes<*>> {
        return (renders(geomKind) - exclude).map {
            when (it) {
                Aes.COLOR -> actualColorAes
                Aes.FILL -> actualFillAes
                else -> it
            }
        }
    }

    private val POINT = listOf(
        Aes.X, Aes.Y,
        Aes.SIZE,
        Aes.STROKE,
        Aes.COLOR,
        Aes.FILL,
        Aes.ALPHA,
        Aes.SHAPE,
        Aes.ANGLE,
        Aes.MAP_ID
    )

    private val PATH = listOf(
        Aes.X, Aes.Y,
        Aes.SIZE, // path width
        Aes.LINETYPE,
        Aes.COLOR,
        Aes.ALPHA,
        Aes.SPEED,
        Aes.FLOW
    )

    private val POLYGON = listOf(
        Aes.X, Aes.Y,
        Aes.SIZE, // path width
        Aes.LINETYPE,
        Aes.COLOR,
        Aes.FILL,
        Aes.ALPHA,
        Aes.MAP_ID
    )

    private val AREA = listOf(
        Aes.X, Aes.Y,
        Aes.QUANTILE,
        Aes.SIZE,
        Aes.LINETYPE,
        Aes.COLOR,
        Aes.FILL,
        Aes.ALPHA
    )


    private fun renderedAesList(geomKind: GeomKind): List<Aes<*>> {
        return when (geomKind) {
            GeomKind.POINT,
            GeomKind.BLANK-> POINT
            GeomKind.PATH -> PATH
            GeomKind.LINE -> PATH

            GeomKind.SMOOTH -> listOf(
                Aes.X, Aes.Y,
                Aes.YMIN, Aes.YMAX,

                Aes.SIZE, // path width
                Aes.LINETYPE,
                Aes.COLOR,
                Aes.FILL,
                Aes.ALPHA
            )

            GeomKind.BAR -> listOf(
                Aes.X,
                Aes.Y,
                Aes.COLOR,
                Aes.FILL,
                Aes.ALPHA,
                Aes.WIDTH,
                Aes.SIZE
            )

            GeomKind.HISTOGRAM -> listOf(
                Aes.X, Aes.Y,
                Aes.COLOR,
                Aes.FILL,
                Aes.ALPHA,
                //Aes.WEIGHT,    // ToDo: this is actually handled by 'stat' (bin,count)
                Aes.WIDTH,
                Aes.SIZE
            )

            GeomKind.DOT_PLOT -> listOf(
                Aes.X,
                Aes.BINWIDTH,
                Aes.STACKSIZE,
                Aes.COLOR,
                Aes.FILL,
                Aes.ALPHA,
                Aes.STROKE
            )

            GeomKind.TILE,
            GeomKind.BIN_2D -> listOf(
                Aes.X, Aes.Y,
                Aes.WIDTH,
                Aes.HEIGHT,
                Aes.ALPHA,
                Aes.COLOR,
                Aes.FILL,
                Aes.LINETYPE,
                Aes.SIZE
            )

            GeomKind.ERROR_BAR -> listOf(
                // vertical representation
                Aes.X,
                Aes.YMIN, Aes.YMAX,
                Aes.WIDTH,

                // horizontal
                Aes.Y,
                Aes.XMIN, Aes.XMAX,
                Aes.HEIGHT,

                Aes.ALPHA,
                Aes.COLOR,
                Aes.LINETYPE,
                Aes.SIZE
            )

            GeomKind.CROSS_BAR -> listOf(
                Aes.X, Aes.Y,
                // vertical representation
                Aes.YMIN, Aes.YMAX,
                Aes.WIDTH,
                // horizontal
                Aes.XMIN, Aes.XMAX,
                Aes.HEIGHT,

                Aes.ALPHA,
                Aes.COLOR,
                Aes.FILL,
                Aes.LINETYPE,
                Aes.SIZE
            )

            GeomKind.LINE_RANGE -> listOf(
                // vertical representation
                Aes.X,
                Aes.YMIN, Aes.YMAX,
                // horizontal
                Aes.Y,
                Aes.XMIN, Aes.XMAX,

                Aes.ALPHA,
                Aes.COLOR,
                Aes.LINETYPE,
                Aes.SIZE
            )

            GeomKind.POINT_RANGE -> listOf(
                Aes.X, Aes.Y,
                // vertical representation
                Aes.YMIN, Aes.YMAX,
                // horizontal
                Aes.XMIN, Aes.XMAX,

                Aes.ALPHA,
                Aes.COLOR,
                Aes.FILL,
                Aes.LINETYPE,
                Aes.SHAPE,
                Aes.SIZE,
                Aes.STROKE,
                Aes.LINEWIDTH
            )

            GeomKind.CONTOUR -> PATH
            GeomKind.CONTOURF -> POLYGON
            GeomKind.POLYGON -> POLYGON
            GeomKind.MAP -> listOf(
                // auto-wired to 'x' or 'long' and to 'y' or 'lat'
                Aes.X, Aes.Y,
                Aes.SIZE, // path width
                Aes.LINETYPE,
                Aes.COLOR,
                Aes.FILL,
                Aes.ALPHA
            )

            GeomKind.AB_LINE -> listOf(
                Aes.INTERCEPT, Aes.SLOPE,
                Aes.SIZE, // path width
                Aes.LINETYPE,
                Aes.COLOR,
                Aes.ALPHA
            )

            GeomKind.H_LINE -> listOf(
                Aes.YINTERCEPT,
                Aes.SIZE, // path width
                Aes.LINETYPE,
                Aes.COLOR,
                Aes.ALPHA
            )

            GeomKind.V_LINE -> listOf(
                Aes.XINTERCEPT,
                Aes.SIZE, // path width
                Aes.LINETYPE,
                Aes.COLOR,
                Aes.ALPHA
            )

            GeomKind.BAND -> listOf(
                // vertical representation
                Aes.XMIN, Aes.XMAX,
                // horizontal representation
                Aes.YMIN, Aes.YMAX,

                Aes.ALPHA,
                Aes.COLOR,
                Aes.FILL,
                Aes.LINETYPE,
                Aes.SIZE
            )

            GeomKind.BOX_PLOT -> listOf(
                Aes.LOWER,
                Aes.MIDDLE,
                Aes.UPPER,

                Aes.X,
                Aes.YMAX,
                Aes.YMIN,

                Aes.ALPHA,
                Aes.COLOR,
                Aes.FILL,
                Aes.LINETYPE,
                Aes.SHAPE,
                Aes.SIZE, // line width
                Aes.WIDTH
            )

            GeomKind.AREA_RIDGES -> listOf(
                Aes.X,
                Aes.Y,
                Aes.HEIGHT,
                Aes.QUANTILE,

                Aes.ALPHA,
                Aes.COLOR,
                Aes.FILL,
                Aes.LINETYPE,
                Aes.SIZE
            )

            GeomKind.VIOLIN -> listOf(
                Aes.X,
                Aes.Y,
                Aes.VIOLINWIDTH,
                Aes.QUANTILE,

                Aes.ALPHA,
                Aes.COLOR,
                Aes.FILL,
                Aes.LINETYPE,
                Aes.SIZE,
                Aes.WIDTH
            )

            GeomKind.Y_DOT_PLOT -> listOf(
                Aes.X,
                Aes.Y,
                Aes.BINWIDTH,
                Aes.STACKSIZE,

                Aes.COLOR,
                Aes.FILL,
                Aes.ALPHA,
                Aes.STROKE
            )

            GeomKind.RIBBON -> listOf(
                //vertical representation
                Aes.X,
                Aes.YMIN, Aes.YMAX,
                //horizontal
                Aes.Y,
                Aes.XMIN, Aes.XMAX,

                Aes.SIZE,
                Aes.LINETYPE,
                Aes.COLOR,
                Aes.FILL,
                Aes.ALPHA
            )

            GeomKind.AREA -> AREA
            GeomKind.DENSITY -> AREA
            GeomKind.DENSITY2D -> PATH
            GeomKind.DENSITY2DF -> POLYGON
            GeomKind.JITTER -> POINT
            GeomKind.Q_Q -> listOf(
                Aes.X, Aes.Y,
                Aes.SAMPLE,
                Aes.SIZE,
                Aes.STROKE,
                Aes.COLOR,
                Aes.FILL,
                Aes.ALPHA,
                Aes.SHAPE
            )

            GeomKind.Q_Q_2 -> POINT
            GeomKind.Q_Q_LINE -> listOf(
                Aes.X, Aes.Y,
                Aes.SAMPLE,
                Aes.SIZE,
                Aes.LINETYPE,
                Aes.COLOR,
                Aes.ALPHA
            )

            GeomKind.Q_Q_2_LINE -> PATH
            GeomKind.FREQPOLY -> PATH
            GeomKind.STEP -> PATH
            GeomKind.RECT -> listOf(
                Aes.XMIN, Aes.XMAX,
                Aes.YMIN, Aes.YMAX,
                Aes.SIZE,
                Aes.LINETYPE,
                Aes.COLOR,
                Aes.FILL,
                Aes.ALPHA
            )

            GeomKind.SEGMENT -> listOf(
                Aes.X, Aes.Y,
                Aes.XEND, Aes.YEND,
                Aes.SIZE,
                Aes.LINETYPE,
                Aes.COLOR,
                Aes.ALPHA,
                Aes.SPEED,
                Aes.FLOW,
                Aes.SIZE_START,
                Aes.SIZE_END,
                Aes.STROKE_START,
                Aes.STROKE_END
            )

            GeomKind.CURVE -> listOf(
                Aes.X, Aes.Y,
                Aes.XEND, Aes.YEND,
                Aes.SIZE,
                Aes.LINETYPE,
                Aes.COLOR,
                Aes.ALPHA,
                Aes.SIZE_START,
                Aes.SIZE_END,
                Aes.STROKE_START,
                Aes.STROKE_END
            )

            GeomKind.SPOKE -> listOf(
                Aes.X, Aes.Y,
                Aes.ANGLE, Aes.RADIUS,
                Aes.SIZE,
                Aes.LINETYPE,
                Aes.COLOR,
                Aes.ALPHA,
            )

            GeomKind.TEXT -> listOf(
                Aes.X, Aes.Y,
                Aes.SIZE,
                Aes.COLOR,
                Aes.ALPHA,
                Aes.LABEL,
                Aes.FAMILY,
                Aes.FONTFACE,
                Aes.HJUST,
                Aes.VJUST,
                Aes.ANGLE,
                Aes.LINEHEIGHT
            )

            GeomKind.LABEL -> listOf(
                Aes.X, Aes.Y,
                Aes.SIZE,
                Aes.COLOR,
                Aes.FILL,
                Aes.ALPHA,
                Aes.LABEL,
                Aes.FAMILY,
                Aes.FONTFACE,
                Aes.HJUST,
                Aes.VJUST,
                Aes.ANGLE,
                Aes.LINEHEIGHT
            )

            GeomKind.LIVE_MAP -> listOf(
                Aes.ALPHA,
                Aes.COLOR,
                Aes.FILL,
                Aes.SIZE,
                Aes.SHAPE,
                Aes.FRAME,
                Aes.X,
                Aes.Y
            )

            GeomKind.RASTER -> listOf(
                Aes.X, Aes.Y,
                Aes.WIDTH, // not rendered but required for correct x aes range computation
                Aes.HEIGHT, // -- the same --
                Aes.FILL,
                Aes.ALPHA
            )

            GeomKind.IMAGE -> listOf(
                Aes.XMIN,
                Aes.XMAX,
                Aes.YMIN,
                Aes.YMAX,
                Aes.COLOR  // not rendered but necessary for color legend to appear.
            )

            GeomKind.PIE -> listOf(
                Aes.X,
                Aes.Y,
                Aes.SLICE,
                Aes.EXPLODE,
                Aes.SIZE,
                Aes.FILL,
                Aes.ALPHA,
                Aes.COLOR,
                Aes.STROKE
            )

            GeomKind.LOLLIPOP -> listOf(
                Aes.X, Aes.Y,
                Aes.SIZE,
                Aes.STROKE,
                Aes.LINEWIDTH,
                Aes.COLOR,
                Aes.FILL,
                Aes.ALPHA,
                Aes.SHAPE,
                Aes.LINETYPE
            )
        }
    }
}