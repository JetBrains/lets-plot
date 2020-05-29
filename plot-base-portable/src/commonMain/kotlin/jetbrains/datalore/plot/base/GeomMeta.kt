/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base

import kotlin.native.concurrent.ThreadLocal

// In Kotlin Native objects a frozen by default. Annotate with `ThreadLocal` to unfreeze.
// See:  https://github.com/JetBrains/kotlin-native/blob/master/IMMUTABILITY.md
// Required mutations:
//      -   `renderedAesByGeom` map
@ThreadLocal
object GeomMeta {
    private val renderedAesByGeom = HashMap<GeomKind, List<Aes<*>>>()

    fun renders(geomKind: GeomKind): List<Aes<*>> {
        if (!renderedAesByGeom.containsKey(geomKind)) {
            renderedAesByGeom[geomKind] =
                renderedAesList(geomKind)
        }
        return renderedAesByGeom[geomKind]!!
    }

    private val POINT = listOf(
        Aes.X, Aes.Y,
        Aes.SIZE,
        Aes.COLOR,
        Aes.FILL,
        Aes.ALPHA,
        Aes.SHAPE
        // strokeWidth
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
        Aes.ALPHA
    )

    private val AREA = listOf(
        Aes.X, Aes.Y,
        Aes.SIZE,
        Aes.LINETYPE,
        Aes.COLOR,
        Aes.FILL,
        Aes.ALPHA
    )


    private fun renderedAesList(geomKind: GeomKind): List<Aes<*>> {
        return when (geomKind) {
            GeomKind.POINT -> POINT
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
                Aes.X,
                Aes.YMIN, Aes.YMAX,
                Aes.WIDTH,
                Aes.ALPHA,
                Aes.COLOR,
                Aes.LINETYPE,
                Aes.SIZE
            )

            GeomKind.CROSS_BAR -> listOf(
                Aes.X,
                Aes.YMIN, Aes.YMAX, Aes.MIDDLE,
                Aes.WIDTH,

                Aes.ALPHA,
                Aes.COLOR,
                Aes.FILL,
                Aes.LINETYPE,
                Aes.SHAPE,
                Aes.SIZE
            )

            GeomKind.LINE_RANGE -> listOf(
                Aes.X,
                Aes.YMIN, Aes.YMAX,
                Aes.ALPHA,
                Aes.COLOR,
                Aes.LINETYPE,
                Aes.SIZE
            )

            GeomKind.POINT_RANGE -> listOf(
                Aes.X, Aes.Y,
                Aes.YMIN, Aes.YMAX,
                Aes.ALPHA,
                Aes.COLOR,
                Aes.FILL,
                Aes.LINETYPE,
                Aes.SHAPE,
                Aes.SIZE
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

            GeomKind.BOX_PLOT -> listOf(
                Aes.LOWER, // NaN for 'outlier' data-point
                Aes.MIDDLE, // NaN for 'outlier' data-point
                Aes.UPPER, // NaN for 'outlier' data-point

                Aes.X,
                Aes.Y, // NaN for 'box' data-point (used for outliers)
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

            GeomKind.RIBBON -> listOf(
                Aes.X,
                Aes.YMIN, Aes.YMAX,
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
                Aes.FLOW
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
                Aes.ANGLE
            )

            GeomKind.LIVE_MAP -> listOf( // ToDo: not static - depends on 'display mode'
                Aes.ALPHA,
                Aes.COLOR,
                Aes.FILL,
                Aes.SIZE,
                Aes.SHAPE,
                Aes.FRAME,
                Aes.X,
                Aes.Y,
                Aes.SYM_X,
                Aes.SYM_Y
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
                Aes.YMAX
            )
        }
    }
}