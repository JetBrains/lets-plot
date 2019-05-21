package jetbrains.datalore.visualization.plot.builder.presentation

import visualization.plot.gog.plot.presentation.CssResource
import visualization.plot.gog.plot.presentation.StyleType
import visualization.plot.gog.plot.presentation.Selector
import kotlin.jvm.JvmStatic

object Style {
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

    private val CSS_CLASS = CssResource().
            addSelector(Selector(".$PLOT_CONTAINER").addStyle(StyleType.FONT_FAMILY, Defaults.FONT_FAMILY_NORMAL)).
            addSelector(Selector("text").addStyle(StyleType.FONT_SIZE, Defaults.FONT_MEDIUM, "px").addStyle(StyleType.FILL, Defaults.TEXT_COLOR)).
            addSelector(Selector(".$PLOT_GLASS_PANE").addStyle(StyleType.CURSOR, "crosshair")).
            addSelector(Selector(".$PLOT_TOOLTIP").addStyle(StyleType.POINTER_EVENTS, "none").addStyle(StyleType.OPACITY, 0)).
            addSelector(Selector(".$PLOT_TOOLTIP.shown").addStyle(StyleType.OPACITY, 1)).
            addSelector(Selector(listOf(".$PLOT_TOOLTIP.shown", ".back")).addStyle(StyleType.OPACITY, 0.8)).
            addSelector(Selector(listOf(".$PLOT_TOOLTIP", "text")).addStyle(StyleType.FONT_SIZE, Defaults.Common.Tooltip.FONT_SIZE, "px")).
            addSelector(Selector(listOf(".$AXIS", "line")).addStyle(StyleType.SHAPE_RENDERING, "crispedges")).
            addSelector(Selector(".highlight").addStyle(StyleType.FILL_OPACITY, 0.75))

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

    @JvmStatic
    fun main(args: Array<String>) {
        val pattern = "\\s+".toRegex()
        val a = pattern.replace(CSS, " ").trimStart()
        val b = pattern.replace(CSS_CLASS.toString(), " ").trimStart()

        if (a != b) {
            throw Exception("String not equal")
        }
    }

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
