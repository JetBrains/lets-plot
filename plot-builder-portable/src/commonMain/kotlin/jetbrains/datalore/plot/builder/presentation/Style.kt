/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.presentation

import jetbrains.datalore.plot.builder.presentation.Defaults.BACKDROP_COLOR
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.AXIS_TOOLTIP_FONT_SIZE
import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.DATA_TOOLTIP_FONT_SIZE
import jetbrains.datalore.plot.builder.presentation.Defaults.FONT_FAMILY_NORMAL
import jetbrains.datalore.plot.builder.presentation.Defaults.FONT_MEDIUM
import jetbrains.datalore.plot.builder.presentation.Defaults.TEXT_COLOR

/**
 * Duplicating stylesheet for JavaFX platform is defined in
 * plot-builder/src/jvmMain/resources/svgMapper/jfx/plot.css
 */
object Style {
    const val JFX_PLOT_STYLESHEET = "/svgMapper/jfx/plot.css"

    // classes
    const val PLOT_CONTAINER = "plt-container"
    const val PLOT = "plt-plot"
    const val PLOT_TITLE = "plt-plot-title"

    const val PLOT_TRANSPARENT = "plt-transparent"
    const val PLOT_BACKDROP = "plt-backdrop"

    const val AXIS = "plt-axis"

    const val AXIS_TITLE = "plt-axis-title"
    const val TICK = "tick"
    const val SMALL_TICK_FONT = "small-tick-font"

    const val BACK = "back"

    const val LEGEND = "plt_legend"
    const val LEGEND_TITLE = "legend-title"

    const val PLOT_DATA_TOOLTIP = "plt-data-tooltip"
    const val PLOT_AXIS_TOOLTIP = "plt-axis-tooltip"

    private val CSS = """
        |.$PLOT_CONTAINER {
        |	font-family: $FONT_FAMILY_NORMAL;
        |	cursor: crosshair;
        |	user-select: none;
        |	-webkit-user-select: none;
        |	-moz-user-select: none;
        |	-ms-user-select: none;
        |}
        |.$PLOT_BACKDROP {
        |   fill: $BACKDROP_COLOR;
        |}
        |.$PLOT_TRANSPARENT .$PLOT_BACKDROP {
        |   visibility: hidden;
        |}
        |text {
        |	font-size: ${FONT_MEDIUM}px;
        |	fill: $TEXT_COLOR;
        |}
        |.$PLOT_DATA_TOOLTIP text {
        |	font-size: ${DATA_TOOLTIP_FONT_SIZE}px;
        |}
        |.$PLOT_AXIS_TOOLTIP text {
        |	font-size: ${AXIS_TOOLTIP_FONT_SIZE}px;
        |}
        |.$AXIS line {
        |	shape-rendering: crispedges;
        |}
    """.trimMargin()

    val css: String
        get() {
            val css = StringBuilder(CSS.toString())
            css.append('\n')
            for (labelSpec in PlotLabelSpec.values()) {
                val selector = selector(labelSpec)
                css.append(LabelCss[labelSpec, selector])
            }
            return css.toString()
        }

    private fun selector(labelSpec: PlotLabelSpec): String {
        return when (labelSpec) {
            PlotLabelSpec.PLOT_TITLE -> ".$PLOT_TITLE"
            PlotLabelSpec.AXIS_TICK -> ".$AXIS .$TICK text"
            PlotLabelSpec.AXIS_TICK_SMALL -> ".$AXIS.$SMALL_TICK_FONT .$TICK text"
            PlotLabelSpec.AXIS_TITLE -> ".$AXIS_TITLE text"
            PlotLabelSpec.LEGEND_TITLE -> ".$LEGEND .$LEGEND_TITLE text"
            PlotLabelSpec.LEGEND_ITEM -> ".$LEGEND text"
        }
    }
}
