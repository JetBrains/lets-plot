package jetbrains.datalore.visualization.plot.builder.presentation

/**
 * Duplicating stylesheet for JavaFX platform is defined in
 * visualization-plot-builder/src/jvmMain/resources/svgMapper/jfx/plot.css
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

    const val PLOT_GLASS_PANE = "plt-glass-pane"
    const val PLOT_TOOLTIP = "plt-tooltip"
    const val AXIS_TOOLTIP = "axis-tooltip"

    val CSS = CssResourceBuilder()
        .add(SelectorBuilder(PLOT_CONTAINER)
            .fontFamily(Defaults.FONT_FAMILY_NORMAL)
        )
        .add(SelectorBuilder(SelectorType.TEXT)
            .fontSize(Defaults.FONT_MEDIUM, SizeMeasure.PX)
            .fill(Defaults.TEXT_COLOR)
        )
        .add(SelectorBuilder(PLOT_GLASS_PANE)
            .cursor(CursorValue.CROSSHAIR)
        )
        .add(SelectorBuilder(PLOT_TOOLTIP)
            .pointerEvents(PointerEventsValue.NONE)
            .opacity(0.0f)
        )
        .add(SelectorBuilder(listOf(PLOT_TOOLTIP, "shown"))
            .opacity(1.0f)
        )
        .add(SelectorBuilder(listOf(PLOT_TOOLTIP, "shown")).innerSelector("back")
            .opacity(1.0f)
        )
        .add(SelectorBuilder(AXIS).innerSelector(SelectorType.LINE)
            .shapeRendering(ShapeRenderingValue.CRISPEDGES)
        )
        .add(SelectorBuilder("highlight")
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
