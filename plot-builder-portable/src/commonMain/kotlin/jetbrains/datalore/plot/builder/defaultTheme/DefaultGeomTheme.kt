/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.defaultTheme

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.GeomKind
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.AXIS
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.AXIS_LINE
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.LINE
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.PLOT_BKGR_RECT
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption.RECT
import jetbrains.datalore.plot.builder.presentation.FontFamilyRegistry
import jetbrains.datalore.plot.base.aes.GeomTheme

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
        internal class InheritedColors(
            options: Map<String, Any>,
            fontFamilyRegistry: FontFamilyRegistry
        ) : ThemeValuesAccess(options, fontFamilyRegistry) {

            private val lineKey = listOf(AXIS_LINE + "_x", AXIS_LINE + "_y", AXIS_LINE, AXIS, LINE)

            private val backgroundKey = listOf(PLOT_BKGR_RECT, RECT)

            fun lineColor(): Color {
                return getColor(getElemValue(lineKey), ThemeOption.Elem.COLOR)
            }

            fun backgroundFill(): Color {
                return getColor(getElemValue(backgroundKey), ThemeOption.Elem.FILL)
            }
        }

        private class FixedColors(geomKind: GeomKind) {
            val color = if (geomKind == GeomKind.SMOOTH) {
                Color.MAGENTA
            } else {
                Color.PACIFIC_BLUE
            }
            val fill = Color.PACIFIC_BLUE
        }

        // defaults for geomKind
        fun forGeomKind(geomKind: GeomKind, inheritedColors: InheritedColors): GeomTheme {
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
                    color = inheritedColors.lineColor()
                    size *= sizeMultiplier
                }

                GeomKind.CONTOUR,
                GeomKind.DENSITY2D -> {
                    color = inheritedColors.lineColor()
                }

                GeomKind.AREA_RIDGES,
                GeomKind.AREA,
                GeomKind.DENSITY,
                GeomKind.RECT,
                GeomKind.RIBBON,
                GeomKind.MAP -> {
                    color = inheritedColors.lineColor()
                    fill = inheritedColors.lineColor()
                    alpha = 0.1
                    size *= sizeMultiplier
                }

                GeomKind.VIOLIN,
                GeomKind.CROSS_BAR,
                GeomKind.BOX_PLOT -> {
                    color = inheritedColors.lineColor()
                    fill = inheritedColors.backgroundFill()
                    size *= sizeMultiplier
                }

                GeomKind.POINT,
                GeomKind.JITTER,
                GeomKind.Q_Q,
                GeomKind.Q_Q_2 -> {
                    color = inheritedColors.lineColor()
                    fill = inheritedColors.backgroundFill()
                    size = 2.0 * sizeMultiplier
                    lineWidth *= sizeMultiplier
                }

                GeomKind.DOT_PLOT,
                GeomKind.Y_DOT_PLOT -> {
                    color = inheritedColors.backgroundFill()
                    fill = inheritedColors.lineColor()
                }

                GeomKind.POINT_RANGE -> {
                    color = inheritedColors.lineColor()
                    fill = inheritedColors.backgroundFill()
                    size *= sizeMultiplier              // mid-point size
                    lineWidth = 1.0 * sizeMultiplier    // line width and stroke for point
                }

                GeomKind.LOLLIPOP -> {
                    color = inheritedColors.lineColor()
                    fill = inheritedColors.backgroundFill()
                    size = 2.0                          // point size
                    lineWidth = 1.0 * sizeMultiplier    // line width and stroke for point
                }

                GeomKind.SMOOTH -> {
                    fill = inheritedColors.lineColor()
                    alpha = 0.15
                    size *= sizeMultiplier
                }

                GeomKind.BAR,
                GeomKind.POLYGON -> {
                    color = inheritedColors.backgroundFill()
                    size *= sizeMultiplier
                }

                GeomKind.HISTOGRAM,
                GeomKind.TILE,
                GeomKind.BIN_2D -> {
                    color = inheritedColors.backgroundFill()
                    fill = inheritedColors.lineColor()
                    size = 0.0
                }

                GeomKind.CONTOURF,
                GeomKind.DENSITY2DF -> {
                    color = inheritedColors.backgroundFill()
                    size = 0.0
                }

                GeomKind.TEXT, GeomKind.LABEL -> {
                    color = inheritedColors.lineColor()
                    fill = inheritedColors.backgroundFill() // background for label
                    size = 7.0
                }

                GeomKind.PIE -> {
                    size = 10.0
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