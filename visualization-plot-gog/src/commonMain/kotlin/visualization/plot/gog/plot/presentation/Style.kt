package jetbrains.datalore.visualization.plot.gog.plot.presentation

/**
 * We have to define styles in plane text instead of
 * using GWT resource support (ResourceBundle)
 * because we need styles to work in both - web browser and AWT (via Batik).
 * Unfortunately, GWT ResourceBundle won't work in AWT.
 */
object Style {
    val PLOT_CONTAINER = "plt-container"
    val PLOT = "plt-plot"
    val PLOT_TITLE = "plt-plot-title"

    val AXIS = "plt-axis"

    val AXIS_TITLE = "plt-axis-title"
    val TICK = "tick"
    val SMALL_TICK_FONT = "small-tick-font"

    val BACK = "back"

    val LEGEND = "plt_legend"
    val LEGEND_TITLE = "legend-title"

    val PLOT_GLASS_PANE = "plt-glass-pane"

    private val CSS = "                                              " +
            " .plt-container {" +
            "    font-family: " + Defaults.FONT_FAMILY_NORMAL + ";" +
            " }                                            " +
            "                                              " +
            " text {" +
            "   font-size: " + Defaults.FONT_MEDIUM + "px;" +
            "   fill: " + Defaults.TEXT_COLOR + ";" +
            " }                                            " +
            "                                              " +
            " .plt-glass-pane {                                            " +
            "    cursor: crosshair;                                " +
            " }                                             " +
            "                                              " +
            " .plt-tooltip {                                            " +
            "    pointer-events: none;                                " +
            "    opacity: 0;                                          " +
            " }                                             " +
            " .plt-tooltip.shown {                                            " +
            "    opacity: 1;                                          " +
            " }                                             " +
            " .plt-tooltip.shown .back {                                            " +
            "    opacity: 0.8;                                          " +
            " }                                             " +
            " .plt-tooltip text {                                            " +
            "   font-size: " + Defaults.Common.Tooltip.FONT_SIZE + "px;" +
            " }                                             " +
            "                                              " +
            " .plt-axis line {                                      " +
            "   shape-rendering: crispedges;               " +
            " } " +
            "                                              " +
            " .highlight {" +
            "   fill-opacity: 0.75;" +
            " }" +
            "                                              " +
            ""


    val css: String
        get() {
            val css = StringBuilder(CSS)
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
