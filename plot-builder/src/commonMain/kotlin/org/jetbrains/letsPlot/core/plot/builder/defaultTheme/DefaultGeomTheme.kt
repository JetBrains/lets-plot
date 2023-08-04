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
        private class FixedColors(geomKind: GeomKind) {
            val color = if (geomKind == GeomKind.SMOOTH) {
                Color.MAGENTA
            } else {
                Color.PACIFIC_BLUE
            }
            val fill = Color.PACIFIC_BLUE
        }

        // defaults for geomKind
        fun forGeomKind(geomKind: GeomKind, colorTheme: ColorTheme): GeomTheme {
            val fixedColors = FixedColors(geomKind)

            var color = fixedColors.color
            var fill = fixedColors.fill
            var alpha = 1.0
            var size = 0.5
            var lineWidth = 0.5

            val sizeMultiplier = 1.5

            when (geomKind) {
                GeomKind.PATH,
                GeomKind.LINE,
                GeomKind.AB_LINE,
                GeomKind.H_LINE,
                GeomKind.V_LINE,
                GeomKind.SEGMENT,
                GeomKind.STEP,
                GeomKind.FREQPOLY,
                GeomKind.Q_Q_LINE,
                GeomKind.Q_Q_2_LINE,
                GeomKind.ERROR_BAR,
                GeomKind.LINE_RANGE -> {
                    color = colorTheme.pen()
                    size *= sizeMultiplier
                }

                GeomKind.CONTOUR,
                GeomKind.DENSITY2D -> {
                    color = colorTheme.pen()
                }

                GeomKind.AREA_RIDGES,
                GeomKind.AREA,
                GeomKind.DENSITY,
                GeomKind.RECT,
                GeomKind.RIBBON,
                GeomKind.MAP -> {
                    color = colorTheme.pen()
                    fill = Colors.withOpacity(colorTheme.pen(), 0.1)
                    size *= sizeMultiplier
                }

                GeomKind.VIOLIN,
                GeomKind.CROSS_BAR,
                GeomKind.BOX_PLOT -> {
                    color = colorTheme.pen()
                    fill = colorTheme.paper()
                    size *= sizeMultiplier
                }

                GeomKind.POINT,
                GeomKind.JITTER,
                GeomKind.Q_Q,
                GeomKind.Q_Q_2 -> {
                    color = colorTheme.pen()
                    fill = colorTheme.paper()
                    size = 2.0 * sizeMultiplier
                    lineWidth *= sizeMultiplier
                }

                GeomKind.DOT_PLOT,
                GeomKind.Y_DOT_PLOT -> {
                    color = colorTheme.paper()
                }

                GeomKind.POINT_RANGE -> {
                    color = colorTheme.pen()
                    fill = colorTheme.paper()
                    size *= sizeMultiplier              // mid-point size
                    lineWidth = 1.0 * sizeMultiplier    // line width and stroke for point
                }

                GeomKind.LOLLIPOP -> {
                    color = colorTheme.pen()
                    fill = colorTheme.paper()
                    size = 2.0                          // point size
                    lineWidth = 1.0 * sizeMultiplier    // line width and stroke for point
                }

                GeomKind.SMOOTH -> {
                    fill = colorTheme.pen()
                    alpha = 0.15
                    size *= sizeMultiplier
                }

                GeomKind.BAR -> {
                    color = Color.TRANSPARENT
                    size *= sizeMultiplier
                }

                GeomKind.HISTOGRAM -> {
                    color = colorTheme.pen()
                    fill = colorTheme.pen()
                }

                GeomKind.POLYGON -> {
                    color = colorTheme.paper()
                    size *= sizeMultiplier
                }

                GeomKind.TILE,
                GeomKind.BIN_2D -> {
                    color = Color.TRANSPARENT
                    fill = colorTheme.pen()
                    size *= sizeMultiplier
                }

                GeomKind.CONTOURF,
                GeomKind.DENSITY2DF -> {
                    color = Color.TRANSPARENT
                    size *= sizeMultiplier
                }

                GeomKind.TEXT, GeomKind.LABEL -> {
                    color = colorTheme.pen()
                    fill = colorTheme.paper() // background for label
                    size = 7.0
                }

                GeomKind.PIE -> {
                    color = Color.TRANSPARENT
                    size = 10.0
                    lineWidth *= sizeMultiplier
                }

                GeomKind.RASTER,
                GeomKind.IMAGE,
                GeomKind.LIVE_MAP -> {
                }
            }

            return DefaultGeomTheme(color, fill, alpha, size, lineWidth)
        }
    }
}