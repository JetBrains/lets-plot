/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.defaultTheme

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.Colors
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
        val BASE = DefaultGeomTheme(
            color = Color.PACIFIC_BLUE,
            fill = Color.PACIFIC_BLUE,
            alpha = 1.0,
            size = 0.5,
            lineWidth = 0.5
        )

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

        // defaults for geomKind
        fun forGeomKind(geomKind: GeomKind, inheritedColors: InheritedColors): GeomTheme {
            var color = BASE.color
            var fill = BASE.fill
            var alpha = BASE.alpha
            var size = BASE.size
            var lineWidth = BASE.lineWidth

            when (geomKind) {
                GeomKind.PATH,
                GeomKind.LINE,
                GeomKind.AB_LINE,
                GeomKind.H_LINE,
                GeomKind.V_LINE,
                GeomKind.SEGMENT,
                GeomKind.STEP,
                GeomKind.FREQPOLY,
                GeomKind.CONTOUR,
                GeomKind.DENSITY2D,
                GeomKind.Q_Q_LINE,
                GeomKind.Q_Q_2_LINE,
                GeomKind.ERROR_BAR,
                GeomKind.LINE_RANGE -> {
                    color = inheritedColors.lineColor()
                }

                GeomKind.HISTOGRAM,
                GeomKind.CROSS_BAR,
                GeomKind.BOX_PLOT,
                GeomKind.AREA_RIDGES,
                GeomKind.VIOLIN,
                GeomKind.AREA,
                GeomKind.DENSITY,
                GeomKind.RECT,
                GeomKind.RIBBON -> {
                    color = inheritedColors.lineColor()
                    fill = inheritedColors.lineColor()
                    alpha = 0.1
                }

                GeomKind.MAP -> {
                    color = inheritedColors.lineColor()
                    fill = inheritedColors.lineColor()
                    alpha = 0.1
                    size = 0.2
                }

                GeomKind.POINT,
                GeomKind.JITTER,
                GeomKind.Q_Q,
                GeomKind.Q_Q_2 -> {
                    color = inheritedColors.lineColor()
                    fill = inheritedColors.backgroundFill()
                    size = 2.0
                }

                GeomKind.POINT_RANGE -> {
                    color = inheritedColors.lineColor()
                    fill = inheritedColors.backgroundFill()
                    lineWidth = 1.0 // line width and stroke for point
                }

                GeomKind.LOLLIPOP -> {
                    color = inheritedColors.lineColor()
                    fill = inheritedColors.backgroundFill()
                    size = 2.0
                    lineWidth = 1.0 // line width and stroke for point
                }

                GeomKind.SMOOTH -> {
                    color = Color.MAGENTA
                    fill = inheritedColors.lineColor()
                    alpha = 1.5 // Geometry uses (value / 10) for alpha: SmoothGeom.kt:91 (PROPORTION)
                }

                GeomKind.DOT_PLOT,
                GeomKind.Y_DOT_PLOT,
                GeomKind.BAR,
                GeomKind.TILE,
                GeomKind.BIN_2D,
                GeomKind.POLYGON -> {
                    color = inheritedColors.backgroundFill()
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