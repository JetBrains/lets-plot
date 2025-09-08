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
import org.jetbrains.letsPlot.core.plot.base.theme.DefaultFontFamilyRegistry
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

    override fun addClassName(className: String) = updateAndReset { myClassName = className }

    fun textColor(): WritableProperty<Color?> {
        return object : WritableProperty<Color?> {
            override fun set(value: Color?) = updateAndReset {
                // duplicate in 'style' to override styles of container
                myTextColor = value
            }
        }
    }

    fun setHorizontalAnchor(anchor: HorizontalAnchor) = updateAndReset { myHorizontalAnchor = anchor }

    fun setVerticalAnchor(anchor: VerticalAnchor) = updateAndReset { myVerticalAnchor = anchor }

    fun setFontSize(px: Double) = updateAndReset { myFontSize = px }

    /**
     * @param cssName : normal, bold, bolder, lighter
     */
    fun setFontWeight(cssName: String?) = updateAndReset { myFontWeight = cssName }

    /**
     * @param cssName : normal, italic, oblique
     */
    fun setFontStyle(cssName: String?) = updateAndReset { myFontStyle = cssName }

    /**
     * @param fontFamily : for example 'sans-serif' or 'Times New Roman'
     */
    fun setFontFamily(fontFamily: String?) = updateAndReset { myFontFamily = fontFamily }

    fun setTextOpacity(value: Double?) = updateAndReset { myTextOpacity = value }

    fun setX(x: Double) = updateAndReset { xStart = x }

    fun setY(y: Double) = updateAndReset { yStart = y }

    fun setLineHeight(v: Double) = updateAndReset { myLineHeight = v }

    private inline fun <T> updateAndReset(setter: () -> T) {
        setter()
        resetLines()
    }

    // Each time a property is changed, the whole lines list is rebuilt and reset to the rootGroup
    private fun resetLines() {
        rootGroup.children().clear()
        constructLines().forEach(rootGroup.children()::add)
    }

    private fun constructLines(): List<SvgTextElement> {
        val font = Font(
            family = DefaultFontFamilyRegistry().get(myFontFamily ?: FontFamily.DEF_FAMILY_NAME),
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
            wrapWidth,
            markdown = markdown,
            anchor = myHorizontalAnchor,
            initialX = xStart ?: 0.0
        )
        myLinesSize = lines.size
        val horizontalAnchors = horizontalAnchorByLine(lines)
        return lines.map(updateLinesAttributes(styleAttr))
            .mapIndexed(updateAnchors(horizontalAnchors))
            .mapIndexed(::repositionLines)
    }

    // Determines the horizontal anchor for each line based on the first tspan child
    // Anchor should always be LEFT if the first tspan has a defined x attribute
    private fun horizontalAnchorByLine(lines: List<SvgTextElement>): List<HorizontalAnchor> {
        return lines.map { line ->
            getFirstTSpanChild(line)?.x()?.get() != null
        }.map { firstNodeHasDefinedX ->
            when (firstNodeHasDefinedX) {
                true -> HorizontalAnchor.LEFT
                false -> myHorizontalAnchor
            }
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

    private fun updateAnchors(horizontalAnchors: List<HorizontalAnchor>): (Int, SvgTextElement) -> SvgTextElement {
        return { i, line ->
            line.setAttribute(SvgConstants.SVG_TEXT_ANCHOR_ATTRIBUTE, toTextAnchor(horizontalAnchors[i]))
            myVerticalAnchor?.let { line.setAttribute(SvgConstants.SVG_TEXT_DY_ATTRIBUTE, toDY(it)) }
            line
        }
    }

    private fun repositionLines(i: Int, line: SvgTextElement): SvgTextElement {
        val totalHeightShift = myLineHeight * (linesCount() - 1)

        val adjustedYStart = yStart - when (myVerticalAnchor) {
            VerticalAnchor.TOP -> 0.0
            VerticalAnchor.CENTER -> totalHeightShift / 2
            VerticalAnchor.BOTTOM -> totalHeightShift
            else -> 0.0
        }

        line.y().set(adjustedYStart + myLineHeight * i)
        return line
    }

    fun linesCount() = myLinesSize

    companion object {
        fun splitLines(text: String) = text.split('\n').map(String::trim)

        internal fun getFirstTSpanChild(svgTextElement: SvgTextElement): SvgTSpanElement? {
            val firstChild = svgTextElement.children().firstOrNull() ?: return null
            return when (firstChild) {
                is SvgTSpanElement -> firstChild
                is SvgAElement -> firstChild.children().single() as SvgTSpanElement // First child can be a link element with a tspan inside
                else -> throw IllegalStateException(
                    "Expected SvgTSpanElement or SvgAElement, but got: ${firstChild::class.simpleName}."
                )
            }
        }
    }
}