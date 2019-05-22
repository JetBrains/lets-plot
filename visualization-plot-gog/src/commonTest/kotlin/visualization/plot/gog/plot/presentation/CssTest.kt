package visualization.plot.gog.plot.presentation

import jetbrains.datalore.visualization.plot.gog.plot.presentation.Defaults
import jetbrains.datalore.visualization.plot.gog.plot.presentation.Style
import kotlin.test.Test
import kotlin.test.assertEquals

class CssTest {

    private val cssTest = """.plt-container {
    font-family: """ + Defaults.FONT_FAMILY_NORMAL + """;
}
text {
    font-size: """ + Defaults.FONT_MEDIUM  +"""px;
    fill: """ + Defaults.TEXT_COLOR + """;
}
.plt-glass-pane {
    cursor: crosshair;
}
.plt-tooltip {
    pointer-events: none;
    opacity: 0.0;
}
.plt-tooltip.shown {
    opacity: 1.0;
}
.plt-tooltip.shown .back {
    opacity: 0.8;
}
.plt-tooltip text {
    font-size: 12px;
}
.plt-axis line {
    shape-rendering: crispedges;
}
.highlight {
    fill-opacity: 0.75;
}
"""

    @Test
    fun cssCompare() {
        val cssResource = Style.CSS
        val cssString = cssResource.toString().replace("\t", "    ")

        assertEquals(cssTest, cssString)
    }
}