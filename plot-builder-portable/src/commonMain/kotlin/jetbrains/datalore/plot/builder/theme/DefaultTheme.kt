/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.theme

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.base.values.Colors
import jetbrains.datalore.plot.builder.guide.LegendDirection
import jetbrains.datalore.plot.builder.guide.LegendJustification
import jetbrains.datalore.plot.builder.guide.LegendPosition


open class DefaultTheme : Theme {

    override fun axisX(): AxisTheme {
        return AXIS_THEME
    }

    override fun axisY(): AxisTheme {
        return AXIS_THEME
    }

    override fun legend(): LegendTheme {
        return LEGEND_THEME
    }

    override fun facets(): FacetsTheme {
        return FACETS_THEME
    }

    override fun plot(): PlotTheme {
        return PLOT_THEME
    }

    override fun panel(): PanelTheme {
        return object : PanelTheme {
            override fun show(): Boolean = false

            override fun color(): Color = Color.BLACK

            override fun fill(): Color = Color.BLACK

            override fun size(): Double = 1.0
        }
    }

    override fun multiTile(): Theme {
        return object : DefaultTheme() {
            override fun axisX(): AxisTheme {
                return AXIS_THEME_MULTI_TILE
            }

            override fun axisY(): AxisTheme {
                return AXIS_THEME_MULTI_TILE
            }

            override fun plot(): PlotTheme {
                return PLOT_THEME_MULTI_TILE
            }

            override fun panel(): PanelTheme {
                return object : PanelTheme {
                    override fun show(): Boolean = true

                    override fun color(): Color = Colors.lighter(Color.VERY_LIGHT_GRAY, 0.9)

                    override fun fill(): Color = Color.TRANSPARENT

                    override fun size(): Double = 1.0
                }
            }
        }
    }

    companion object {
        private val AXIS_THEME = DefaultAxisTheme()

        private val AXIS_THEME_MULTI_TILE: AxisTheme = object : DefaultAxisTheme() {
            override fun showLine(): Boolean {
                return false  // replaced by inner frame
            }
        }

        private val LEGEND_THEME: LegendTheme = object : LegendTheme {
            override fun keySize(): Double {
                return 23.0
            }

            override fun margin(): Double {
                return 5.0
            }

            override fun padding(): Double {
                return 5.0
            }

            override fun position(): LegendPosition {
                return LegendPosition.RIGHT
            }

            override fun justification(): LegendJustification {
                return LegendJustification.CENTER
            }

            override fun direction(): LegendDirection {
                return LegendDirection.AUTO
            }

            override fun backgroundFill(): Color {
                return Color.WHITE
            }
        }

        private val FACETS_THEME: FacetsTheme = object : FacetsTheme {
            override fun stripFill(): Color {
                return Colors.lighter(Color.VERY_LIGHT_GRAY, 0.9)
            }

            override fun stripColor(): Color {
                return Color.BLACK
            }

            override fun stripSize(): Double {
                return 0.0
            }
        }

        private val PLOT_THEME: PlotTheme = object : PlotTheme {
        }

        private val PLOT_THEME_MULTI_TILE: PlotTheme = object : PlotTheme {
        }
    }
}
