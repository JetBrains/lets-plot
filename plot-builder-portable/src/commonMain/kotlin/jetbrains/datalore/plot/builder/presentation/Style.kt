/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.presentation

import jetbrains.datalore.plot.builder.presentation.Defaults.FONT_FAMILY_NORMAL
import jetbrains.datalore.plot.builder.presentation.Defaults.FONT_MEDIUM
import jetbrains.datalore.plot.builder.presentation.Defaults.TEXT_COLOR

/**
 * Duplicating stylesheet for JavaFX platform is defined in
 * plot-builder/src/jvmMain/resources/svgMapper/jfx/plot.css
 */
object Style {
    const val JFX_PLOT_STYLESHEET = "/svgMapper/jfx/plot.css"

    val DEFAULT_STYLE_RENDERER = DefaultTextStyler()

    // classes
    const val PLOT_CONTAINER = "plt-container"
    const val PLOT = "plt-plot"
    const val PLOT_TITLE = "plot-title"
    const val PLOT_SUBTITLE = "plot-subtitle"
    const val PLOT_CAPTION = "plot-caption"

    const val AXIS = "plt-axis"
    const val AXIS_TITLE = "axis-title"
    const val AXIS_TEXT = "axis-text"
    const val TICK = "tick"

    const val LEGEND = "legend"
    const val LEGEND_TITLE = "legend-title"
    const val LEGEND_ITEM = "legend-item"

    const val TOOLTIP_TEXT = "tooltip-text"
    const val TOOLTIP_TITLE = "tooltip-title"
    const val TOOLTIP_LABEL = "tooltip-label"
    const val AXIS_TOOLTIP = "axis-tooltip"

    const val FACET_STRIP_TEXT = "facet-strip-text"

    private val CSS = """
        |.$PLOT_CONTAINER {
        |	font-family: $FONT_FAMILY_NORMAL;
        |	cursor: crosshair;
        |	user-select: none;
        |	-webkit-user-select: none;
        |	-moz-user-select: none;
        |	-ms-user-select: none;
        |}
        |text {
        |	font-size: ${FONT_MEDIUM}px;
        |	fill: $TEXT_COLOR;
        |	
        |	text-rendering: optimizeLegibility;
        |}
        |.$AXIS line {
        |	shape-rendering: crispedges;
        |}
    """.trimMargin()

    fun generateCSS(): String {
        val css = StringBuilder(CSS)
        css.append('\n')
        css.append(DEFAULT_STYLE_RENDERER.css())
        return css.toString()
    }
}
