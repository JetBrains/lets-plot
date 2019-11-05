/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base

import kotlin.native.concurrent.ThreadLocal

@ThreadLocal  // objects a frozen by default but we are going to mutate `renderedAesByGeom` map
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
        Aes.SHAPE,
        Aes.MAP_ID
        // strokeWidth
    )

    private val PATH = listOf(
        Aes.X, Aes.Y,
        Aes.SIZE, // path width
        Aes.LINETYPE,
        Aes.COLOR,
        Aes.ALPHA,
        Aes.MAP_ID,
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

            GeomKind.TILE -> listOf(
                Aes.X, Aes.Y,
                Aes.WIDTH,
                Aes.HEIGHT,
                Aes.COLOR,
                Aes.FILL,
                Aes.ALPHA,
                Aes.LINETYPE,
                Aes.SIZE
            )

            GeomKind.ERROR_BAR -> listOf(
                Aes.X,
                Aes.YMIN, Aes.YMAX,
                Aes.WIDTH,
                Aes.SIZE, // path width
                Aes.LINETYPE,
                Aes.COLOR,
                Aes.ALPHA
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
                Aes.ALPHA,
                Aes.MAP_ID
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
                Aes.Y, // NaN for 'box' data-point
                Aes.YMAX,
                Aes.YMIN,

                Aes.ALPHA,
                Aes.COLOR,
                Aes.FILL,
                Aes.LINETYPE,
                Aes.SHAPE,
                Aes.SIZE, // path width
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
                Aes.ALPHA,
                Aes.MAP_ID
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
                Aes.MAP_ID,
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
                Aes.YMAX
            )
        }
    }
}