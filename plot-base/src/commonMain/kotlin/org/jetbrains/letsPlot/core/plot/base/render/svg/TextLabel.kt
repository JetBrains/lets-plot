/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.svg

import org.jetbrains.letsPlot.commons.intern.observable.property.Property
import org.jetbrains.letsPlot.commons.intern.observable.property.WritableProperty
import org.jetbrains.letsPlot.commons.intern.util.TextWidthEstimator
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.Font
import org.jetbrains.letsPlot.commons.values.FontFamily
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text.HorizontalAnchor
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text.toDY
import org.jetbrains.letsPlot.core.plot.base.render.svg.Text.toTextAnchor
import org.jetbrains.letsPlot.core.plot.base.render.text.RichText
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgConstants
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgConstants.SVG_STYLE_ATTRIBUTE
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextContent
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgTextElement
import kotlin.math.roundToInt

class TextLabel(private val text: String, private val markdown: Boolean = false) : SvgComponent() {
    private var myText: SvgTextElement = SvgTextElement()
    private var myClassName: String? = null
    private var myTextColor: Color? = null
    private var myFontSize = 0.0
    private var myFontWeight: String? = null
    private var myFontFamily: String? = null
    private var myFontStyle: String? = null
    private var myHorizontalAnchor: HorizontalAnchor = RichText.DEF_HORIZONTAL_ANCHOR
    private var myVerticalAnchor: Text.VerticalAnchor? = null

    init {
        resetText()
    }

    override fun buildComponent() {

    }

    override fun addClassName(className: String) {
        myClassName = className
        resetText()
    }

    fun textColor(): WritableProperty<Color?> {
        return object : WritableProperty<Color?> {
            override fun set(value: Color?) {
                // duplicate in 'style' to override styles of container
                myTextColor = value
                resetText()
            }
        }
    }

    fun textOpacity(): WritableProperty<Double?> {
        return myText.fillOpacity()
    }

    fun x(): Property<Double?> {
        return myText.x()
    }

    fun y(): Property<Double?> {
        return myText.y()
    }

    fun setHorizontalAnchor(anchor: HorizontalAnchor) {
        myHorizontalAnchor = anchor
        resetText()
    }

    fun setVerticalAnchor(anchor: Text.VerticalAnchor) {
        myVerticalAnchor = anchor
        resetText()
    }

    fun setFontSize(px: Double) {
        myFontSize = px
        resetText()
    }

    /**
     * @param cssName : normal, bold, bolder, lighter
     */
    fun setFontWeight(cssName: String?) {
        myFontWeight = cssName
        resetText()
    }

    /**
     * @param cssName : normal, italic, oblique
     */
    fun setFontStyle(cssName: String?) {
        myFontStyle = cssName
        resetText()
    }

    /**
     * @param fontFamily : for example 'sans-serif' or 'Times New Roman'
     */
    fun setFontFamily(fontFamily: String?) {
        myFontFamily = fontFamily
        resetText()
    }

    // Similar to MultilineLabel#resetLines()
    private fun resetText() {
        rootGroup.children().clear()
        updateText()
        rootGroup.children().add(myText)
    }

    // Similar to MultilineLabel#constructLines()
    private fun updateText() {
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
        val textElement = RichText.toSvg(
            text.replace("\n", " "), // TextLabel is a single-line text element
            font,
            TextWidthEstimator::widthCalculator,
            markdown = markdown,
            anchor = myHorizontalAnchor
        ).firstOrNull() ?: SvgTextElement()
        val actualHorizontalAnchor = getActualHorizontalAnchor(textElement)
        myText = textElement
            .let { updateTextAttributes(styleAttr)(it) }
            .let { updateAnchors(actualHorizontalAnchor)(it) }
    }

    // Similar to MultilineLabel#getActualHorizontalAnchor()
    private fun getActualHorizontalAnchor(textElement: SvgTextElement): HorizontalAnchor {
        val x = MultilineLabel.getFirstTSpanChild(textElement)?.getAttribute(SvgTextContent.X)?.get()
        return when (x) {
            null -> myHorizontalAnchor
            else -> HorizontalAnchor.LEFT
        }
    }

    // Similar to MultilineLabel#updateLinesAttributes()
    private fun updateTextAttributes(styleAttr: String): (SvgTextElement) -> SvgTextElement {
        return { textElement ->
            textElement.setAttribute(SVG_STYLE_ATTRIBUTE, styleAttr)
            myClassName?.let { textElement.addClass(it) }
            myTextColor?.let { textElement.fillColor() } // set attribute for svg->canvas mapping to work
            textElement
        }
    }

    // Similar to MultilineLabel#updateAnchors()
    private fun updateAnchors(horizontalAnchor: HorizontalAnchor): (SvgTextElement) -> SvgTextElement {
        return { textElement ->
            textElement.setAttribute(SvgConstants.SVG_TEXT_ANCHOR_ATTRIBUTE, toTextAnchor(horizontalAnchor))
            // replace "dominant-baseline" with "dy" because "dominant-baseline" is not supported by Batik
            //    myText.setAttribute("dominant-baseline", toDominantBaseline(anchor));
            myVerticalAnchor?.let { textElement.setAttribute(SvgConstants.SVG_TEXT_DY_ATTRIBUTE, toDY(it)) }
            textElement
        }
    }
}
