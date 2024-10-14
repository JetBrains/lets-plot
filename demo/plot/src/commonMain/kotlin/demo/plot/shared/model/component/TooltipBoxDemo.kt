/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.shared.model.component

import demo.plot.common.model.SimpleDemoBase
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.core.plot.base.render.linetype.LineType
import org.jetbrains.letsPlot.core.plot.base.render.linetype.NamedLineType
import org.jetbrains.letsPlot.core.plot.base.render.svg.GroupComponent
import org.jetbrains.letsPlot.core.plot.builder.presentation.Style.AXIS_TOOLTIP_TEXT
import org.jetbrains.letsPlot.core.plot.builder.presentation.Style.TOOLTIP_LABEL
import org.jetbrains.letsPlot.core.plot.builder.presentation.Style.TOOLTIP_TITLE
import org.jetbrains.letsPlot.core.plot.builder.tooltip.component.TooltipBox
import org.jetbrains.letsPlot.core.plot.builder.tooltip.spec.TooltipSpec

class TooltipBoxDemo : SimpleDemoBase(DEMO_BOX_SIZE) {

    override val cssStyle: String
        get() = """
           |.$TOOLTIP_TEXT_CLASS {
           |    font-size: 18.0px;
           |    fill: #000000;
           |}
           |.$TOOLTIP_TITLE {
           |    font-size: 20.0px;
           |    fill: #000000;
           |    font-weight: bold;
           |}
           |.$TOOLTIP_LABEL {
           |    font-size: 18.0px;
           |    fill: #000000;
           |    font-weight: bold;
           |}
           |.$AXIS_TOOLTIP_TEXT {
           |    font-size: 15.0px;
           |}""".trimMargin()

    // TooltipBox uses SvgPeer - split creation and update so update can be called after attach
    fun createModels(): List<Pair<GroupComponent, () -> Unit>> {
        return myTooltipList.map(::tooltip)
    }

    private fun tooltip(spec: MyTooltipSpec): Pair<GroupComponent, () -> Unit> {
        val groupComponent = GroupComponent()
        val tooltipBox = TooltipBox()
        groupComponent.add(tooltipBox.rootGroup)
        return groupComponent to with(tooltipBox, spec)
    }

    private fun with(
        tooltipBox: TooltipBox,
        spec: MyTooltipSpec,
    ): () -> Unit = {
        with(spec) {
            tooltipBox.update(
                fillColor,
                textColor,
                borderColor,
                strokeWidth,
                lineType,
                lines,
                title,
                textClassName,
                rotate,
                tooltipMinWidth,
                borderRadius,
                markerColors
            )
            tooltipBox.setPosition(
                tooltipCoord = DoubleVector(0.0, 0.0),
                pointerCoord = pointerCoord ?: DoubleVector(0.0, 0.0),
                orientation = orientation,
                rotate = rotate
            )
        }
    }

    companion object {
        const val TOOLTIP_TEXT_CLASS = "tooltip-text"
        private val DEMO_BOX_SIZE = DoubleVector(250.0, 150.0)

        private val WITH_LABEL = TooltipSpec.Line.withLabelAndValue("some label:", "value")
        private val WITH_HYPERLINK = TooltipSpec.Line.withLabelAndValue("It's a <a href=\"https://lets-plot.org/\">link</a>!", "")
        private val STATIC_TEXT = TooltipSpec.Line.withValue("only value")
        private val SPLITTED_TEXT = TooltipSpec.Line.withValue("Line #1\nand\nLine #2")
        private val EMPTY_LINE = TooltipSpec.Line.withValue("")

        private class MyTooltipSpec(
            val fillColor: Color = Color.WHITE,
            val textColor: Color? = Color.BLACK,
            val borderColor: Color = Color.BLACK,
            val strokeWidth: Double = 2.0,
            val lineType: LineType = NamedLineType.SOLID,
            val lines: List<TooltipSpec.Line>,
            val title: String? = null,
            val textClassName: String = TOOLTIP_TEXT_CLASS,
            val rotate: Boolean = false,
            val tooltipMinWidth: Double? = null,
            val borderRadius: Double = 4.0,
            val markerColors: List<Color> = emptyList(),
            val orientation: TooltipBox.Orientation = TooltipBox.Orientation.VERTICAL,
            val pointerCoord: DoubleVector? = null,
        )

        private val myTooltipList = listOf(
            // general tooltip
            MyTooltipSpec(
                fillColor = Color.LIGHT_YELLOW,
                textColor = Color.BLUE,
                borderColor = Color.BLACK,
                lines = listOf(WITH_LABEL, STATIC_TEXT),
                markerColors = listOf(Color.DARK_GREEN, Color.GRAY),
                pointerCoord = DoubleVector(83.0, 90.0)
            ),
            // with hyperlink
            MyTooltipSpec(
                fillColor = Color.LIGHT_YELLOW,
                textColor = Color.BLUE,
                borderColor = Color.BLACK,
                lines = listOf(WITH_HYPERLINK),
                markerColors = listOf(Color.DARK_GREEN, Color.GRAY),
                pointerCoord = DoubleVector(37.0, 40.0)
            ),
           // with horizontal orientation
            MyTooltipSpec(
                fillColor = Color.LIGHT_BLUE,
                textColor = Color.BLUE,
                borderColor = Color.BLUE,
                lines = listOf(WITH_LABEL, STATIC_TEXT),
                markerColors = listOf(Color.LIGHT_PINK, Color.DARK_BLUE),
                pointerCoord = DoubleVector(200.0, 20.0),
                orientation = TooltipBox.Orientation.HORIZONTAL
            ),
            // with title
            MyTooltipSpec(
                lines = listOf(WITH_LABEL, STATIC_TEXT),
                title = "Title",
                markerColors = listOf(Color.LIGHT_PINK, Color.DARK_BLUE),
                pointerCoord = DoubleVector(100.0, 120.0)
            ),
            // with multiline title and lines
            MyTooltipSpec(
                lines = listOf(SPLITTED_TEXT),
                title = "Title #1\nand\nTitle #2",
                pointerCoord = DoubleVector(120.0, 50.0),
                markerColors = listOf(Color.LIGHT_PINK, Color.DARK_BLUE),
                orientation = TooltipBox.Orientation.HORIZONTAL
            ),
            // with empty line
            MyTooltipSpec(
                lines = listOf(WITH_LABEL, EMPTY_LINE, STATIC_TEXT),
                pointerCoord = DoubleVector(100.0, 120.0)
            ),
            /*
            // with splitted text
            MyTooltipSpec(
                lines = listOf(WITH_LABEL, SPLITTED_TEXT),
                pointerCoord = DoubleVector(83.0, 90.0)
            ),*/
            // axis tooltip
            MyTooltipSpec(
                fillColor = Color.GRAY,
                textColor = Color.WHITE,
                borderColor = Color.BLACK,
                lines = listOf(STATIC_TEXT),
                textClassName = AXIS_TOOLTIP_TEXT,
                borderRadius = 0.0
            ),
            // rotated tooltip
            MyTooltipSpec(
                lines = listOf(STATIC_TEXT),
                rotate = true,
                pointerCoord = DoubleVector(30.0, 100.0)
            ),
            // rotated multiline tooltip
            MyTooltipSpec(
                lines = listOf(SPLITTED_TEXT),
                rotate = true,
                pointerCoord = DoubleVector(30.0, 80.0)
            )
        )
    }
}
