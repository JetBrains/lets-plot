/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.defaultTheme

import jetbrains.datalore.base.values.Colors
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.builder.theme.Theme

object GeomFlavorUtil {

    fun getFlavors(geomKind: GeomKind, theme: Theme): Map<Aes<*>, Any> {
        if (!theme.hasFlavor()) {
            return emptyMap()
        }
        return when (geomKind) {
            GeomKind.POINT,
            GeomKind.JITTER,

            GeomKind.DOT_PLOT,
            GeomKind.Y_DOT_PLOT,

            GeomKind.PATH,
            GeomKind.LINE,
            GeomKind.SEGMENT,
            GeomKind.AB_LINE,
            GeomKind.H_LINE,
            GeomKind.V_LINE,
            GeomKind.STEP,
            GeomKind.CONTOUR,
            GeomKind.DENSITY2D,
            GeomKind.FREQPOLY,

            GeomKind.AREA,
            GeomKind.DENSITY,
            GeomKind.RIBBON,

            GeomKind.AREA_RIDGES,

            GeomKind.MAP,

            GeomKind.BOX_PLOT,
            GeomKind.ERROR_BAR,
            GeomKind.CROSS_BAR,
            GeomKind.LINE_RANGE,
            GeomKind.POINT_RANGE,

            GeomKind.VIOLIN,
            GeomKind.RECT,

            GeomKind.HISTOGRAM,

            GeomKind.Q_Q,
            GeomKind.Q_Q_2,
            GeomKind.Q_Q_LINE,
            GeomKind.Q_Q_2_LINE,

            GeomKind.LOLLIPOP,

            GeomKind.TEXT,
            GeomKind.LABEL -> {
                val lineColor = theme.horizontalAxis(false).lineColor()
                mapOf(
                    Aes.COLOR to lineColor,
                    Aes.FILL to Colors.withOpacity(lineColor, 0.5)
                )
            }

            GeomKind.BAR,
            GeomKind.TILE,
            GeomKind.BIN_2D,
            GeomKind.POLYGON,
            GeomKind.CONTOURF,
            GeomKind.DENSITY2DF -> {
                mapOf(
                    Aes.COLOR to theme.plot().backgroundFill()
                )
            }

            GeomKind.SMOOTH,
            GeomKind.RASTER,
            GeomKind.PIE,
            GeomKind.IMAGE,
            GeomKind.LIVE_MAP -> {
                emptyMap()
            }
        }
    }
}