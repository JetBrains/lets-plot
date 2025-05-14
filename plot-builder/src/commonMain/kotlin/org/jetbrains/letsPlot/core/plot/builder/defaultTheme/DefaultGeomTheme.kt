/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.defaultTheme

import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.Colors
import org.jetbrains.letsPlot.core.plot.base.GeomKind
import org.jetbrains.letsPlot.core.plot.base.aes.AesInitValue.DEFAULT_ALPHA
import org.jetbrains.letsPlot.core.plot.base.aes.GeomTheme
import org.jetbrains.letsPlot.core.plot.base.theme.ColorTheme

internal class DefaultGeomTheme private constructor(
    private val color: Color,
    private val fill: Color,
    private val alpha: Double,
    private val size: Double,
    private val lineWidth: Double,
    private val pen: Color,
    private val pointSize: Double
) : GeomTheme {
    override fun color() = color

    override fun fill() = fill

    override fun alpha() = alpha

    override fun size() = size

    override fun lineWidth() = lineWidth

    override fun pen() = pen

    override fun pointSize() = pointSize

    companion object {
        private const val COMMON_POINT_SIZE = 3.0
        private const val COMMON_LINE_WIDTH = 0.75
        private const val THIN_LINE_WIDTH = 0.5
        private const val ZERO_LINE_WIDTH = 0.0

        private const val LOLLIPOP_SIZE = 2.0
        private const val PIE_SIZE = 10.0
        private const val TEXT_SIZE = 7.0

        // defaults for geomKind
        internal fun forGeomKind(geomKind: GeomKind, colorTheme: ColorTheme): GeomTheme {

            // Size: point size or line width - depending on the geom kind.
            val size = when (geomKind) {
                GeomKind.POINT,
                GeomKind.JITTER,
                GeomKind.SINA,
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
                GeomKind.LABEL,
                GeomKind.TEXT_REPEL,
                GeomKind.LABEL_REPEL -> TEXT_SIZE

                GeomKind.PIE -> PIE_SIZE

                GeomKind.TILE,
                GeomKind.BIN_2D,
                GeomKind.HEX -> ZERO_LINE_WIDTH

                else -> COMMON_LINE_WIDTH
            }

            val pointSize = when (geomKind) {
                GeomKind.TEXT_REPEL,
                GeomKind.LABEL_REPEL -> COMMON_POINT_SIZE
                else -> 1.0
            }

            // Linewidth (also used for "stroke")
            val lineWidth = COMMON_LINE_WIDTH

            // Color
            val color = when (geomKind) {
                GeomKind.POLYGON,
                GeomKind.DOT_PLOT,
                GeomKind.Y_DOT_PLOT,
                GeomKind.BAR,
                GeomKind.PIE,
                GeomKind.TILE,
                GeomKind.BIN_2D,
                GeomKind.HEX -> colorTheme.paper()

                GeomKind.CONTOURF,
                GeomKind.DENSITY2DF -> Color.TRANSPARENT

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
                GeomKind.BAND,
                GeomKind.MAP -> Colors.withOpacity(color, 0.1)

                GeomKind.BAR,
                GeomKind.PIE,
                GeomKind.POLYGON,
                GeomKind.CONTOURF,
                GeomKind.DENSITY2DF,
                GeomKind.DOT_PLOT,
                GeomKind.RASTER,
                GeomKind.Y_DOT_PLOT -> colorTheme.brush()

                GeomKind.HISTOGRAM,
                GeomKind.SMOOTH,
                GeomKind.TILE,
                GeomKind.BIN_2D,
                GeomKind.HEX -> colorTheme.pen()

                else -> colorTheme.paper()
            }

            // Alpha
            val alpha = when (geomKind) {
                GeomKind.SMOOTH -> 0.15
                else -> DEFAULT_ALPHA
            }

            return DefaultGeomTheme(color, fill, alpha, size, lineWidth, colorTheme.pen(), pointSize)
        }
    }
}