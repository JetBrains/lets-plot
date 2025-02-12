/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.livemap

import org.jetbrains.letsPlot.core.plot.base.GeomKind

interface LivemapConstants {

    enum class Theme {
        COLOR,
        LIGHT,
        DARK
    }

    enum class Projection {
        EPSG3857,
        EPSG4326,
        AZIMUTHAL,
        CONIC
    }
}

val SUPPORTED_LAYERS = setOf(
    GeomKind.POINT,
    GeomKind.H_LINE, GeomKind.V_LINE, GeomKind.SEGMENT, GeomKind.CURVE, GeomKind.SPOKE,
    GeomKind.RECT, GeomKind.TILE, GeomKind.BIN_2D, GeomKind.HEX,
    GeomKind.DENSITY2D, GeomKind.CONTOUR, GeomKind.PATH,
    GeomKind.TEXT, GeomKind.LABEL,
    GeomKind.DENSITY2DF, GeomKind.CONTOURF, GeomKind.POLYGON, GeomKind.MAP,
    GeomKind.PIE
)
