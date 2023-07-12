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
    private val renderedAesByGeom = HashMap<GeomKind, List<org.jetbrains.letsPlot.core.plot.base.Aes<*>>>()

    private fun renders(geomKind: GeomKind): List<org.jetbrains.letsPlot.core.plot.base.Aes<*>> {
        if (!renderedAesByGeom.containsKey(geomKind)) {
            renderedAesByGeom[geomKind] =
                renderedAesList(geomKind)
        }
        return renderedAesByGeom[geomKind]!!
    }

    fun renders(
        geomKind: GeomKind,
        actualColorAes: org.jetbrains.letsPlot.core.plot.base.Aes<Color>,
        actualFillAes: org.jetbrains.letsPlot.core.plot.base.Aes<Color>,
        exclude: List<org.jetbrains.letsPlot.core.plot.base.Aes<*>> = emptyList()
    ): List<org.jetbrains.letsPlot.core.plot.base.Aes<*>> {
        return (renders(geomKind) - exclude).map {
            when (it) {
                org.jetbrains.letsPlot.core.plot.base.Aes.COLOR -> actualColorAes
                org.jetbrains.letsPlot.core.plot.base.Aes.FILL -> actualFillAes
                else -> it
            }
        }
    }

    private val POINT = listOf(
        org.jetbrains.letsPlot.core.plot.base.Aes.X, org.jetbrains.letsPlot.core.plot.base.Aes.Y,
        org.jetbrains.letsPlot.core.plot.base.Aes.SIZE,
        org.jetbrains.letsPlot.core.plot.base.Aes.STROKE,
        org.jetbrains.letsPlot.core.plot.base.Aes.COLOR,
        org.jetbrains.letsPlot.core.plot.base.Aes.FILL,
        org.jetbrains.letsPlot.core.plot.base.Aes.ALPHA,
        org.jetbrains.letsPlot.core.plot.base.Aes.SHAPE,
        org.jetbrains.letsPlot.core.plot.base.Aes.MAP_ID
    )

    private val PATH = listOf(
        org.jetbrains.letsPlot.core.plot.base.Aes.X, org.jetbrains.letsPlot.core.plot.base.Aes.Y,
        org.jetbrains.letsPlot.core.plot.base.Aes.SIZE, // path width
        org.jetbrains.letsPlot.core.plot.base.Aes.LINETYPE,
        org.jetbrains.letsPlot.core.plot.base.Aes.COLOR,
        org.jetbrains.letsPlot.core.plot.base.Aes.ALPHA,
        org.jetbrains.letsPlot.core.plot.base.Aes.SPEED,
        org.jetbrains.letsPlot.core.plot.base.Aes.FLOW
    )

    private val POLYGON = listOf(
        org.jetbrains.letsPlot.core.plot.base.Aes.X, org.jetbrains.letsPlot.core.plot.base.Aes.Y,
        org.jetbrains.letsPlot.core.plot.base.Aes.SIZE, // path width
        org.jetbrains.letsPlot.core.plot.base.Aes.LINETYPE,
        org.jetbrains.letsPlot.core.plot.base.Aes.COLOR,
        org.jetbrains.letsPlot.core.plot.base.Aes.FILL,
        org.jetbrains.letsPlot.core.plot.base.Aes.ALPHA,
        org.jetbrains.letsPlot.core.plot.base.Aes.MAP_ID
    )

    private val AREA = listOf(
        org.jetbrains.letsPlot.core.plot.base.Aes.X, org.jetbrains.letsPlot.core.plot.base.Aes.Y,
        org.jetbrains.letsPlot.core.plot.base.Aes.QUANTILE,
        org.jetbrains.letsPlot.core.plot.base.Aes.SIZE,
        org.jetbrains.letsPlot.core.plot.base.Aes.LINETYPE,
        org.jetbrains.letsPlot.core.plot.base.Aes.COLOR,
        org.jetbrains.letsPlot.core.plot.base.Aes.FILL,
        org.jetbrains.letsPlot.core.plot.base.Aes.ALPHA
    )


    private fun renderedAesList(geomKind: GeomKind): List<org.jetbrains.letsPlot.core.plot.base.Aes<*>> {
        return when (geomKind) {
            GeomKind.POINT -> POINT
            GeomKind.PATH -> PATH
            GeomKind.LINE -> PATH

            GeomKind.SMOOTH -> listOf(
                org.jetbrains.letsPlot.core.plot.base.Aes.X, org.jetbrains.letsPlot.core.plot.base.Aes.Y,
                org.jetbrains.letsPlot.core.plot.base.Aes.YMIN, org.jetbrains.letsPlot.core.plot.base.Aes.YMAX,

                org.jetbrains.letsPlot.core.plot.base.Aes.SIZE, // path width
                org.jetbrains.letsPlot.core.plot.base.Aes.LINETYPE,
                org.jetbrains.letsPlot.core.plot.base.Aes.COLOR,
                org.jetbrains.letsPlot.core.plot.base.Aes.FILL,
                org.jetbrains.letsPlot.core.plot.base.Aes.ALPHA
            )

            GeomKind.BAR -> listOf(
                org.jetbrains.letsPlot.core.plot.base.Aes.X,
                org.jetbrains.letsPlot.core.plot.base.Aes.Y,
                org.jetbrains.letsPlot.core.plot.base.Aes.COLOR,
                org.jetbrains.letsPlot.core.plot.base.Aes.FILL,
                org.jetbrains.letsPlot.core.plot.base.Aes.ALPHA,
                org.jetbrains.letsPlot.core.plot.base.Aes.WIDTH,
                org.jetbrains.letsPlot.core.plot.base.Aes.SIZE
            )

            GeomKind.HISTOGRAM -> listOf(
                org.jetbrains.letsPlot.core.plot.base.Aes.X, org.jetbrains.letsPlot.core.plot.base.Aes.Y,
                org.jetbrains.letsPlot.core.plot.base.Aes.COLOR,
                org.jetbrains.letsPlot.core.plot.base.Aes.FILL,
                org.jetbrains.letsPlot.core.plot.base.Aes.ALPHA,
                //Aes.WEIGHT,    // ToDo: this is actually handled by 'stat' (bin,count)
                org.jetbrains.letsPlot.core.plot.base.Aes.WIDTH,
                org.jetbrains.letsPlot.core.plot.base.Aes.SIZE
            )

            GeomKind.DOT_PLOT -> listOf(
                org.jetbrains.letsPlot.core.plot.base.Aes.X,
                org.jetbrains.letsPlot.core.plot.base.Aes.BINWIDTH,
                org.jetbrains.letsPlot.core.plot.base.Aes.STACKSIZE,
                org.jetbrains.letsPlot.core.plot.base.Aes.COLOR,
                org.jetbrains.letsPlot.core.plot.base.Aes.FILL,
                org.jetbrains.letsPlot.core.plot.base.Aes.ALPHA,
                org.jetbrains.letsPlot.core.plot.base.Aes.STROKE
            )

            GeomKind.TILE,
            GeomKind.BIN_2D -> listOf(
                org.jetbrains.letsPlot.core.plot.base.Aes.X, org.jetbrains.letsPlot.core.plot.base.Aes.Y,
                org.jetbrains.letsPlot.core.plot.base.Aes.WIDTH,
                org.jetbrains.letsPlot.core.plot.base.Aes.HEIGHT,
                org.jetbrains.letsPlot.core.plot.base.Aes.ALPHA,
                org.jetbrains.letsPlot.core.plot.base.Aes.COLOR,
                org.jetbrains.letsPlot.core.plot.base.Aes.FILL,
                org.jetbrains.letsPlot.core.plot.base.Aes.LINETYPE,
                org.jetbrains.letsPlot.core.plot.base.Aes.SIZE
            )

            GeomKind.ERROR_BAR -> listOf(
                // vertical representation
                org.jetbrains.letsPlot.core.plot.base.Aes.X,
                org.jetbrains.letsPlot.core.plot.base.Aes.YMIN, org.jetbrains.letsPlot.core.plot.base.Aes.YMAX,
                org.jetbrains.letsPlot.core.plot.base.Aes.WIDTH,

                // horizontal
                org.jetbrains.letsPlot.core.plot.base.Aes.Y,
                org.jetbrains.letsPlot.core.plot.base.Aes.XMIN, org.jetbrains.letsPlot.core.plot.base.Aes.XMAX,
                org.jetbrains.letsPlot.core.plot.base.Aes.HEIGHT,

                org.jetbrains.letsPlot.core.plot.base.Aes.ALPHA,
                org.jetbrains.letsPlot.core.plot.base.Aes.COLOR,
                org.jetbrains.letsPlot.core.plot.base.Aes.LINETYPE,
                org.jetbrains.letsPlot.core.plot.base.Aes.SIZE
            )

            GeomKind.CROSS_BAR -> listOf(
                org.jetbrains.letsPlot.core.plot.base.Aes.X,
                org.jetbrains.letsPlot.core.plot.base.Aes.YMIN, org.jetbrains.letsPlot.core.plot.base.Aes.YMAX, org.jetbrains.letsPlot.core.plot.base.Aes.MIDDLE,
                org.jetbrains.letsPlot.core.plot.base.Aes.WIDTH,

                org.jetbrains.letsPlot.core.plot.base.Aes.ALPHA,
                org.jetbrains.letsPlot.core.plot.base.Aes.COLOR,
                org.jetbrains.letsPlot.core.plot.base.Aes.FILL,
                org.jetbrains.letsPlot.core.plot.base.Aes.LINETYPE,
                org.jetbrains.letsPlot.core.plot.base.Aes.SIZE
            )

            GeomKind.LINE_RANGE -> listOf(
                org.jetbrains.letsPlot.core.plot.base.Aes.X,
                org.jetbrains.letsPlot.core.plot.base.Aes.YMIN, org.jetbrains.letsPlot.core.plot.base.Aes.YMAX,
                org.jetbrains.letsPlot.core.plot.base.Aes.ALPHA,
                org.jetbrains.letsPlot.core.plot.base.Aes.COLOR,
                org.jetbrains.letsPlot.core.plot.base.Aes.LINETYPE,
                org.jetbrains.letsPlot.core.plot.base.Aes.SIZE
            )

            GeomKind.POINT_RANGE -> listOf(
                org.jetbrains.letsPlot.core.plot.base.Aes.X, org.jetbrains.letsPlot.core.plot.base.Aes.Y,
                org.jetbrains.letsPlot.core.plot.base.Aes.YMIN, org.jetbrains.letsPlot.core.plot.base.Aes.YMAX,
                org.jetbrains.letsPlot.core.plot.base.Aes.ALPHA,
                org.jetbrains.letsPlot.core.plot.base.Aes.COLOR,
                org.jetbrains.letsPlot.core.plot.base.Aes.FILL,
                org.jetbrains.letsPlot.core.plot.base.Aes.LINETYPE,
                org.jetbrains.letsPlot.core.plot.base.Aes.SHAPE,
                org.jetbrains.letsPlot.core.plot.base.Aes.SIZE,
                org.jetbrains.letsPlot.core.plot.base.Aes.STROKE,
                org.jetbrains.letsPlot.core.plot.base.Aes.LINEWIDTH
            )

            GeomKind.CONTOUR -> PATH
            GeomKind.CONTOURF -> POLYGON
            GeomKind.POLYGON -> POLYGON
            GeomKind.MAP -> listOf(
                // auto-wired to 'x' or 'long' and to 'y' or 'lat'
                org.jetbrains.letsPlot.core.plot.base.Aes.X, org.jetbrains.letsPlot.core.plot.base.Aes.Y,
                org.jetbrains.letsPlot.core.plot.base.Aes.SIZE, // path width
                org.jetbrains.letsPlot.core.plot.base.Aes.LINETYPE,
                org.jetbrains.letsPlot.core.plot.base.Aes.COLOR,
                org.jetbrains.letsPlot.core.plot.base.Aes.FILL,
                org.jetbrains.letsPlot.core.plot.base.Aes.ALPHA
            )

            GeomKind.AB_LINE -> listOf(
                org.jetbrains.letsPlot.core.plot.base.Aes.INTERCEPT, org.jetbrains.letsPlot.core.plot.base.Aes.SLOPE,
                org.jetbrains.letsPlot.core.plot.base.Aes.SIZE, // path width
                org.jetbrains.letsPlot.core.plot.base.Aes.LINETYPE,
                org.jetbrains.letsPlot.core.plot.base.Aes.COLOR,
                org.jetbrains.letsPlot.core.plot.base.Aes.ALPHA
            )

            GeomKind.H_LINE -> listOf(
                org.jetbrains.letsPlot.core.plot.base.Aes.YINTERCEPT,
                org.jetbrains.letsPlot.core.plot.base.Aes.SIZE, // path width
                org.jetbrains.letsPlot.core.plot.base.Aes.LINETYPE,
                org.jetbrains.letsPlot.core.plot.base.Aes.COLOR,
                org.jetbrains.letsPlot.core.plot.base.Aes.ALPHA
            )

            GeomKind.V_LINE -> listOf(
                org.jetbrains.letsPlot.core.plot.base.Aes.XINTERCEPT,
                org.jetbrains.letsPlot.core.plot.base.Aes.SIZE, // path width
                org.jetbrains.letsPlot.core.plot.base.Aes.LINETYPE,
                org.jetbrains.letsPlot.core.plot.base.Aes.COLOR,
                org.jetbrains.letsPlot.core.plot.base.Aes.ALPHA
            )

            GeomKind.BOX_PLOT -> listOf(
                org.jetbrains.letsPlot.core.plot.base.Aes.LOWER,
                org.jetbrains.letsPlot.core.plot.base.Aes.MIDDLE,
                org.jetbrains.letsPlot.core.plot.base.Aes.UPPER,

                org.jetbrains.letsPlot.core.plot.base.Aes.X,
                org.jetbrains.letsPlot.core.plot.base.Aes.YMAX,
                org.jetbrains.letsPlot.core.plot.base.Aes.YMIN,

                org.jetbrains.letsPlot.core.plot.base.Aes.ALPHA,
                org.jetbrains.letsPlot.core.plot.base.Aes.COLOR,
                org.jetbrains.letsPlot.core.plot.base.Aes.FILL,
                org.jetbrains.letsPlot.core.plot.base.Aes.LINETYPE,
                org.jetbrains.letsPlot.core.plot.base.Aes.SHAPE,
                org.jetbrains.letsPlot.core.plot.base.Aes.SIZE, // line width
                org.jetbrains.letsPlot.core.plot.base.Aes.WIDTH
            )

            GeomKind.AREA_RIDGES -> listOf(
                org.jetbrains.letsPlot.core.plot.base.Aes.X,
                org.jetbrains.letsPlot.core.plot.base.Aes.Y,
                org.jetbrains.letsPlot.core.plot.base.Aes.HEIGHT,
                org.jetbrains.letsPlot.core.plot.base.Aes.QUANTILE,

                org.jetbrains.letsPlot.core.plot.base.Aes.ALPHA,
                org.jetbrains.letsPlot.core.plot.base.Aes.COLOR,
                org.jetbrains.letsPlot.core.plot.base.Aes.FILL,
                org.jetbrains.letsPlot.core.plot.base.Aes.LINETYPE,
                org.jetbrains.letsPlot.core.plot.base.Aes.SIZE
            )

            GeomKind.VIOLIN -> listOf(
                org.jetbrains.letsPlot.core.plot.base.Aes.X,
                org.jetbrains.letsPlot.core.plot.base.Aes.Y,
                org.jetbrains.letsPlot.core.plot.base.Aes.VIOLINWIDTH,
                org.jetbrains.letsPlot.core.plot.base.Aes.QUANTILE,

                org.jetbrains.letsPlot.core.plot.base.Aes.ALPHA,
                org.jetbrains.letsPlot.core.plot.base.Aes.COLOR,
                org.jetbrains.letsPlot.core.plot.base.Aes.FILL,
                org.jetbrains.letsPlot.core.plot.base.Aes.LINETYPE,
                org.jetbrains.letsPlot.core.plot.base.Aes.SIZE,
                org.jetbrains.letsPlot.core.plot.base.Aes.WIDTH
            )

            GeomKind.Y_DOT_PLOT -> listOf(
                org.jetbrains.letsPlot.core.plot.base.Aes.X,
                org.jetbrains.letsPlot.core.plot.base.Aes.Y,
                org.jetbrains.letsPlot.core.plot.base.Aes.BINWIDTH,
                org.jetbrains.letsPlot.core.plot.base.Aes.STACKSIZE,

                org.jetbrains.letsPlot.core.plot.base.Aes.COLOR,
                org.jetbrains.letsPlot.core.plot.base.Aes.FILL,
                org.jetbrains.letsPlot.core.plot.base.Aes.ALPHA,
                org.jetbrains.letsPlot.core.plot.base.Aes.STROKE
            )

            GeomKind.RIBBON -> listOf(
                org.jetbrains.letsPlot.core.plot.base.Aes.X,
                org.jetbrains.letsPlot.core.plot.base.Aes.YMIN, org.jetbrains.letsPlot.core.plot.base.Aes.YMAX,
                org.jetbrains.letsPlot.core.plot.base.Aes.SIZE,
                org.jetbrains.letsPlot.core.plot.base.Aes.LINETYPE,
                org.jetbrains.letsPlot.core.plot.base.Aes.COLOR,
                org.jetbrains.letsPlot.core.plot.base.Aes.FILL,
                org.jetbrains.letsPlot.core.plot.base.Aes.ALPHA
            )

            GeomKind.AREA -> AREA
            GeomKind.DENSITY -> AREA
            GeomKind.DENSITY2D -> PATH
            GeomKind.DENSITY2DF -> POLYGON
            GeomKind.JITTER -> POINT
            GeomKind.Q_Q -> listOf(
                org.jetbrains.letsPlot.core.plot.base.Aes.X, org.jetbrains.letsPlot.core.plot.base.Aes.Y,
                org.jetbrains.letsPlot.core.plot.base.Aes.SAMPLE,
                org.jetbrains.letsPlot.core.plot.base.Aes.SIZE,
                org.jetbrains.letsPlot.core.plot.base.Aes.STROKE,
                org.jetbrains.letsPlot.core.plot.base.Aes.COLOR,
                org.jetbrains.letsPlot.core.plot.base.Aes.FILL,
                org.jetbrains.letsPlot.core.plot.base.Aes.ALPHA,
                org.jetbrains.letsPlot.core.plot.base.Aes.SHAPE
            )

            GeomKind.Q_Q_2 -> POINT
            GeomKind.Q_Q_LINE -> listOf(
                org.jetbrains.letsPlot.core.plot.base.Aes.X, org.jetbrains.letsPlot.core.plot.base.Aes.Y,
                org.jetbrains.letsPlot.core.plot.base.Aes.SAMPLE,
                org.jetbrains.letsPlot.core.plot.base.Aes.SIZE,
                org.jetbrains.letsPlot.core.plot.base.Aes.LINETYPE,
                org.jetbrains.letsPlot.core.plot.base.Aes.COLOR,
                org.jetbrains.letsPlot.core.plot.base.Aes.ALPHA
            )

            GeomKind.Q_Q_2_LINE -> PATH
            GeomKind.FREQPOLY -> PATH
            GeomKind.STEP -> PATH
            GeomKind.RECT -> listOf(
                org.jetbrains.letsPlot.core.plot.base.Aes.XMIN, org.jetbrains.letsPlot.core.plot.base.Aes.XMAX,
                org.jetbrains.letsPlot.core.plot.base.Aes.YMIN, org.jetbrains.letsPlot.core.plot.base.Aes.YMAX,
                org.jetbrains.letsPlot.core.plot.base.Aes.SIZE,
                org.jetbrains.letsPlot.core.plot.base.Aes.LINETYPE,
                org.jetbrains.letsPlot.core.plot.base.Aes.COLOR,
                org.jetbrains.letsPlot.core.plot.base.Aes.FILL,
                org.jetbrains.letsPlot.core.plot.base.Aes.ALPHA
            )

            GeomKind.SEGMENT -> listOf(
                org.jetbrains.letsPlot.core.plot.base.Aes.X, org.jetbrains.letsPlot.core.plot.base.Aes.Y,
                org.jetbrains.letsPlot.core.plot.base.Aes.XEND, org.jetbrains.letsPlot.core.plot.base.Aes.YEND,
                org.jetbrains.letsPlot.core.plot.base.Aes.SIZE,
                org.jetbrains.letsPlot.core.plot.base.Aes.LINETYPE,
                org.jetbrains.letsPlot.core.plot.base.Aes.COLOR,
                org.jetbrains.letsPlot.core.plot.base.Aes.ALPHA,
                org.jetbrains.letsPlot.core.plot.base.Aes.SPEED,
                org.jetbrains.letsPlot.core.plot.base.Aes.FLOW
            )

            GeomKind.TEXT -> listOf(
                org.jetbrains.letsPlot.core.plot.base.Aes.X, org.jetbrains.letsPlot.core.plot.base.Aes.Y,
                org.jetbrains.letsPlot.core.plot.base.Aes.SIZE,
                org.jetbrains.letsPlot.core.plot.base.Aes.COLOR,
                org.jetbrains.letsPlot.core.plot.base.Aes.ALPHA,
                org.jetbrains.letsPlot.core.plot.base.Aes.LABEL,
                org.jetbrains.letsPlot.core.plot.base.Aes.FAMILY,
                org.jetbrains.letsPlot.core.plot.base.Aes.FONTFACE,
                org.jetbrains.letsPlot.core.plot.base.Aes.HJUST,
                org.jetbrains.letsPlot.core.plot.base.Aes.VJUST,
                org.jetbrains.letsPlot.core.plot.base.Aes.ANGLE,
                org.jetbrains.letsPlot.core.plot.base.Aes.LINEHEIGHT
            )

            GeomKind.LABEL -> listOf(
                org.jetbrains.letsPlot.core.plot.base.Aes.X, org.jetbrains.letsPlot.core.plot.base.Aes.Y,
                org.jetbrains.letsPlot.core.plot.base.Aes.SIZE,
                org.jetbrains.letsPlot.core.plot.base.Aes.COLOR,
                org.jetbrains.letsPlot.core.plot.base.Aes.FILL,
                org.jetbrains.letsPlot.core.plot.base.Aes.ALPHA,
                org.jetbrains.letsPlot.core.plot.base.Aes.LABEL,
                org.jetbrains.letsPlot.core.plot.base.Aes.FAMILY,
                org.jetbrains.letsPlot.core.plot.base.Aes.FONTFACE,
                org.jetbrains.letsPlot.core.plot.base.Aes.HJUST,
                org.jetbrains.letsPlot.core.plot.base.Aes.VJUST,
                org.jetbrains.letsPlot.core.plot.base.Aes.ANGLE,
                org.jetbrains.letsPlot.core.plot.base.Aes.LINEHEIGHT
            )

            GeomKind.LIVE_MAP -> listOf(
                org.jetbrains.letsPlot.core.plot.base.Aes.ALPHA,
                org.jetbrains.letsPlot.core.plot.base.Aes.COLOR,
                org.jetbrains.letsPlot.core.plot.base.Aes.FILL,
                org.jetbrains.letsPlot.core.plot.base.Aes.SIZE,
                org.jetbrains.letsPlot.core.plot.base.Aes.SHAPE,
                org.jetbrains.letsPlot.core.plot.base.Aes.FRAME,
                org.jetbrains.letsPlot.core.plot.base.Aes.X,
                org.jetbrains.letsPlot.core.plot.base.Aes.Y
            )

            GeomKind.RASTER -> listOf(
                org.jetbrains.letsPlot.core.plot.base.Aes.X, org.jetbrains.letsPlot.core.plot.base.Aes.Y,
                org.jetbrains.letsPlot.core.plot.base.Aes.WIDTH, // not rendered but required for correct x aes range computation
                org.jetbrains.letsPlot.core.plot.base.Aes.HEIGHT, // -- the same --
                org.jetbrains.letsPlot.core.plot.base.Aes.FILL,
                org.jetbrains.letsPlot.core.plot.base.Aes.ALPHA
            )

            GeomKind.IMAGE -> listOf(
                org.jetbrains.letsPlot.core.plot.base.Aes.XMIN,
                org.jetbrains.letsPlot.core.plot.base.Aes.XMAX,
                org.jetbrains.letsPlot.core.plot.base.Aes.YMIN,
                org.jetbrains.letsPlot.core.plot.base.Aes.YMAX,
                org.jetbrains.letsPlot.core.plot.base.Aes.COLOR  // not rendered but necessary for color legend to appear.
            )

            GeomKind.PIE -> listOf(
                org.jetbrains.letsPlot.core.plot.base.Aes.X,
                org.jetbrains.letsPlot.core.plot.base.Aes.Y,
                org.jetbrains.letsPlot.core.plot.base.Aes.SLICE,
                org.jetbrains.letsPlot.core.plot.base.Aes.EXPLODE,
                org.jetbrains.letsPlot.core.plot.base.Aes.SIZE,
                org.jetbrains.letsPlot.core.plot.base.Aes.FILL,
                org.jetbrains.letsPlot.core.plot.base.Aes.ALPHA,
                org.jetbrains.letsPlot.core.plot.base.Aes.COLOR,
                org.jetbrains.letsPlot.core.plot.base.Aes.STROKE
            )

            GeomKind.LOLLIPOP -> listOf(
                org.jetbrains.letsPlot.core.plot.base.Aes.X, org.jetbrains.letsPlot.core.plot.base.Aes.Y,
                org.jetbrains.letsPlot.core.plot.base.Aes.SIZE,
                org.jetbrains.letsPlot.core.plot.base.Aes.STROKE,
                org.jetbrains.letsPlot.core.plot.base.Aes.LINEWIDTH,
                org.jetbrains.letsPlot.core.plot.base.Aes.COLOR,
                org.jetbrains.letsPlot.core.plot.base.Aes.FILL,
                org.jetbrains.letsPlot.core.plot.base.Aes.ALPHA,
                org.jetbrains.letsPlot.core.plot.base.Aes.SHAPE,
                org.jetbrains.letsPlot.core.plot.base.Aes.LINETYPE
            )
        }
    }
}