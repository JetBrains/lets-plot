/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.defaultTheme

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.Colors
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.plot.base.aes.GeomTheme
import org.jetbrains.letsPlot.core.plot.base.theme.ColorTheme

internal class DefaultGeomTheme private constructor(
    private val color: Color,
    private val fill: Color,
    private val alpha: Double,
    private val size: Double,
    private val lineWidth: Double
) : GeomTheme {
    override fun color() = color

    override fun fill() = fill

    override fun alpha() = alpha

    override fun size() = size

    override fun lineWidth() = lineWidth

    companion object {
        private const val COMMON_POINT_SIZE = 3.0
        private const val COMMON_LINE_WIDTH = 0.75
        private const val THIN_LINE_WIDTH = 0.5
        private const val BOLD_LINE_WIDTH = 1.5

        private const val LOLLIPOP_SIZE = 2.0
        private const val PIE_SIZE = 10.0
        private const val TEXT_SIZE = 7.0

        // defaults for geomKind
        fun forGeomKind(geomKind: GeomKind, colorTheme: ColorTheme): GeomTheme {

            // Size: point size or line width - depending on the geom kind.
            val size = when (geomKind) {
                GeomKind.POINT,
                GeomKind.JITTER,
                GeomKind.Q_Q,
                GeomKind.Q_Q_2 -> COMMON_POINT_SIZE

                GeomKind.HISTOGRAM,
                GeomKind.CONTOUR,
                GeomKind.DENSITY2D -> THIN_LINE_WIDTH

                GeomKind.POINT_RANGE -> {
                    // Actually, the "mid-point" size which is
                    // later multiplied by the "fatten" factor (def 5).
                    COMMON_LINE_WIDTH
                }

                GeomKind.LOLLIPOP -> LOLLIPOP_SIZE            // point size

                GeomKind.TEXT,
                GeomKind.LABEL -> TEXT_SIZE

                GeomKind.PIE -> PIE_SIZE

                else -> COMMON_LINE_WIDTH
            }

            // Linewidth (also used for "stroke")
            val lineWidth = when (geomKind) {
                GeomKind.POINT_RANGE,
                GeomKind.LOLLIPOP -> BOLD_LINE_WIDTH

                else -> COMMON_LINE_WIDTH
            }

            // Color
            val color = when (geomKind) {
                GeomKind.POLYGON,
                GeomKind.DOT_PLOT,
                GeomKind.Y_DOT_PLOT -> colorTheme.paper()

                GeomKind.BAR,
                GeomKind.PIE,
                GeomKind.CONTOURF,
                GeomKind.DENSITY2DF,
                GeomKind.TILE,
                GeomKind.BIN_2D -> Color.TRANSPARENT

                GeomKind.SMOOTH -> Color.MAGENTA

                else -> colorTheme.pen()
            }

            // Fill
            val fill = when (geomKind) {
                GeomKind.AREA_RIDGES,
                GeomKind.AREA,
                GeomKind.DENSITY,
                GeomKind.RECT,
                GeomKind.RIBBON,
                GeomKind.MAP -> Colors.withOpacity(color, 0.1)

                GeomKind.BAR,
                GeomKind.PIE,
                GeomKind.POLYGON,
                GeomKind.CONTOURF,
                GeomKind.DENSITY2DF,
                GeomKind.DOT_PLOT,
                GeomKind.Y_DOT_PLOT -> colorTheme.brush()

                GeomKind.HISTOGRAM,
                GeomKind.SMOOTH,
                GeomKind.TILE,
                GeomKind.BIN_2D -> colorTheme.pen()

                else -> colorTheme.paper()
            }

            // Alpha
            val alpha = when (geomKind) {
                GeomKind.SMOOTH -> 0.15
                else -> 1.0
            }

            return DefaultGeomTheme(color, fill, alpha, size, lineWidth)
        }
    }
}