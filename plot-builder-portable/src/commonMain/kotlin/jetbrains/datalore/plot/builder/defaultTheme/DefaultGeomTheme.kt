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

internal class DefaultGeomTheme(
    private val color: Color?,
    private val fill: Color?,
    private val alpha: Double?,
    private val size: Double?,
    private val lineWidth: Double? = null
) : GeomTheme {
    override fun color() = color

    override fun fill() = fill

    override fun alpha() = alpha

    override fun size() = size

    override fun lineWidth() = lineWidth

    companion object {

        class InheritedColors(
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
            return when (geomKind) {
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
                GeomKind.Q_Q_2_LINE -> {
                    DefaultGeomTheme(
                        color = inheritedColors.lineColor(),
                        fill = null,
                        alpha = 1.0,
                        size = 0.5   // line width
                    )
                }

                GeomKind.SMOOTH -> {
                    DefaultGeomTheme(
                        color = Color.MAGENTA,
                        fill = inheritedColors.lineColor(),
                        alpha = 1.5, // Geometry uses (value / 10) for alpha: SmoothGeom.kt:91 (PROPORTION)
                        size = 0.5   // line width
                    )
                }

                GeomKind.BAR -> {
                    DefaultGeomTheme(
                        color = inheritedColors.backgroundFill(),
                        fill = Color.PACIFIC_BLUE,
                        alpha = 1.0,
                        size = 0.5
                    )
                }

                GeomKind.HISTOGRAM -> {
                    DefaultGeomTheme(
                        color = inheritedColors.lineColor(),
                        fill = Colors.withOpacity(inheritedColors.lineColor(), 0.1),
                        size = 0.5,
                        alpha = 1.0
                    )
                }

                GeomKind.DOT_PLOT,
                GeomKind.Y_DOT_PLOT -> {
                    DefaultGeomTheme(
                        color = inheritedColors.backgroundFill(),
                        fill = Color.PACIFIC_BLUE,
                        alpha = 1.0,
                        size = null
                    )
                }

                GeomKind.TILE,
                GeomKind.BIN_2D -> {
                    DefaultGeomTheme(
                        color = inheritedColors.backgroundFill(),
                        fill = Color.PACIFIC_BLUE,
                        alpha = 1.0,
                        size = 0.5
                    )
                }

                GeomKind.MAP -> {
                    DefaultGeomTheme(
                        color = inheritedColors.lineColor(),
                        fill = Colors.withOpacity(inheritedColors.lineColor(), 0.1),
                        alpha = 1.0,
                        size = 0.2
                    )
                }

                GeomKind.ERROR_BAR,
                GeomKind.LINE_RANGE -> {
                    DefaultGeomTheme(
                        color = inheritedColors.lineColor(),
                        fill = null,
                        alpha = 1.0,
                        size = 0.5
                    )
                }
                GeomKind.POINT_RANGE -> {
                    DefaultGeomTheme(
                        color = inheritedColors.lineColor(),
                        fill = inheritedColors.lineColor(),
                        alpha = 1.0,
                        size = 0.5,     // size of mid-point
                        lineWidth = 0.5
                    )
                }

                GeomKind.CROSS_BAR,
                GeomKind.BOX_PLOT,
                GeomKind.AREA_RIDGES,
                GeomKind.VIOLIN -> {
                    DefaultGeomTheme(
                        color = inheritedColors.lineColor(),
                        fill = inheritedColors.lineColor(),
                        alpha = 0.1,
                        size = 0.5
                    )
                }

                GeomKind.POLYGON -> {
                    DefaultGeomTheme(
                        color = inheritedColors.backgroundFill(),
                        fill = Color.PACIFIC_BLUE,
                        alpha = 1.0,
                        size = 0.5
                    )
                }

                GeomKind.CONTOURF,
                GeomKind.DENSITY2DF -> {
                    DefaultGeomTheme(
                        color = inheritedColors.backgroundFill(),
                        fill = Color.PACIFIC_BLUE,
                        alpha = 1.0,
                        size = 0.0
                    )
                }

                GeomKind.POINT,
                GeomKind.JITTER,
                GeomKind.Q_Q,
                GeomKind.Q_Q_2 -> {
                    DefaultGeomTheme(
                        color = inheritedColors.lineColor(),
                        fill = Colors.withOpacity(inheritedColors.lineColor(), 0.1),
                        alpha = 1.0,
                        size = 2.0,
                    )
                }

                GeomKind.AREA,
                GeomKind.DENSITY,
                GeomKind.RECT,
                GeomKind.RIBBON -> {
                    DefaultGeomTheme(
                        color = inheritedColors.lineColor(),
                        fill = inheritedColors.lineColor(),
                        alpha = 0.1,
                        size = 0.5
                    )
                }

                GeomKind.TEXT -> {
                    DefaultGeomTheme(
                        color = inheritedColors.lineColor(),
                        fill = null,
                        alpha = 1.0,
                        size = 7.0
                    )
                }

                GeomKind.LABEL -> {
                    DefaultGeomTheme(
                        color = inheritedColors.lineColor(),
                        fill = inheritedColors.backgroundFill(),
                        alpha = 1.0,
                        size = 7.0
                    )
                }

                GeomKind.LOLLIPOP -> {
                    DefaultGeomTheme(
                        color = inheritedColors.lineColor(),
                        fill = Colors.withOpacity(inheritedColors.lineColor(), 0.1),
                        alpha = 1.0,
                        size = 2.0,         // point size
                        lineWidth = 0.5,    // stick width
                    )
                }

                GeomKind.PIE -> {
                    DefaultGeomTheme(
                        color = null,
                        fill = Color.PACIFIC_BLUE,
                        alpha = 1.0,
                        size = 10.0
                    )
                }

                GeomKind.RASTER -> {
                    DefaultGeomTheme(
                        color = null,
                        fill = Color.PACIFIC_BLUE,
                        alpha = 1.0,
                        size = null
                    )
                }

                GeomKind.IMAGE,
                GeomKind.LIVE_MAP -> {
                    DefaultGeomTheme(
                        color = null,
                        fill = null,
                        alpha = null,
                        size = null
                    )
                }
            }
        }
    }
}