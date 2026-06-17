/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip.component

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.core.plot.base.render.svg.Label
import org.jetbrains.letsPlot.core.plot.base.render.svg.SvgComponent
import org.jetbrains.letsPlot.core.plot.base.render.svg.SvgUID
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text
import org.jetbrains.letsPlot.core.plot.base.render.text.LineBoxMetrics
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText
import org.jetbrains.letsPlot.core.plot.base.render.text.TextBlockLayout
import org.jetbrains.letsPlot.core.plot.base.theme.DefaultFontFamilyRegistry
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipDefaults
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipMarker
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipModel
import org.jetbrains.letsPlot.core.plot.base.tooltip.TooltipStyle
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgCssResource
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathDataBuilder
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgPathElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgSvgElement
import org.jetbrains.letsPlot.datamodel.svg.style.StyleSheet
import org.jetbrains.letsPlot.datamodel.svg.style.TextStyle
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

internal class ContentBox(
    private val styleSheet: StyleSheet
) : SvgComponent() {
    private val titleContainer = SvgSvgElement().apply {
        x().set(0.0)
        y().set(0.0)
        width().set(0.0)
        height().set(0.0)
    }
    private val linesContainer = SvgSvgElement().apply {
        x().set(0.0)
        y().set(0.0)
        width().set(0.0)
        height().set(0.0)
    }
    private val content = SvgSvgElement().apply {
        x().set(0.0)
        y().set(0.0)
        width().set(0.0)
        height().set(0.0)
    }

    private val colorBars = mutableListOf<SvgPathElement>()
    private val lineYBounds = mutableListOf<Pair<Double, Double>>()
    private val yPositionsBetweenLines = mutableListOf<Double>()
    private val debugRectangles = SvgComponentPool(
        itemFactory = ::RectangleComponent,
        parent = rootGroup
    )
    private var colorBarIndent = 0.0
    private var horizontalContentPadding = TooltipDefaults.H_CONTENT_PADDING
    private var verticalContentPadding = TooltipDefaults.V_CONTENT_PADDING

    val dimension get() = content.run { DoubleVector(width().get()!!, height().get()!!) }
    private val contentRect get() = DoubleRectangle.span(DoubleVector.ZERO, dimension)

    override fun buildComponent() {
        setSvgSvgStyle(content, "tt-c-")
        setSvgSvgStyle(titleContainer, "tt-t-")
        setSvgSvgStyle(linesContainer, "tt-l-")

        add(content)
        content.children().add(titleContainer)
        content.children().add(linesContainer)
    }

    fun update(
        targets: List<TooltipModel.Target>,
        title: String?,
        valueTextColor: Color?,
        tooltipMinWidth: Double?,
        textClassName: String
    ) {
        linesContainer.children().clear()
        titleContainer.children().clear()
        yPositionsBetweenLines.clear()

        val targetLayout = layoutTargets(targets)
        updateContentPadding(targetLayout.lines.size + if (title != null) 1 else 0)
        calculateColorBarIndent(targets)

        val titleComponent = title?.let(::initTitleComponent)
        val rawTitleBBox = getBBox(titleComponent) ?: DoubleRectangle.ZERO
        val minWidthWithTitle = listOfNotNull(tooltipMinWidth, rawTitleBBox.width).maxOrNull()
        val textSize = layoutLines(
            targetLayout.lines,
            valueTextColor,
            minWidthWithTitle,
            textClassName
        )

        val totalTooltipWidth = textSize.x + colorBarIndent + horizontalContentPadding * 2
        val titleTextSize = layoutTitle(
            titleComponent,
            totalTooltipWidth,
            rawTitleBBox
        )

        titleContainer.apply {
            if (titleComponent != null) {
                x().set(0.0)
                y().set(verticalContentPadding)
                width().set(totalTooltipWidth)
                height().set(titleTextSize.y)
            }
        }

        linesContainer.apply {
            x().set(horizontalContentPadding + colorBarIndent)
            y().set(titleTextSize.y + verticalContentPadding)
            width().set(totalTooltipWidth - horizontalContentPadding)
            height().set(textSize.y + titleTextSize.y + verticalContentPadding)
        }

        content.apply {
            width().set(totalTooltipWidth)
            height().set(textSize.y + titleTextSize.y + verticalContentPadding * 2)
        }

        layoutColorBars(targets, targetLayout.lineRanges)
        drawLineSeparators(
            yTitleLinePosition = if (titleComponent != null) titleTextSize.y - verticalContentPadding / 2 else null,
            yPositionsBetweenLines
        )
    }

    private fun updateContentPadding(totalLines: Int) {
        horizontalContentPadding = if (totalLines > 1) {
            TooltipDefaults.CONTENT_EXTENDED_PADDING
        } else {
            TooltipDefaults.H_CONTENT_PADDING
        }
        verticalContentPadding = if (totalLines > 1) {
            TooltipDefaults.CONTENT_EXTENDED_PADDING
        } else {
            TooltipDefaults.V_CONTENT_PADDING
        }
    }

    private fun colorBars(marker: TooltipMarker): List<Pair<Color, Double>> {
        return when {
            marker.majorColor != null && marker.minorColor != null -> listOf(
                marker.minorColor to TooltipDefaults.COLOR_BAR_STROKE_WIDTH,
                marker.majorColor to TooltipDefaults.COLOR_BAR_WIDTH,
                marker.minorColor to TooltipDefaults.COLOR_BAR_STROKE_WIDTH
            )

            marker.majorColor != null -> listOf(marker.majorColor to TooltipDefaults.COLOR_BAR_WIDTH)

            marker.minorColor != null -> listOf(marker.minorColor to TooltipDefaults.COLOR_BAR_STROKE_WIDTH)

            else -> emptyList()
        }
    }

    private fun layoutTargets(targets: List<TooltipModel.Target>): TargetsLayout {
        val lines = ArrayList<TooltipModel.Line>()
        val lineRanges = ArrayList<IntRange>()
        targets.forEach { target ->
            val firstLineIndex = lines.size
            target.title?.let { lines += TooltipModel.Line.withValue(it) }
            lines += target.lines
            val lastLineIndex = lines.lastIndex
            lineRanges += firstLineIndex..lastLineIndex
        }
        return TargetsLayout(lines, lineRanges)
    }

    private fun calculateColorBarIndent(targets: List<TooltipModel.Target>) {
        val markerWidth = targets.maxOfOrNull { target ->
            colorBars(target.marker).sumOf { (_, width) -> width }
        }
        colorBarIndent = if (markerWidth != null && markerWidth != 0.0) {
            markerWidth + horizontalContentPadding
        } else {
            0.0
        }
    }

    private fun layoutColorBars(targets: List<TooltipModel.Target>, lineRanges: List<IntRange>) {
        val targetColorBars = targets.zip(lineRanges).flatMap { (target, lineRange) ->
            val yBounds = lineRange
                .filter { it in lineYBounds.indices }
                .map(lineYBounds::get)
            val top = yBounds.minOfOrNull { (top, _) -> top }
            val bottom = yBounds.maxOfOrNull { (_, bottom) -> bottom }
            if (top == null || bottom == null) {
                emptyList()
            } else {
                var xOffset = 0.0
                colorBars(target.marker).map { colorBar ->
                    BlockColorBar(colorBar.first, colorBar.second, top, bottom, xOffset).also {
                        xOffset += colorBar.second
                    }
                }
            }
        }
        updateColorBarCount(targetColorBars.size)

        colorBars
            .zip(targetColorBars)
            .forEach { (bar, colorBar) ->
                val x = contentRect.left + horizontalContentPadding + colorBar.xOffset
                bar.fillOpacity().set(1.0)
                bar.fillColor().set(colorBar.color)
                bar.d().set(
                    SvgPathDataBuilder().apply {
                        val y = linesContainer.y().get()!! + colorBar.top
                        val bottom = linesContainer.y().get()!! + colorBar.bottom
                        moveTo(x, y)
                        horizontalLineTo(x + colorBar.width)
                        verticalLineTo(bottom)
                        horizontalLineTo(x)
                        verticalLineTo(y)
                    }.build()
                )
            }
    }

    private fun updateColorBarCount(count: Int) {
        while (colorBars.size > count) {
            val bar = colorBars.removeLast()
            rootGroup.children().remove(bar)
        }
        while (colorBars.size < count) {
            SvgPathElement().also { bar ->
                rootGroup.children().add(bar)
                colorBars.add(bar)
            }
        }
    }

    private data class TargetsLayout(
        val lines: List<TooltipModel.Line>,
        val lineRanges: List<IntRange>
    )

    private data class BlockColorBar(
        val color: Color,
        val width: Double,
        val top: Double,
        val bottom: Double,
        val xOffset: Double
    )

    private fun getBBox(textLabel: Label?): DoubleRectangle? {
        if (textLabel == null || textLabel.text.isBlank()) {
            return null
        }
        return textLabel.rootGroup.bBox
    }

    private fun initTitleComponent(titleLine: String): Label {
        val fontSize = styleSheet.getTextStyle(TooltipStyle.TOOLTIP_TITLE).size
        val titleComponent = Label(titleLine)
        titleComponent.addClassName(TooltipStyle.TOOLTIP_TITLE)
        titleComponent.setHorizontalAnchor(Text.HorizontalAnchor.MIDDLE)
        val defaultMetrics = LineBoxMetrics.fromBoxHeight(fontSize)
        val metricsByLine = estimateLineLayoutMetrics(titleLine, TooltipStyle.TOOLTIP_TITLE).map { it ?: defaultMetrics }
        titleComponent.setTextLayout(TextBlockLayout.fromLineBoxes(metricsByLine))
        titleComponent.setFontSize(fontSize)

        titleContainer.children().add(titleComponent.rootGroup)
        return titleComponent
    }

    private fun estimateLineLayoutMetrics(line: String, className: String): List<LineBoxMetrics?> {
        val style = styleSheet.getTextStyle(className)
        return line
            .split("\n")
            .map { Label(it).apply {
                addClassName(className)
                setFontSize(style.size)
            } }
            .map { lineTextLabel ->
                with(linesContainer.children()) {
                    add(lineTextLabel.rootGroup)
                    val height = getBBox(lineTextLabel)?.height
                    remove(lineTextLabel.rootGroup)
                    height
                }
            }
            .let { estimatedHeights ->
                estimateBaseline(line, style, estimatedHeights)
            }
    }

    private fun estimateBaseline(
        line: String,
        style: TextStyle,
        estimatedHeights: List<Double?>
    ): List<LineBoxMetrics?> {
        val font = Font(
            family = DefaultFontFamilyRegistry().get(style.family),
            size = style.size.roundToInt(),
            isBold = style.face.bold,
            isItalic = style.face.italic
        )
        val estimatedMetrics = RichText.measure(line, font).layout.lineBoxes
        if (estimatedMetrics.size == estimatedHeights.size) {
            val scale = estimatedMetrics.sumOf(LineBoxMetrics::boxHeight).let { totalMetricsHeight ->
                if (totalMetricsHeight > 0) {
                    estimatedHeights.filterNotNull().sum() / totalMetricsHeight
                } else {
                    1.0
                }
            }
            return (estimatedMetrics zip estimatedHeights).map { (metrics, height) ->
                if (height == null) {
                    null
                } else {
                    LineBoxMetrics(
                        boxHeight = metrics.boxHeight * scale + TooltipDefaults.INTERVAL_BETWEEN_SUBSTRINGS,
                        topToBaseline = metrics.topToBaseline * scale + TooltipDefaults.INTERVAL_BETWEEN_SUBSTRINGS
                    )
                }
            }
        }
        return estimatedMetrics
    }

    private fun layoutTitle(
        titleComponent: Label?,
        totalTooltipWidth: Double,
        titleBBox: DoubleRectangle
    ): DoubleVector {
        if (titleComponent == null) {
            return DoubleVector.ZERO
        }

        titleComponent.setX(totalTooltipWidth / 2)
        titleComponent.setY(-titleBBox.top)

        return DoubleVector(totalTooltipWidth, verticalContentPadding + titleBBox.height)
    }

    private fun layoutLines(
        lines: List<TooltipModel.Line>,
        valueTextColor: Color?,
        tooltipMinWidth: Double?,
        textClassName: String
    ): DoubleVector {
        lineYBounds.clear()
        val labelFontSize = styleSheet.getTextStyle(TooltipStyle.TOOLTIP_LABEL).size
        val valueFontSize = styleSheet.getTextStyle(textClassName).size
        val components: List<Pair<Label?, Label>> = lines
            .map { line ->
                Pair(
                    line.label?.let(::Label),
                    Label(line.value, wrapWidth = TooltipDefaults.VALUE_LINE_MAX_LENGTH)
                )
            }
        components.onEach { (labelComponent, _) ->
            if (labelComponent != null) {
                labelComponent.addClassName(TooltipStyle.TOOLTIP_LABEL)
                labelComponent.setFontSize(labelFontSize)
                linesContainer.children().add(labelComponent.rootGroup)
            }
        }
        components.onEach { (_, valueComponent) ->
            valueComponent.addClassName(textClassName)
            valueComponent.setFontSize(valueFontSize)
            valueTextColor?.let(valueComponent.textColor()::set)
            linesContainer.children().add(valueComponent.rootGroup)
        }

        val defaultLabelMetrics = LineBoxMetrics.fromBoxHeight(labelFontSize)
        val defaultValueMetrics = LineBoxMetrics.fromBoxHeight(valueFontSize)
        val metricsByLine: List<Pair<List<LineBoxMetrics>?, List<LineBoxMetrics>>> = lines.map { line ->
            Pair(
                line.label?.let {
                    estimateLineLayoutMetrics(it, TooltipStyle.TOOLTIP_LABEL).map { labelMetrics -> labelMetrics ?: defaultLabelMetrics }
                },
                estimateLineLayoutMetrics(line.value, textClassName).map { valueMetrics -> valueMetrics ?: defaultValueMetrics }
            )
        }

        metricsByLine.zip(components).onEach { (metrics, component) ->
            val (labelMetrics, valueMetrics) = metrics
            val (labelComponent, valueComponent) = component
            labelMetrics?.let { labelComponent?.setTextLayout(TextBlockLayout.fromLineBoxes(it)) }
            valueComponent.setTextLayout(TextBlockLayout.fromLineBoxes(valueMetrics))
        }

        val rawBBoxes = components.map { (label, value) -> getBBox(label) to getBBox(value) }
        val maxLabelWidth = rawBBoxes.maxOf { (labelBbox) -> labelBbox?.width ?: 0.0 }
        val defaultLineHeight = metricsByLine
            .flatMap { (labelMetrics, valueMetrics) ->
                listOfNotNull(
                    labelMetrics?.maxOfOrNull(LineBoxMetrics::boxHeight),
                    valueMetrics.maxOfOrNull(LineBoxMetrics::boxHeight)
                )
            }
            .maxOrNull()
            ?: valueFontSize

        val labelWidths = lines.zip(components).map { (line, component) ->
            val label = line.label
            when {
                label == null -> 0.0
                label.isEmpty() && component.second.linesCount() == 1 -> maxLabelWidth
                else -> maxLabelWidth + TooltipDefaults.LABEL_VALUE_INTERVAL
            }
        }
        val valueWidths = rawBBoxes.map { (_, valueBBox) -> valueBBox?.dimension?.x ?: 0.0 }
        val lineWidths = labelWidths.zip(valueWidths)
        val maxLineWidth = lineWidths.maxOf { (labelWidth, valueWidth) ->
            max(tooltipMinWidth ?: 0.0, labelWidth + valueWidth)
        }

        val lineBBoxes = rawBBoxes.zip(lineWidths).map { (bBoxes, width) ->
            val (labelBBox, valueBBox) = bBoxes
            val (labelWidth, valueWidth) = width

            val labelDimension = DoubleVector(
                labelWidth,
                labelBBox?.run { height + top } ?: 0.0
            )
            val valueDimension = DoubleVector(
                valueWidth,
                valueBBox?.run { height + top } ?: if (labelBBox == null) {
                    defaultLineHeight
                } else {
                    0.0
                }
            )
            Pair(
                DoubleRectangle(labelBBox?.origin ?: DoubleVector.ZERO, labelDimension),
                DoubleRectangle(valueBBox?.origin ?: DoubleVector.ZERO, valueDimension)
            )
        }

        val textSize = components
            .zip(lineBBoxes)
            .fold(DoubleVector.ZERO) { textDimension, (lineInfo, bBoxes) ->
                val (labelComponent, valueComponent) = lineInfo
                val (labelBBox, valueBBox) = bBoxes
                val yPosition = textDimension.y - min(valueBBox.top, labelBBox.top)
                valueComponent.setY(yPosition)
                labelComponent?.setY(yPosition)

                when {
                    labelComponent != null && labelBBox.dimension.x > 0 -> {
                        labelComponent.setX(-labelBBox.left)

                        if (valueComponent.linesCount() > 1) {
                            valueComponent.setX(maxLabelWidth + TooltipDefaults.LABEL_VALUE_INTERVAL)
                            valueComponent.setHorizontalAnchor(Text.HorizontalAnchor.LEFT)
                        } else {
                            valueComponent.setX(maxLineWidth)
                            valueComponent.setHorizontalAnchor(Text.HorizontalAnchor.RIGHT)
                        }
                    }

                    valueBBox.dimension.x == maxLineWidth && valueComponent.linesCount() == 1 -> {
                        valueComponent.setX(-valueBBox.left)
                    }

                    else -> {
                        valueComponent.setX(maxLineWidth / 2)
                        valueComponent.setHorizontalAnchor(Text.HorizontalAnchor.MIDDLE)
                    }
                }

                val y = yPosition + max(valueBBox.height, labelBBox.height)
                lineYBounds += textDimension.y to y
                yPositionsBetweenLines.add(y + TooltipDefaults.LINE_INTERVAL / 2)

                DoubleVector(
                    x = maxLineWidth,
                    y = y + TooltipDefaults.LINE_INTERVAL
                )
            }.subtract(DoubleVector(0.0, TooltipDefaults.LINE_INTERVAL))
            .also { yPositionsBetweenLines.removeLastOrNull() }

        return textSize
    }

    private fun drawLineSeparators(yTitleLinePosition: Double?, yPositionsBetweenLines: List<Double>) {
        fun drawLineSeparator(path: SvgPathElement, toSvgElem: SvgSvgElement) {
            path.strokeWidth().set(TooltipDefaults.LINE_SEPARATOR_WIDTH)
            path.strokeOpacity().set(1.0)
            path.strokeColor().set(Color.gray(80))

            toSvgElem.children().add(path)
        }

        if (yTitleLinePosition != null) {
            val pathData = SvgPathDataBuilder().apply {
                moveTo(horizontalContentPadding, yTitleLinePosition)
                horizontalLineTo(titleContainer.width().get()!! - horizontalContentPadding)
            }.build()
            drawLineSeparator(SvgPathElement(pathData), titleContainer)
        }

        yPositionsBetweenLines.map { y ->
            SvgPathDataBuilder().apply {
                with(content) {
                    val padding = 2.0
                    moveTo(x().get()!! + padding, y)
                    horizontalLineTo(width().get()!! - horizontalContentPadding * 2 - colorBarIndent - padding)
                }
            }.build()
        }.forEach { pathData -> drawLineSeparator(SvgPathElement(pathData), linesContainer) }
    }

    fun drawDebugRect() {
        fun drawRect(rectComponent: RectangleComponent, svgElem: SvgSvgElement, color: Color) {
            rectComponent.update(
                svgElem.x().get()!!,
                svgElem.y().get()!!,
                svgElem.width().get()!!,
                svgElem.height().get()!!,
                color
            )
        }

        val rectangles = debugRectangles.provide(3)
        drawRect(rectangles[0], content, Color.RED)
        drawRect(rectangles[1], titleContainer, Color.DARK_GREEN)
        drawRect(rectangles[2], linesContainer, Color.ORANGE)
    }

    private fun setSvgSvgStyle(svgSvgElement: SvgSvgElement, prefix: String) {
        val id = SvgUID.get(prefix)
        svgSvgElement.id().set(id)
        svgSvgElement.setStyle(object : SvgCssResource {
            override fun css(): String {
                return buildString {
                    styleSheet.getClasses().forEach { className ->
                        append(styleSheet.toCSS(className, id))
                    }
                }
            }
        })
    }

    private class RectangleComponent : SvgComponent() {
        private val rect = SvgPathElement()

        init {
            rect.strokeWidth().set(1.0)
            rect.fillOpacity().set(0.0)
        }

        override fun buildComponent() {
            add(rect)
        }

        fun update(x: Double, y: Double, w: Double, h: Double, color: Color) {
            val pathData = SvgPathDataBuilder().apply {
                moveTo(x, y)
                horizontalLineTo(w)
                verticalLineTo(h)
                horizontalLineTo(x)
                verticalLineTo(y)
            }.build()
            rect.d().set(pathData)
            rect.strokeColor().set(color)
        }
    }

}

