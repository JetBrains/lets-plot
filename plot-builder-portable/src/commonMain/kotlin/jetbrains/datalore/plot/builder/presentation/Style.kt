/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.presentation

import jetbrains.datalore.plot.builder.presentation.Defaults.Common.Tooltip.AXIS_TOOLTIP_FONT_SIZE
import jetbrains.datalore.plot.builder.presentation.SelectorType.LINE
import jetbrains.datalore.plot.builder.presentation.SelectorType.TEXT

/**
 * Duplicating stylesheet for JavaFX platform is defined in
 * plot-builder/src/jvmMain/resources/svgMapper/jfx/plot.css
 *
 * ToDo: revert this code back to plane readable text.
 */
object Style {
    const val JFX_PLOT_STYLESHEET = "/svgMapper/jfx/plot.css"

    const val PLOT_CONTAINER = "plt-container"
    const val PLOT = "plt-plot"
    const val PLOT_TITLE = "plt-plot-title"

    const val AXIS = "plt-axis"

    const val AXIS_TITLE = "plt-axis-title"
    const val TICK = "tick"
    const val SMALL_TICK_FONT = "small-tick-font"

    const val BACK = "back"

    const val LEGEND = "plt_legend"
    const val LEGEND_TITLE = "legend-title"

    const val PLOT_DATA_TOOLTIP = "plt-data-tooltip"
    const val PLOT_AXIS_TOOLTIP = "plt-axis-tooltip"
    val CSS = CssResourceBuilder()
        .add(
            SelectorBuilder(PLOT_CONTAINER)
            .fontFamily(Defaults.FONT_FAMILY_NORMAL)
            .cursor(CursorValue.CROSSHAIR)
            .userSelect(UserSelectValue.NONE)
        )
        .add(
            SelectorBuilder(TEXT)
            .fontSize(
                Defaults.FONT_MEDIUM,
                SizeMeasure.PX
            )
            .fill(Defaults.TEXT_COLOR)
        )
        .add(
            SelectorBuilder(PLOT_DATA_TOOLTIP)
            .innerSelector(TEXT)
            .fontSize(
                Defaults.Common.Tooltip.DATA_TOOLTIP_FONT_SIZE,
                SizeMeasure.PX
            )
        )
        .add(
            SelectorBuilder(PLOT_AXIS_TOOLTIP).innerSelector(TEXT)
            .fontSize(AXIS_TOOLTIP_FONT_SIZE,
                SizeMeasure.PX
            )
        )
        .add(
            SelectorBuilder(AXIS).innerSelector(LINE)
            .shapeRendering(ShapeRenderingValue.CRISPEDGES)
        )
        .add(
            SelectorBuilder("highlight")
            .fillOpacity(0.75f)
        )
        .build()

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
