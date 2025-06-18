/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.svg

import org.jetbrains.letsPlot.commons.intern.observable.property.WritableProperty
import org.jetbrains.letsPlot.commons.intern.util.TextWidthEstimator
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.commons.values.FontFamily
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text.HorizontalAnchor
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text.VerticalAnchor
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text.toDY
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text.toTextAnchor
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgAElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgConstants
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTSpanElement
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextElement
import kotlin.math.roundToInt


class MultilineLabel(
    val text: String,
    private val wrapWidth: Int = -1,
    private val markdown: Boolean = false
) : SvgComponent() {
    private var myLinesSize: Int = 0
    private var myClassName: String? = null
    private var myTextColor: Color? = null
    private var myTextOpacity: Double? = null
    private var myFontSize = 0.0
    private var myFontWeight: String? = null
    private var myFontFamily: String? = null
    private var myFontStyle: String? = null
    private var myLineHeight = 0.0
    private var myHorizontalAnchor: HorizontalAnchor = RichText.DEF_HORIZONTAL_ANCHOR
    private var myVerticalAnchor: VerticalAnchor? = null
    private var xStart: Double? = null
    private var yStart = 0.0

    init {
        resetLines()
    }

    override fun buildComponent() {
    }

    override fun addClassName(className: String) {
        myClassName = className
        resetLines()
    }

    fun textColor(): WritableProperty<Color?> {
        return object : WritableProperty<Color?> {
            override fun set(value: Color?) {
                // duplicate in 'style' to override styles of container
                myTextColor = value
                resetLines()
            }
        }
    }

    fun setHorizontalAnchor(anchor: HorizontalAnchor) {
        myHorizontalAnchor = anchor
        resetLines()
    }

    fun setVerticalAnchor(anchor: VerticalAnchor) {
        myVerticalAnchor = anchor
        resetLines()
    }

    fun setFontSize(px: Double) {
        myFontSize = px
        resetLines()
    }

    /**
     * @param cssName : normal, bold, bolder, lighter
     */
    fun setFontWeight(cssName: String?) {
        myFontWeight = cssName
        resetLines()
    }

    /**
     * @param cssName : normal, italic, oblique
     */
    fun setFontStyle(cssName: String?) {
        myFontStyle = cssName
        resetLines()
    }

    /**
     * @param fontFamily : for example 'sans-serif' or 'Times New Roman'
     */
    fun setFontFamily(fontFamily: String?) {
        myFontFamily = fontFamily
        resetLines()
    }

    fun setTextOpacity(value: Double?) {
        myTextOpacity = value
        resetLines()
    }

    fun setX(x: Double) {
        xStart = x
        resetLines()
    }

    fun setY(y: Double) {
        yStart = y
        resetLines()
    }

    fun setLineHeight(v: Double) {
        myLineHeight = v
        resetLines()
    }

    private fun resetLines() {
        rootGroup.children().clear()
        constructLines().forEach(rootGroup.children()::add)
    }

    private fun constructLines(): List<SvgTextElement> {
        // TODO: DefaultFontFamilyRegistry().get(myFontFamily) should be used, but to do it DefaultFontFamilyRegistry should be moved to the plot-base module
        // Note that `font` needed only to estimate text width for lines with LaTeX formulas, that contains fractions
        val fontFamily = myFontFamily ?: FontFamily.DEF_FAMILY_NAME
        val font = Font(
            family = FontFamily(fontFamily, fontFamily == "monospace"),
            size = myFontSize.roundToInt(),
            isBold = myFontWeight == "bold",
            isItalic = myFontStyle == "italic"
        )
        val styleAttr = Text.buildStyle(
            myTextColor,
            myFontSize,
            myFontWeight,
            myFontFamily,
            myFontStyle
        )
        val lines = RichText.toSvg(
            text,
            font,
            TextWidthEstimator::widthCalculator,
            wrapWidth,
            markdown = markdown,
            anchor = myHorizontalAnchor
        )
        myLinesSize = lines.size
        val actualHorizontalAnchor = getActualHorizontalAnchor(lines)
        return lines.map(updateLinesAttributes(styleAttr))
            .map(updateAnchors(actualHorizontalAnchor))
            .mapIndexed(::repositionLines)
    }

    private fun getActualHorizontalAnchor(lines: List<SvgTextElement>): HorizontalAnchor {
        val firstNodeHasDefinedX = lines.any { line ->
            getFirstTSpanChild(line)?.x()?.get() != null
        }
        return when (firstNodeHasDefinedX) {
            true -> HorizontalAnchor.LEFT
            false -> myHorizontalAnchor
        }
    }

    private fun updateLinesAttributes(styleAttr: String): (SvgTextElement) -> SvgTextElement {
        return { line ->
            line.setAttribute(SvgConstants.SVG_STYLE_ATTRIBUTE, styleAttr)
            myClassName?.let { line.addClass(it) }
            myTextColor?.let { line.fillColor() } // set attribute for svg->canvas mapping to work
            myTextOpacity?.let { line.fillOpacity().set(it) }
            xStart?.let { line.x().set(it) }
            line
        }
    }

    private fun updateAnchors(horizontalAnchor: HorizontalAnchor): (SvgTextElement) -> SvgTextElement {
        return { line ->
            line.setAttribute(SvgConstants.SVG_TEXT_ANCHOR_ATTRIBUTE, toTextAnchor(horizontalAnchor))
            myVerticalAnchor?.let { line.setAttribute(SvgConstants.SVG_TEXT_DY_ATTRIBUTE, toDY(it)) }
            line
        }
    }

    private fun repositionLines(index: Int, line: SvgTextElement): SvgTextElement {
        val totalHeightShift = myLineHeight * (linesCount() - 1)

        val adjustedYStart = yStart - when (myVerticalAnchor) {
            VerticalAnchor.TOP -> 0.0
            VerticalAnchor.CENTER -> totalHeightShift / 2
            VerticalAnchor.BOTTOM -> totalHeightShift
            else -> 0.0
        }

        line.y().set(adjustedYStart + myLineHeight * index)
        return line
    }

    fun linesCount() = myLinesSize

    companion object {
        fun splitLines(text: String) = text.split('\n').map(String::trim)

        internal fun getFirstTSpanChild(svgTextElement: SvgTextElement): SvgTSpanElement? {
            val firstChild = svgTextElement.children().firstOrNull()
            return when (firstChild) {
                is SvgTSpanElement -> firstChild
                is SvgAElement -> firstChild.children().firstOrNull() as? SvgTSpanElement? // First child can be a link element with a tspan inside
                else -> null
            }
        }
    }
}