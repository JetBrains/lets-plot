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
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgConstants
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextElement
import kotlin.math.roundToInt


class MultilineLabel(
    val text: String,
    private val wrapWidth: Int = -1,
    private val markdown: Boolean = false
) : SvgComponent() {
    private var myLines: List<SvgTextElement> = emptyList()
    private var myTextColor: Color? = null
    private var myFontSize = 0.0
    private var myFontWeight: String? = null
    private var myFontFamily: String? = null
    private var myFontStyle: String? = null
    private var myLineHeight = 0.0
    private var myVerticalAnchor: VerticalAnchor? = null
    private var yStart = 0.0

    init {
        resetLines()
    }

    override fun buildComponent() {
    }

    override fun addClassName(className: String) {
        myLines.forEach { it.addClass(className) }
    }

    fun textColor(): WritableProperty<Color?> {
        return object : WritableProperty<Color?> {
            override fun set(value: Color?) {
                // set attribute for svg->canvas mapping to work
                myLines.forEach(SvgTextElement::fillColor)

                // duplicate in 'style' to override styles of container
                myTextColor = value
                updateStyleAttribute()
            }
        }
    }

    fun setHorizontalAnchor(anchor: HorizontalAnchor) {
        myLines.forEach {
            it.setAttribute(SvgConstants.SVG_TEXT_ANCHOR_ATTRIBUTE, toTextAnchor(anchor))
        }
    }

    fun setVerticalAnchor(anchor: VerticalAnchor) {
        myVerticalAnchor = anchor
        myLines.forEach {
            it.setAttribute(SvgConstants.SVG_TEXT_DY_ATTRIBUTE, toDY(anchor))
        }
        repositionLines()
    }

    fun setFontSize(px: Double) {
        myFontSize = px
        updateStyleAttribute()
    }

    /**
     * @param cssName : normal, bold, bolder, lighter
     */
    fun setFontWeight(cssName: String?) {
        myFontWeight = cssName
        updateStyleAttribute()
    }

    /**
     * @param cssName : normal, italic, oblique
     */
    fun setFontStyle(cssName: String?) {
        myFontStyle = cssName
        updateStyleAttribute()
    }

    /**
     * @param fontFamily : for example 'sans-serif' or 'Times New Roman'
     */
    fun setFontFamily(fontFamily: String?) {
        myFontFamily = fontFamily
        updateStyleAttribute()
    }

    fun setTextOpacity(value: Double?) {
        myLines.forEach { it.fillOpacity().set(value) }
    }

    private fun updateStyleAttribute() {
        resetLines()
        val styleAttr = Text.buildStyle(
            myTextColor,
            myFontSize,
            myFontWeight,
            myFontFamily,
            myFontStyle
        )
        myLines.forEach { it.setAttribute(SvgConstants.SVG_STYLE_ATTRIBUTE, styleAttr) }
    }

    fun setX(x: Double) {
        myLines.forEach { it.x().set(x) }
    }

    fun setY(y: Double) {
        yStart = y
        repositionLines()
    }

    fun setLineHeight(v: Double) {
        myLineHeight = v
        repositionLines()
    }

    private fun resetLines() {
        // Remove the current text elements from the rootGroup
        myLines.forEach(rootGroup.children()::remove)
        // The font is used here to estimate the width of the text
        val font = Font(
            family = FontFamily( myFontFamily ?: "sans-serif", false),
            size = myFontSize.roundToInt().let { if (it > 0) it else 1 },
            isBold = myFontWeight == "bold",
            isItalic = myFontStyle == "italic"
        )
        myLines = RichText.toSvg(text, font, TextWidthEstimator::widthCalculator, wrapWidth, markdown = markdown)
        myLines.forEach(rootGroup.children()::add)
    }

    private fun repositionLines() {
        val totalHeightShift = myLineHeight * (myLines.size - 1)

        val adjustedYStart = yStart - when (myVerticalAnchor) {
            VerticalAnchor.TOP -> 0.0
            VerticalAnchor.CENTER -> totalHeightShift / 2
            VerticalAnchor.BOTTOM -> totalHeightShift
            else -> 0.0
        }

        myLines.forEachIndexed { index, elem ->
            elem.y().set(adjustedYStart + myLineHeight * index)
        }
    }

    fun linesCount() = myLines.size

    companion object {
        fun splitLines(text: String) = text.split('\n').map(String::trim)
    }
}